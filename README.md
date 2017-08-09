# README #

This is web application "In Memory File Manager".
Until application is runnig, you can create, update, delete, copy and move file and directories
across web-ui interface

### How run application? ###

This application is spring boot web application with embaded tomcat server.
For start up application run next commands:

* ```git clone git@bitbucket.org:gavrilov-a/filesystem.git && cd filesystem```
* ```mvn install```
* ```java -jar target/filesystem-${version}.jar```


### Hotkeys ###

`Ctrl + C` - copy file to buffer

`Ctrl + X` - cut file to buffer

`Ctrl + V` - paste file from buffer to current directory
