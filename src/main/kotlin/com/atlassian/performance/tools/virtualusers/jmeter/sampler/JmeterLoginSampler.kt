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

class JmeterLoginSampler(option: JmeterOptions, dataMemory: JmeterDataMemory, myCookieManager: CookieManager) : CompositeSampler(mutableListOf<Sampler>(), "Jmeter Login In") {

    init {
        sampleList.add(basePostHtmlRequest(option, myCookieManager).apply {
            path = "/login.jsp"
            postBodyRaw = false
            arguments = Arguments().apply {
                addArgument(HTTPArgument("os_username", option.user, false).apply { isUseEquals = true })
                addArgument(HTTPArgument("os_password", option.pass, false).apply { isUseEquals = true })
                addArgument(HTTPArgument("os_destination", "", false).apply { isUseEquals = true })
                addArgument(HTTPArgument("user_role", "", false).apply { isUseEquals = true })
                addArgument(HTTPArgument("atl_token", "", false).apply { isUseEquals = true })
                addArgument(HTTPArgument("login", "Log In", true).apply { isUseEquals = true })
            }
        })

//        tree.add(login, baseGetHtmlRequest(option).apply {
//            path = "/"
//        })

        sampleList.add(baseJsonRequest(option, myCookieManager).apply {
            path = "/rest/webResources/1.0/resources"
            postBodyRaw = true
            arguments = Arguments().apply {
                addArgument(HTTPArgument("", "{\"r\":[\"com.atlassian.jira.jira-header-plugin:newsletter-signup-tip\"],\"c\":[\"com.atlassian.jira.plugins.jira-development-integration-plugin:0\"],\"xc\":[\"_super\",\"atl.dashboard\",\"jira.global\",\"atl.general\",\"jira.general\",\"browser-metrics-plugin.contrib\",\"atl.global\",\"jira.dashboard\",\"jira.global.look-and-feel\"],\"xr\":[\"com.atlassian.jira.jira-postsetup-announcements-plugin:post-setup-announcements\",\"com.atlassian.gadgets.dashboard:gadgets-adgs\",\"com.atlassian.jira.jira-issue-nav-components:adgs\",\"com.atlassian.jira.jira-issue-nav-components:detailslayout-adgs\",\"com.atlassian.jira.jira-issue-nav-components:simpleissuelist-adgs\",\"com.atlassian.jira.jira-issue-nav-plugin:adgs-styles\",\"com.atlassian.jira.jira-issue-nav-components:orderby-less-adgs\",\"com.atlassian.jira.jira-issue-nav-components:pager-less-adgs\",\"com.atlassian.jira.jira-issue-nav-components:issueviewer-adgs\",\"com.atlassian.jira.gadgets:introduction-dashboard-item-resource-adgs\",\"com.atlassian.jira.jira-tzdetect-plugin:tzdetect-banner-component\",\"com.atlassian.jira.jira-tzdetect-plugin:tzdetect-lib\",\"com.atlassian.jira.jira-postsetup-announcements-plugin:post-setup-announcements-lib\",\"jira.webresources:calendar-en\",\"jira.webresources:calendar-localisation-moment\",\"jira.webresources:bigpipe-js\",\"jira.webresources:bigpipe-init\"]}"
                    , true).apply { isAlwaysEncoded = false })
            }
        })

    }

}

