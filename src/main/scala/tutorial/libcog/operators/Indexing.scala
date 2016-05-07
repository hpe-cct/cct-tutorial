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
  * A simple example demonstrating dimensionality reduction by indexing
  * @author Matthew Pickett
  */
object Indexing extends CogDebuggerApp(
  new ComputeGraph{
    val img = GrayscaleImage("resources/oranges.jpg")

    //FIELD INDEXING:
    //extract row 52
    val row52 = img(52)
    //extract row 52, column 52
    val point_r52_c52 = img(52)(52)

    //extract a block from the img
    val block = img(40 until 140, 80 until 180)

    //extract a random point indexed by two fields
    val randomRow = ScalarField.random()*img.rows - 1f
    val randomColumn = ScalarField.random()*img.columns - 1f
    val randomPoint = img(randomRow)(randomColumn)

    //TENSOR INDEXING:
    val vec = VectorField.random(Shape(10,10),Shape(10))
    //extract the fifth element at each field point returning a scalar field
    val element5 = vectorElement(vec, 5)

    val mat = MatrixField.random(Shape(10,10),Shape(5,5))
    //extract the second row of each matrix
    val row2 = matrixRow(mat, 2)
    //extract elment 2,3 of each matrix
    val element2_3 = vectorElement(matrixRow(mat, 2), 3)

    probeAll
  }
)