package com.github.jlgrock

import groovy.transform.ToString
import org.apache.commons.lang3.StringEscapeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.stereotype.Component

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

@Configuration
@PropertySource("application.properties")
@Component
@ToString
class Config {

    private static Logger LOGGER = LoggerFactory.getLogger(Config)

    @Value('${log.regex}')
    String regex

    @Value('${git.repository.uri}')
    String gitRepository

    @Value('${git.directory}')
    String gitDirectory

    @Value('${mail.server.uri}')
    String mailServerUri

    @Value('${mail.to.address}')
    String mailToAddress

    @Value('${mail.from.address}')
    String mailFromAddress

    @Value('${mail.subject}')
    String mailSubject

    @Value('${mail.username}')
    String mailUsername

    @Value('${mail.password}')
    String mailPassword

    @Value('${git.readfrom.date}')
    String firstUpdate

    @Value('${config.propertiesFile.location}')
    String configPropertiesFile

    @Value('${config.interval}')
    String interval

    Pattern getRegex() {
        Pattern.compile(Pattern.quote(regex));
    }

    Path getGitDirectory() {
        Paths.get(gitDirectory)
    }

    Path getConfigPropertiesFile() {
        return Paths.get(configPropertiesFile)
    }

    Long getInterval() {
        Long.parseLong(interval)
    }

    Date getFirstUpdate() {
        new Date(Long.parseLong(firstUpdate))
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer()
    }


}
