package com.kkirillov.exchange

import com.kkirillov.exchange.models.{Client, Order, OrderMethod}
import org.scalatest.OptionValues

final class StockExchangeStateSpec extends CommonSpec with OptionValues {
  behavior of "StockExchangeState"

  it should "drop orders" in {
    val order1 = Order(1, "C1", OrderMethod.Sell, "A", 5, 5)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)
    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val nses = ses.dropOrder(Order(1, "C1", OrderMethod.Sell, "A", 5, 5))

    nses.ordersDb.sellIndex.values.flatten shouldNot contain(order1)
    nses.ordersDb.buyIndex.values.flatten should contain(order2)
  }

  it should "perform transaction" in {
    val order1 = Order(1, "C1", OrderMethod.Sell, "A", 5, 5)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)

    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val nses = ses.transact(order2, order1)

    nses.ordersDb.buyIndex shouldBe empty
    nses.ordersDb.sellIndex shouldBe empty

    nses.clientsDb.get("C1").value.dollarBalance shouldBe 125
    nses.clientsDb.get("C2").value.dollarBalance shouldBe 75

    nses.clientsDb.get("C1").value.stockQuantities.get("A").value shouldBe 5
    nses.clientsDb.get("C2").value.stockQuantities.get("A").value shouldBe 5
  }
}
