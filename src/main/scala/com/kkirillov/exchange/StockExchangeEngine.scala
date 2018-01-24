package com.kkirillov.exchange

import com.kkirillov.exchange.models.{Client, Order}
import Db._
import cats.data.OptionT
import cats.implicits._

import scala.annotation.tailrec

case class StockExchangeState(clientsDb: ClientsDB, ordersDb: OrdersDB) {
  def transact(buyOrder: Order, sellOrder: Order): StockExchangeState = {
    val buyer = clientsDb(buyOrder.client).buy(sellOrder)
    val seller = clientsDb(sellOrder.client).sell(buyOrder)
    copy(clientsDb = clientsDb + mkClientIndex(buyer) + mkClientIndex(seller)).dropOrder(buyOrder).dropOrder(sellOrder)
  }

  def dropOrder(order: Order): StockExchangeState =
    copy(ordersDb = ordersDb.dropOrder(order))
}

object StockExchangeState {
  def apply(clients: List[Client], orders: List[Order]): StockExchangeState =
    StockExchangeState(buildClientsDb(clients), buildOrdersDb(orders))
}

object StockExchangeEngine {
  private def balanceBid(state: StockExchangeState, buyOrderOpt: Option[Order]): StockExchangeState =
    buyOrderOpt.map { buyOrder =>
      state.ordersDb.sellIndex.getOrElse(OrdersKey(buyOrder), Set.empty).filterNot(_.client == buyOrder.client).headOption match {
        case Some(sellOrder) => state.transact(buyOrder, sellOrder)
        case _ => state.dropOrder(buyOrder)
      }
    }.getOrElse(state)

  @tailrec
  def eval(state: StockExchangeState): StockExchangeState = state.ordersDb match {
    case OrdersDB(buyIndex, _) if buyIndex.isEmpty => state
    case OrdersDB(_, sellIndex) if sellIndex.isEmpty => state
    case OrdersDB(buyIndex, sellIndex) =>
      val candidates = buyIndex.keySet.intersect(sellIndex.keySet)
      if (candidates.isEmpty) state
      else eval(candidates.foldLeft(state) {
        case (oldState, candidate) =>
          balanceBid(oldState, buyIndex.get(candidate).flatMap(_.headOption))
      })
  }
}