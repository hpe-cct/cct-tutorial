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

import cogdebugger._
import cogio.webcam.ColorWebcam
import libcog._
import toolkit.computervision.colorfunctions.Luma
import toolkit.computervision.motion.OpticFlowPolynomial
import toolkit.filtering.spatial.LaplacianFilter

object CameraExample extends CogDebuggerApp(
  new ComputeGraph {
    val sensor = ColorWebcam(width = 640, height = 480, framerate = 30)
    val flow = new OpticFlowPolynomial(Luma(toVectorField(sensor))).flow
    val laplacian = convolve(toVectorField(sensor), LaplacianFilter(), BorderZero)

    probe(flow)
    probe(laplacian)
  }
)