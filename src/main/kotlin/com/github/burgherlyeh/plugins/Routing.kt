package com.github.burgherlyeh.plugins

import com.github.burgherlyeh.apis.Scheduler
import com.github.burgherlyeh.utils.toDateTime
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.*

fun Application.configureRouting() {
    val scheduler = Scheduler()

    routing {
        get("/events") {
            val startTime = try {
                call.parameters["startTime"]?.let { LocalDateTime.parse(it) }
            } catch (e: DateTimeParseException) {
                null
            }
            val endTime = try {
                call.parameters["endTime"]?.let { LocalDateTime.parse(it) }
            } catch (e: DateTimeParseException) {
                null
            }
            val tag = call.parameters["tag"]

            val events = scheduler.getEvents(startTime, endTime, tag)
            call.respond(events)
        }
        route("/event") {
            delete("/{id}") {
                val id = call.parameters["id"]!!
                val eventToDelete = scheduler.getEvent(id)
                scheduler.deleteEvent(eventToDelete)
                call.respond(HttpStatusCode.OK)
            }
            put("/{id}") {
                val id = call.parameters["id"]!!
                val eventToUpdate = scheduler.getEvent(id)
                val updatedEvent = call.receive<Event>()
                scheduler.updateEvent(eventToUpdate, updatedEvent)
                call.respond(HttpStatusCode.OK)
            }
            post("") {
                val event = call.receive<Event>()
                scheduler.addEvent(event)
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}
