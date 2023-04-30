package com.github.burgherlyeh.apis

import com.github.burgherlyeh.utils.toDateTime
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import java.io.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Scheduler {
    private val applicationName = "Star Calendar"
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private val tokensDirectoryPath = "tokens"
    private val scopes = listOf(CalendarScopes.CALENDAR_READONLY)
    private val credentialsFilePath = "/credentials.json"
    private val calendarId = "primary"

    private val service: Calendar

    init {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credentials = getCredentials(httpTransport)
        service = Calendar.Builder(credentials.transport, credentials.jsonFactory, credentials)
            .setApplicationName("Kotlin Google Calendar Example")
            .build()
    }

    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        val inputStream = this.javaClass.getResourceAsStream(credentialsFilePath)
            ?: throw FileNotFoundException("Resource not found: $credentialsFilePath")
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun getEvent(id: String): Event = service.events().get(calendarId, id).execute()

    fun getEvents(
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        tag: String?
    ): List<Event> = service.events()
        .list(calendarId)
        .setTimeMin((startTime ?: LocalDateTime.MIN).toDateTime())
        .setTimeMax((endTime ?: LocalDateTime.MAX).toDateTime())
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()
        .items
        .filter { it.summary.contains(tag ?: "") }

    fun deleteEvent(eventToDelete: Event) {
        service.events().delete(calendarId, eventToDelete.id).execute()
    }

    fun updateEvent(eventToUpdate: Event, updatedEvent: Event) {
        service.events().update(calendarId, eventToUpdate.id, updatedEvent).execute()
    }

    fun addEvent(event: Event) {
        service.events().insert(calendarId, event).execute()
    }


    fun main() {
        val now = DateTime(System.currentTimeMillis())
        val events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items = events.items
        if (items.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in items) {
                var start = event.start.dateTime
                if (start == null) {
                    start = event.start.date
                }
                System.out.printf("%s (%s)\n", event.summary, start)
            }
        }
    }
}
