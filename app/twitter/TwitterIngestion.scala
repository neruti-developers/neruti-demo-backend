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
      .setOAuthConsumerKey("")
      .setOAuthConsumerSecret("")
      .setOAuthAccessToken("")
      .setOAuthAccessTokenSecret("")
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
