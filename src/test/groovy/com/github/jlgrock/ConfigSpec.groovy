package com.github.jlgrock

import spock.lang.Specification

import java.nio.file.Paths
import java.util.regex.Pattern

class ConfigSpec extends Specification {
    def 'accessors and mutators work'() {
        when:
            Config config = new Config()
            config.gitDirectory = 'abc'
            config.gitRepository = 'def'
            config.mailFromAddress = 'ghi'
            config.mailPassword = 'jhk'
            config.mailServerUri = 'lmn'
            config.mailSubject = 'opq'
            config.mailToAddress = 'rst'
            config.mailUsername = 'uvw'
            config.regex = "[SNPPROT-\\d*]"
            config.configPropertiesFile = "test"
            config.interval = "600000"
            config.firstUpdate = "1234"
        then:
            config.gitDirectory == Paths.get('abc')
            config.gitRepository == 'def'
            config.mailFromAddress == 'ghi'
            config.mailPassword == 'jhk'
            config.mailServerUri == 'lmn'
            config.mailSubject == 'opq'
            config.mailToAddress == 'rst'
            config.mailUsername == 'uvw'
            config.regex.toString() == Pattern.compile(Pattern.quote("[SNPPROT-\\d*]")).toString()
            config.configPropertiesFile == Paths.get("test")
            config.interval == 600000l
            config.firstUpdate == new Date(Long.parseLong("1234"))
    }
}
