//package com.cacuna.kafka.models
//
//import java.text.NumberFormat
//
//
//object ShareVolume {
//  def sum(csv1: ShareVolume, csv2: ShareVolume): ShareVolume = {
//    csv1.copy(shares = csv1.shares + csv2.shares)
//  }
//
//  def newBuilder = new ShareVolume.Builder
//
////  def newBuilder(stockTransaction: StockTransaction): ShareVolume.Builder = {
////    val builder = new ShareVolume.Builder
////    builder.symbol = stockTransaction.getSymbol
////    builder.shares = stockTransaction.getShares
////    builder.industry = stockTransaction.getIndustry
////    builder
////  }
//
//  def newBuilder(copy: ShareVolume): ShareVolume.Builder = {
//    copy.copy()
//  }
//
//  class Builder (symbol: Option[String] = None, shares: Int = 0, industry: Option[String] = None) {
//    def apply(): ShareVolume = {
//      ShareVolume(symbol, shares, industry)
//    }
//  }
//
//}
//
//case class ShareVolume (symbol: Option[String] = None, shares: Int = 0, industry: Option[String] = None) {
//  override def toString: String = {
//    val numberFormat = NumberFormat.getInstance
//    "ShareVolume{" + "symbol='" + symbol + '\'' + ", shares=" + numberFormat.format(shares) + '}'
//  }
//
//  def getShares = {
//    shares
//  }
//}
