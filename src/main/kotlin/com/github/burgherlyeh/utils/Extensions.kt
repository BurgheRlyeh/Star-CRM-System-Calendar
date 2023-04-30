package com.github.burgherlyeh.utils

import com.google.api.client.util.DateTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDateTime.toDateTime(): DateTime = DateTime(
    Date.from(
        this.atZone(ZoneId.systemDefault()).toInstant()
    )
)