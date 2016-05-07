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

package tutorial.cogio

import cogdebugger.CogDebuggerApp
import cogio.imagefiles.ColorImage
import cogx.platform.types.Pixel
import libcog._

/** Examples of a constant color field, both programmatic and read from an
   * image file.
   *
   * @author Greg Snider
   */

object ConstantColorFieldExample extends CogDebuggerApp (
   new ComputeGraph {
     // Programmatic example.
     val Rows = 100
     val Columns = 200
     def pixelValue(row: Int, column: Int): Pixel = {
       // We set blue from minimum at the top to maximum at the bottom. Left to
       // right, red changes from max to min, while green changes min to max.
       val blue = row / Rows.toFloat
       val red = (Columns - column) / Columns.toFloat
       val green = column / Columns.toFloat
       new Pixel(red, green, blue)
     }
     val constantField = ColorField(Rows, Columns, pixelValue )

     // Constant color field read from file.
     val flower = ColorImage("resources/flower.jpg")

     probeAll
   }
 )
