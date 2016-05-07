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
  * A simple example demonstrating how to use upsampling and downsampling
  * @author Matthew Pickett
  */
object UpAndDownsample extends CogDebuggerApp(
  new ComputeGraph{
    val img = GrayscaleImage("resources/oranges.jpg")

    //downsample by a factor of 2
    val down2 = downsample(img, 2)
    //downsample with a phase offset of 1
    val down2_1 = downsample(img, 2, 1)

    //upsample by a factor of 2
    val up2 = upsample(img, 2)
    //upsample by a factor of 2 with a phase offset of 1
    val up2_1 = upsample(img, 2, 1)


    probeAll
  }
)
