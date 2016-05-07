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

import libcog._
import cogdebugger._
import cogio._

object RotateField extends CogDebuggerApp (
  new ComputeGraph {
    val oranges = GrayscaleImage("resources/oranges.jpg")
    val InRows = oranges.fieldType.rows
    val InCols = oranges.fieldType.columns

    val OutRows = InCols
    val OutCols = InRows

    // Create a warp field, given the desired angle
    def warpField(inToOutClockwiseAngle: Double) = {
      val outToInClockwiseAngle = -inToOutClockwiseAngle
      val rotationMatrix = Matrix(
        Array(math.cos(outToInClockwiseAngle).toFloat, -math.sin(outToInClockwiseAngle).toFloat),
        Array(math.sin(outToInClockwiseAngle).toFloat, math.cos(outToInClockwiseAngle).toFloat)
      )
      // The warp field, sized per the globally specified output field shape
      VectorField(OutRows, OutCols, (r,c) => {

        // The rotation math described in http://en.wikipedia.org/wiki/Rotation_matrix involves column vectors:
        //
        //  | x |
        //  | y |
        //
        // Note: our column 'c' is in the x direction (i.e. the first coordinate of the Vector)

        // A column vector for this point in the warped output field.
        val outVector = new Vector(c,r)
        // Shift to a coordinate system with (0,0) at the image center.  We view the image now with the pixels
        // as unit squares and the upper left corner of the image as:.
        //
        //  | x = -OutCols/2 |
        //  | y = -OutRows/2 |
        //
        // The center of what was the (0,0) output pixel is offset by +1/2 in each dimension.
        val centerShift = new Vector((1 - OutCols)/2f, (1 - OutRows)/2f)
        val shiftedVector = outVector + centerShift
        // Perform the rotation via matrix multiplication
        val rotatedVector = rotationMatrix * shiftedVector
        // Undo the previous centering shift, now relative to the input field size
        val centerUnshift = new Vector((InCols - 1)/2f, (InRows - 1)/2f)
        val fromVector = rotatedVector + centerUnshift
        // The delta is now the offset from the input point that is used to get to this output point
        val delta = outVector - fromVector
        // Convert column vector back to Vector(rowOffset, colOffset)
        new Vector(delta(1), delta(0))
      })
    }

    val rotated45 = warp(oranges, warpField(math.Pi/4), BorderZero)
    val rotated90 = warp(oranges, warpField(math.Pi/2), BorderZero)
    val rotated180 = warp(oranges, warpField(math.Pi), BorderZero)
    val rotated270 = warp(oranges, warpField(3*math.Pi/2), BorderZero)

    probeAll   // Makes all fields visible in the debugger
  }
)
