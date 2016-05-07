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
import libcog._

/**
  * A simple example demonstrating the core matrix operations
  * @author Matthew Pickett
  */
object MatrixOps extends CogDebuggerApp(
  new ComputeGraph{
    val A = MatrixField.random(Shape(10,10),Shape(2,2))
    val y = VectorField.random(Shape(10,10),Shape(2))

    //use Gauss-Jordan to invert the matrices at each field point. Only works
    //  for small, non-singular matrices and is not designed to be robust.
    val inv = invertMatrices(A)

    //use pseudo inverse to solve 2x2 linear system
    val x = solve(A, y)

    //perform matrix vector multiply A dot x
    val y2 = transform(A, x)

    //perform matrix vector multiply A` dot x
    val At = transposeMatrices(A)
    val y3 = transform(At, x)

    probeAll
  }
)

