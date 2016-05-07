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
import toolkit.neuralnetwork.util.{CorrectCount, LowPass}
import toolkit.neuralnetwork.Implicits._
import toolkit.neuralnetwork.layer.FullyConnectedLayer


object DualPortRegression extends CogDebuggerApp(new ComputeGraph {
  val dir = MNISTdata.dir
  val batchSize = 120
  val valBatchSize = 40
  val lr = StandardLearningRule(0.01f, 0.9f, 0.0005f)

  val data = ByteDataSource(new File(dir, "train-images.idx3-ubyte").toString,
    Shape(28, 28), 1, batchSize, headerLen = 16)

  val label = ByteLabelSource(new File(dir, "train-labels.idx1-ubyte").toString,
    10, batchSize, headerLen = 8)

  val fc = FullyConnectedLayer(data, 10, lr)
  val loss = CrossEntropySoftmax(fc, label)
  val normLoss = loss / batchSize
  val correct = CorrectCount(fc.forward, label.forward, batchSize, 0.01f) / batchSize
  val avgCorrect = LowPass(correct, 0.001f)

  val valData = ByteDataSource(new File(dir, "t10k-images.idx3-ubyte").toString,
    Shape(28, 28), 1, valBatchSize, headerLen = 16)

  val valLabel = ByteLabelSource(new File(dir, "t10k-labels.idx1-ubyte").toString,
    10, valBatchSize, headerLen = 8)

  val valFc = FullyConnected(valData, fc.weights)
  val valCorrect = CorrectCount(valFc.forward, valLabel.forward, valBatchSize, 0.01f) / valBatchSize.toFloat
  val valAvgCorrect = LowPass(valCorrect, 0.001f)

  normLoss.activateSGD()

  probe(data.forward)
  probe(label.forward)
  probe(fc.forward)
  probe(fc.weights.forward)
  probe(loss.forward)
  probe(normLoss.forward)
  probe(correct)
  probe(avgCorrect)
  probe(valCorrect)
  probe(valAvgCorrect)
})
