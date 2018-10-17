package com.atlassian.performance.tools.virtualusers.action

import com.atlassian.performance.tools.concurrency.api.representsInterrupt
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.virtualusers.api.diagnostics.Diagnostics
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class DiagnosingAction(
    private val delegate: Action,
    private val diagnostics: Diagnostics
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        try {
            logger.trace("Running $delegate")
            delegate.run()
        } catch (e: Exception) {
            if (e.representsInterrupt().not()) {
                diagnostics.diagnose(e)
            }
            throw Exception("Failed to run $delegate", e)
        }
    }

    override fun toString() = "DiagnosingAction($delegate)"
}