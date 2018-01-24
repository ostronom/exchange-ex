package com.kkirillov.exchange

import com.kkirillov.exchange.models.{Client, Order, OrderMethod}
import org.scalatest.OptionValues

final class StockExchangeEngineSpec extends CommonSpec with OptionValues {
  behavior of "StockExchangeEngine"

  it should "match exactly correspoding orders" in {
    val order1 = Order(1, "C1", OrderMethod.Sell, "A", 5, 5)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)
    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val result = StockExchangeEngine.eval(ses)

    result.clientsDb.get("C1").value.dollarBalance shouldBe 125
    result.clientsDb.get("C1").value.stockQuantities.get("A").value shouldBe 5

    result.clientsDb.get("C2").value.dollarBalance shouldBe 75
    result.clientsDb.get("C2").value.stockQuantities.get("A").value shouldBe 5

    result.ordersDb.sellIndex shouldBe empty
    result.ordersDb.buyIndex shouldBe empty
  }

  it should "ignore non-matching by method orders" in {
    val order1 = Order(1, "C1", OrderMethod.Buy, "A", 5, 5)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)
    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val result = StockExchangeEngine.eval(ses)

    result shouldBe ses
  }

  it should "ignore non-matching by price orders" in {
    val order1 = Order(1, "C1", OrderMethod.Sell, "A", 6, 5)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)
    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val result = StockExchangeEngine.eval(ses)

    result shouldBe ses
  }

  it should "ignore non-matching by quantity orders" in {
    val order1 = Order(1, "C1", OrderMethod.Sell, "A", 5, 6)
    val order2 = Order(2, "C2", OrderMethod.Buy, "A", 5, 5)
    val ses = StockExchangeState(List(
      Client("C1", 100, Map("A" -> 10, "B" -> 0)),
      Client("C2", 100, Map("A" -> 0, "B" -> 10))
    ), List(order1, order2))

    val result = StockExchangeEngine.eval(ses)

    result shouldBe ses
  }

  it should "process orders" in {
    val clients = List(Client("C1", 1000, Map("A" -> 1, "B" -> 10)), Client("C2", 2000, Map("A" -> 5, "B" -> 1)))
    val orders = List(
      Order(1, "C2", OrderMethod.Buy, "B", 200, 10),
      Order(2, "C1", OrderMethod.Buy, "A", 100, 3),
      Order(3, "C2", OrderMethod.Sell, "A", 100, 3),
      Order(4, "C1", OrderMethod.Sell, "A", 500, 10),
      Order(5, "C1", OrderMethod.Sell, "B", 200, 10)
    )

    val result = StockExchangeEngine.eval(StockExchangeState(clients, orders))

    result.clientsDb.get("C1").value.dollarBalance shouldBe 2700
    result.clientsDb.get("C1").value.stockQuantities.get("A").value shouldBe 4
    result.clientsDb.get("C1").value.stockQuantities.get("B").value shouldBe 0

    result.clientsDb.get("C2").value.dollarBalance shouldBe 300
    result.clientsDb.get("C2").value.stockQuantities.get("A").value shouldBe 2
    result.clientsDb.get("C2").value.stockQuantities.get("B").value shouldBe 11

    result.ordersDb.sellIndex should have size 1
    result.ordersDb.sellIndex.headOption.value._2 should have size 1
    result.ordersDb.sellIndex.headOption.value._2.headOption.value shouldBe Order(4, "C1", OrderMethod.Sell, "A", 500, 10)

  }
}
