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
  * A simple example demonstrating common unary operators
  * @author Matthew Pickett
  */
object Unary extends CogDebuggerApp(
  new ComputeGraph{
    val noise = ScalarField.random(50,50)*2f-1f

    //compute the absolute value of each field point
    val absOp = abs(noise)
    //compute the square
    val sqOp = sq(noise)
    //compute the square root
    val sqrtOp =  sqrt(noise)
    //compute the exp
    val expOp = exp(noise)
    //compute the log
    val logOp = log(noise)
    //return the truncated integer part of each field point
    val floorOp = floor(noise*10f)
    //return each field point to the 3rd power
    val powOp = pow(noise, 3)
    //return the reciprocal of each point. does not throw exceptions for 1/0
    val reciprocalOp = reciprocal(noise)
    //return the sign of each point
    val signumOp = signum(noise)

    probeAll
  }
)
