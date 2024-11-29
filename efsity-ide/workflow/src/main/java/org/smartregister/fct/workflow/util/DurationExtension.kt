package org.smartregister.fct.workflow.util

import java.time.Duration
import java.time.format.DateTimeParseException
import kotlin.time.Duration as KotlinDuration
import org.hl7.fhir.r4.model.Timing

/**
 * Parses a string that represents a duration in ISO-8601 format and returns the parsed Duration
 * value. If parsing fails a default of 1 day duration value is returned
 */
fun KotlinDuration.Companion.tryParse(durationString: String): Duration {
  return try {
    Duration.parse(durationString)
  } catch (ex: DateTimeParseException) {
    return Duration.ofDays(1)
  }
}

fun Timing.extractFhirpathPeriod() =
  this.repeat.let { if (it.hasPeriod()) "${it.period} '${it.periodUnit.display}'" else "" }

fun Timing.extractFhirpathDuration() =
  this.repeat.let { if (it.hasDuration()) "${it.duration} '${it.durationUnit.display}'" else "" }
