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

package tutorial.toolkit.neuralnetwork

import java.io.File

import cogdebugger.CogDebuggerApp
import libcog._
import toolkit.neuralnetwork.function._
import toolkit.neuralnetwork.policy.StandardLearningRule
import toolkit.neuralnetwork.source.{ByteDataSource, ByteLabelSource}
import toolkit.neuralnetwork.util.{CorrectCount, NormalizedLowPass}
import toolkit.neuralnetwork.Implicits._
import toolkit.neuralnetwork.layer.{BiasLayer, ConvolutionLayer, FullyConnectedLayer}


object CIFARTrain extends CogDebuggerApp(new ComputeGraph {

  def batchSize = 200
  def intermediateFeatures = 256
  def LR = 1e-3f
  def decay = 1e-4f
  val offset = 0
  val momentum = 0.9f
  val lr = StandardLearningRule(LR, momentum, decay)

  val (dataFile, dataCount) = ("/home/shruti/cog/data/cifar10/train_data.bin", 50000)
  val (labelFile, labelCount) = ("/home/shruti/cog/data/cifar10/train_labels.bin", 50000)

  val data = ByteDataSource(dataFile, Shape(32, 32), 3, batchSize, fieldCount = Some(dataCount), resetState = offset)
  val label = ByteLabelSource(labelFile, 10, batchSize, fieldCount = Some(labelCount), resetState = offset)

  val c1 = ConvolutionLayer(data, Shape(5, 5), 32, BorderValid, lr)
  val b1 = BiasLayer(c1, lr)
  val m1 = MaxPooling(b1)
  val r1 = ReLU(m1)

  val c2 = ConvolutionLayer(r1, Shape(5, 5), 32, BorderValid, lr)
  val b2 = BiasLayer(c2, lr)
  val m2 = MaxPooling(b2)
  val r2 = ReLU(m2)

  val fc3 = FullyConnectedLayer(r2, intermediateFeatures, lr)
  val b3 = BiasLayer(fc3, lr)
  val d3 = Dropout(b3)
  val r3 = Tanh(d3)

  val fc4 = FullyConnectedLayer(r3, 10, lr)
  val b4 = BiasLayer(fc4, lr)
  val d4 = Dropout(b4)
  val loss = CrossEntropySoftmax(d4, label)

  loss.activateSGD()

  probe(data.forward)
  probe(label.forward)
  probe(loss.forward)
})

