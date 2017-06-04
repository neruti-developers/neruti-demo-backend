package com.neruti
/**
  * Created by austin on 03/06/2017.
  */
object CustomFunctions {

  def foldLeftSum[A](tuples: List[(A, Int)]) = tuples.foldLeft(Map.empty[A, Int])({
    case (acc, (k, v)) => acc + (k -> (v + acc.get(k).getOrElse(0)))
  }).toList



}
