package com.github.jlgrock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

@Component
class GitLogReader {

    private static Logger LOGGER = LoggerFactory.getLogger(GitLogReader)

    Config config

    GitRepositoryHelper gitRepositoryHelper

    FileDirCounter fileDirCounter

    PropsUtil propsUtil

    Boolean isSetup = Boolean.FALSE

    EmailHelper emailHelper

    @Autowired
    GitLogReader(Config configIn, GitRepositoryHelper gitRepositoryHelperIn, FileDirCounter fileDirCounterIn, PropsUtil propsUtilIn, EmailHelper emailHelperIn) {
        config = configIn
        gitRepositoryHelper = gitRepositoryHelperIn
        fileDirCounter = fileDirCounterIn
        propsUtil = propsUtilIn
        emailHelper = emailHelperIn

        //TODO check config object
    }

    def readLogs() throws Exception {
        LOGGER.info("config: ${config}")

        if (!isSetup) {
            setupGitProject()
        }

        // update the repository
        gitRepositoryHelper.fetchRepository()

        Date lastDate = propsUtil.readLastDate()

        //now that the repository is up to date, get the logs
        LogResults logs = gitRepositoryHelper.captureLogs(lastDate)

        if(logs.logMessages.size() > 0) {
            LOGGER.debug("Found new messages.  Processing...")
            parseAndEmailLogs(logs)
            propsUtil.saveLastDate(logs.calculateLastLogMessageDate())
        } else {
            LOGGER.debug("Found no new messages.")
        }
    }

    def setupGitProject() {
        //check to see if the directory already exists
        Path path = config.gitDirectory

        if (Files.exists(path)) {
            //if isDirectory is false, it could indicate non-existence, so it must be checked for existence first
            if (!Files.isDirectory(path)) {
                LOGGER.error("Current git Directory path refers to a file.  This must be a directory (which does not have to be created).")
                System.exit(1)
            }

            LOGGER.debug("Current git Directory path refers to a directory")
            def count = countFiles()
            if (count.totalNumberOfNonGitObjects > 0) {
                LOGGER.error("Current git Directory path refers to a directory that is non-empty.  Please make sure to provide an empty/new directory for cloning.")
                System.exit(1)
            }
            if (count.totalNumberOfObjects == 0) {
                LOGGER.debug("Deleting empty directory {} for cloning into it", path.toAbsolutePath())
                path.toFile().deleteDir()
            }
        }

        // at this point, if the file exists, it is a git dir, otherwise, it needs to be cloned
        if (!Files.isDirectory(path)) {
            gitRepositoryHelper.cloneRepository()
        }

        isSetup = Boolean.TRUE
    }

    def countFiles() {
        try {
            Files.walkFileTree(config.gitDirectory, fileDirCounter)
        } catch (IOException e) {
            LOGGER.error("Problem accessing the git Directory - unknown IO exception", e)
            System.exit(1)
        }
        LOGGER.debug("Total number of objects counted: {}", fileDirCounter.totalNumberOfObjects)
        LOGGER.debug("Total number of non-git objects counted: {}", fileDirCounter.totalNumberOfNonGitObjects)
        ["totalNumberOfObjects": fileDirCounter.totalNumberOfObjects, "totalNumberOfNonGitObjects": fileDirCounter.totalNumberOfNonGitObjects]
    }

    def parseAndEmailLogs(LogResults logs) {
        def emailBody = "<html><head></head><body><table><tr><th>Git Commit</th><th>Commit Time</th><th>User</th><th>Email</th><th>Comment</th></tr>"
        logs.logMessages.each {
            if (!config.regex.matcher(it.fullMessage).find()) {
                    emailBody += "<tr><td>${it.id.name()}</td><td>${it.commitTime}</td><td>${it.username}</td><td>${it.emailAddress}</td><td>${it.fullMessage}</td></tr>"
            }
        }
        emailBody += "</table></body></html>"
        emailHelper.mail(emailBody)
    }
}
