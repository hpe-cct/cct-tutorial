#Getting Started

The HPE Cognitive Computing Toolkit (CCT) is a GPU-accelerated platform for deep learning and other advanced analytics. 
The CCT-Tutorial contains a number of examples from each of the repositories that comprise the Cognitive Computing Toolkit. 
This guide can be used to get an introduction to the platform.

## Intro

The CCT platform is software platform for developing-massively parallel applications that execute on multi-core processors 
such as GPUs. CCT differs from most other parallel programming paradigms (such as MPI, actors, transactional memory) by 
exposing the parallelism implicitly in the programming model rather than through explicit mechanisms and data structures. 
The model contains no threads, locks, message queues, critical sections, or races. It is a deterministic, massively-parallel 
programming model. It is also a declarative dataflow programming model, meaning that a CCT application describes the structure 
of the computation, not sequential actions.

### Language

CCT is written in the Scala programming language which runs on the Java Virtual Machine (JVM).

### Libraries 

The cct-tutorial currently has the following library dependencies.

**cct-core** - This is the core library and API. It contains the compiler and runtime system necessary to optimize and 
distribute a CCT application. This package is currently imported as `libcog`. The cct-core library also contains the visual 
debugger, *cogdebugger*. 

**cct-io** - This library provides a set of objects which can be used for getting data into and out of a running CCT 
application using the field initialization, *sensor*, and *actuator* primatives that are part of the cct-core API.  
For example, it has APIs to read an image file or movie file. This package is currently imported into some of the 
tutorial examples as `cogio`.

**cct-nn** - This library provides APIs to support deep learning and neural networks. This package is currently imported 
into some of the tutorial examples as `toolkit.neuralnetwork`.

**cct-sandbox** - This library contains unstable CCT libraries for applications including signal processing and computer vision.

**cogdebugger** - This is a UI Wrapper for debugging CCT applications. It allows developers to visualize the compute graph, 
inspect the fields, and step through the compute graph. The visual debugger is part of the cct-core library, but the package 
is imported separately as `cogdebugger`.

## Abstractions

The CCT programming model has three core abstractions: *tensor fields*, *operators*, and *compute graphs*.  

* A *tensor field* is a multidimensional array (field) of multidimensional arrays (tensors) of elements (e.g. 32-bit floating 
point numbers).  Fields are used for inputs, outputs, computations, and persistent state. 
* An *operator* combines one or more tensor fields to create a new tensor field. 
* A *compute graph* combines tensor fields and operators into a single, massively-parallel unit of computation. 
 
The compute graph is a state machine, which evolves in discrete time. A single tick, or "step" of the CCT clock sends the 
input data through the entire compute graph to its outputs. Persistent state, for learning and adaptation, is handled using 
*feedback*. The state of a field can be updated at each step and fed back into the compute graph at the next step, providing 
control loops and learning.

Using these simple abstractions, the CCT compiler optimizes the computation across operators and can distribute the work 
efficiently to scale from one CPU to clusters with millions of GPU cores.

<center>
![CCT Compute Graph](doc/cctComputeGraph.png)
</center>

## Introductory Examples

### Example #1
`Counter` is a very simple example. Here is the code:

    package tutorial.libcog.fields

    import cogdebugger._
    import libcog._
    
    object Counter extends CogDebuggerApp(
      new ComputeGraph {
        val counter = ScalarField(200, 200)
        counter <== counter + 1
      }
    )


It can also be found [here](https://github.com/hpe-cct/cct-tutorial/blob/master/src/main/scala/tutorial/libcog/fields/Counter.scala)
and at this location  in your IDE: `./cct-tutorial/src/scala/tutorial/libcog/fields/Counter.scala`.

This program defines one field, `counter`, which is a 2-dimensional scalar field with 200 rows and 200 columns. It then uses the 
feedback operator `<==` to increment itself by 1 with each clock tick or step.

This next figure shows `Counter` running in the visual debugger. 


<center>
![cogdebugger running Counter](doc/cctVisualDebugger.png)
</center>

The visual debugger is a graphical tool that allows you to step, reset, and "peek inside" a `ComputeGraph` to visualize the 
computation while it executes. Clicking on the blue box labeled "counter" in the left pane, opens the "counter" window in the 
right-pane (as shown here). The "counter" window on the right shows that the "counter" field is a ScalarField with a size of 
200x200. Placing the cursor over a point in the field will momentarily bring up a tooltip displaying the coordinates and value 
at that point. In this case, value is "1.0" at the location (1,2) after stepping through the graph one time. The "Cycle" value 
at the top shows how many steps have been taken, which in this case is "1". All 40,000 points have a value of "1" after 1 cycle. 
The buttons in the top left allow you to control stepping through the graph. Clicking "Step 1" will add 1 to every point, which 
is 40,000 additions. Clicking "Run" with a "0" in the adjacent box, steps through the graph until "Stop" is clicked.  Clicking 
"Reset" resets the ComputeGraph fields back to their initial state.  

Stay tuned - More content coming soon. 
