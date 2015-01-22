package com.github.jlgrock

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class EmailHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(EmailHelper)

    Config config
    MessageGenerator messageGenerator

    @Autowired
    EmailHelper(Config configIn, MessageGenerator messageGeneratorIn) {
        LOGGER.trace("Creating EmailHelper")
        config = configIn
        messageGenerator = messageGeneratorIn
    }

    public boolean mail(String emailBody) {
        Transport transport
        try {
            LOGGER.debug("Setting Mail Properties..");
            Properties mailServerProperties;
            mailServerProperties = System.getProperties()
            mailServerProperties.put("mail.smtp.port", "587")
            mailServerProperties.put("mail.smtp.auth", "true")
            mailServerProperties.put("mail.smtp.starttls.enable", "true")

            LOGGER.debug("get Mail Session..");
            def mailSession = Session.getDefaultInstance(mailServerProperties, null)

            MimeMessage mimeMessage = messageGenerator.createMessage(emailBody, mailSession)

            LOGGER.debug("Sending mail")
            transport = mailSession.getTransport("smtp")
            transport.connect(config.mailServerUri, config.mailUsername, config.mailPassword)
            transport.sendMessage(mimeMessage, new InternetAddress(config.mailToAddress))
        } catch ( AddressException e ) {
            LOGGER.error("Invalid Address", e)
            return false
        } catch ( MessagingException e ) {
            LOGGER.error("Unable to Send Mail", e)
            return false
        }
        finally {
            try {
                transport.close()
            } catch ( Throwable e ) {
                //do nothing
            }
        }
        true
    }
}