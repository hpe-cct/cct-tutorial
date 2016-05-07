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
import cogio.moviefiles.GrayscaleMovie
import cogx.runtime.ComputeGraph

/** An example of a Sensor that streams a movie read from a movie file.
   *
   * @author Greg Snider
   */
object GrayscaleMovieExample extends CogDebuggerApp (
   new ComputeGraph {
     // If Synchronous is true, the movie will run at its correct frame rate,
     // degrading gracefully if the computation is compute bound; if false,
     // the movie will run as fast as it can.
     val Synchronous = false
     val movieFile = "resources/courtyard.mp4"
     val movie = GrayscaleMovie(movieFile, Synchronous)
   }
 )
