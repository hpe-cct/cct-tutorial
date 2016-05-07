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
  * A simple example demonstrating how to use the operators related to
  * taking dot products of fields with different combinations of shapes
  * @author Matthew Pickett
  */
object DotProducts extends CogDebuggerApp(
  new ComputeGraph{
    val img = GrayscaleImage("resources/oranges.jpg").downsample(factor = 8)
    val noise = ScalarField.random(img.rows, img.columns)
    val vec = vectorField(img,noise)

    //a simple dot product with multiplies each tensor element of two fields
    //  and sums the result, reducing to a scalar field

    val simpleDot = dot(vec, vec)
    val simpleDotTest = simpleDot === reduceSum(vec*vec)

    //crossDot takes the dot product of each element of a matrix field with
    // a 2D scalar field as if that scalar field were a 0D matrix field
    val randomMat = MatrixField.random(Shape(3,3),Shape(img.rows,img.columns))
    val crossDotOp = crossDot(randomMat, img)

    //copy img into a matrix field the same shape as randomMat
    val replicateOp = replicate(img, randomMat.fieldShape)

    //reverseCrossDot, same as the result of (randomMat*randomField).fieldReduceSum
    // put into a 2D scalar field
    val randomField = ScalarField.random(3,3)
    val reverseCrossDotOp = reverseCrossDot(randomMat, randomField)

    probeAll
  }
)

