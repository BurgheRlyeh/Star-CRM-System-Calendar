package com.github.burgherlyeh.plugins

import com.github.burgherlyeh.module
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class RoutingTest {

    @Test
    fun testGetEvents() = testApplication {
        application {
            module()
        }
        client.get("/events").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteEventId() = testApplication {
        application {
            module()
        }
        client.delete("/event/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutEventId() = testApplication {
        application {
            module()
        }
        client.put("/event/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostEvent() = testApplication {
        application {
            module()
        }
        client.post("/event").apply {
            TODO("Please write your test here")
        }
    }
}