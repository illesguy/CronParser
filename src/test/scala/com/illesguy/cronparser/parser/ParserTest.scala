package com.illesguy.cronparser.parser

import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.mockito.MockitoSugar

import scala.util.Success

@RunWith(classOf[JUnitRunner])
class ParserTest extends FlatSpec with Matchers with MockitoSugar {

  "CronStringParser" should "return a failure if cron string is missing parts" in {
    val result = CronStringParser.parseCronString("* * * *")

    result.isFailure should be (true)
    val resultException = result.failed.get
    resultException.getClass.getName should be ("java.lang.IllegalArgumentException")
    resultException.getMessage should be ("Cron string needs 6 parts, 5 fields and a command!")
  }

  it should "return a failure if cron string has an invalid pattern" in {
    val result = CronStringParser.parseCronString("* * * 13 * command")

    result.isFailure should be (true)
    val resultException = result.failed.get
    resultException.getClass.getName should be ("java.lang.IllegalArgumentException")
    resultException.getMessage should be ("Invalid pattern 13")
  }

  it should "return a correctly formatted string for a correct cron string input" in {
    val expectedResult =
      """minute          0
        |hour            0
        |day of month    1
        |month           1
        |day of week     0
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0 0 1 1 0 command")

    result should be (Success(expectedResult))
  }

  it should "return a correctly formatted string for a correct cron string input with * fields" in {
    val expectedResult =
      """minute          0
        |hour            0
        |day of month    1
        |month           1
        |day of week     0 1 2 3 4 5 6
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0 0 1 1 * command")

    result should be (Success(expectedResult))
  }

  it should "return a correctly formatted string for a correct cron string input with comma separated values" in {
    val expectedResult =
      """minute          0
        |hour            0
        |day of month    1
        |month           1 3 5 7
        |day of week     0
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0 0 1 1,3,5,7,14 0 command")

    result should be (Success(expectedResult))
  }

  it should "return a correctly formatted string for a correct cron string input with dash values" in {
    val expectedResult =
      """minute          0
        |hour            0
        |day of month    10 11 12 13 14 15
        |month           1
        |day of week     0
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0 0 10-15 1 0 command")

    result should be (Success(expectedResult))
  }

  it should "return a correctly formatted string for a correct cron string input with / values" in {
    val expectedResult =
      """minute          0
        |hour            0 3 6 9 12 15 18 21
        |day of month    1
        |month           1
        |day of week     0
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0 */3 1 1 0 command")

    result should be (Success(expectedResult))
  }

  it should "return a correctly formatted string for a correct cron string with different field types" in {
    val expectedResult =
      """minute          0 5 10 15 20 25 30
        |hour            0 3 6 9 12 15 18 21
        |day of month    1 7 12 18
        |month           3 4 5 6 7
        |day of week     0 1 2 3 4 5 6
        |command         command""".stripMargin

    val result = CronStringParser.parseCronString("0-30/5 */3 1,7,18,12 3-7 * command")

    result should be (Success(expectedResult))
  }
}
