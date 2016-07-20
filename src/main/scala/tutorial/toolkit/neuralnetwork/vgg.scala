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
import toolkit.neuralnetwork.layer.{BiasLayer, ConvolutionLayer, FullyConnectedLayer}


/** A version of Vgg_A created from the NeuralNetwork package primitives */


object Vgg_a extends CogDebuggerApp(new ComputeGraph (optimize = false) {

//object Vgg_a extends App {

  //Cog.printOptimized = true
  //Cog.verboseOptimizer = true
  //Cog.verboseKernelMerging = true

  val LR = 0.01f
  val momentum = 0.9f
  val decay = 0.0005f
  val lr = StandardLearningRule(LR, momentum, decay)


  // Batch size can be at most 48 (on 12GB TitanX) because the FFT convolution of the 1st layer is large.
  // A space-domain convolution will be smaller because th406e downsampling will be integrated with the convolve.
  val batchSize = 64

  val data = RandomSource(Shape(224, 224), 3, batchSize)
  val label = RandomSource(Shape(), 1000, batchSize)

  //Receiptive Field Size=3, Number of Channels=64
  val c1 = ConvolutionLayer(data, Shape(3, 3), 64, BorderValid, lr)
  val b1 = BiasLayer(c1, lr)
  val r1 = ReLU(b1)
  val m1 = MaxPooling(r1, poolSize = 2, stride = 2)

  val c2 = ConvolutionLayer(m1, Shape(3, 3), 128, BorderValid, lr)
  val b2 = BiasLayer(c2, lr)
  val r2 = ReLU(b2)
  val m2 = MaxPooling(r2, poolSize = 2, stride = 2)

  val c3 = ConvolutionLayer(m2, Shape(3, 3), 256, BorderValid, lr)
  val b3 = BiasLayer(c3, lr)
  val r3 = ReLU(b3)

  val c4 = ConvolutionLayer(r3, Shape(3, 3), 256, BorderValid, lr)
  val b4 = BiasLayer(c4, lr)
  val r4 = ReLU(b4)
  val m4 = MaxPooling(r4, poolSize = 2, stride = 2)

  val c5 = ConvolutionLayer(m4, Shape(3, 3), 512, BorderValid, lr)
  val b5 = BiasLayer(c5, lr)
  val r5 = ReLU(b5)

  val c6 = ConvolutionLayer(r5, Shape(3, 3), 512, BorderValid, lr)
  val b6 = BiasLayer(c6, lr)
  val r6 = ReLU(b6)
  val m6 = MaxPooling(r6, poolSize = 2, stride = 2)

  val c7 = ConvolutionLayer(m6, Shape(3, 3), 512, BorderValid, lr)
  val b7 = BiasLayer(c6, lr)
  val r7 = ReLU(b7)

  val c8 = ConvolutionLayer(r7, Shape(3, 3), 512, BorderValid, lr)
  val b8 = BiasLayer(c8, lr)
  val r8 = ReLU(b8)
  val m8 = MaxPooling(r8, poolSize = 3, stride = 2)

  val fc9 = FullyConnectedLayer(m8, 4096, lr)
  val b9 = BiasLayer(fc9, lr)
  val r9 = ReLU(b9)

  val fc10 = FullyConnectedLayer(r9, 4096, lr)
  val b10 = BiasLayer(fc10, lr)
  val r10 = ReLU(b10)

  val fc11 = FullyConnectedLayer(r10, 1000, lr)
  val b11 = BiasLayer(fc11, lr)
  val loss = CrossEntropySoftmax(b11, label)


  val correct = CorrectCount(b11.forward, label.forward, batchSize, 0.01f) / batchSize
  val avgCorrect = NormalizedLowPass(correct, 0.001f)
  val avgLoss = NormalizedLowPass(loss.forward, 0.001f)

  loss.activateSGD()

  var start = java.lang.System.currentTimeMillis()

  step(1)

  var end = java.lang.System.currentTimeMillis()
  var time_taken = end - start
  println("Step-1 time taken in ms is:", time_taken)

  var time1 = 0L

  for( i <- 1 to 500) {
    start = java.lang.System.currentTimeMillis()
    step(1)
    end = java.lang.System.currentTimeMillis()
    time_taken = end - start
    time1 = time1 + time_taken
    println("Step time taken is:" + time_taken + "ms")
  }

  val avg_time = time1/500
  println("Avg Time taken is:" + avg_time + "ms")
  println("Accuracy is:" + read(correct).asInstanceOf[ScalarFieldReader].read())


  probe(data.forward)
  probe(label.forward)
  probe(loss.forward)
  probe(avgCorrect)
  probe(avgLoss)
})

//}



