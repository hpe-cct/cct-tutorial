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
IntelliJ will pull in all necessary dependencies automatically.  For more detailed
installation instructions go to [Download and Setup](https://github.com/hpe-cct/cct-tutorial/blob/master/DownloadAndSetup.md).

The [Getting Started](https://github.com/hpe-cct/cct-tutorial/blob/master/GettingStarted.md) page provides an introduction to the CCT platform using examples from the `cct-tutorial`.

A draft of the CCT programming guide is available
[here](http://hpe-cct.github.io/docs/CogProgrammingTutorial_4_1.pdf). Note that
this is an early document, and still refers to CCT by its original internal
working name (Cog ex Machina or Cog).

## API Documentation

CCT includes four user-visible components. The core provides the compiler, runtime, visual debugger, and standard library. 
The I/O library includes several useful sensors for standard data types. This is a separate module becuase it has significant
dependencies. The NN library includes support for deep learning and similar gradient descent methods. The sandbox includes a 
number of library routines that don't cluster into coarse enough chunks to justify independent libraries. All four modules 
are included as dependencies for the tutorial.

Scaladoc documentation for these modules is available here:

  * [cct-core](https://hpe-cct.github.io/scaladoc/cct-core_2.11-5.0.0-alpha.3/)
  * [cct-io](https://hpe-cct.github.io/scaladoc/cct-io_2.11-0.8.7/)
  * [cct-nn](https://hpe-cct.github.io/scaladoc/cct-nn_2.11-2.0.0-alpha.2/)
  * [cct-sandbox](https://hpe-cct.github.io/scaladoc/cct-sandbox_2.11-1.2.9/)
