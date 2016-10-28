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

package tutorial.libcog.operators

import cogdebugger.CogDebuggerApp
import libcog._


object OperatorWithVariants extends App {

/** Simple example to show GPU operator variants
  *
  * In this example, the optimizer profiles two different ways to do the calculation.
  * The optimizer decides at run-time which variant performs the best and uses that one.
  *
  * This example does a multiplication of a small tensor using both small and big tensor
  * addressing modes. Big tensor addressing mode is required when each tensor has more
  * than 4 elements. When using the big tensor addressing mode, we can also using a different
  * threading model that isn't available when using small tensor addressing.
  *
  * Additional detail about the profiling is available with the following flag
  * -Dcog.verboseProfiler = true
  *
  * To change the number of steps the profiler executes prior to timing its kernels (warm-up)
  * (default = 1)
  * -Dcog.profilerWarmupSteps=10
  *
  * To change the number of steps the profiler executes to time its kernels (default = 1)
  * -Dcog.profilerSteps=100
  *
  */


  def multiplyWithVariants(vA: Field, vB: Field): Field = {
    val variants = 0 until 2
    val names = variants.map(x => s"multiply_variant_${x}_of_2").toArray
    require(vA.tensorShape <= Shape(4), "Requires a small tensor")
    require(vA.fieldType == vB.fieldType, "Requires fields to be same shape")
    GPUOperator(vA.fieldType, names) { i =>
      i match {
        case 0 =>
          val tensor = _tensorVar(vA)
          tensor := _readTensor(vA)
          val tensorB = _tensorVar(vB)
          tensorB := _readTensor(vB)
          _writeTensor(_out0, tensor * tensorB)
        case 1 =>
          _globalThreads(vA.fieldShape, vA.tensorShape)
          _forEachTensorElement(vA.tensorShape) {
            val element = _tensorElementVar(vA)
            element := _readTensorElement(vA, _tensorElement)
            val elementB = _tensorElementVar(vB)
            elementB := _readTensorElement(vB, _tensorElement)
            _writeTensorElement(_out0, element * elementB, _tensorElement)
          }
        case _ =>
          throw new RuntimeException("Unexpected variant requested")
      }
    }
  }

  val cg = new ComputeGraph {
    val inA = VectorField.random(Shape(64,64),Shape(4))
    val inB = VectorField.random(Shape(64,64),Shape(4))
    val outA = multiplyWithVariants(inA, inB)

    val inC = VectorField.random(Shape(1024,1024),Shape(4))
    val inD = VectorField.random(Shape(1024,1024),Shape(4))
    val outC = multiplyWithVariants(inC, inD)

    val inE = VectorField.random(Shape(1000,1000),Shape(4))
    val inF = VectorField.random(Shape(1000,1000),Shape(4))
    val outE = multiplyWithVariants(inE, inF)

    probeAll
  }

  val debugger = new CogDebuggerApp(cg) {}
  debugger.main(Array())
}
