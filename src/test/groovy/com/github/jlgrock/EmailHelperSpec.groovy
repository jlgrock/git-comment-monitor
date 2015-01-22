package com.github.jlgrock

import org.jvnet.mock_javamail.Mailbox
import org.mockito.Mockito
import spock.lang.Specification

import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailHelperSpec extends Specification {

    def "should send an email"() {
        setup:
            Config config = Mock(Config)

            MimeMessage mimeMessage = Mock(MimeMessage)
            MessageGenerator messageGenerator = Mock(MessageGenerator)

            // Don't need to mock this as this reads from the classpath and I've injected via the test classpath
            Session mailSession = Session.getDefaultInstance(new Properties())
            Address address = Mock(Address.class)

            //Clear out any messages before running
            Mailbox.clearAll()
        when:
            messageGenerator.createMessage(_, _) >> mimeMessage
            mimeMessage.getAllRecipients() >> ([new InternetAddress("jlgrock@gmail.com")] as Address[])

            config.mailUsername >> "jlgrock"
            config.mailPassword >> "wbgdutsjjkqjwzla"
            config.mailServerUri >> "smtp.test.com"
            config.mailFromAddress >> "jlgrock@gmail.com"
            config.mailToAddress >> "jlgrock@gmail.com"
            config.mailSubject >> "TestingSend"

            EmailHelper emailHelper = new EmailHelper(config, messageGenerator)

        then:
            emailHelper.mail("test") == true

        when:
            def inbox = Mailbox.get("jlgrock@gmail.com")

        then:
            inbox.size() == 1
    }

    //TODO test toString

}
