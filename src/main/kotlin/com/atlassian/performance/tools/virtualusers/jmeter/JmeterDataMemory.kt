package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.*
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import com.atlassian.performance.tools.jirasoftwareactions.api.JiraSoftwareScenario
import com.atlassian.performance.tools.jvmtasks.api.TaskTimer
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import okhttp3.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.URI
import java.util.concurrent.TimeUnit

class JmeterDataMemory() {

    private val httpClient = OkHttpClient.Builder()
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private val random = SeededRandom()
    val issueMemory = AdaptiveIssueMemory(AdaptiveIssueKeyMemory(random), random)
    val projectMemory = AdaptiveProjectMemory(random)
    val jqlMemory = AdaptiveJqlMemory(random)
    val issueTypes = mutableMapOf<String, String>()
    val nonEditiableIssueTypes = listOf("Epic", "Story")

    fun setup(
        options: VirtualUserOptions
    ) {
        //setupIssuetypes(options)
        setupProjects(options)
        setupIssues(options)
    }

    private fun setupIssuetypes(options: VirtualUserOptions) {
        val target = options.target
        val credential = Credentials.basic(target.userName, target.password)
        val request = Request.Builder()
            .url(target.webApplication.resolve("rest/api/2/issuetype").toString())
            .header("Authorization", credential)
            .get()
            .build()
        TaskTimer.time("Fetch issues types via REST") {
            httpClient.newCall(request).execute()
        }.use { response ->
            if (response.code() == 200) {
                val jarray: JSONArray = JSONParser().parse(response.body()?.string()) as JSONArray
                jarray.forEach {
                    (it as JSONObject).also { issueType ->
                        issueTypes.put(issueType.get("id").toString(), issueType.get("name").toString())
                    }
                }
            } else {
                throw Exception(
                    "Failed to fetch issue types :" +
                        " response code ${response.code()}," +
                        " response body ${response.body()?.string()}"
                )
            }
        }
    }

    private fun setupIssues(
        options: VirtualUserOptions
    ) {
        val target = options.target
        val payload = """
            {
                "jql": "",
                "startAt": 0,
                "maxResults": 1000,
                "fields": [
                    "issuetype"
                ]
            }
            """.trimIndent()
        val requestBody = RequestBody.create(MediaType.parse("application/json"), payload)
        val credential = Credentials.basic(target.userName, target.password)
        val request = Request.Builder()
            .url(target.webApplication.resolve("rest/api/2/search").toString())
            .header("Authorization", credential)
            .post(requestBody)
            .build()
        TaskTimer.time("Fetch issues via REST") {
            httpClient.newCall(request).execute()
        }.use { response ->
            if (response.code() == 200) {
                val jarray: JSONObject = JSONParser().parse(response.body()?.string()) as JSONObject
                val issuesContent: JSONArray = jarray.get("issues") as JSONArray
                issuesContent.forEach {
                    (it as JSONObject).also {
                        val type = ((it.get("fields") as JSONObject).get("issuetype") as JSONObject).get("name").toString()
                        issueMemory.remember(listOf(Issue(
                            key = it.get("key").toString(),
                            id = it.get("id").toString().toLong(),
                            type = type,
                            editable = type !in nonEditiableIssueTypes)))
                    }
                }
            } else {
                throw Exception(
                    "Failed to fetch issues :" +
                        " response code ${response.code()}," +
                        " response body ${response.body()?.string()}"
                )
            }
        }
    }

    private fun setupProjects(options: VirtualUserOptions) {
        val target = options.target
        val credential = Credentials.basic(target.userName, target.password)
        val maxProjectNum = 100
        val request = Request.Builder()
            .url(target.webApplication.resolve("rest/api/2/project?recent=$maxProjectNum&maxResults=$maxProjectNum").toString())
            .header("Authorization", credential)
            .get()
            .build()
        TaskTimer.time("Fetch projects via REST") {
            httpClient.newCall(request).execute()
        }.use { response ->
            if (response.code() == 200) {
                val jarray: JSONArray = JSONParser().parse(response.body()?.string()) as JSONArray
                jarray.forEach {
                    (it as JSONObject).also { project ->
                        projectMemory.remember(listOf(Project(
                            key = project.get("key").toString(),
                            name = project.get("key").toString())))
                        jqlMemory.remember(listOf("project = ${project.get("key").toString()}"))
                    }
                }
            } else {
                throw Exception(
                    "Failed to fetch projects :" +
                        " response code ${response.code()}," +
                        " response body ${response.body()?.string()}"
                )
            }
        }
    }

}

fun main(args: Array<String>) {
    val vo = VirtualUserOptions(
        target = VirtualUserTarget(
            webApplication = URI("http://10.116.145.228:8080/"),
            userName = "admin",
            password = "MasterPassword18"),
        behavior = VirtualUserBehavior.Builder(JiraSoftwareScenario::class.java).build()
    )
    JmeterDataMemory().setup(vo)


}
