package com.github.jlgrock

import groovy.transform.ToString

@ToString
class LogResults {
    List<GitLogMessage> logMessages

    LogResults(List<GitLogMessage> logMessagesIn) {
        logMessages = logMessagesIn
    }

    Date calculateLastLogMessageDate() {
        Date lastDate = null
        logMessages.each { GitLogMessage it ->
            if (it.commitTime > lastDate || lastDate == null) {
                lastDate = it.commitTime
            }
        }
        lastDate
    }
}
