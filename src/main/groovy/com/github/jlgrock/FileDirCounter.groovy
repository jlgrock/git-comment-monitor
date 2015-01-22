package com.github.jlgrock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

@Component
class FileDirCounter implements FileVisitor<Path> {
    private static Logger LOGGER = LoggerFactory.getLogger(FileDirCounter)

    Config config

    def totalNumberOfObjects = 0
    def totalNumberOfNonGitObjects = 0

    @Autowired
    FileDirCounter(Config configIn) {
        LOGGER.debug("Instantiating FileDirCounter")
        config = configIn
    }

    @Override
    FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        LOGGER.debug("analyzing Directory [{}]", dir.toAbsolutePath())
        if (dir == config.gitDirectory.resolve(".git")) {
            return FileVisitResult.SKIP_SUBTREE
        }
        if (dir != config.gitDirectory) {
            LOGGER.debug("adding Directory to counter")
            ++totalNumberOfObjects
        }
        return FileVisitResult.CONTINUE
    }

    @Override
    FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        LOGGER.debug("adding File to counter [{}]", file.toAbsolutePath())
        ++totalNumberOfObjects
        return FileVisitResult.CONTINUE
    }

    @Override
    FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE
    }

    @Override
    FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE
    }
}
