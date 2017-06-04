package mq

import java.io._
import java.net.{ServerSocket, Socket}

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client._
import org.mortbay.util.ajax.JSONPojoConvertor
import play.api.libs.json.Json
import services.LanguageProcessing
import twitter4j.Status
import com.redis._
import org.apache.spark.sql.SQLContext



/**
  * Created by root on 2/21/17.
  */
object MQConsumer  {

  private val EXCHANGE_TWITTER = "twitter_exchange"

  def consume(sqlContext: SQLContext, topic: String): Unit= {
    val connectionFactory = new ConnectionFactory
    connectionFactory.setHost("localhost")
    val connection = connectionFactory.newConnection
    val channel = connection.createChannel
    channel.exchangeDeclare(EXCHANGE_TWITTER, "topic")

    channel.queueDeclare(topic, true, false, false, null);

    channel.queueBind(topic, EXCHANGE_TWITTER, topic)

    val r = new RedisClient("localhost", 6379)

    val consumer_sentiment = new DefaultConsumer(channel) {
      @throws[IOException]
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]) {

        val output = deserialise(body).asInstanceOf[Status]
        val sentiment = LanguageProcessing.findSentiment(output.getText,sqlContext)
        val mapper = new ObjectMapper()
        val jsonMessageInString = mapper.writeValueAsString(output)
        val jsonSentimentInString = mapper.writeValueAsString(sentiment)

        val jsonobj = Json.obj("status"->Json.parse(jsonMessageInString),"sentiment"->jsonSentimentInString)
        r.lpush(topic,Json.stringify(jsonobj))
      }
    }

    channel.basicConsume(topic, true, consumer_sentiment)
  }

  def deserialise(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close
    value
  }


}
