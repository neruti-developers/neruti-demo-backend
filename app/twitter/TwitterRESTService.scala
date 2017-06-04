package twitter
import scala.concurrent.ExecutionContext.Implicits.global
import com.danielasfregola.twitter4s.TwitterClient
import com.neruti.CustomFunctions

import scala.concurrent.Future
/**
  * Created by austin on 02/06/2017.
  */
class TwitterRESTService(twitterClient: TwitterClient) {

  def getWordCount():Future[List[(String,Int)]]= {

    twitterClient.searchTweet(query = "tn2050 OR transformasi nasional 2050 OR #tn2050",count = 100).map(
      search=> search.statuses.map(
        tweet => {
          val place = tweet.place.getOrElse(null)
          if(place!=null) {
            tweet.text.split(" ").map(word => (word+" "+tweet.place.getOrElse(null).full_name,1))
          }
          else {
            tweet.text.split(" ").map(word => (word+" no-location",1))
          }

        }
      ).flatten
    )
  }

  def getSentence:Future[List[(String,String)]]= {
    twitterClient.searchTweet(query = "tn2050 OR transformasi nasional 2050 OR #tn2050",count = 100).map(
      search=> search.statuses.map(
        tweet => {
          val place = tweet.place.getOrElse(null)
          if(place!=null) {
            (tweet.text, place.full_name)
          }
          else {
            (tweet.text,"no location")
          }
        }
      )
    )
  }

}
