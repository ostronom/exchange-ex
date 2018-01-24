package com.kkirillov.exchange.models

case class Order(id: Int, client: String, method: OrderMethod, stock: String, bidPrice: Int, bid: Int) {
  lazy val value: Int = bid * bidPrice
}