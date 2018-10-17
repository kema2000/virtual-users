package com.atlassian.performance.tools.virtualusers

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.LogInAction
import com.atlassian.performance.tools.jiraactions.api.action.SetUpAction
import com.atlassian.performance.tools.jvmtasks.api.Backoff
import com.atlassian.performance.tools.jvmtasks.api.IdempotentAction
import com.atlassian.performance.tools.virtualusers.action.DiagnosingAction
import com.atlassian.performance.tools.virtualusers.action.ShakyAction
import com.atlassian.performance.tools.virtualusers.api.diagnostics.Diagnostics
import com.atlassian.performance.tools.virtualusers.collections.CircularIterator
import com.atlassian.performance.tools.virtualusers.measure.JiraNodeCounter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.Thread.interrupted
import java.time.Duration

/**
 * Applies load on a Jira via page objects. Explores the instance to learn about data and choose pages to visit.
 * Wanders preset Jira pages with different proportions of each page. Their order is random.
 */
internal class ExploratoryVirtualUser(
    private val jira: WebJira,
    private val nodeCounter: JiraNodeCounter,
    private val actions: Iterable<Action>,
    private val setUpAction: SetUpAction,
    private val logInAction: LogInAction,
    private val diagnostics: Diagnostics
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val loginRetryLimit: Int = 1_000_000

    fun setUpJira() {
        logger.info("Setting up Jira...")
        DiagnosingAction(logInAction, diagnostics).run()
        DiagnosingAction(setUpAction, diagnostics).run()
        logger.info("Jira is set up")
    }

    /**
     * Repeats [actions] until the thread is interrupted.
     */
    fun applyLoad() {
        logger.info("Applying load...")
        logIn()
        val node = jira.getJiraNode()
        nodeCounter.count(node)
        val actionNames = actions.map { it.javaClass.simpleName }
        logger.debug("Circling through $actionNames")
        for (action in CircularIterator(actions)) {
            ShakyAction(DiagnosingAction(action, diagnostics)).run()
            if (interrupted()) {
                logger.info("Scenario finished on cue")
                break
            }
        }
    }

    private fun logIn() {
        IdempotentAction("log in") {
            DiagnosingAction(logInAction, diagnostics).run()
        }.retry(
            backoff = StaticBackoff(Duration.ofSeconds(5)),
            maxAttempts = loginRetryLimit
        )
    }
}

private class StaticBackoff(
    private val backOff: Duration
) : Backoff {
    override fun backOff(attempt: Int): Duration = backOff
}
