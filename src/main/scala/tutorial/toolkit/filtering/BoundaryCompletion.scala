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

package tutorial.toolkit.filtering

import libcog._
import cogdebugger._
import toolkit.filtering.spatial.GaussianFilter

/**
 * BoundaryCompletion example.
 */
object BoundaryCompletion extends CogDebuggerApp (
  new ComputeGraph {

    final val ImageSize = 149
    val imageRows = ImageSize
    val imageColumns = ImageSize
    final val sigma = 10.0f
    val inputImageMatrix = InputImage(ImageSize)
    val input = ComplexField(ImageSize, ImageSize,
      (r,c) => inputImageMatrix(r,c))
    /**
     * The input image field, expressed as a 2D array of complex
     * numbers. Each complex number expresses the orientation of the
     * strongest filter response, along with the strength of that response
     * (magnitude of the complex number).
     */
    val FilterSize = (sigma * 5).toInt | 1
    val w0 = basisFilter(FilterSize, sigma, 0) * 0.01f * 0.2f
    val w2 = basisFilter(FilterSize, sigma, 2) * 0.01f * 0.2f
    val w4 = basisFilter(FilterSize, sigma, 4) * 0.01f * 0.2f
    val w6 = basisFilter(FilterSize, sigma, 6) * 0.01f * 0.2f
    val w8 = basisFilter(FilterSize, sigma, 8) * 0.01f * 0.2f

//    probe(w0, w2, w4, w6, w8)

    // Debug
    val stickness = input.magnitude
    val orientation = input.phase
//    stickness.probe("in stickness")
//    orientation.probe("in orientation")

    val bipole0 = (orientation * (-2 * I)).exp.conjugate *
            input.magnitude convolve(w0, BorderClamp)
    val bipole1 = (orientation * (0 * I)).exp *
            input.magnitude convolve(w2, BorderClamp)
    val bipole2 = (orientation * (-2 * I)).exp *
            input.magnitude convolve(w4, BorderClamp)
    val bipole3 = (orientation * (-4 * I)).exp *
            input.magnitude convolve(w6, BorderClamp)
    val bipole4 = (orientation * (-6 * I)).exp *
            input.magnitude convolve(w8, BorderClamp)
    val bipole5 = (orientation * (0 * I)).exp *
            input.magnitude convolve(w0, BorderClamp)
    val bipole6 = (orientation * (-2 * I)).exp *
            input.magnitude convolve(w2, BorderClamp)
    val bipole7 = (orientation * (-4 * I)).exp *
            input.magnitude convolve(w4, BorderClamp)

//    val u = (bipole0 + (bipole1 * 4f) + (bipole2 * 6f) + (bipole3 * 4f) + bipole4).magnitude +
//            ((bipole5 * 6f) + (bipole6 * 8f) + (bipole7 * 2f)).realPart
    val u2 = bipole0 + (bipole1 * 4f) + (bipole2 * 6f) + (bipole3 * 4f) + bipole4
    val u0 = ((bipole5 * 6f) + (bipole6 * 8f) + (bipole7 * 2f)).realPart

    // ballness + stickness
    val u = u2.magnitude + u0
//    u.probe("ballness + stickness")

    val ballness = -u2.magnitude + u0
//    ballness.probe("ballness")

    def dogKernelMaker(width: Int, outerSigma: Float, innerSigma: Float) = {
      val inner = GaussianFilter(width, innerSigma).normalizeL1
      val outer = GaussianFilter(width, outerSigma).normalizeL1
      inner - outer
    }
    val dogKernel = dogKernelMaker(25, 5f, 0.9f)
    val output = (u.convolve(dogKernel, BorderClamp) - 0.1f).max(0f).sqrt

    /*
     * Figure 8 shaped filter used for boundary completion.
     */
    def basisFilter(filterSize: Int, sigma: Float, ms: Int): ComplexField = {
      ComplexField(filterSize, filterSize, (row: Int, col: Int) => {
        require(filterSize % 2 == 1)
        val Offset = filterSize / 2
        def gaussian(x: Int, y: Int) =
          math.exp(-(x*x + y*y) / (2*sigma*sigma)).toFloat
        def wms(x: Int, y: Int, ms: Int): Complex = {
          if (x == 0 && y == 0) {
            Complex(1, 0)
          } else {
            val arg = Complex(x, y) * (1.0 / math.sqrt(x*x + y*y)).toFloat
            arg.power(ms) * gaussian(x, y)
          }
        }
        wms(col - Offset, row - Offset, ms)
      })
    }
    val inputView = input.magnitude

    val fieldsToProbe = Array(input,
      bipole0, bipole1, bipole2, bipole3, bipole4, bipole5, bipole6, bipole7,
      output, inputView, u2, u0)
    fieldsToProbe.foreach(_.probe())

    /**
     * Input image of a broken circle crossed by a broken diagonal line. This
     * uses a complex field to represent the field, where the phase of each element
     * represents the direction of the edge at that point in the field.
     */
    object InputImage {
      def apply(imageSize: Int): ComplexMatrix = {
        //require(imageSize % 2 == 1)
        //val image = Array.ofDim[Complex](imageSize, imageSize)
        val image = Array.fill(imageSize, imageSize)(Complex(0, 0))
        val Offset = imageSize / 2
        val DiagonalEdge = Complex.polar(1, math.Pi.toFloat / 4)
//        val HorizontalEdge = Complex.polar(1, 0)
//        val VerticalEdge = Complex.polar(1, math.Pi.toFloat / 2)
        val Radius = imageSize / 4  + 0.5f
        val deltaAngle = 0.01f   // radians
        var angle = 0.0f
        val BadAngle1 = (math.Pi - math.Pi/4).toFloat
        val BadAngle2 = 0f
        while (angle <= 2 * math.Pi) {
          if (angle < BadAngle1 - 0.15f || angle > BadAngle1 + 0.15f) {
            if (angle > BadAngle2 + 0.3f) {
              var x = Radius * math.cos(angle).toFloat
              var y = Radius * math.sin(angle).toFloat
              val tangent = (angle - math.Pi / 2).toFloat
              val edge = Complex.polar(1, tangent)
              image(y.toInt + Offset)(x.toInt + Offset) = edge
              image(y.toInt + Offset)(x.toInt + Offset) = edge
              x = (Radius + 1) * math.cos(angle).toFloat
              y = (Radius + 1) * math.sin(angle).toFloat
              image(y.toInt + Offset)(x.toInt + Offset) = edge
              image(y.toInt + Offset)(x.toInt + Offset) = edge
              //x = (Radius + 2) * Math.cos(angle)
              //y = (Radius + 2) * Math.sin(angle)
              //image(y.toInt, x.toInt) = edge
              //image(y.toInt, x.toInt) = edge
            }
          }
          angle += deltaAngle
        }
        val Inset = 15
        val Middle = imageSize / 2
        for (i <- Inset until Middle - 4) {
          image(i)(i) = DiagonalEdge
          image(i+1)(i) = DiagonalEdge
          //this(i+2, i) = DiagonalEdge
        }
        for (i <- Middle + 4 until imageSize - Inset) {
          image(i)(i) = DiagonalEdge
          image(i+1)(i) = DiagonalEdge
          //this(i+1, i) = DiagonalEdge
        }
        val matrix = new ComplexMatrix(image)
        require(matrix.rows == imageSize && matrix.columns == imageSize)
        matrix
      }
    }

  }
)