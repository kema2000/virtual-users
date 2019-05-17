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

class JmeterViewDashboardSampler(option: JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager) : CompositeSampler(mutableListOf<Sampler>(), "Jmeter View Dashboard") {

    init {
        sampleList.add(baseGetHtmlRequest(option, myCookieManager).apply {
            path = "/secure/Dashboard.jspa"
        })

//        sampleList.add(baseJsonRequest(option, myCookieManager).apply {
//            path = "/rest/webResources/1.0/resources"
//            postBodyRaw = true
//            arguments = Arguments().apply {
//                addArgument(HTTPArgument("", "{\"r\":[\"com.atlassian.jira.jira-header-plugin:newsletter-signup-tip\"],\"c\":[\"com.atlassian.jira.plugins.jira-development-integration-plugin:0\"],\"xc\":[\"_super\",\"atl.dashboard\",\"jira.global\",\"atl.general\",\"jira.general\",\"browser-metrics-plugin.contrib\",\"atl.global\",\"jira.dashboard\",\"jira.global.look-and-feel\"],\"xr\":[\"com.atlassian.jira.jira-postsetup-announcements-plugin:post-setup-announcements\",\"com.atlassian.gadgets.dashboard:gadgets-adgs\",\"com.atlassian.jira.jira-issue-nav-components:adgs\",\"com.atlassian.jira.jira-issue-nav-components:detailslayout-adgs\",\"com.atlassian.jira.jira-issue-nav-components:simpleissuelist-adgs\",\"com.atlassian.jira.jira-issue-nav-plugin:adgs-styles\",\"com.atlassian.jira.jira-issue-nav-components:orderby-less-adgs\",\"com.atlassian.jira.jira-issue-nav-components:pager-less-adgs\",\"com.atlassian.jira.jira-issue-nav-components:issueviewer-adgs\",\"com.atlassian.jira.gadgets:introduction-dashboard-item-resource-adgs\",\"com.atlassian.jira.jira-tzdetect-plugin:tzdetect-banner-component\",\"com.atlassian.jira.jira-tzdetect-plugin:tzdetect-lib\",\"com.atlassian.jira.jira-postsetup-announcements-plugin:post-setup-announcements-lib\",\"jira.webresources:calendar-en\",\"jira.webresources:calendar-localisation-moment\",\"jira.webresources:bigpipe-js\",\"jira.webresources:bigpipe-init\"]}"
//                    , true).apply { isAlwaysEncoded = false })
//            }
//        })

    }

}

