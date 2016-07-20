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

import scala.collection.mutable.ListBuffer

import libcog.{ScalarFieldReader}


object CIFARBenchmark extends App  {

  val (net, batchSize) = args.length match {
    case 0 => ("cifar10_quick", 200)
    case 1 => (args(0), 256)
    case 2 => (args(0), args(1).toInt)
    case _ => throw new RuntimeException(s"illegal arguments (${args.toList})")
  }

  require(net == "cifar10_quick", s"network $net isn't supported")

  println("net:" + net)
  println("batch size: " + batchSize)

  val cg1 = new CIFAR(learningEnabled = false, batchSize = batchSize)

  val forward = new ListBuffer[Double]()
  val backward = new ListBuffer[Double]()


  cg1 withRelease {

    println("starting compilation (Testing)")
    cg1.step
    println("compilation finished (Testing)")

    val correct = cg1.read(cg1.correct).asInstanceOf[ScalarFieldReader].read()
    println("initial accuracy: " + correct)

    for (i <- 1 to 50000) {
      val start = System.nanoTime()
      cg1.step
      val stop = System.nanoTime()
      val elapsed = (stop - start).toDouble / 1e6
      forward += elapsed

      if (i % 5000 == 0) {
        val correct = cg1.read(cg1.correct).asInstanceOf[ScalarFieldReader].read()
        println("Iteration: " + i + " Sample: " + (i * batchSize) + " Accuracy: " + correct)
      }
    }

  }

  val cg2 = new CIFAR(learningEnabled = true, batchSize = batchSize)

  cg2 withRelease {
    println("starting compilation (Training)")
    cg2.step
    println("compilation finished (Training)")

    val loss = cg2.read(cg2.loss.forward).asInstanceOf[ScalarFieldReader].read()
    val correct = cg2.read(cg2.correct).asInstanceOf[ScalarFieldReader].read()

    println("initial loss: " + loss)
    println("initial accuracy: " + correct)

    for (i <- 1 to 50000) {
      val start = System.nanoTime()
      cg2.step
      val stop = System.nanoTime()
      val elapsed = (stop - start).toDouble / 1e6

      backward += elapsed

      if (i % 5000 == 0) {
        val loss = cg2.read(cg2.loss.forward).asInstanceOf[ScalarFieldReader].read()
        val correct = cg2.read(cg2.correct).asInstanceOf[ScalarFieldReader].read()
        println("Iteration: " + i + " Sample: " + (i * batchSize) + " Loss: " + loss + " Accuracy: " + correct)
      }
    }
  }


  val forwardtime = forward.sum / forward.length
  val total_time = backward.sum / backward.length
  println("Average Forward pass: " + forwardtime + " ms.")
  println("Average Forward-Backward: " + total_time + " ms.")
}
