package com.kkirillov.exchange.models

import com.kkirillov.exchange.CommonSpec
import org.scalatest.OptionValues

final class ClientSpec extends CommonSpec with OptionValues {
  behavior of "Client model"

  it should "correctly handle buying process" in {
    val c = Client("C1", 100, Map("A" -> 10, "B" -> 20)).buy(Order(1, "C2", OrderMethod.Sell, "A", 5, 5))
    c.dollarBalance shouldBe 75
    c.stockQuantities.get("A").value shouldBe 15

    val c2 = c.buy(Order(2, "C2", OrderMethod.Sell, "C", 10, 1))
    c2.dollarBalance shouldBe 65
    c2.stockQuantities.get("C").value shouldBe 1
  }

  it should "correctly handle selling process" in {
    val c = Client("C1", 100, Map("A" -> 10, "B" -> 20)).sell(Order(1, "C2", OrderMethod.Buy, "A", 5, 5))
    c.dollarBalance shouldBe 125
    c.stockQuantities.get("A").value shouldBe 5

    val c2 = c.sell(Order(2, "C2", OrderMethod.Buy, "C", 10, 1))
    c2.dollarBalance shouldBe 135
    c2.stockQuantities.get("C").value shouldBe -1
  }
}
