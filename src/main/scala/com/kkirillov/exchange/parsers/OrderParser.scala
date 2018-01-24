package com.kkirillov.exchange.parsers

import com.kkirillov.exchange.models.{OrderMethod, Order}

// FIXME: no detailed error reporting here
object OrderParser {
  private def parseMethod(method: String) = method match {
    case "s" => OrderMethod.Sell
    case "b" => OrderMethod.Buy
    case _ => throw new RuntimeException(s"Failed to parse order method: `$method`")
  }

  def parse(s: String, id: Int): Order = s.split('\t').toList match {
    case client :: method :: stock :: bid :: qty :: Nil =>
      Order(id, client, parseMethod(method), stock, bid.toInt, qty.toInt)
    case _ => throw new RuntimeException(s"Failed to parse order: `$s`")
  }
}
