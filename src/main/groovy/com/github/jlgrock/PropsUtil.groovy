package com.github.jlgrock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PropsUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(PropsUtil)

    Config config

    @Autowired
    PropsUtil(Config configIn) {
        config = configIn
    }

    Date readLastDate() {
        if (config.configPropertiesFile == null) {
            LOGGER.error("Read Failure: Configuration File is not set.  Please add 'config.propertiesFile.location' to the application.properties")
            System.exit(1)
        }
        Properties userProperties = new Properties()
        try {
            File f = config.configPropertiesFile.toFile()
            if (!f.exists()) {
                LOGGER.debug("Read Failure: File not found. Using first update parameter")
                return config.firstUpdate
            }

            FileReader fileIn = new FileReader(f)
            userProperties.load(fileIn)
            fileIn.close();
        } catch (FileNotFoundException e) {
            LOGGER.debug("Read Failure: Lost file after check")
            System.exit(1)
            return config.firstUpdate
        } catch (IOException e) {
            LOGGER.error("Read Failure: Error reading file.", e)
            System.exit(1)
        }

        Date returnVal
        if (userProperties.get("repository.scandate.start") == null) {
            returnVal = config.firstUpdate
        } else {
            returnVal = new Date(Long.parseLong((String) userProperties.get("repository.scandate.start")))
        }
        returnVal
    }

    void saveLastDate(Date date) {
        if (config.configPropertiesFile == null) {
            LOGGER.error("Save Failure: Configuration File is not set.  Please add 'config.propertiesFile.location' to the application.properties")
            System.exit(1)
        }
        try {
            Properties userProperties = new Properties()
            userProperties.setProperty("repository.scandate.start", date.time.toString())
            FileOutputStream userFileOut = new FileOutputStream(config.configPropertiesFile.toFile())
            userProperties.store(userFileOut, "User Details")
            userFileOut.close()
        } catch (IOException e) {
            LOGGER.error("Save Failure: Error reading file", e)
        }
    }
}