package com.github.jlgrock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan
class Main implements CommandLineRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(Main)

    Config config

    GitLogReader gitLogReader

    @Autowired
    Main(Config configIn, GitLogReader gitLogReaderIn) {
        gitLogReader = gitLogReaderIn
        config = configIn
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Git Monitor Application.")
        LOGGER.debug("Classpath: " + System.getProperty("java.class.path", ".").tokenize(File.pathSeparator))
        SpringApplication.run(Main.class, args)
    }

    @Override
    void run(String... args) throws Exception {
        Timer timer = new Timer();

        def interval = config.interval
        if (interval < 1000) {
            LOGGER.error("interval of [$interval] not valid.  Setting interval to default of 60000")
            interval = 60000
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                gitLogReader.readLogs()
            }
        }, 0, interval)
    }

}