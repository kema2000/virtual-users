package com.atlassian.performance.tools.virtualusers.jmeter.sampler

import com.atlassian.performance.tools.virtualusers.jmeter.JmeterDataMemory
import com.atlassian.performance.tools.virtualusers.jmeter.JmeterOptions
import org.apache.jmeter.config.Arguments
import org.apache.jmeter.protocol.http.control.CookieManager
import org.apache.jmeter.protocol.http.sampler.HTTPSampler
import org.apache.jmeter.protocol.http.util.HTTPArgument
import org.apache.jmeter.reporters.ResultAction
import org.apache.jmeter.samplers.*

class JmeterCreateIssueSampler(option : JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager)
    : CompositeSampler(mutableListOf<Sampler>(), "Jmeter Full Create Issue") {

    val resultAction = CreateIssueResultParser()

    init {
        sampleList.add(basePostHtmlRequest(option, myCookieManager).apply {
            path = "/secure/QuickCreateIssue!default.jspa?decorator=none"
            postBodyRaw = false
            addTestElement(resultAction)
        })

        sampleList.add(baseJsonRequest(option, myCookieManager).apply {
            path = "/rest/quickedit/1.0/userpreferences/create"
            postBodyRaw = true
            arguments = Arguments().apply {
                addArgument(HTTPArgument("", "{\"useQuickForm\":false,\"fields\":[\"summary\",\"description\",\"priority\",\"versions\",\"components\"],\"showWelcomeScreen\":true}"
                ,true).apply { isAlwaysEncoded = false })
            }
        })

        //sampleList.add(JmeterCreateIssueSubmitSampler(option, dataMemory, myCookieManager))
    }

}

class CreateIssueResultParser : ResultAction(){

    var response = ""

    override fun sampleOccurred(e: SampleEvent?) {
        super.sampleOccurred(e)
        response = e!!.result.responseDataAsString
    }
}

class JmeterCreateIssueSubmitSampler(option : JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager)
    : CompositeSampler(mutableListOf<Sampler>(), "Jmeter Create Issue") {

    init {
        //prepare the payload

        /**
         *
         *
         */
        val pid = dataMemory.projectMemory.recall()!!.key
        val issuetype = "Task"
        val atl_token = ""
        val formToken = ""
        val summary =  "summary"
        val duedate = ""
        val reporter = option.user
        val environment = "" //""Environment - " + generator((('a'..'z') + ' ' * 5).join(), 20)
        val description = "Description"
        val timetracking_originalestimate = ""
        val timetracking_remainingestimate = ""
        val isCreateIssue = "true"
        val hasWorkStarted = ""
        val resolution = ""
        val request_body = "pid=${pid}&issuetype=${issuetype}&atl_token=${atl_token}&formToken=${formToken}&summary=${summary}&duedate=${duedate}&reporter=${reporter}&environment=${environment}&description=${description}&timetracking_originalestimate=${timetracking_originalestimate}&timetracking_remainingestimate=${timetracking_remainingestimate}&isCreateIssue=${isCreateIssue}&hasWorkStarted=${hasWorkStarted}&resolution=${resolution}"

        sampleList.add(basePostHtmlRequest(option, myCookieManager).apply {
            path = "/secure/QuickCreateIssue.jspa?decorator=none"
            postBodyRaw = false
            arguments = Arguments().apply {
                addArgument(HTTPArgument("", request_body,true).apply { isAlwaysEncoded = false })
            }
        })
    }

}

