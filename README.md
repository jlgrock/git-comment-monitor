# git-comment-monitor
A simple tool to scan new git logs for a regular expression.  This is great for when you are using a hosted solution (GitHub), and you can't install server-side scripts (the ideal solution).  Although this does not fix the problem of people being able to commit a change without a issue number, it does help to identify the problem so it can be addressed.

## How to install:
(This should be better in the future...)
* Run `gradle distZip distTar`
* Move the package from the `/build' directory to the appropriate system (local or server based)
* extract it
* Edit the file at location `APP_DIR/conf/application.properties` with the properties that make sense for your repository
* run the executable in the /bin folder
* you can kill with Ctrl-C and it will start up where it left off last time

If you want to reset the clock, delete `APP_DIR/conf/config.properties`

## Details
This system installs as an application on any computer running the Java Virtual Machine.  It will read the settings from the `application.properties` file.  Please note that all properties currently in the `applications.properties` file are required, though they can be null.  The application will check out the entire git repository to a local directory (usually in the folder `data`) and parse the comments, starting from the date indicated in the properties file.  It will then save a file called `config.properties`, which it uses to keep track of the current position in the log history.  If you should ever desire to change the start point, you can either adjust the initial point in the `application.properties` file or just delete the `config.properties` file (at which, it will start at the initial point again).  When it identifies an issue without the appropriate match, it will send an email out an email identifying the commits that are a problem.  This will continue monitoring changes to the git repository and updating `config.properties` every time it sees a change (a new thread will polls every 60 seconds, which is configurable).  As this can run continuously, and stores the pointer to the current timestamp in a file locally, you can start this up on your local machine and let it run, or you can start it on a server and let it run indefinitely.  Just kill the process (Ctrl-C) when you are done using it.

## Build requirements
* Gradle 2.3+
