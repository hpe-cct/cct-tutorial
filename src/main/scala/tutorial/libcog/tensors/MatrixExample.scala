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

package tutorial.libcog.tensors

import libcog._

/** Example for functionally defining a `Matrix`. A `Matrix` is a CPU - resident algebraic object.
  * They are primarily used for initializing and reading `MatrixField`s, populating `MatrixSensor`s,
  * and computing with CPU `Operator`s
  * @author Matthew Pickett
  */
object MatrixExample extends App{
  val identityFcn = (row:Int, column:Int) => if(row == column) 1f else 0f

  //create and print Matrix using identityFcn
  val identity = Matrix(4, 4, identityFcn)
  println("A 4 x 4 identity Matrix: ")
  identity.print

  //create a new matrix which twice the identity matrix
  val doubleMat = identity*2
  println("2x the identity Matrix: ")
  doubleMat.print

  val randVec = Vector.random(4)
  println(s"randomly intialized vector: $randVec")
  println(s"transformed by identity matrix: ${identity * randVec}")
  println(s"transformed by doubleMat: ${doubleMat * randVec}")
}

