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
import cogio._
import libcog._

/**
 * @author Matthew Pickett
 */
object BackgroundSubtraction extends CogDebuggerApp (
  new ComputeGraph {
    val movieFile = "resources/courtyard.mp4"
    val movie = ColorMovie(movieFile, synchronous = false)
    val movieVector = vectorField(movie)
    val background = VectorField(movie.fieldShape, Shape(3))
    background <== 0.999f*background + 0.001f*movieVector
    val backgroundColor = colorField(background)
    val suspicious = reduceSum(abs(background - movieVector))
    probe(movie)
    probe(background)
    probe(suspicious)

  }
)
