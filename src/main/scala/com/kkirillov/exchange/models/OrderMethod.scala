package com.kkirillov.exchange.models

sealed trait OrderMethod

object OrderMethod {
  case object Sell extends OrderMethod
  case object Buy extends OrderMethod
}
