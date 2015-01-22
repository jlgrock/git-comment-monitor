# git-comment-monitor
A simple tool to scan new git logs for a regular expression

How it works:
(This should be better in the future...)
* Run `gradle distZip distTar`
* Move the package from the `/build' directory to the appropriate system (local or server based)
* extract it
* Edit the file at location `APP_DIR/conf/application.properties` with the properties that make sense for your repository
* run the executable in the /bin folder
* you can kill with Ctrl-C and it will start up where it left off last time

If you want to reset the clock, delete `APP_DIR/conf/config.properties`
