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
  private def getValuesForPattern(possibleValues: Seq[Int], pattern: String): String = pattern match {
    case s"$newPattern/$stepSize" =>
      val step = stepSize.toInt
      val newPossibleValues = possibleValues.filter(_ % step == 0)
      getValuesForPattern(newPossibleValues, newPattern)

    case s"$lower-$upper" =>
      val lowerBound = lower.toInt
      val upperBound = upper.toInt
      possibleValues.filter(v => lowerBound <= v && v <= upperBound).mkString(" ")

    case p if p.contains(",") =>
      val selectedValues = p.split(",").map(_.toInt)
      possibleValues.filter(selectedValues.contains).mkString(" ")

    case "*" => possibleValues.mkString(" ")

    case p if possibleValues.contains(p.toInt) => p

    case _ => throw new IllegalArgumentException(s"Invalid pattern $pattern")
  }
}
