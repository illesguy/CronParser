package com.illesguy.cronparser.main

import com.illesguy.cronparser.parser.CronStringParser
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

object Main extends App {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)

  args.toSeq.headOption.map(CronStringParser.parseCronString) match {

    case Some(Success(parsedCronString)) => logger.info(s"Cron string successfully parsed:\n$parsedCronString")

    case Some(Failure(ex)) => logger.error("Error occurred while parsing cron string.", ex)

    case None => logger.error("""No input cron string passed in! Usage: build/bin/CronParser "cron_string_to_parse"""")
  }
}
