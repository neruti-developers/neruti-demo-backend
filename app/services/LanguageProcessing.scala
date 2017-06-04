package services
import java.io.{DataOutputStream, StringReader}
import java.net.{ServerSocket, Socket}

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions._
import com.databricks.spark.corenlp.functions._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.streaming._
import twitter4j.Status

import scala.concurrent.ExecutionContext
/**
  * Created by root on 07/04/2017.
  */
object LanguageProcessing {

  def initiateSpark():SparkSession = {
    SparkSession
      .builder()
      .appName("Neruti Demo")
      .master("local[*]")
      .getOrCreate()
  }

  def initiateSQL(sparkSession: SparkSession):SQLContext = {
    new org.apache.spark.sql.SQLContext(sparkSession.sparkContext)
  }

  def findSentiment(line: String,sqlContext:SQLContext): Int = {

    import sqlContext.implicits._
    // Turn input to seq and convert to DataFrame(RDD)
    val input = Seq(
      (1, "<xml>"+line+"</xml>")
    ).toDF("id", "text")
    // Split sentence and perform sentiment analysis
    val output = input
      .select(cleanxml('text).as('doc))
      .select(explode(ssplit('doc)).as('sen))
      .select('sen, sentiment('sen).as('sentiment))
      .collect()
      .map(_.getInt(1))

    // Convert to List
    val list = ArrayUtil.toList(output)

    // Getting the mode
    list.foldLeft(
      Map.empty[Int, Int].withDefaultValue(0), -1 -> Double.NegativeInfinity
    ) {
      case ((m, (maxV, maxCount)), v) =>
        val count = m(v) + 1
        if (count > maxCount) (m.updated(v, count), v -> count)
        else (m.updated(v, count), maxV -> maxCount)
    }._2._1

  }


  object ArrayUtil {
    def toList[a](array: Array[a]): List[a] = {
      if (array == null || array.length == 0) Nil
      else if (array.length == 1) List(array(0))
      else array(0) :: toList(array.slice(1, array.length))
    }
  }

}
