package com.github.jlgrock

import spock.lang.Specification

class PropsUtilSpec extends Specification {

    File f = new File("config.properties")
    Date date1 = new Date(123456789)
    Date date2 = new Date(9123456789)

    def "test something"() {
        setup:
            Config config = Mock(Config)

        when: 'properties file is not there and props are saved'
            config.configPropertiesFile >> f
            PropsUtil propsUtil = new PropsUtil(config)
            propsUtil.saveLastDate(date1)

        then: 'file is created and contains date'
            propsUtil.readLastDate() == date1

        when: 'updated again when file already exists'
            propsUtil.saveLastDate(date2)

        then:
            propsUtil.readLastDate() == date2
    }

    def cleanup() {
        f.delete()
    }
}
