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


import cogdebugger.CogDebuggerApp
import libcog._
import toolkit.neuralnetwork.function._
import toolkit.neuralnetwork.policy.StandardLearningRule
import toolkit.neuralnetwork.source.RandomSource
import toolkit.neuralnetwork.util.{CorrectCount, NormalizedLowPass}
import toolkit.neuralnetwork.Implicits._
import toolkit.neuralnetwork.layer.{ConvolutionLayer, FullyConnectedLayer}
import toolkit.neuralnetwork.DifferentiableField

/** A version of AlexNet created from the NeuralNetwork package primitives */


object WorkingAlexNet extends CogDebuggerApp(new ComputeGraph (optimize = false) {

  val LR = 0.01f
  val momentum = 0.9f
  val decay = 0.0005f
  val lr = StandardLearningRule(LR, momentum, decay)

  val batchSize = 1 //128

  val enableNormalization = true
  val (k, alpha, beta, windowSize) = (2.0f, 1e-4f, 0.75f, 5)
  def normalize(in: DifferentiableField): DifferentiableField = {
    if (!enableNormalization) {
      in
    } else {
      in * AplusBXtoN(VectorMeanSquares(in, windowSize, BorderCyclic), a = k, b = 5 * alpha, n = -beta)
    }
  }

  val data = RandomSource(Shape(54, 54), 3, batchSize)
  val label = RandomSource(Shape(), 1000, batchSize)

  val c1 = ConvolutionLayer(data, Shape(11, 11), 96, BorderValid, lr, stride = 4)
  val r1 = ReLU(c1)
  val n1 = normalize(r1)
  val p1 = MaxPooling(n1, poolSize = 3, stride = 2)

  val c2 = ConvolutionLayer(p1, Shape(5, 5), 256, BorderZero, lr)
  val r2 = ReLU(c2)
  val n2 = normalize(r2)
  val p2 = MaxPooling(n2, poolSize = 3, stride = 2)

  val c3 = ConvolutionLayer(p2, Shape(3, 3), 384, BorderZero, lr)
  val r3 = ReLU(c3)

  val c4 = ConvolutionLayer(r3, Shape(3, 3), 384, BorderZero, lr)
  val r4 = ReLU(c4)

  val c5 = ConvolutionLayer(r4, Shape(3, 3), 256, BorderZero, lr)
  val r5 = ReLU(c5)
  val p5 = MaxPooling(r5, poolSize = 3, stride = 2)

  val fc6 = FullyConnectedLayer(p5, 4096, lr)
  val d6 = Dropout(fc6)
  val r6 = ReLU(d6)

  val fc7 = FullyConnectedLayer(r6, 4096, lr)
  val d7 = Dropout(fc7)
  val r7 = ReLU(d7)

  val fc8 = FullyConnectedLayer(r7, 1000, lr)

  val loss = CrossEntropySoftmax(fc8, label) / batchSize

  loss.activateSGD()

  val correct = CorrectCount(fc8.forward, label.forward, batchSize, 0.01f) / batchSize
  val avgCorrect = NormalizedLowPass(correct, 0.001f)
  val avgLoss = NormalizedLowPass(loss.forward, 0.001f)

  var start = java.lang.System.currentTimeMillis()

  step(1)

  var end = java.lang.System.currentTimeMillis()
  var time_taken = end - start
  println("Step-1 time taken is:" + time_taken + "ms")

  var time1 = 0L

  for( i <- 1 to 500) {
    start = java.lang.System.currentTimeMillis()
    step(1)
    end = java.lang.System.currentTimeMillis()
    time_taken = end - start
    time1 = time1 + time_taken
    //println("Step time taken in ms is:" + time_taken)
  }

  val avg_time = time1/500
  println("Avg Time taken in is:"+ avg_time + "ms")
  println("Accuracy  is:", read(correct).asInstanceOf[ScalarFieldReader].read())


  probe(data.forward)
  probe(label.forward)
  probe(loss.forward)
  probe(correct)
  probe(avgCorrect)
  probe(avgLoss)
})


