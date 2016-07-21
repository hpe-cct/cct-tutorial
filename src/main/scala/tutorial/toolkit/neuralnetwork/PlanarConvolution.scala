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
import toolkit.neuralnetwork.DifferentiableField
import toolkit.neuralnetwork.DifferentiableField.GradientPort


case class PlanarConvolution(input: DifferentiableField,
                             kernel: Field,
                             borderPolicy: BorderPolicy,
                             samplingPolicy: ConvolutionSamplingPolicy = NoSamplingConvolution
                            ) extends DifferentiableField {
  require(kernel.fieldShape.dimensions == 2, "kernel must be 2D")
  require(kernel.tensorShape.points == 1, "kernel must contain scalar elements")
  require(borderPolicy == BorderZero || borderPolicy == BorderValid,
    "only BorderZero and BorderValid policies is supported")
  require(samplingPolicy == NoSamplingConvolution || samplingPolicy.isInstanceOf[DownsampleOutputConvolution],
    "only NoSampling and DownsampleOutput sampling policies are supported")

  override val batchSize: Int = input.batchSize

  override val forward: Field = _forward(input.forward)

  override val inputs: Map[Symbol, GradientPort] =
    Map('input -> GradientPort(input, dx => _forward(dx), dx => adjoint(dx)))

  private def _forward(in: Field): Field = {
    convolve(in, kernel, borderPolicy, samplingPolicy)
  }

  def adjoint(in: Field): Field = {
    val upPolicy = samplingPolicy match {
      case NoSamplingConvolution => NoSamplingConvolution
      case DownsampleOutputConvolution(step) => UpsampleInputConvolution(step)
      case p => throw new RuntimeException(s"unsupported sampling policy $p")
    }

    borderPolicy match {
      case BorderZero => crossCorrelate(in, kernel, BorderZero, upPolicy)
      case BorderValid => crossCorrelate(in, kernel, BorderValid, upPolicy)
      case p => throw new RuntimeException(s"unsupported border policy $p")
    }
  }
}
