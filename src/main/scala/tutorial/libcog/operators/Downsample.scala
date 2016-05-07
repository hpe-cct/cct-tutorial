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
 * A simple app to demonstrate downsampling
 * Created by Dick Carter on 12/5/2014.
 */
object Downsample extends CogDebuggerApp(
  new ComputeGraph{
    val Size = 6
    // create a size x size input with alternating colors to best show the impact of downsampling with phases
    def multicoloredField(size: Int) = {
      ColorField(size, size, (r,c) => {
        val color = (r%2, c%2) match {
          case (0, 0) => new Pixel(220,64,64)   // reddish
          case (0, 1) => new Pixel(128,255,128) // greenish
          case (1, 0) => new Pixel(255,255,0)   // yellow
          case (1, 1) => new Pixel(64,128,255)  // bluish
        }
        val darkest = 2f  // makes sure the origin color is not black
        val scale = (darkest + c/2 + r/2 * size/2) / (size/2 * size/2 + darkest - 1)
        new Pixel(color.redFloat * scale, color.greenFloat * scale, color.blueFloat * scale)
      })
    }

    val input = multicoloredField(Size)

    //downsample by a factor of 2: this will pull out the red pixels
    val downsample2_phase0 = downsample(input, 2)
    //downsample by a factor of 2 with a phase offset of 1: this will pull out the blue pixels
    val downsample2_phase1 = downsample(input, 2, 1)

    probeAll
  }
)
