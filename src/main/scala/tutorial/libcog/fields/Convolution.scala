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

package tutorial.libcog.fields

import libcog._
import cogdebugger._
import cogio._

object Convolution extends CogDebuggerApp(
  new ComputeGraph {
    val flower = GrayscaleImage("resources/flower.jpg")

    //manually define the elments in the filter
    val filterElements = Array(
      Array(0f, 1f, 0f),
      Array(1f, -4f, 1f),
      Array(0f, 1f, 0f)
    )

    //create a static filter field based on the filter elements
    val filter = ScalarField(3,3, (r,c)=>filterElements(r)(c))

    //perform convolution with the BorderClamp border policy
    val filtered = convolve(flower, filter, BorderClamp)
    
    probeAll
  }
)
