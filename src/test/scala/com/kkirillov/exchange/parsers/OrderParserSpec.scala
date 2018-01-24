package com.kkirillov.exchange.parsers

import com.kkirillov.exchange.CommonSpec
import com.kkirillov.exchange.models.{Order, OrderMethod}

final class OrderParserSpec extends CommonSpec {
  behavior of "OrderParser"

  it should "parse order" in {
    OrderParser.parse("C1\ts\tC2\t1\t2", 1) shouldBe Order(1, "C1", OrderMethod.Sell, "C2", 1, 2)
    OrderParser.parse("C1\tb\tC2\t1\t2", 1) shouldBe Order(1, "C1", OrderMethod.Buy, "C2", 1, 2)
  }
}
