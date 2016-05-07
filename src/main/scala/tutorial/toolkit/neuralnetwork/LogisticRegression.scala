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
import toolkit.neuralnetwork.layer.{BiasLayer, FullyConnectedLayer}


object LogisticRegression extends CogDebuggerApp(new ComputeGraph {
  val dir = MNISTdata.dir
  val batchSize = 120
  val lr = StandardLearningRule(0.01f, 0.9f, 0.0005f)

  val data = ByteDataSource(new File(dir, "train-images.idx3-ubyte").toString,
    Shape(28, 28), 1, batchSize, headerLen = 16)

  val label = ByteLabelSource(new File(dir, "train-labels.idx1-ubyte").toString,
    10, batchSize, headerLen = 8)

  val b = BiasLayer(data, lr)

  val fc = FullyConnectedLayer(b, 10, lr)
  val loss = CrossEntropySoftmax(fc, label)
  val normLoss = loss / batchSize
  val correct = CorrectCount(fc.forward, label.forward, batchSize, 0.01f) / batchSize
  val avgCorrect = NormalizedLowPass(correct, 0.001f)
  val avgLoss = NormalizedLowPass(normLoss.forward, 0.001f)

  normLoss.activateSGD()

  probe(data.forward)
  probe(label.forward)
  probe(b.forward)
  probe(fc.forward)
  probe(loss.forward)
  probe(normLoss.forward)
  probe(correct)
  probe(avgCorrect)
  probe(avgLoss)
})
