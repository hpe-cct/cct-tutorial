#Download and Set up Instructions

*   [System Requirements](#sysreq)
*   [Programming Environment](#lang)
*   [Download, Install, and Configure Programming Environment](#steps)
*   [Download CCT-Tutorial and Verify](#clone)
*   [Common Problems](#gotchas)

<h2 id="sysreq">System Requirements</h2>
* 64-bit architecture (Linux, Windows, OS X)
* GPU Hardware (NVIDIA, AMD, Intel)
* OpenCL (already included by default with most GPU drivers)

<h2 id="lang">Programming Environment</h2>
* Scala 
* SBT (Scala build tool)
* IntelliJ IDEA (preferred IDE) 

<h2 id="steps">Download, Install, and Configure Programming Environment</h2>

Note: These instructions are based on the preferred IDE, IntelliJ IDEA. 

1.  Download and install the lastest 64-bit JDK (JDK8).
2.  Download and install the latest [IntelliJ IDEA IDE](https://www.jetbrains.com/idea/download). (The Community Edition works fine.) 
3.  Download and install the Scala Plugin in IntelliJ. This plugin includes SBT. 

     From the *Welcome to IntelliJ IDEA* window, select *Configure*, select *Install JetBrains Plugins* (or *Browse Repositories*), select *Scala*, click *Install*

4.  Configure the project SDK in IntelliJ 

     From the *Welcome to IntelliJ IDEA* window, select *Configure*, select *Project Defaults*, select *Project Structure*. If there is *\<No SDK>*, click *New* to add the JDK that was installed in Step 1.

Now you are ready to clone a CCT repository. 

<h2 id="clone">Download CCT-Tutorial and Verify</h2>

1. Check out from Version Control.

    *  From the *Welcome to IntelliJ IDEA* window, select *Check out from Version Control*, select *GitHub*, enter `https://github.com/hpe-cct/cct-tutorial.git"` for the *Git Repository URL*. Enter the *Parent Directory*. Then click *Clone*.
    *  Click "Yes" to open the `build.sbt` project file.
    *  Check "Use auto-import" and "Create directories..", make sure the proper "Project "SDK" is selected, then click "OK".
    *  Select both the root and build modules to include. Click "OK".

2. Verify. A good first example to try is the `BackgroundSubtraction` application,
available
[here](https://github.com/hpe-cct/cct-tutorial/blob/master/src/main/scala/tutorial/cogio/BackgroundSubtraction.scala). 

    *  In your IDE, navigate to the directory `cct-tutorial/src/main/scala/tutorial/cogio/`. 
    *  Open `BackgroundSubtraction.scala`. 
    *  Right click in the source window and select *Run 'BackgroundSubtraction'*. 
    *  Verify that the application compiles and launches in a debugger window. 
    *  You can then double click on any field in the left panel, such as "suspicious". 
    *  And then click *Run*.

Now you are ready for the next step, [Getting Started](https://github.com/hpe-cct/cct-tutorial/blob/master/README.md). (Note: this is just a place holder link for now)

<h2 id="gotchas">Common Problems</h2>
1.  **Problem:**  The BackgroundSubtraction application has an error loading the courtyard.mp4 file.

    **Solution:** The courtyard.mp4 file in the CCT-Tutorial repository is stored with Git LFS. You will need to download and install this program.  More information can be found here: http://git-lfs.github.com.

2.  **Problem:** IntelliJ IDEA does not show any *Featured Plugins*, *Browse JetBrains Plugins*, or in *Browse Repository*.
  
    **Solution:** If you are behind a firewall, you will need to configure an HTTP proxy setting in IntelliJ. To find the proxy settings dialog box from the *Welcome to IntelliJ IDEA* window, select *Configure*, select *Plugins*, click *Browse repositories...*, click *HTTP Proxy Settings...*. Enter your proxy settings here.

3.  **Problem:** Intellij IDEA doesn't work with 64-bit JDK in older versions of IntelliJ IDEA. 
 
    **Solution:** Make sure you launch the 64-bit version of IntelliJ IDEA.

4.  **Problem:** IntelliJ IDEA cannot find the 64-bit JDK VM in older versions of IntelliJ IDEA.

    **Solution:** Make sure you set the JAVA_HOME and IDEA_JDK_64 environment variables to the 64-bit JDK you have installed.
