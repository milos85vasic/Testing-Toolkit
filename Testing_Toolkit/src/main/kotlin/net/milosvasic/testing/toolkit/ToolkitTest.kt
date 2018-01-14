package net.milosvasic.testing.toolkit

import net.milosvasic.logger.ConsoleLogger
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

abstract class ToolkitTest {

    private val lock = ReentrantLock()
    private val logger = ConsoleLogger()
    private val condition = lock.newCondition()

    @Test
    fun testFunctionality() {
        beforeTest()
        testImplementation()
        afterTest()
    }

    protected abstract fun beforeTest()

    protected abstract fun testImplementation()

    protected abstract fun afterTest()

    protected fun sleep(time: Long) {
        sleep(time, true)
    }

    protected fun sleep(time: Long, seconds: Boolean) {
        var totalTime: Long = time
        if (seconds) {
            totalTime *= 1000
        }
        try {
            Thread.sleep(totalTime)
        } catch (e: Exception) {
            Assert.fail("Failed sleeping for: " + time)
        }
    }

    protected fun lock() {
        lock(10)
    }

    protected fun lock(timeoutInSeconds: Long) {
        log("Lock [ ON ]: " + timeoutInSeconds)
        lock.lock()
        try {
            condition.await(timeoutInSeconds, TimeUnit.SECONDS)
        } catch (e: Exception) {
            Assert.fail("Lock failed: " + e.message)
        } finally {
            lock.unlock()
        }
    }

    protected fun unlock() {
        log("Lock [ OFF ]")
        lock.lock()
        try {
            condition.signal()
        } finally {
            lock.unlock()
        }
    }

    private fun log(msg: String) {
        logger.v(javaClass.simpleName, msg)
    }

}