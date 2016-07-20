/*
 * (c) Copyright 2016 Hewlett Packard Enterprise Development LP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tutorial.toolkit.neuralnetwork

import libcog._
import toolkit.neuralnetwork.DifferentiableField
import toolkit.neuralnetwork.DifferentiableField.GradientPort

/** Sum a centered neighborhood (in the vector direction) of element squares.
  *
  * The implementation can handle the case where component vectors have length < windowSize.
  *
  * @author Dick Carter
  *
  * @param in1 the input signal
  * @param windowSize the size of the window over which to sum the square
  */
case class VectorMeanSquaresBorderCyclic(in1: DifferentiableField, windowSize: Int) extends DifferentiableField {

  require(windowSize > 0, s"VectorMeanSquaresBorderCyclic: window $windowSize must be positive.")
  require(windowSize % 2 == 1, s"VectorMeanSquaresBorderCyclic: window $windowSize must be odd.")

  private val halo = (windowSize - 1) / 2

  // Make a zeroed Array of element variables
  private def makeInitedArray(x: Field, size: Int) = Array.tabulate(size) { (i) =>
    val e = _tensorElementVar(x)
    e := 0.0f
    e
  }

  // Helper function to shift in a new element and also add it to the existing elements
  private def addToArray(a: Array[GPUVariable], newVal: GPUExpression): Unit = {
    for (i <- 0 until a.length-1)
      a(i) := a(i+1) + newVal
    a(a.length-1) := newVal
  }

  // Take an index into the vector that might be negative or above range, and produce the equivalent in-range index.
  private def cyclicWrap(i: Int, vecLen: Int) = ((i % vecLen) + vecLen) % vecLen

  /** Forward function.
    *
    * Each (x,y) point in the input field is essentially `batchSize` independent vectors (one for each
    * image of the batch) that have been stacked.  This GPUOperator allocates one thread to normalize
    * each of these independent vectors.
    */
  private def _forward(in: (Field, Int)): (Field, Int) = {
    val (x, batchSize) = in
    require(x.tensorOrder == 1, "tensor order 1 is required")
    require(x.tensorShape(0) % batchSize == 0, "tensor length must be an integer multiple of the batch size")
    val vecLen = x.tensorShape(0) / batchSize
    require(vecLen >= windowSize, s"VectorMeanSquaresBorderCyclic: vector length $vecLen must be >= window size $windowSize.")

    if (windowSize == 1)
      (x*x, batchSize)
    else {
      val out = GPUOperator(x.fieldType, "vectorMeanSquaresBorderCyclic") {
        _globalThreads(x.fieldShape, Shape(batchSize))

        // A queue of partial sums for elements yet-to-be written
        val sums = makeInitedArray(x, windowSize-1)

        // Prologue- handles reads that occur before any writing can begin
        for (i <- -halo until halo) {
          val a = _tensorElementVar(x)
          a := _readTensorElement(x, _tensorElement * vecLen + cyclicWrap(i, vecLen))
          val aSquared = _tensorElementVar(x)
          aSquared := a * a
          addToArray(sums, aSquared)
        }

        // Main loop- where each read completes a value that can then be written
        for (i <- halo until vecLen + halo) {
          val a = _tensorElementVar(x)
          a := _readTensorElement(x, _tensorElement * vecLen + cyclicWrap(i, vecLen))
          val aSquared = _tensorElementVar(x)
          aSquared := a * a
          _writeTensorElement(_out0, (sums(0) + aSquared)*(1f/windowSize), _tensorElement * vecLen - halo + i)
          addToArray(sums, aSquared)
        }
      }
      (out, batchSize)
    }
  }

  private def jacobian(dx: Field, in: (Field, Int)): Field = {
    val (x, batchSize) = in
    require(x.tensorOrder == 1, "tensor order 1 is required")
    require(x.tensorShape(0) % batchSize == 0, "tensor length must be an integer multiple of the batch size")
    val vecLen = x.tensorShape(0) / batchSize

    if (windowSize == 1)
      x * dx * 2.0f
    else {
      val out = GPUOperator(x.fieldType, "jacobianVectorMeanSquaresBorderCyclic") {
        _globalThreads(x.fieldShape, Shape(batchSize))

        // A queue of partial sums for elements yet-to-be written
        val sums = makeInitedArray(x, windowSize-1)

        // Prologue- handles reads that occur before any writing can begin
        for (i <- -halo until halo) {
          val a = _tensorElementVar(x)
          val delta = _tensorElementVar(x)
          delta := _readTensorElement(x, _tensorElement * vecLen + cyclicWrap(i, vecLen)) *
            _readTensorElement(dx, _tensorElement * vecLen + cyclicWrap(i, vecLen)) * 2.0f
          addToArray(sums, delta)
        }

        // Main loop- where each read completes a value that can then be written
        for (i <- halo until vecLen + halo) {
          val delta = _tensorElementVar(x)
          delta := _readTensorElement(x, _tensorElement * vecLen + cyclicWrap(i, vecLen)) *
            _readTensorElement(dx, _tensorElement * vecLen + cyclicWrap(i, vecLen)) * 2.0f
          _writeTensorElement(_out0, (sums(0) + delta)*(1f/windowSize), _tensorElement * vecLen - halo + i)
          addToArray(sums, delta)
        }
      }
      out
    }
  }

  /*
   * The transpose of the jacobian matrix is not equal to itself, so the jacobianAdjoint requires
   * its own unique implementation.  Consider the case of vectorLen 4, batchSize = 2, windowSize = 3.
   * The jacobian is calculated as:
   *
   *      +-                               -+            +-     -+
   *      |  X0  X1  0   0   0   0   0  X7   |            |  dX0  |
   *      |  X0  X1  X2  0   0   0   0   0   |            |  dX1  |
   *      |  0   X1  X2  X3  0   0   0   0   |            |  dX2  |
   *  2 * |  0   0   X2  X3  0   0   0   0   |     X      |  dX3  |
   *      |  0   0   0   0   X4  X5  0   0   |            |  dX4  |
   *      |  0   0   0   0   X4  X5  X6  0   |            |  dX5  |
   *      |  0   0   0   0   0   X5  X6  X7  |            |  dX6  |
   *      |  X0  0   0   0   0   0   X6  X7  |            |  dX7  |
   *      +-                                -+            +-     -+
   *
   * The jacobian adjoint calculation uses the transpose of the above square matrix as follows:
   *
   *      +-                               -+            +-     -+
   *      |  X0  X0  0   0   0   0   0  X0   |            |  dY0  |
   *      |  X1  X1  X1  0   0   0   0   0   |            |  dY1  |
   *      |  0   X2  X2  X2  0   0   0   0   |            |  dY2  |
   *  2 * |  0   0   X3  X3  0   0   0   0   |     X      |  dY3  |
   *      |  0   0   0   0   X4  X4  0   0   |            |  dY4  |
   *      |  0   0   0   0   X5  X5  X5  0   |            |  dY5  |
   *      |  0   0   0   0   0   X6  X6  X6  |            |  dY6  |
   *      |  X7  0   0   0   0   0   X7  X7  |            |  dY7  |
   *      +-                                -+            +-     -+
   *
   *  For windowSize = 1, the jacobian matrix is populated only along the diagonal, so a simpler approach
   *  is possible for this degenerate case.
   *
   */
  private def jacobianAdjoint(grad: Field, in: (Field, Int)): Field = {
    val (x, batchSize) = in
    require(x.tensorOrder == 1, "tensor order 1 is required")
    require(x.tensorShape(0) % batchSize == 0, "tensor length must be an integer multiple of the batch size")
    val vecLen = x.tensorShape(0) / batchSize

    if (windowSize == 1)
      x * grad * 2.0f
    else {
      val out = GPUOperator(x.fieldType, "jacobianAdjointVectorMeanSquaresBorderCyclic") {
        _globalThreads(x.fieldShape, Shape(batchSize))

        // A queue of partial sums for elements yet-to-be written
        val sums = makeInitedArray(x, windowSize-1)

        // Prologue- handles reads that occur before any writing can begin
        for (i <- -halo until halo) {
          val a = _tensorElementVar(x)
          val delta = _tensorElementVar(x)
          delta := _readTensorElement(grad, _tensorElement * vecLen + cyclicWrap(i, vecLen))
          addToArray(sums, delta)
        }

        // Main loop- where each read completes a value that can then be written
        for (i <- halo until vecLen + halo) {
          val delta = _tensorElementVar(x)
          delta := _readTensorElement(grad, _tensorElement * vecLen + cyclicWrap(i, vecLen))
          val writeIndex = _tensorElement * vecLen - halo + i
          val writeVal = _tensorElementVar(x)
          writeVal := (sums(0) + delta) * _readTensorElement(x, writeIndex) * (2f/windowSize)
          _writeTensorElement(_out0, writeVal, writeIndex)
          addToArray(sums, delta)
        }
      }
      out
    }
  }

  private val in = (in1.forward, in1.batchSize)

  override val batchSize: Int = in1.batchSize

  override val forward: Field = _forward(in1.forward, batchSize)._1

  override val inputs: Map[Symbol, GradientPort] =
    Map('input -> GradientPort(in1, jacobian(_, in), jacobianAdjoint(_, in)))
}
