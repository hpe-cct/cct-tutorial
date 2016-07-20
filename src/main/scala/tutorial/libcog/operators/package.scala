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
import cogio._
import cogio.imagefiles.{ColorImage, GrayscaleImage}
import libcog._



/**
  * A simple example demonstrating how to use convolution on movies
  *
  * @author Matthew Pickett
  */
object Sample1 extends CogDebuggerApp(
  new ComputeGraph{

    // If Synchronous is true, the movie will run at its correct frame rate,
    // degrading gracefully if the computation is compute bound; if false,
    // the movie will run as fast as it can.
    val Synchronous = false //This is the default setting

    //val movieFile = "resources/courtyard-copy.mp4"
    val movieFile = "resources/scenery.mp4"
    //val movieFile = "resources/waterfalls2.mp4"
    //val movieFile = "resources/forest2.mp4"
    //val movieFile = "resources/SampleVideo_1280x720_20mb.mp4"

    val movie = ColorMovie(movieFile, Synchronous)
    val movieVector = vectorField(movie)

    val randomFilter1 = ScalarField.random(3, 3)
    val randomFilter2 = ScalarField.random(15, 15)

    val convolved1 = convolve(movieVector, randomFilter1, BorderClamp)
    val convolved2 = convolve(movieVector, randomFilter2, BorderValid)

    probeAll
  }
)
