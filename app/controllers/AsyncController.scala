package controllers


import java.util.Date
import javax.inject._

import com.redis._
import mq.MQConsumer
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller, WebSocket}
import services.LanguageProcessing
import twitter.{TwitterIngestion, TwitterRESTService, WordCount, WordSentiment}
import twitter4j._

import scala.async.Async.async
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import com.danielasfregola.twitter4s.TwitterClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.neruti.CustomFunctions
import play.api.libs.json.Json
import twitter.WordCount.wordCountWrites


@Singleton
class AsyncController @Inject()(implicit ec: ExecutionContext, ws: WSClient) extends Controller {


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

  def wordCount = Action.async{
    val consumerToken = ConsumerToken("7K1EdywmZVe9KUs0DQnF7Bx8l", "1xx3ec4fkOobiC3n20DFDlHIXkQjChuNmkGXH9oZY8hUqgZ2Rq")
    val accessToken = AccessToken("424248149-N4N9twncOy1k8CDCfZptAxfXT4ZaWOPmEDGaYLrY", "RyTosA9rpE5lIdvpynDbVwSRksqDSDe2dVQytjAft7Vva")
    val client = new TwitterClient(consumerToken, accessToken)
    val service = new TwitterRESTService(client)
    service.getWordCount().map(
      t =>
        {
          val res =CustomFunctions.foldLeftSum(t)
          Ok(Json.obj("tweets"->res.map(sth => new WordCount(sth._1.split(" ")(0),sth._2,sth._1.split(" ")(1)))))}
    )
  }

  def wordSentiment = Action.async{
    val consumerToken = ConsumerToken("7K1EdywmZVe9KUs0DQnF7Bx8l", "1xx3ec4fkOobiC3n20DFDlHIXkQjChuNmkGXH9oZY8hUqgZ2Rq")
    val accessToken = AccessToken("424248149-N4N9twncOy1k8CDCfZptAxfXT4ZaWOPmEDGaYLrY", "RyTosA9rpE5lIdvpynDbVwSRksqDSDe2dVQytjAft7Vva")
    val client = new TwitterClient(consumerToken, accessToken)
    val service = new TwitterRESTService(client)
    service.getWordCount().map(
      t =>
      {
        val res =CustomFunctions.foldLeftSum(t)
        Ok(Json.obj("tweets"->res.map(sth => new WordSentiment(sth._1.split(" ")(0),LanguageProcessing.findSentiment(sth._1.split(" ")(0),LanguageProcessing.initiateSQL(LanguageProcessing.initiateSpark())),sth._1.split(" ")(1)))))}
    )
  }

  def sentenceSentiment = Action.async{
    val consumerToken = ConsumerToken("7K1EdywmZVe9KUs0DQnF7Bx8l", "1xx3ec4fkOobiC3n20DFDlHIXkQjChuNmkGXH9oZY8hUqgZ2Rq")
    val accessToken = AccessToken("424248149-N4N9twncOy1k8CDCfZptAxfXT4ZaWOPmEDGaYLrY", "RyTosA9rpE5lIdvpynDbVwSRksqDSDe2dVQytjAft7Vva")
    val client = new TwitterClient(consumerToken, accessToken)
    val service = new TwitterRESTService(client)
    service.getSentence.map(
      t =>
      {
        Ok(Json.obj("tweets"->t.map(sth => new WordSentiment(sth._1,LanguageProcessing.findSentiment(sth._1,LanguageProcessing.initiateSQL(LanguageProcessing.initiateSpark())),sth._2))))}
    )
  }





}
