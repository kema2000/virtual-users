package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad

data class JmeterOptions(
    val jiraHost: String,
    val jiraPort: Int,
    val user: String,
    val pass: String,
    val load : VirtualUserLoad
)
