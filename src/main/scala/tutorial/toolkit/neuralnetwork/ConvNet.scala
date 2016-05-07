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


object ConvNet extends CogDebuggerApp(new ComputeGraph {
  val dir = MNISTdata.dir
  val batchSize = 300
  val intermediateFeatures = 256
  val outputClasses = 10
  val LR = 0.01f
  val momentum = 0.9f
  val decay = 0.0005f

  val lr = StandardLearningRule(LR, momentum, decay)

  val data = ByteDataSource(new File(dir, "train-images.idx3-ubyte").toString,
    Shape(28, 28), 1, batchSize, headerLen = 16)

  val label = ByteLabelSource(new File(dir, "train-labels.idx1-ubyte").toString,
    10, batchSize, headerLen = 8)

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
  val r3 = Tanh(b3)

  val fc4 = FullyConnectedLayer(r3, outputClasses, lr)
  val b4 = BiasLayer(fc4, lr)
  val loss = CrossEntropySoftmax(b4, label) / batchSize

  val correct = CorrectCount(b4.forward, label.forward, batchSize, 0.01f) / batchSize
  val avgCorrect = NormalizedLowPass(correct, 0.001f)
  val avgLoss = NormalizedLowPass(loss.forward, 0.001f)

  loss.activateSGD()

  probe(data.forward)
  probe(label.forward)
  probe(loss.forward)
  probe(avgCorrect)
  probe(avgLoss)
})
