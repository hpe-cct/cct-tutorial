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


/** A version of OverFeat created from the NeuralNetwork package primitives */

object OverFeat extends CogDebuggerApp(new ComputeGraph (optimize = false) {
//object OverFeat extends App {

    //Cog.printOptimized = true
    //Cog.verboseOptimizer = true
    //Cog.verboseKernelMerging = true

    val LR = 0.01f
    val momentum = 0.9f
    val decay = 0.0005f
    val lr = StandardLearningRule(LR, momentum, decay)


    val batchSize = 128

    val data = RandomSource(Shape(230, 230), 3, batchSize)
    val label = RandomSource(Shape(), 1000, batchSize)


    val c1 = ConvolutionLayer(data, Shape(11, 11), 96, BorderValid, lr, stride = 4)
    val b1 = BiasLayer(c1, lr)
    val r1 = ReLU(b1)
    val m1 = MaxPooling(r1, poolSize = 2, stride = 2)

    val c2 = ConvolutionLayer(m1, Shape(5, 5), 256, BorderValid, lr, stride = 1)
    val b2 = BiasLayer(c2, lr)
    val r2 = ReLU(b2)
    val m2 = MaxPooling(r2, poolSize = 2, stride = 2)

    val c3 = ConvolutionLayer(m2, Shape(3, 3), 512, BorderValid, lr, stride = 1)
    val b3 = BiasLayer(c3, lr)
    val r3 = ReLU(b3)

    val c4 = ConvolutionLayer(r3, Shape(3, 3), 1024, BorderValid, lr, stride = 1)
    val b4 = BiasLayer(c4, lr)
    val r4 = ReLU(b4)

    val c5 = ConvolutionLayer(r4, Shape(3, 3), 1024, BorderValid, lr, stride = 1)
    val b5 = BiasLayer(c5, lr)
    val r5 = ReLU(b5)
    val m5 = MaxPooling(r5, poolSize = 2, stride = 2)


    val fc6 = FullyConnectedLayer(m5, 3072, lr)
    val b6 = BiasLayer(fc6, lr)
    val r6 = ReLU(b6)

    val fc7 = FullyConnectedLayer(r6, 4096, lr)
    val b7 = BiasLayer(fc7, lr)
    val r7 = ReLU(b7)

    val fc8 = FullyConnectedLayer(r7, 1000, lr)
    val b8 = BiasLayer(fc8, lr)

    val loss = CrossEntropySoftmax(b8, label)


    val correct = CorrectCount(b8.forward, label.forward, batchSize, 0.01f) / batchSize
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


