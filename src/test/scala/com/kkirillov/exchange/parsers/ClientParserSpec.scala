package com.kkirillov.exchange.parsers

import com.kkirillov.exchange.CommonSpec
import com.kkirillov.exchange.models.Client

final class ClientParserSpec extends CommonSpec {
  behavior of "ClientParser"

  it should "parse client" in {
    ClientParser.parse("C1\t1234\t1\t2\t3\t4") shouldBe Client("C1", 1234, Map("A" -> 1, "B" -> 2, "C" -> 3, "D" -> 4))
  }
}
