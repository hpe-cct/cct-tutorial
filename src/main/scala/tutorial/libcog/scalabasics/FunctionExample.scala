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

object FunctionExample extends App{

  //A few simple examples of functions with no parameters that don't return anything
  val f1Value = () => {println("A parameterless Unit function as a val")}
  def f1Def(){
    println("A parameterless Unit function as a def")
  }
  f1Value()
  f1Def()

  //A few examples of function values that take parameters and return a result
  val squareInt:Int=>Int = (x:Int) => x*x
  val sqrtFloat:Float=>Float = (x:Float) => math.sqrt(x).toFloat

  val twoSquared = squareInt(2)
  println("Two squared: " + twoSquared)
  val sqrtOfPi = sqrtFloat(math.Pi.toFloat)
  println("Square root of Pi: " + sqrtOfPi)

  //An example of using string comprehensions for inline code evaluation
  //We use string comprehensions extensively in the examples for brevity
  println(s"Four squared: ${squareInt(4)}")

  //We can use functional arguments to other functions to do powerful things, in
  // this case the composition of arbitrary functions.
  def compose(x:Int, f1:Int=>Int, f2:Float=>Float) = f2(f1(x).toFloat)
  println(s"Do nothing, in a complicated way: ${compose(5, squareInt, sqrtFloat)}")

  //Scala has a bunch of support for programming in a declarative way which helps to
  // avoid many of the pitfalls associated with mutability and indexing. For example
  // Arrays can be defined functionally rather than based on loops:
  val first10squares = List.tabulate(10){i=>squareInt(i)}
  println(s"The squares of the first 10 positive integers: $first10squares")
  //This avoids any indexing errors or reasoning about mutability, circumventing many
  // a forehead-slap-inducing bug

  //Collections can be functionally transformed using map as well, a very common pattern that avoids
  // tons of boilerplate and potential bugs:
  val first10Ints = first10squares.map(x=> sqrtFloat(x).toInt)
  println(s"The first 10 positive integers: $first10Ints")
}
