package com.kkirillov.exchange.models


case class Client(name: String, dollarBalance: Int, stockQuantities: Map[String, Int]) {
  def buy(order: Order): Client =
    /*if (order.client == name) throw new IllegalArgumentException("Cannot buy from self")
    else*/ copy(
    dollarBalance = dollarBalance - order.value,
    stockQuantities = stockQuantities + (order.stock -> (stockQuantities.getOrElse(order.stock, 0) + order.bid))
  )

  def sell(order: Order): Client =
    /*if (order.client == name) throw new IllegalArgumentException("Cannot sell to self")
    else*/ copy(
    dollarBalance = dollarBalance + order.value,
    stockQuantities = stockQuantities + (order.stock -> (stockQuantities.getOrElse(order.stock, 0) - order.bid))
  )
}