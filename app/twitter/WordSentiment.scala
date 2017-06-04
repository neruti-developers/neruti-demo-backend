package twitter

import play.api.libs.json.Json

/**
  * Created by austin on 03/06/2017.
  */
case class WordSentiment( word: String = "",
                          sentiment: Int= 0,
                          location: String = "") {

}

object WordSentiment {
  implicit val wordSentimentReads = Json.reads[WordSentiment]
  implicit val wordSentimentWrites = Json.writes[WordSentiment]
  implicit val toJson = Json.format[WordSentiment]
}
