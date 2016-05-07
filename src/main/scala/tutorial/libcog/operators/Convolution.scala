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
import cogio.imagefiles.GrayscaleImage
import libcog._


/**
  * A simple example demonstrating how to use convolution
  * @author Matthew Pickett
  */
object Convolution extends CogDebuggerApp(
  new ComputeGraph{
    val img = GrayscaleImage("resources/oranges.jpg")

    val randomFilter = ScalarField.random(9,9)

    val convolved = convolve(img, randomFilter, BorderClamp)
    val crossCorrelated = crossCorrelate(img, randomFilter, BorderClamp)

    //using a BorderValid border policy
    val valid = convolve(img, randomFilter, BorderValid)

    //using a sampling policy, now with BorderClamp borderPolicy
    val downsampled = convolve(img, randomFilter,
      borderPolicy = BorderClamp,
      samplingPolicy = DownsampleOutputConvolution(2))

    //another example of using both policies
    val upsampled = convolve(img, randomFilter,
      borderPolicy = BorderFull,
      samplingPolicy = UpsampleInputConvolution(2))

    probeAll
  }
)
