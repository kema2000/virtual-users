package com.atlassian.performance.tools.virtualusers.jmeter

import com.atlassian.performance.tools.virtualusers.api.TemporalRate
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.collections.CircularIterator
import org.apache.jmeter.control.GenericController
import org.apache.jmeter.samplers.Sampler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.time.Instant

class TimedController(load : VirtualUserLoad) : GenericController() {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val maxLoad: TemporalRate = load.maxOverallLoad //TemporalRate(2.0, Duration.ofSeconds(1))
    private var duration = load.hold + load.ramp + load.flat //Duration.ofSeconds(3)

    var samplerList:List<Sampler> = listOf()

    private var actionsPerformed = 0.0
    private val start = Instant.now()
    private var iterator = CircularIterator(samplerList)

    fun setup(){
        iterator = CircularIterator(samplerList)
    }

    override fun next(): Sampler? {

        val actualTimeSoFar = Duration.between(start, Instant.now())
        if (actualTimeSoFar > duration) {
            return null
        } else {
            val expectedTimeSoFar = maxLoad.scaleChange(actionsPerformed).time
            val extraTime = expectedTimeSoFar - actualTimeSoFar
            if (extraTime > Duration.ZERO) {
                Thread.sleep(extraTime.toMillis())
            }
            return iterator.next()
        }
    }

}
