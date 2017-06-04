package twitter

import play.api.libs.json.Json


/**
  * Created by austin on 03/06/2017.
  */
case class WordCount(text: String = "",
                     value: Int= 0,
                     location: String = "") {
}

object WordCount {
  implicit val wordCountReads = Json.reads[WordCount]
  implicit val wordCountWrites = Json.writes[WordCount]
  implicit val toJson = Json.format[WordCount]
}
