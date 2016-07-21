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

/** Take the mean of a centered neighborhood (in the vector direction) of element squares.
  *
  * The implementation can handle the case where component vectors have length < windowSize.
  *
  * This class is based on the description of AlexNet normalization, with two main differences.
  * The summing of values is divided by the number of values included in the sum, i.e. an average
  * of the squares is output.  Note that for a window size of 5, the top and bottom elements of the
  * vector will have 3 elements summed (so a divisor of 3), while most elements will have the full
  * window of 5 elements summed with a divisor of 5.
  *
  * Finally, besides the "BorderZero" approach used by AlexNet, a "BorderCyclic" policy is available
  * that offers a more symmetric treatment of the elements.
  *
  * @author Dick Carter
  */
object VectorMeanSquares {

  /** Factory method for creating the VectorMeanSquares Node based on BorderPolicy and windowSize
    *
    * @param in1 the input signal
    * @param windowSize the size of the window over which to sum the square
    * @param borderPolicy the border policy affecting handling of the top and bottom elements of the vector
    * @return the node performing the function
    */
  def apply(in1: DifferentiableField, windowSize: Int, borderPolicy: BorderPolicy = BorderCyclic): DifferentiableField = {
    require(windowSize > 0, s"VectorMeanquares: window $windowSize must be positive.")
    require(windowSize % 2 == 1, s"VectorMeanSquares: window $windowSize must be odd.")

    val halo = (windowSize - 1) / 2
    val (inField, batchSize) = (in1.forward, in1.batchSize)
    require(inField.tensorOrder == 1, "tensor order 1 is required")
    require(inField.tensorShape(0) % batchSize == 0, "tensor length must be an integer multiple of the batch size")
    val vecLen = inField.tensorShape(0) / batchSize

    borderPolicy match {
      case BorderZero =>
        if (halo + 1 >= vecLen)
          ??? //VectorMeanSquaresAll(in1)
        else
          ??? //VectorMeanSquaresBorderZero(in1, windowSize)
      case BorderCyclic =>
        if (windowSize >= vecLen)
          ??? //VectorMeanSquaresAll(in1)
        else
          VectorMeanSquaresBorderCyclic(in1, windowSize)
      case x => throw new RuntimeException(s"VectorMeanSquares: BorderPolicy $x not supported- use BorderZero or BorderCyclic.")
    }
  }
}
