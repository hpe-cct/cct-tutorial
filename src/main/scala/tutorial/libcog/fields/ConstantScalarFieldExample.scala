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

import cogdebugger.CogDebuggerApp
import libcog._

/** Example of creating constant scalar fields, both programmatically and by
   * reading from an image file.
   *
   * @author Greg Snider
   */

object ConstantScalarFieldExample extends CogDebuggerApp(
   new ComputeGraph {
     // Programmatic constant scalar field
     val constantField = ScalarField(100, 100, (row, col) => row + col)

     probeAll
   }
 )
