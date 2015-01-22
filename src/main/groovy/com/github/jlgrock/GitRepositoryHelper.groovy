package com.github.jlgrock

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Paths

@Component
class GitRepositoryHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(GitRepositoryHelper)

    Config config

    @Autowired
    GitRepositoryHelper(Config configIn) {
        config = configIn
    }
    public void fetchRepository() {
        File f = config.gitDirectory.toFile()
        Repository repository = new FileRepositoryBuilder()
                .setWorkTree(f)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()

        Git git = new Git(repository)

        StoredConfig storedConfig = git.getRepository().getConfig()
        storedConfig.setString("remote", "origin", "url", config.gitRepository)
        storedConfig.save()

        LOGGER.debug("Starting fetch")
        PullResult result = git.pull().call()
    }

    public void cloneRepository() throws IOException {
        File f = config.gitDirectory.toFile()
        println("Cloning from " + config.gitRepository + " to " + f.absolutePath);
        Git result = Git.cloneRepository()
                .setURI(config.gitRepository)
                .setDirectory(f)
                .setBranch("master")
                .setBare(false)
                .setRemote("origin").
                setNoCheckout(false)
                .call()
        try {
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            println("Having repository: " + result.getRepository().getDirectory())

        } finally {
            result.close();
        }
    }

    public LogResults captureLogs(Date lastDate) {
        LOGGER.debug("captureLogs called with lastDate=${lastDate}")
        File f = config.gitDirectory.toFile()

        Repository repository = new FileRepositoryBuilder()
                .setWorkTree(f)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        Git git = new Git(repository)

        List<RevCommit> commits = git.log().call().asList()
        new LogResults(commits.findAll { it.authorIdent.when > lastDate }.collect { new GitLogMessage(it.id, it.authorIdent.when, it.fullMessage, it.authorIdent.name, it.authorIdent.emailAddress) })
    }

}
