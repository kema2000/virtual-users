package com.atlassian.performance.tools.virtualusers.action

import com.atlassian.performance.tools.concurrency.api.representsInterrupt
import com.atlassian.performance.tools.jiraactions.api.action.Action
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ShakyAction(
    private val delegate: Action
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        try {
            delegate.run()
        } catch (e: Exception) {
            if (e.representsInterrupt()) {
                Thread.currentThread().interrupt()
            } else {
                logger.error("Failed to run $delegate, but we keep running", e)
            }
        }
    }

    override fun toString() = "ShakyAction($delegate)"
}