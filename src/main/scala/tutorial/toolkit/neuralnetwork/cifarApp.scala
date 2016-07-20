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


import libcog._
import toolkit.neuralnetwork.function._
import toolkit.neuralnetwork.policy.StandardLearningRule
import toolkit.neuralnetwork.source.{ByteDataSource, ByteLabelSource}
import toolkit.neuralnetwork.util.{Classify, CorrectCount, NormalizedLowPass}
import toolkit.neuralnetwork.Implicits._
import toolkit.neuralnetwork.layer.{BiasLayer, ConvolutionLayer, FullyConnectedLayer}

import libcog.{ScalarFieldReader, Actuator}


object CIFARApp extends App {

  val cg = new ComputeGraph(optimize=false){

  def batchSize = 100
    def testbatchSize = 50
  def intermediateFeatures = 256
  def LR = 1e-3f
  def decay = 1e-4f
  val offset = 0
  val momentum = 0.9f
  val lr = StandardLearningRule(LR, momentum, decay)

    //Training

  val (dataFile, dataCount) = ("/home/shruti/cog/data/cifar10/train_data.bin", 50000)
  val (labelFile, labelCount) = ("/home/shruti/cog/data/cifar10/train_labels.bin", 50000)

  var data = ByteDataSource(dataFile, Shape(32, 32), 3, batchSize, fieldCount = Some(dataCount), resetState = offset)
  var label = ByteLabelSource(labelFile, 10, batchSize, fieldCount = Some(labelCount), resetState = offset)


    val c1 = ConvolutionLayer(data, Shape(5, 5), 32, BorderZero, lr)
    val b1 = BiasLayer(c1, lr)
    val m1 = MaxPooling(b1, poolSize = 3, stride = 2)
    val r1 = ReLU(m1)

    val c2 = ConvolutionLayer(r1, Shape(5, 5), 32, BorderZero, lr)
    val b2 = BiasLayer(c2, lr)
    val r2 = ReLU(b2)
    val p2 = AveragePooling(r2, poolSize = 3, stride = 2)

    val c3 = ConvolutionLayer(p2, Shape(5, 5), 64, BorderZero, lr)
    val b3 = BiasLayer(c3, lr)
    val r3 = ReLU(b3)
    val p3 = AveragePooling(r3, poolSize = 3, stride = 2)

    val fc64 = FullyConnectedLayer(p3, 64, lr)
    val r64 = ReLU(fc64)

    val fc10 = FullyConnectedLayer(r64, 10, lr)
    val loss = CrossEntropySoftmax(fc10, label) / batchSize

    val correct = CorrectCount(fc10.forward, label.forward, batchSize, 0.01f) / batchSize
    val avgCorrect = NormalizedLowPass(correct, 0.01f)
    val avgLoss = NormalizedLowPass(loss.forward, 0.01f)

    //Testing

    val (testdataFile, testdataCount) = ("/home/shruti/cog/data/cifar10/test_data.bin", 10000)
    val (testlabelFile, testlabelCount) = ("/home/shruti/cog/data/cifar10/test_labels.bin", 10000)

    val testdata = ByteDataSource(testdataFile, Shape(32, 32), 3, testbatchSize, fieldCount = Some(testdataCount), resetState = offset)
    val testlabel = ByteLabelSource(testlabelFile, 10, testbatchSize, fieldCount = Some(testlabelCount), resetState = offset)

    var testc1 = Convolution(testdata, c1.weights, BorderZero)
    var testb1 = BiasLayer(testc1, lr)
    var testm1 = MaxPooling(testb1, poolSize = 3, stride = 2)
    var testr1 = ReLU(testm1)

    var testc2 = Convolution(testr1, c2.weights, BorderZero)
    var testb2 = BiasLayer(testc2, lr)
    val testr2 = ReLU(testb2)
    val testp2 = AveragePooling(testr2, poolSize = 3, stride = 2)

    var testc3 = Convolution(testp2, c3.weights, BorderZero)
    var testb3 = BiasLayer(testc3, lr)
    val testr3 = ReLU(testb3)
    val testp3 = AveragePooling(testr3, poolSize = 3, stride = 2)

    var testfc64 = FullyConnected(testp3, fc64.weights)
    var testr64 = ReLU(testfc64)

    var testfc10 = FullyConnected(testr64, fc10.weights)

    //var testloss = CrossEntropySoftmax(testd4, testlabel)

    val testCorrect = CorrectCount(testfc10.forward, testlabel.forward, testbatchSize, 0.01f) / testbatchSize
    val testAvgCorrect = NormalizedLowPass(testCorrect, 0.001f)

    loss.activateSGD()

    /*def printToConsole(prefix: String)(data: ScalarFieldReader): Unit = {
      // assumes there is exactly one scalar value in 'data'
      println(prefix+data.read())
    }

    val correctPrinter = Actuator(correct, printToConsole("correct: ")(_))
    val avgCorrectPrinter = Actuator(avgCorrect, printToConsole("avgCorrect: ")(_))
    val testcorrectPrinter = Actuator(testCorrect, printToConsole("testcorrect: ")(_))
    val testavgCorrectPrinter = Actuator(testAvgCorrect, printToConsole("testavgCorrect: ")(_)) */


    //Timing

    var start = java.lang.System.currentTimeMillis()
    step(1)
    var end = java.lang.System.currentTimeMillis()
    var time_taken = end - start

    //println("Train Step-1 time taken in ms is:" + time_taken)

    var time1 = 0L
      //Train for 10 Epochs, with 500 steps per epoch
    for( i <- 1 to 10000) {
      start = java.lang.System.currentTimeMillis()
      step(1)
      end = java.lang.System.currentTimeMillis()
      time_taken = end - start
      time1 = time1 + time_taken
    }

      var avg_time = time1 / 10000
      println("Total Time taken for training(for 10000 steps): " + time1 + "ms")
      println("Average Time taken for training is: " + time1 + "ms")

  }

  cg.withRelease {

    println("Accuracy results....")
    println("Training")
    println("Correct = " + cg.read(cg.correct).asInstanceOf[ScalarFieldReader].read())
    println("AvgCorrect = " + cg.read(cg.avgCorrect).asInstanceOf[ScalarFieldReader].read())
    println("Testing")
    println("TestCorrect = " + cg.read(cg.testCorrect).asInstanceOf[ScalarFieldReader].read())
    println("AvgTestCorrect = " + cg.read(cg.testAvgCorrect).asInstanceOf[ScalarFieldReader].read())

    cg.reset
  }
}

