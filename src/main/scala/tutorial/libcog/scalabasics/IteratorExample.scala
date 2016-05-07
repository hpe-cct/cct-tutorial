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

import java.util.{Calendar, GregorianCalendar}

object IteratorExample extends App{
  //An iterator is a powerful functional collection that enables one to calculate
  // elements of a collection on demand. These are used in Cog for defining operator interfaces to
  // Fields.

  //Let's start by defining an iterator that acts an interface to the time at which it was created.
  val iter = new Iterator[Int] {
    private val cal = new GregorianCalendar()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val second = cal.get(Calendar.SECOND)

    private var i = 0

    //each iterator must define a hasNext function
    def hasNext = i < 3

    //each iterator must define a next function that returns its wrapped type, in this case an Int
    def next() = i match {
      case 0 => i += 1; hour
      case 1 => i += 1; minute
      case 2 => i += 1; second
      case _ => throw new IndexOutOfBoundsException
    }
  }

  println(s"The time is: ${iter.next()}:${iter.next()}:${iter.next()}")

  //This is false because the iterator has been consumed
  println(iter.hasNext)
  //The next call to next will throw an exception because hasNext is false
  try{
    iter.next()
  }
  catch {
    case e:IndexOutOfBoundsException => println("oops, trying to read a consumed iterator")
  }
}
