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
 * A simple app to demonstrate upsampling
 * Created by Dick Carter on 12/5/2014.
 */
object Upsample extends CogDebuggerApp(
  new ComputeGraph{
    val Size = 3
    // create a size x size input with a gradation of red pixels
    def shadedField(size: Int) = {
      ColorField(size, size, (r,c) => {
        val darkest = 4f  // makes sure the origin color is not black
        val scale = (darkest + c + r * size) / (darkest + size * size - 1)
        new Pixel(scale, 0f, 0f)
      })
    }

    val input = shadedField(Size)
    //upsample by a factor of 2
    val upsample2_phase0 = upsample(input, 2)
    //upsample by a factor of 2 with a phase offset of 1
    val upsample2_phase1 = upsample(input, 2, 1)
    probeAll
  }
)
