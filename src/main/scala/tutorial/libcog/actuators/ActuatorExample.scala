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

package tutorial.libcog.actuators

import libcog._

object ActuatorExample extends App {
  val graph = new ComputeGraph {

    // Initialize a field to (1,2,3,4)
    val field = ScalarField(4, (column) => 1 + column)
    //Increment each element by one each tick
    field <== field + 1

    // define an actuator that outputs the field elements to an array each tick
    // and specifies an initial actuator state of (4,3,2,1)
    val actuatorData = new Array[Float](4)
    val actuator = Actuator(field, actuatorData, (column) => 4 - column)
  }
  import graph._
  withRelease {
    // reset the graph, print actuator data
    reset
    println(actuatorData.mkString("  "))
    // step the graph 5 times, print actuator data after each step
    for(i <- 0 until 5) {
      step
      println(actuatorData.mkString("  "))
    }
  }
}
