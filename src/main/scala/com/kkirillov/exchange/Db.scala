package com.kkirillov.exchange

import cats.implicits._
import com.kkirillov.exchange.models.{Client, Order, OrderMethod}

case class OrdersKey(stock: String, bid: Int, bidPrice: Int)
object OrdersKey {
  def apply(o: Order): OrdersKey = OrdersKey(o.stock, o.bid, o.bidPrice)
}

case class OrdersDB(buyIndex: OrdersDB.OrdersIndex, sellIndex: OrdersDB.OrdersIndex) {
  private def removeFromIndex(index: OrdersDB.OrdersIndex, order: Order): OrdersDB.OrdersIndex = {
    val key = OrdersKey(order)
    index.get(key).map(_.filterNot(_ == order)) match {
      case Some(nextValue) if nextValue.nonEmpty  => index + (key -> nextValue)
      case _ => index - key
    }
  }

  def dropOrder(order: Order): OrdersDB =
    order.method match {
      case OrderMethod.Sell => copy(sellIndex = removeFromIndex(sellIndex, order))
      case OrderMethod.Buy => copy(buyIndex = removeFromIndex(buyIndex, order))
    }
}

object OrdersDB {
  type OrdersIndex = Map[OrdersKey, Set[Order]]

  private val emptyIndex = Map.empty[OrdersKey, Set[Order]]
  def buildIndex(orders: List[Order]): OrdersIndex =
    orders.foldLeft(emptyIndex) {
      case (m, o) =>
        val key = OrdersKey(o)
        m + (key -> m.get(key).map(_ + o).getOrElse(Set(o)))
    }

  val tupled = Function.tupled(apply _)
}

object Db {
  type ClientsDB = Map[String, Client]

  def buildOrdersDb(orders: List[Order]): OrdersDB =
    OrdersDB.tupled(orders.partition(_.method == OrderMethod.Buy).bimap(OrdersDB.buildIndex, OrdersDB.buildIndex))

  def mkClientIndex(client: Client): (String, Client) =
    client.name -> client

  def buildClientsDb(clients: List[Client]): ClientsDB =
    clients.map(mkClientIndex).toMap
}
