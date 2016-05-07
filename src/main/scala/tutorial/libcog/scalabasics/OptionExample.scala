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

package tutorial.libcog.scalabasics

object OptionExample extends App{
  //options in scala are monads which are used in similar ways as nulls in Java, but
  // enable a more declarative, immutable programming style. An option can either be
  // Some or None. None when it contains nothing and Some when it contains something.

  //Let's declare two concrete cases of an optional Int
  val two:Option[Int] = Some(2)
  val none:Option[Int] = None

  //The option enables us to build functions that can take optional inputs:
  def squareIfExists(x:Option[Int]):Option[Int] = {
    if(x.isDefined) Some(x.get*x.get) else None
  }

  println(s"two squared: ${squareIfExists(two)}")
  println(s"none squared: ${squareIfExists(none)}")

  //Some of the nice features of Options include the ability to apply a mapping
  // function which tersely does the same thing as the above function
  println(s"two squared: ${two.map(x=> x*x)}")
  println(s"none squared: ${none.map(x=> x*x)}")
}
