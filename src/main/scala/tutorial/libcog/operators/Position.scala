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
  * A simple example demonstrating how to use the position operators which return
  * positions based on local or global maxima/minima
  * @author Matthew Pickett
  */
object Position extends CogDebuggerApp(
  new ComputeGraph{
    val img = ScalarField(50,50,(i,j)=> 1f/(1f+(24-i)*(24-i) + (24-j)*(24-j)))

    //should return the global max (24,24)
    val pos = maxPosition(img)

    val neighborhood = Matrix(3,3,(i,j)=>1f)
    //returns the local neighborhood staistics
    val localMaxOp = localMax(img, neighborhood)
    val localMaxPos = localMaxPosition(img, neighborhood)
    val localMinOp = localMin(img, neighborhood)
    val localMinPos = localMinPosition(img, neighborhood)

    probeAll
  }
)
