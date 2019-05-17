package com.atlassian.performance.tools.virtualusers.jmeter.sampler

import com.atlassian.performance.tools.virtualusers.jmeter.JmeterDataMemory
import com.atlassian.performance.tools.virtualusers.jmeter.JmeterOptions
import org.apache.jmeter.config.Arguments
import org.apache.jmeter.protocol.http.control.CookieManager
import org.apache.jmeter.protocol.http.sampler.HTTPSampler
import org.apache.jmeter.protocol.http.util.HTTPArgument
import org.apache.jmeter.samplers.AbstractSampler
import org.apache.jmeter.samplers.Entry
import org.apache.jmeter.samplers.SampleResult
import org.apache.jmeter.samplers.Sampler

class JmeterJqlSampler(option : JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager)
    : CompositeSampler(mutableListOf<Sampler>(), "Jmeter JQL") {

    init {
        sampleList.add(basePostHtmlRequest(option, myCookieManager).apply {
            path = "/rest/issueNav/1/issueTable"
            postBodyRaw = false
            arguments = Arguments().apply {
                addArgument(HTTPArgument("startIndex", "0", false).apply { isUseEquals = true })
                addArgument(HTTPArgument("jql", dataMemory.jqlMemory.recall(), false).apply { isUseEquals = true })
                addArgument(HTTPArgument("layoutKey", "split-view", false).apply { isUseEquals = true })
            }
        })

    }

}

