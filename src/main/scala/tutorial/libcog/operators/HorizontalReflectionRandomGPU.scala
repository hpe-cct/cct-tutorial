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

import cogdebugger._
import cogio._
import libcog._


/**
  * A simple example demonstrating a CPU operator (extends GPU operator example)
  */

object HorizontalReflectionRandomGPU extends App {


  def randomHorizontalReflection(img: Field): Field = {
    require(img.fieldShape.dimensions == 2, "Requires a 2D field")

    val flipField = ScalarField(0f)
    val flipBoolean = RandomFlipOperator(flipField)   // this calls the CPU operator

    GPUOperator(img.fieldType, "randomHorizontalRefection") {
      _globalThreads(img.fieldShape, img.tensorShape)
      val flip = _readTensor(flipBoolean)     // read the value from the CPU operator
      _if(_isnotequal(flip, 0f)) {
        val x = _readTensorElement(img, _tensorElement)
        _writeTensorElement(_out0, x, _tensorElement)
      }
      _else {
        val readRow = _row
        val readColumn = _columns - _column - 1
        val x = _readTensorElement(img, readRow, readColumn, _tensorElement)
        _writeTensorElement(_out0, x, _tensorElement)
      }
    }
  }


  val cg = new ComputeGraph {
    val img = ColorImage("resources/oranges.jpg")
    val imgReflection = randomHorizontalReflection(img.toVectorField)
    probeAll
  }
  val debugger = new CogDebuggerApp(cg) {}
  debugger.main(Array())
}
