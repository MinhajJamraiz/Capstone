<strong>1- For running the DHL Capstone Application (including JAVAFX SDK for windows x64):<strong>  <br>

we have included a jar application in RunnableJarFile. JavaFX SDK is also provided in the same folder.  <br>
cd into RunnableJarFile folder and open terminal.  <br>

Run the following command in the terminal. <br>

java --module-path "C:\Users\mohsi\RunnableJarFile\openjfx-25_windows-x64_bin-sdk\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml -jar DHL_CapstoneApplication_Group4.jar <br>


Please adjust the module path for the lib folder as it is in your system. <br>

This should open the application. <br>

Refer to corresponding screencast video on how to use it.  <br>






<strong>2- For Building Project in Eclipse: <strong> <br>

  <strong>SETTING UP JAVAFX <strong> <br>
  1: Install Eclipse <br>
  2: Install JavaFX (Remember the path at which you are installing these. Used in step 6) <br>
  3: Create a project on Eclipse (Select File --> New --> Project --> JavaFX --> JavaFX Project) <br>
  4: Remove Main file and add a new class file in the project with name DHLWareHouseMainWithLogs.java <br>
  5: Copy the code from repo file and paste it in this file and save. <br>

  6: Right Click on Project folder in Eclipse and Click on Properties -> Java Build Path <br>
  7: Click on MODULE PATH and Select ADD EXTERNAL JARs <br>
  8: Find your JavaFX Folder -> lib <br>
  9: Select all the files in lib folder and click open <br>
  10: Select Apply and Close. <br>
<br>
<br>
<strong>SETTING UP JAVAFX VM <strong> <br>
1: Right Click the project folder -> Run As -> Run Configuration <br>
2: GoTO Java Application and Select your project. <br>
3: Go to Arguments Section <br>
4: Enter this command in VM Arguments :<br>
<strong>--module-path "path to lib folder of JavaFX" --add-modules javafx.controls,javafx.fxml <strong> <br>
<strong>!!!! NOTE: <strong> Please change (path to lib folder of JavaFX) to actual path of JavaFX lib folder <br>
5: Select Apply and Close. <br>
<br>
<br>
  <strong>SETTING UP JUNIT <strong> <br>
  1: Right Click on Project folder in Eclipse and Click on Properties -> Java Build Path <br>
  2: Click on CLASS PATH and Select ADD LIBRARY <br>
  3: Select JUNIT -> JUNIT5 -> Finish <br>
  4: Select Apply and Close. <br>
<br>
<br>
<strong>SETTING UP JUNIT VM <strong> <br>
1: Right Click the project folder -> Run As -> Run Configuration <br>
2: GoTO JUNIT and Select your project. <br>
3: Go to Arguments Section <br>
4: Enter this command in VM Arguments <br>
<br>
<strong>--module-path "path to lib folder of JavaFX" --add-modules javafx.controls,javafx.fxml <strong> <br>
<strong>!!!! NOTE: <strong> Please change (path to lib folder of JavaFX) to actual path of JavaFX lib folder <br>



5: Apply --> Close <br>
6: Run the Project.
