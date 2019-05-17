package com.atlassian.performance.tools.virtualusers.jmeter.sampler

import com.atlassian.performance.tools.virtualusers.jmeter.JmeterOptions
import org.apache.jmeter.protocol.http.control.CookieManager
import org.apache.jmeter.protocol.http.control.Header
import org.apache.jmeter.protocol.http.control.HeaderManager
import org.apache.jmeter.protocol.http.sampler.HTTPSampler
import org.apache.jmeter.samplers.AbstractSampler
import org.apache.jmeter.samplers.Entry
import org.apache.jmeter.samplers.SampleResult
import org.apache.jmeter.samplers.Sampler

abstract class CompositeSampler(protected val sampleList: MutableList<Sampler>, val label : String) : AbstractSampler() {

    override fun sample(e: Entry?): SampleResult {
        return CompositeSampleResult(sampleList.map {
            it.sample(e)
        }).apply {
            sampleLabel = label

            if(resultList.all { res -> res.isResponseCodeOK }){
                setResponseCodeOK()
                isSuccessful = true
            } else{
                responseCode = resultList.first { !it.isResponseCodeOK() }.responseCode
                isSuccessful = false
            }
        }
    }

    fun baseJsonRequest(option : JmeterOptions, myCookieManager: CookieManager) = HTTPSampler().apply {
        cookieManager = myCookieManager
        headerManager = HeaderManager().apply {
            add(Header("Accept-Language", "en-US,en;q=0.5"))
            add(Header("Pragma", "no-cache"))
            add(Header("Accept", "application/json, text/javascript, */*; q=0.01"))
            add(Header("X-Requested-With", "XMLHttpRequest"))
            add(Header("Content-Type", "application/json"))
            add(Header("Cache-Control", "no-cache"))
            add(Header("Accept-Encoding", "gzip, deflate"))
        }
        domain = option.jiraHost
        port = option.jiraPort
        method = "POST"
        postBodyRaw = true
        followRedirects = true
        autoRedirects = false
        useKeepAlive = true
        doMultipartPost = false
    }

    fun baseGetHtmlRequest(option : JmeterOptions, myCookieManager: CookieManager) = HTTPSampler().apply {
        cookieManager = myCookieManager
        domain = option.jiraHost
        port = option.jiraPort
        method = "GET"
    }

    fun basePostHtmlRequest(option : JmeterOptions, myCookieManager: CookieManager) = HTTPSampler().apply {
        cookieManager = myCookieManager
        //headerManager = theHeaderManager
        domain = option.jiraHost
        port = option.jiraPort
        method = "POST"
        postBodyRaw = false

        followRedirects = true
        autoRedirects = false
        useKeepAlive = true
        doMultipartPost = false
    }

}

class CompositeSampleResult(val resultList: List<SampleResult>) : SampleResult() {

    override fun getTime(): Long {
        val lastEndTime = resultList.map { it.endTime }.max()
        val firstStartTime = resultList.map { it.startTime }.min()
        if(lastEndTime != null && firstStartTime != null) return lastEndTime-firstStartTime
        return -1
    }

}

