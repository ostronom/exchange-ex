package com.kkirillov.exchange.parsers

import com.kkirillov.exchange.models.{Client, Stock}


// FIXME: no detailed error reporting here
object ClientParser {
  private def parseQuantities(balances: List[String]): Map[String, Int] =
    balances.map(_.toInt).zip(Stock.stocks).map(_.swap).toMap

  def parse(s: String): Client = s.split('\t').toList match {
    case name :: dollarBalance :: stockBalances =>
      Client(name, dollarBalance.toInt, parseQuantities(stockBalances))
    case _ => throw new RuntimeException(s"Failed to parse client: `$s`")
  }
}
