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

class JmeterViewBacklogSampler(option: JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager) : CompositeSampler(mutableListOf<Sampler>(), "Jmeter View Backlog") {

    init {
        sampleList.add(baseGetHtmlRequest(option, myCookieManager).apply {
            path = "/secure/RapidBoard.jspa"
        })

    }

}

