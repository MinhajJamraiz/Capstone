Capstone Project: DHL smart automated warehouse system 

GROUP 4

1: Muhammad Mohsin Abbasi ESE 7221880
2: Minhaj Jamraiz Abbasi MDT 7223983
3: Asaad Saeed Hamid Alawy MDT 7219758
4: Wajihah Kainat MDT 7223295


SCREENCAST: 
 	https://drive.google.com/file/d/1ABeULHBNc-juCYCASDQQOP-kEQH9wCTt/view?usp=sharing



SETUP AND CONFIGURATION METHODS:

1- For running the DHL Capstone Application (including JAVAFX SDK for windows x64):

      we have included a jar application in RunnableJarFile. JavaFX SDK is also provided in the same folder.  
      cd into RunnableJarFile folder and open terminal.  

      Run the following command in the terminal. 

      java --module-path "C:\Users\mohsi\RunnableJarFile\openjfx-25_windows-x64_bin-sdk\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml -jar DHL_CapstoneApplication_Group4.jar


      Please adjust the module path for the lib folder as it is in your system.

      This should open the application.

      Refer to corresponding screencast video on how to use it.






2- For Building Project in Eclipse:

      SETTING UP JAVAFX 
        1: Install Eclipse 
        2: Install JavaFX (Remember the path at which you are installing these. Used in step 6)
        3: Create a project on Eclipse (Select File --> New --> Project --> JavaFX --> JavaFX Project)
        4: Remove Main file and add a new class file in the project with name DHLWareHouseMainWithLogs.java 
        5: Copy the code from repo file and paste it in this file and save. 

        6: Right Click on Project folder in Eclipse and Click on Properties -> Java Build Path 
        7: Click on MODULE PATH and Select ADD EXTERNAL JARs 
        8: Find your JavaFX Folder -> lib 
        9: Select all the files in lib folder and click open 
        10: Select Apply and Close. 


      SETTING UP JAVAFX VM 
        1: Right Click the project folder -> Run As -> Run Configuration 
        2: GoTO Java Application and Select your project.
        3: Go to Arguments Section 
        4: Enter this command in VM Arguments :
            --module-path "path to lib folder of JavaFX" --add-modules javafx.controls,javafx.fxml
            !!!! NOTE: Please change (path to lib folder of JavaFX) to actual path of JavaFX lib folder 
        5: Select Apply and Close. 


      SETTING UP JUNIT 
         1: Right Click on Project folder in Eclipse and Click on Properties -> Java Build Path
         2: Click on CLASS PATH and Select ADD LIBRARY 
         3: Select JUNIT -> JUNIT5 -> Finish 
         4: Select Apply and Close.



      SETTING UP JUNIT VM 
        1: Right Click the project folder -> Run As -> Run Configuration 
        2: GoTO JUNIT and Select your project.
        3: Go to Arguments Section 
        4: Enter this command in VM Arguments 
                --module-path "path to lib folder of JavaFX" --add-modules javafx.controls,javafx.fxml
                !!!! NOTE: Please change (path to lib folder of JavaFX) to actual path of JavaFX lib folder 
        5: Apply --> Close 

