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
  * A simple example demonstrating how to use
  * @author Matthew Pickett
  */
object Reductions extends CogDebuggerApp(
  new ComputeGraph{
    val img = GrayscaleImage("resources/oranges.jpg")
    val noise = ScalarField.random(img.rows, img.columns)

    //Field reductions: find the max, min, median or sum over each tensor point
    //  in a field. This reduces to a 0D field of the same type as the input.
    val imgMax = fieldReduceMax(img)
    val imgMin = fieldReduceMin(img)
    val imgMedian = fieldReduceMedian(img)
    val imgSum = fieldReduceSum(img)

    //Tensor reductions: find the max, min, or sum over each field point
    //  in a field. This reduces to a scalar field of the same shape as the input.
    val vec = vectorField(img, noise)
    val vecMax = reduceMax(vec)
    val vecMin = reduceMin(vec)
    val vecSum = reduceSum(vec)

    probeAll
  }
)