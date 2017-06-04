package twitter

import java.io.DataOutputStream
import java.net.{ServerSocket, Socket}

import play.api.libs.json.Json
import services.LanguageProcessing
import twitter4j._
import mq.{MQConsumer, MQProducer}

/**
  * Created by root on 2/21/17.
  */
object TwitterIngestion {
  var channel =MQProducer.initiateRabbitMQProducer
  def ingestTwitterStream(topic: String): TwitterStream ={
    val util = new Util(topic)
    val twitterStream = new TwitterStreamFactory(util.config).getInstance
    twitterStream.addListener(util.simpleStatusListener)
    val statuses = twitterStream.filter(new FilterQuery().track(Array(topic)))
    return twitterStream
  }

  def stopConnection(twitterStream: TwitterStream): Unit ={
    twitterStream.cleanUp()
    twitterStream.shutdown()
  }

  class Util(topic:String){
    val config = new twitter4j.conf.ConfigurationBuilder()
      .setOAuthConsumerKey("7K1EdywmZVe9KUs0DQnF7Bx8l")
      .setOAuthConsumerSecret("1xx3ec4fkOobiC3n20DFDlHIXkQjChuNmkGXH9oZY8hUqgZ2Rq")
      .setOAuthAccessToken("424248149-N4N9twncOy1k8CDCfZptAxfXT4ZaWOPmEDGaYLrY")
      .setOAuthAccessTokenSecret("RyTosA9rpE5lIdvpynDbVwSRksqDSDe2dVQytjAft7Vva")
      .build()

    def simpleStatusListener = new StatusListener() {
      def onStatus(status: Status) {
        MQProducer.produce(channel,topic,status)
      }

      def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

      def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

      def onException(ex: Exception) {
        ex.printStackTrace
      }

      def onScrubGeo(arg0: Long, arg1: Long) {}

      def onStallWarning(warning: StallWarning) {}
    }
  }



}
