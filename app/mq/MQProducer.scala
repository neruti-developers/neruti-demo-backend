package mq

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import twitter4j.Status
import com.rabbitmq.client.{Channel, ConnectionFactory}


/**
  * Created by root on 2/21/17.
  */
object MQProducer {

  private val EXCHANGE_TWITTER = "twitter_exchange"

  def initiateRabbitMQProducer:Channel = {
    val factory = new ConnectionFactory
    factory.setHost("localhost")
    val connection = factory.newConnection
    val channel = connection.createChannel()
    channel.exchangeDeclare(EXCHANGE_TWITTER, "topic")
    channel
  }

  def produce(channel: Channel,topic:String ,status:Status): Unit ={
    println(status.getText)
    channel.basicPublish(EXCHANGE_TWITTER,topic,null,serialise(status))
  }


  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    stream.toByteArray
  }

}
