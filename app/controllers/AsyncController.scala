package controllers


import java.util.Date
import javax.inject._

import com.redis._
import mq.MQConsumer
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.mvc.{Action, Controller, WebSocket}
import services.LanguageProcessing
import twitter.TwitterIngestion
import twitter4j._

import scala.async.Async.async
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global



@Singleton
class AsyncController @Inject()(implicit ec: ExecutionContext) extends Controller {


  def index = WebSocket.using[String] {
    request => {
      var channel: Option[Concurrent.Channel[String]] = None
      val outEnumerator: Enumerator[String] = Concurrent.unicast(c => channel = Some(c))
      val inIteratee: Iteratee[String, Unit] = Iteratee.foreach[String](receivedString => {
        val r = new RedisClient("localhost", 6379)
        var twitterStream:TwitterStream = null
        val sparkSession = LanguageProcessing.initiateSpark()
        val sQLContext = LanguageProcessing.initiateSQL(sparkSession)
        if(receivedString =="nrt-stop-1989-there-shall-be-no-such-query"){
          TwitterIngestion.stopConnection(twitterStream)
          sparkSession.stop()
        } else {
          try{
            TwitterIngestion.ingestTwitterStream(receivedString)
            MQConsumer.consume(sQLContext,receivedString)
          } catch {
            case e: Exception => e.printStackTrace()
          }
        }


        async{
          while(true){
            channel.foreach(_.push(r.rpop(receivedString).get))
            Thread.sleep(200)
          }
        }

      })
      (inIteratee, outEnumerator)
    }
  }

  def sentimentCheck = Action{
    request =>{
      val data = request.body.asText.get
      val fixed = new String(data.getBytes("Windows-1252"), "UTF-8")
      val sentiment = LanguageProcessing.findSentiment(fixed,LanguageProcessing.initiateSQL(LanguageProcessing.initiateSpark()))
      Ok(String.valueOf(sentiment))
    }
  }


}
