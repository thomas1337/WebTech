The servlet was created with eclipse J2EE. You have to download it and open this project.
Furthermore, Tomcat must be installed at your computer.

Rightclick at you project, select properties, select Targeted Runtimes and add 
Tomcat to it (select your Tomcat version and set the path to Tomcat directory).

Then, you need to open your Tomcat folder (installation path), go to libs and add
both jars of the DBdriver and the JSONlib to it (is nessesary to add it to the runtime
environment, otherwise the class is missing!)

Like all the other webtech projects we worked on, the url must be set at the main.js
file. If the servlet is hosted and accessed from localhost then nothing has to be
changed. If so, then ignore this part.

The project is a dynamic web project. Simply run it as a Tomcat server.



Update: The res_id issue has been fixed. It should be displayed after reservation at 
the message field below the input fields.