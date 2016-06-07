# CCT Tutorial

The HPE Cognitive Computing Toolkit (CCT) is a GPU-accelerated platform for deep
learning and other advanced analytics. It provides an embedded domain-specific
language (DSL) designed to maximize ease of programmability, while preserving
the semantics necessary to generate efficient GPU code. CCT is particularly
powerful for applications that require combining deep learning techniques with
more conventional signal processing or computer vision algorithms. The CCT DSL
lives inside the Scala language, on top of the Java Virtual Machine (JVM).

To get started, you’ll need a machine with a relatively current NVIDIA GPU and a
1.8 JDK installed. While CCT emits OpenCL GPU kernels and thus may run on AMD or
Intel GPUs as well, these are not regularly tested hardware configurations.
[IntelliJ IDEA](https://www.jetbrains.com/idea/) is the recommended option for a
development environment.

The free Community Edition of IntelliJ is sufficient. Once you have IntelliJ
installed, you just need to install the Scala plugin from the IntelliJ plugin
manager. That will give you a full development environment for CCT applications.
IntelliJ will pull in all necessary dependencies automatically.

A good first example to try is the `BackgroundSubtraction` application,
available
[here](https://github.com/hpe-cct/cct-tutorial/blob/master/src/main/scala/tutorial/cogio/BackgroundSubtraction.scala).

A draft of the CCT programming guide is available
[here](http://hpe-cct.github.io/docs/CogProgrammingTutorial_4_1.pdf). Note that
this is an early document, and still refers to CCT by its original internal
working name (Cog ex Machina or Cog).
