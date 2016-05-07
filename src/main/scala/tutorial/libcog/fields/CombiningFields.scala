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

package tutorial.libcog.fields

import libcog._
import cogx.runtime.ComputeGraph

/**
 * Shows various legal combinations of different fields.  Uncomment ILLEGAL lines to throw exceptions.
 */
object CombiningFields extends App {
  val cg = new ComputeGraph {
    val fieldShape = Shape(100,100)
    val bigFieldShape = Shape(200, 200)
    val vectorShape = Shape(5)
    val scalarField = ScalarField(fieldShape)
    val bigScalarField = ScalarField(bigFieldShape)
    val vectorField = VectorField(fieldShape, vectorShape)
    val matrixField = MatrixField(fieldShape, Shape(7, 7))
    val complexField = ComplexField(fieldShape)
    val complexVectorField = ComplexVectorField(fieldShape, vectorShape)
    val zeroDimScalarField = ScalarField(1.234f)
    val zeroDimVectorField = VectorField(new Vector(5))

    val x1 = scalarField + scalarField                 // OK
    val x1c = complexField + complexField              // OK

//    val x2 = scalarField - bigScalarField              // ILLEGAL, different input field shapes

    val x3 = vectorField * scalarField                 // OK
    val x3c = complexVectorField * scalarField         // OK

    val x4 = vectorField / complexField                // OK
    val x4c = complexVectorField / complexField        // OK

    val x5v = vectorField * vectorField                // OK (element-wise multiplication)
    val x5m = matrixField * matrixField                // OK (element-wise multiplication)
    val x5c = complexVectorField * complexVectorField  // OK (element-wise multiplication)

    val x5c2 = complexVectorField * vectorField        // OK (complex and real fields can be combined)

    val x6 = vectorField + zeroDimVectorField          // OK, vector field <op> 0D vector field.

    val x7 = vectorField + zeroDimScalarField          // OK, vector field <op> 0D scalar field.

    val x8 = scalarField + zeroDimScalarField          // OK, scalar field <op> 0D scalar field.

//    val x9 = vectorField * matrixField               // ILLEGAL, arithmetically incompatible tensor shapes

//    val x10 = scalarField + zeroDimVectorField       // ILLEGAL, scalar field <op> 0D vector field.
    probeAll
  }

  cg.withRelease { cg.reset }

}
