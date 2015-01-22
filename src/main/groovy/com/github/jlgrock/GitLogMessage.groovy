package com.github.jlgrock

import groovy.transform.ToString
import org.eclipse.jgit.lib.ObjectId

@ToString
class GitLogMessage {
    ObjectId id
    String fullMessage
    Date commitTime
    String username
    String emailAddress

    GitLogMessage(ObjectId idIn, Date commitTimeIn, String fullMessageIn, String usernameIn, String emailAddressIn) {
        id = idIn
        commitTime = commitTimeIn
        fullMessage = fullMessageIn
        username = usernameIn
        emailAddress = emailAddressIn
    }
}
