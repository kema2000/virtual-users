package com.atlassian.performance.tools.virtualusers.api.config

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.Browser
import com.atlassian.performance.tools.virtualusers.api.browsers.HeadlessChromeBrowser

class VirtualUserBehavior private constructor(
    @Deprecated(
        message = "There should be no need to display help from Java API. Read the Javadoc or sources instead."
    )
    internal val help: Boolean,
    internal val scenario: Class<out Scenario>,
    val load: VirtualUserLoad,
    val jmeterLoadEnabled : Boolean = false,
    val jmeterLoad: VirtualUserLoad,
    internal val seed: Long,
    internal val diagnosticsLimit: Int,
    internal val browser: Class<out Browser>,
    internal val skipSetup: Boolean,
    internal val createUsers: Boolean
) {

    @Deprecated(
        message = "Use the VirtualUserBehavior.Builder instead"
    )
    constructor(
        help: Boolean,
        scenario: Class<out Scenario>,
        load: VirtualUserLoad,
        jmeterLoad: VirtualUserLoad,
        seed: Long,
        diagnosticsLimit: Int,
        browser: Class<out Browser>,
        skipSetup: Boolean
    ) : this(
        help = help,
        scenario = scenario,
        load = load,
        jmeterLoad = jmeterLoad,
        seed = seed,
        diagnosticsLimit = diagnosticsLimit,
        browser = browser,
        skipSetup = skipSetup,
        createUsers = false
    )

    @Deprecated(
        message = "Use the VirtualUserBehavior.Builder instead"
    )
    @Suppress("DEPRECATION")
    constructor(
        help: Boolean,
        scenario: Class<out Scenario>,
        load: VirtualUserLoad,
        jmeterLoad: VirtualUserLoad,
        seed: Long,
        diagnosticsLimit: Int,
        browser: Class<out Browser>
    ) : this(
        help = help,
        scenario = scenario,
        load = load,
        jmeterLoad = jmeterLoad,
        seed = seed,
        diagnosticsLimit = diagnosticsLimit,
        browser = browser,
        skipSetup = false
    )

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use the VirtualUserBehavior.Builder instead"
    )
    constructor(
        scenario: Class<out Scenario>,
        load: VirtualUserLoad,
        jmeterLoad: VirtualUserLoad,
        seed: Long,
        diagnosticsLimit: Int,
        browser: Class<out Browser>
    ) : this(
        help = false,
        scenario = scenario,
        load = load,
        jmeterLoad = load,
        seed = seed,
        diagnosticsLimit = diagnosticsLimit,
        browser = browser
    )

    @Deprecated(
        message = "Use the VirtualUserBehavior.Builder.load instead",
        replaceWith = ReplaceWith(
            "Builder(this).load(load).build()",
            "com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior.Builder"
        )
    )
    fun withLoad(
        load: VirtualUserLoad
    ): VirtualUserBehavior = Builder(this).load(load).build()

    class Builder(
        private var scenario: Class<out Scenario>
    ) {
        private var load: VirtualUserLoad = VirtualUserLoad.Builder().build()
        private var jmeterLoadEnabled : Boolean = false
        private var jmeterLoad: VirtualUserLoad = VirtualUserLoad.Builder().build()
        private var seed: Long = 12345
        private var diagnosticsLimit: Int = 16
        private var browser: Class<out Browser> = HeadlessChromeBrowser::class.java
        private var skipSetup = false
        private var createUsers = false

        fun scenario(scenario: Class<out Scenario>) = apply { this.scenario = scenario }
        fun load(load: VirtualUserLoad) = apply { this.load = load }
        fun jmeterLoad(load: VirtualUserLoad) = apply { this.jmeterLoad = load }
        fun jmeterLoadEnabled(enable: Boolean) = apply { this.jmeterLoadEnabled = enable }
        fun seed(seed: Long) = apply { this.seed = seed }
        fun diagnosticsLimit(diagnosticsLimit: Int) = apply { this.diagnosticsLimit = diagnosticsLimit }
        fun browser(browser: Class<out Browser>) = apply { this.browser = browser }
        fun skipSetup(skipSetup: Boolean) = apply { this.skipSetup = skipSetup }
        fun createUsers(createUsers: Boolean) = apply { this.createUsers = createUsers }

        constructor(
            behavior: VirtualUserBehavior
        ) : this(
            scenario = behavior.scenario
        ) {
            load = behavior.load
            scenario = behavior.scenario
            diagnosticsLimit = behavior.diagnosticsLimit
            browser = behavior.browser
            skipSetup = behavior.skipSetup
            createUsers = behavior.createUsers
        }

        @Suppress("DEPRECATION")
        fun build(): VirtualUserBehavior = VirtualUserBehavior(
            help = false,
            scenario = scenario,
            load = load,
            jmeterLoadEnabled = jmeterLoadEnabled,
            jmeterLoad = jmeterLoad,
            seed = seed,
            diagnosticsLimit = diagnosticsLimit,
            browser = browser,
            skipSetup = skipSetup,
            createUsers = createUsers
        )
    }
}
