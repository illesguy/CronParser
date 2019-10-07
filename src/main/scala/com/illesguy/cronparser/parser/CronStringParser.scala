package com.illesguy.cronparser.parser

import scala.annotation.tailrec
import scala.util.Try

object CronStringParser {

  private val allMinutes: Seq[Int] = 0 to 59
  private val allHours: Seq[Int] = 0 to 23
  private val allDaysOfMonth: Seq[Int] = 1 to 31
  private val allMonths: Seq[Int] = 1 to 12
  private val allDaysOfWeek: Seq[Int] = 0 to 6

  def parseCronString(cronString: String): Try[String] = Try {
    val cronParts = cronString.split("\\s")

    if (cronParts.length < 6) {
      throw new IllegalArgumentException("Cron string needs 6 parts, 5 fields and a command!")
    }

    val minutePattern = cronParts.head
    val hourPattern = cronParts(1)
    val dayOfMonthPattern = cronParts(2)
    val monthPattern = cronParts(3)
    val dayOfWeekPattern = cronParts(4)
    val command = cronParts(5)

    f"""minute          ${getValuesForPattern(allMinutes, minutePattern)}
       |hour            ${getValuesForPattern(allHours, hourPattern)}
       |day of month    ${getValuesForPattern(allDaysOfMonth, dayOfMonthPattern)}
       |month           ${getValuesForPattern(allMonths, monthPattern)}
       |day of week     ${getValuesForPattern(allDaysOfWeek, dayOfWeekPattern)}
       |command         $command""".stripMargin
  }

  @tailrec
  private def getValuesForPattern(possibleValues: Seq[Int], pattern: String): String = {
    if (pattern.contains("/")) {
      val parts = pattern.split("/")
      val stepSize = parts.last.toInt
      val newPossibleValues = possibleValues.filter(_ % stepSize == 0)
      val newPattern = parts.head
      getValuesForPattern(newPossibleValues, newPattern)

    } else if (pattern == "*") {
      possibleValues.mkString(" ")

    } else if (pattern.contains(",")) {
      val requiredValues = pattern.split(",").map(_.toInt)
      possibleValues.filter(requiredValues.contains).mkString(" ")

    } else if (pattern.contains("-")) {
      val borders = pattern.split("-")
      val bottom = borders.head.toInt
      val top = borders.last.toInt
      possibleValues.filter(v => bottom <= v && v <= top).mkString(" ")

    } else if (possibleValues.contains(pattern.toInt)) {
      pattern

    } else {
      throw new IllegalArgumentException(s"Invalid pattern $pattern")
    }
  }
}
