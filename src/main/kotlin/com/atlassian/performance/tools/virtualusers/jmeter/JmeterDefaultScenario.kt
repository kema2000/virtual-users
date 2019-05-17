package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.scenario.addMultiple
import com.atlassian.performance.tools.virtualusers.jmeter.sampler.*
import org.apache.jmeter.protocol.http.control.CookieManager
import org.apache.jmeter.samplers.Sampler

class JmeterDefaultScenario{


    fun getActionList(option: JmeterOptions, dataMemory: JmeterDataMemory, cookieManager: CookieManager) : List<Sampler>{

        val seededRandom = SeededRandom()
        val scenario: MutableList<Sampler> = mutableListOf()
        val createIssue = JmeterCreateIssueSampler(option, dataMemory, cookieManager)
        val searchWithJql = JmeterJqlSampler(option, dataMemory, cookieManager)
        val viewIssue = JmeterViewIssueSampler(option, dataMemory, cookieManager)
        val projectSummary = JmeterProjectSummarySampler(option, dataMemory, cookieManager)
        val viewDashboard = JmeterViewDashboardSampler(option, dataMemory, cookieManager)
        val editIssue = JmeterEditIssueSampler(option, dataMemory, cookieManager)
        val addComment = JmeterAddCommentSampler(option, dataMemory, cookieManager)
        val browseProjects = JmeterBrowseProjectsSampler(option, dataMemory, cookieManager)
        val viewBoard = JmeterViewBoardSampler(option, dataMemory, cookieManager)
        val viewBacklog = JmeterViewBacklogSampler(option, dataMemory, cookieManager)
        val browseBoards = JmeterBrowseBoardsSampler(option, dataMemory, cookieManager)

        val actionProportions = mapOf(
            createIssue to 5,
            searchWithJql to 20,
            viewIssue to 55,
            projectSummary to 5,
            viewDashboard to 10,
//            editIssue to 5,
//            addComment to 2,
            browseProjects to 5,
            viewBoard to 10,
            viewBacklog to 10,
            browseBoards to 2
        )
        actionProportions.entries.forEach { scenario.addMultiple(element = it.key, repeats = it.value) }
        scenario.shuffle(seededRandom.random)
        return scenario
    }

}
