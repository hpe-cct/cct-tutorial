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

package tutorial.libcog.sensors

import java.util.{Calendar, GregorianCalendar}

import libcog._

/**An example illustrating how to implement a Scalar Sensor and Actuator using
  * scala Iterators and Functions.
  * @author Matthew Pickett
  */
object ScalarSensorActuatorExample extends App{
  //Define the Sensor function which must be a parameterless function that returns
  // an Option[Iterator[Float]]
  val getTime = () => {
    val cal = new GregorianCalendar()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    println(s"Executing Sensor function. $hour:$minute:$second")

    val iter = new Iterator[Float] {
      private var i = 0

      def hasNext = i < 3

      def next() = i match {
        case 0 => i += 1; hour.toFloat
        case 1 => i += 1; minute.toFloat
        case 2 => i += 1; second.toFloat
      }
    }

    Some(iter)
  }

  //Define the Actuator function which is a Unit function that takes an Iterator[Float]
  val printIterator = (x:Iterator[Float]) => {
    val hour = x.next().toInt
    val minute = x.next().toInt
    val second = x.next().toInt
    println(s"Executing Actuator function. $hour:$minute:$second")
  }

  //Define the ComputeGraph as simply a Sensor and an Actuator
  //The ComputeGraph is the definition of the Cog operations
  val cg = new ComputeGraph {
    val date = new Sensor(Shape(3), getTime)
    val printer = new Actuator(date, printIterator)
  }

 /*All of the following will be executed in a ComputeGraph's first call to step:
   1) The ComputeGraph is compiled and optimized. OpenCL code is generated.
   2) The runtime Actor system is started and buffer memory is allocated.
   3) Field buffers are initialized according to their init function.
   4) Sensor function is called once to fill its CPU buffer.
   5) A full compute pass is calculated to propagate Sensor CPU buffer into Sensor's GPU buffer. Consequently
      a full compute pass is first called with the Sensor's Field initialized to all zeroes. This first pass is
      also used to fill feedback buffers.
   6) A second full compute pass is calculated to propagate the first Sensor reading through the compute Graph.

   With the first Sensor call (step 4) and the two full compute passes (5,6) the Sensor
   function is called 3 times during the first step.

   Actuator functions are only called during compute passes, so the actuator function
   will be called twice during the first step. The first time it will be called on the zero-intialized Field buffer, and
   the second time it will be called on the initial value calculated in step 4. Thus, because of
   the double-buffered Sensor, the Field values that an Actuator function operates on will always be two cycles behind the current
   execution value of the Sensor function. */

  println("First ComputeGraph step")
  cg.step(1)

  for(i<-0 until 10){
    Thread.sleep(1000)
    println("")
    println("Stepping ComputeGraph again.")
    cg.step(1)
  }

  //release ComputeGraph resources. This must be done explicitly to shutdown the Actor system.
  cg.release
}
