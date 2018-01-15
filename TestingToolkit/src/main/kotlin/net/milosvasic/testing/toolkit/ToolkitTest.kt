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

    protected abstract val tag: String

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

    protected fun lock(timeoutInSeconds: Long, seconds: Boolean = true) {
        log("Lock [ ON ]: " + timeoutInSeconds)
        lock.lock()
        try {
            val timeUnit = if (seconds) {
                TimeUnit.SECONDS
            } else {
                TimeUnit.MILLISECONDS
            }
            condition.await(timeoutInSeconds, timeUnit)
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

    protected fun log(msg: String) = logger.v(tag, msg)

    protected fun err(msg: String) = logger.e(tag, msg)

    protected fun wrn(msg: String) = logger.w(tag, msg)

}