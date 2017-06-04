# NerutiDemo (Backend) - Sentiment Analysis Map Visualisation (SAMV)
>   by Neruti Developers

This application(SAMV) is for demo purpose only due to limited capabilities in the following areas:

  - Word Standardization 
  - Language Detection
  - Model is pretrained using Stanford Sentiment Treebank
  - Lemmatization
  - Removing not usable symbols 

# Version

  - 12 April 2017 - The first version pushed - 1.0


### Installation of Backend

SAMV requires a few packages to run and it uses Play Framework as our backend technology (API).
1.  [Java+Scala](https://www.scala-lang.org/download/) 
2.  [sbt](http://www.scala-sbt.org/) 
3.  [RabbitMQ](https://www.rabbitmq.com/download.html) 
4.  [Redis](https://redis.io/) 
5.  [Spark](http://spark.apache.org/) 

The demo is tested on ***Ubuntu 16.04*** Platform.

Clone/ Fork this repo and get started

You will need to go to app>twitter>TwitterIngestion to use your Twitter OAuthToken and Credential

```sh
$ cd nrt-demo-backend
$ sbt run
```

Run frontend repo until Spark Session has finished initializing.
You can monitor MQ and Spark at its monitoring port, refer to Google/contributors if unclear.

### Frontend
Please visit [SAMV FrontEnd Repo](https://github.com/neruti-developers/neruti-demo-frontend) for more.

# Bugs
As this is a demo for Neruti Developers and fixing bugs are not our priority in this repo.
  - Stopping Twitter Stream, SparkSession may not execute
  - Only able to open one instance/tab to query one search term at a time. This is due to sharing of Twitter application 
  key/OAuth Key, this can be fixed by allowing user to submit their own OAuth key in frontend and every submission of 
  query will call TwitterUtil again
  - Unable to catch exception in frontend connection if backend connection has been closed

### Contributor List
*Austin Goh*  - austin@neruti.com 

---
***Disclaimer*** *This repo is provided as it-is.  No technical suppoort will be provided.  If you require commercial 
technical support, please contact info@neruti.com*

Enjoy, 
*Austin (zhao yang)*