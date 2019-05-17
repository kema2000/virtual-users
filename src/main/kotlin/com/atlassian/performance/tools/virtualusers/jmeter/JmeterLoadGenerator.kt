package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.Issue
import com.atlassian.performance.tools.jiraactions.api.memories.Project
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import com.atlassian.performance.tools.jirasoftwareactions.api.JiraSoftwareScenario
import com.atlassian.performance.tools.virtualusers.LoadGenerator
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import org.apache.jmeter.control.GenericController
import org.apache.jmeter.engine.StandardJMeterEngine
import org.apache.jmeter.util.JMeterUtils
import org.apache.jorphan.collections.HashTree
import org.apache.jmeter.protocol.http.sampler.HTTPSampler
import org.apache.sis.internal.metadata.OtherLocales.setFirst
import org.apache.jmeter.control.LoopController
import org.apache.jmeter.control.ThroughputController
import org.apache.jmeter.control.TransactionController
import org.apache.jmeter.testelement.TestPlan
import org.junit.experimental.results.ResultMatchers.isSuccessful
import sun.plugin2.main.server.LiveConnectSupport.getResult
import org.apache.jmeter.samplers.SampleResult
import org.apache.jmeter.samplers.SampleEvent
import org.apache.jmeter.reporters.Summariser
import org.apache.jmeter.reporters.ResultCollector
import org.apache.jmeter.threads.JMeterThread
import org.apache.jmeter.threads.ListenerNotifier
import org.apache.jorphan.collections.ListedHashTree
import org.apache.jmeter.threads.TestCompiler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI
import java.time.Duration


class JmeterLoadGenerator(
    private val vuoptions: VirtualUserOptions
) : LoadGenerator {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    val vuList = mutableListOf<JmeterVitualUser>()

    override fun setup() {

        if(!vuoptions.behavior.jmeterLoadEnabled){
            return
        }

        logger.info("Setup JmeterLoadGenerator...")

        val dataMemory = JmeterDataMemory().apply {
            setup(vuoptions)
        }

        val option = JmeterOptions(
            jiraHost = vuoptions.target.webApplication.host,
            jiraPort = vuoptions.target.webApplication.port,
            user = vuoptions.target.userName,
            pass = vuoptions.target.password,
            load = vuoptions.behavior.jmeterLoad)

        vuList.addAll(
            (1..option.load.virtualUsers).map {
                JmeterVitualUser(name = "JPT-Jmeter-$it", option = option, myDataMemory = dataMemory).apply {
                    setup()
                }
            })
    }

    override fun run() {
        logger.info("Launching JmeterLoadGenerator...")
        vuList.forEach { vu ->
            vu.run()
        }
    }

}

fun main(args: Array<String>) {
    val vo = VirtualUserOptions(
        target = VirtualUserTarget(
            webApplication = URI("http://10.116.145.228:8080/"),
            userName = "admin",
            password = "MasterPassword18"),
        behavior = VirtualUserBehavior.Builder(JiraSoftwareScenario::class.java)
            .jmeterLoadEnabled(true)
            .jmeterLoad(
                VirtualUserLoad.Builder()
                    .virtualUsers(1)
                    .flat(Duration.ofSeconds(0))
                    .ramp(Duration.ofSeconds(1))
                    .flat(Duration.ofSeconds(2))
                    .build())
            .build()
    )

    val jmeterLoadGenerator = JmeterLoadGenerator(vuoptions = vo)
    jmeterLoadGenerator.setup()
    jmeterLoadGenerator.run()
}

