package com.kkirillov.exchange

import com.kkirillov.exchange.models.Stock
import com.kkirillov.exchange.parsers.{ClientParser, OrderParser}

import scala.io.Source

object Main extends App {
  private def linesOf(filename: String): Iterator[String] =
    Source.fromFile(filename, "UTF-8").getLines

  private def parseClients(filename: String) =
    linesOf(filename).map(ClientParser.parse).toList

  private def parseOrders(filename: String) =
    linesOf(filename).zipWithIndex.map(Function.tupled(OrderParser.parse)).toList

  args.toList match {
    case clientsDb :: ordersDb :: Nil =>
      StockExchangeEngine.eval(StockExchangeState(parseClients(clientsDb), parseOrders(ordersDb))).clientsDb.values.toList.sortBy(_.name).foreach { c =>
        val stocks = Stock.stocks.map(c.stockQuantities.getOrElse(_, 0)).mkString("\t")
        println(s"${c.name}\t${c.dollarBalance}\t$stocks")
      }
    case _ => println("Failure.\nTwo arguments rqeuired: `clients` filename and `orders` filename")
  }
}
