package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.virtualusers.jmeter.sampler.CompositeSampleResult
import com.atlassian.performance.tools.virtualusers.jmeter.sampler.JmeterLoginSampler
import org.apache.jmeter.control.LoopController
import org.apache.jmeter.protocol.http.control.CookieManager
import org.apache.jmeter.threads.JMeterThread
import org.apache.jmeter.threads.ListenerNotifier
import org.apache.jmeter.threads.TestCompiler
import org.apache.jorphan.collections.ListedHashTree
import org.apache.jmeter.reporters.ResultCollector
import org.apache.jmeter.reporters.Summariser
import org.apache.jmeter.samplers.SampleEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class JmeterVitualUser(val name: String, val option: JmeterOptions, val myDataMemory: JmeterDataMemory) {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    val myCookieManager = CookieManager().apply {
        clearEachIteration = true
        isEnabled = true
        testStarted()
    }

    var myMainController = TimedController(option.load).apply {
        samplerList = JmeterDefaultScenario().getActionList(option, myDataMemory, myCookieManager)
    }

    fun setup(){
        myMainController.setup()
    }

    fun run() {

        val mainLoop = LoopController().apply {
            loops = 1
            setContinueForever(false)
            setFirst(true)
            initialize()
        }

        // Main Thread Group
        val threadGroup = org.apache.jmeter.threads.ThreadGroup()

        //build the hash tree
        val hashTree = ListedHashTree()
        hashTree.add(mainLoop)

        hashTree.add(mainLoop, JmeterLoginSampler(option, myDataMemory, myCookieManager))
        hashTree.add(mainLoop, myMainController)
        myMainController.samplerList.onEach {
            hashTree.add(myMainController, it)
        }

        val summer = MyResultCollector("summary").apply { testStarted() }
        hashTree.add(mainLoop, summer)
        hashTree.add(myMainController, summer)

        //launch the test
//        val compiler = TestCompiler(hashTree)
//        hashTree.traverse(compiler)
        val notifier = ListenerNotifier()
        val thread = JMeterThread(hashTree, threadGroup, notifier).apply {
            setOnErrorStopTest(false)
            setThreadGroup(threadGroup)
        }
        Thread(Runnable {
            thread.run()
        }).apply {
            name = this@JmeterVitualUser.name
        }.start()

    }
}

class MyResultCollector(name : String) : Summariser(name) {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun sampleOccurred(e: SampleEvent) {
        super.sampleOccurred(e)
        val r = e.result
        if (r is CompositeSampleResult && r.isSuccessful) {
            logger.info("<${r.sampleLabel}> Response time in milliseconds: ${r.time}")
        }
    }
}

