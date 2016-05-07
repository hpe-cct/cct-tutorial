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

/** Example for functionally defining a `Vector`. A `Vector` is a CPU - resident algebraic object.
  * They are primarily used for initializing and reading `VectorField`s, populating `VectorSensor`s,
  * and computing with CPU `Operator`s
  * @author Matthew Pickett
  */
object VectorExample extends App{
  //define some scala functions
  val increasingFcn = (x:Int) => x/10f
  val decreasingFcn = (offset:Int, x:Int) => (offset-x)/10f

  //Must define vector length and a scala function (Int) => Float to intialize tensors
  //use increasingFcn to init vector
  val sequence1 = Vector(10, increasingFcn)
  //use partially applied decreasingFcn to init vector
  val sequence2 = Vector(10, decreasingFcn(9,_))

  println(s"sequence1: $sequence1")
  println(s"sequence2: $sequence2")

  //Calculate the sum of the two vectors and print. Note the round-off error.
  println(s"sum by adding vectors: ${sequence1+sequence2}")

  //define another scala function which calculates the sum
  def sumFcn(offset:Int, x:Int) = increasingFcn(x) + decreasingFcn(offset, x)
  //use partially applied sumFcn to init vector
  val sum = Vector(10, sumFcn(9,_))
  println(s"sum by adding functions: $sum")

  //calculate the dot product of the two vectors
  println(s"dot product: ${sequence1.dot(sequence2)}")

  //generate a Vector randomly
  println(s"a randomly initialized Vector: ${Vector.random(10)}")
}
