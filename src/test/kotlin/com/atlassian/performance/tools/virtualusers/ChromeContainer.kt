package com.atlassian.performance.tools.virtualusers

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.virtualusers.api.browsers.Browser
import com.atlassian.performance.tools.virtualusers.api.browsers.CloseableRemoteWebDriver
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
internal class ChromeContainer : Browser {
    private val dockerisedChrome = DockerisedChrome()
    override fun start(): CloseableRemoteWebDriver {
        val browser = dockerisedChrome.start()
        return object : CloseableRemoteWebDriver(browser.driver) {
            override fun close() {
                super.close()
                browser.close()
            }
        }
    }
}