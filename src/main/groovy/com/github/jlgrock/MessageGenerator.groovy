package com.github.jlgrock

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class MessageGenerator {
    Config config

    @Autowired
    MessageGenerator(Config configIn) {
        config = configIn
    }

    MimeMessage createMessage(String messageBody, Session mailSession) {
        MimeMessage generateMailMessage = new MimeMessage(mailSession)
        generateMailMessage.setFrom(config.mailFromAddress)
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(config.mailToAddress))
        generateMailMessage.setSubject(config.mailSubject)
        generateMailMessage.setContent(messageBody, "text/html")
        generateMailMessage
    }

}
