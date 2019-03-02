package realworld.com.utils

import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter.SetUnit
import slick.jdbc.meta.{MQName, MTable}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object DatabaseCleaner {

  def cleanDatabase(databaseConnector: DatabaseConnector)(
    implicit
    executionContext: ExecutionContext
  ): Unit = {
    import databaseConnector._
    import databaseConnector.profile.api._

    val truncatesFuture = db
//      val tables = Await.result(db.run(MTable.getTables), 1.seconds).toList

      .run(
        MTable.getTables
//        sql"""SELECT * FROM pg_catalog.pg_tables"""
//        sql"""\dt"""
//        .as[(String, String)]
      )
      .map {
        _.filter{
          case MTable(tableName, "TABLE", _, _, _, _) => true
          case _ => false
        }.map {
          case MTable(tableName, _, _, _, _, _) =>
            println(tableName.name)
            SQLActionBuilder(List(s"SET CONSTRAINTS ALL DEFERRED; ", s"TRUNCATE TABLE ${tableName.name} CASCADE;", s" SET CONSTRAINTS ALL IMMEDIATE;"), SetUnit).asUpdate
        }
      }

    Await.result(
      truncatesFuture.flatMap(
        truncates =>
          db.run(
            DBIO.sequence(
              List(
//              List(sqlu"""ALTER TABLE b DISABLE TRIGGER ALL"""),
              truncates,
//              List(sqlu"""SET FOREIGN_KEY_CHECKS = 1;""")
            ).flatten
            ).transactionally
          )
      ),
      5.seconds
    )
  }

}
//package com.sergigp.horus.infrastructure
//
//import slick.jdbc.SQLActionBuilder
//import slick.jdbc.SetParameter.SetUnit
//
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration._
//
//import play.api.db.slick.DatabaseConfigProvider
//import play.api.inject.Injector

//import slick.driver.JdbcProfile
//import slick.driver.MySQLDriver.api._
//import slick.jdbc.SQLActionBuilder
//import slick.jdbc.SetParameter.SetUnit

//object EnvironmentArranger {
//  def cleanDatabase(injector: Injector): Unit = {
////    val dbConfig = injector.instanceOf[DatabaseConfigProvider].get[JdbcProfile]
//    val truncatesFuture = dbConfig.db.run(
//      sql"""show full tables where Table_Type = "BASE TABLE"""".as[(String, String)]
//    ).map {
//      _.map { case (tableName, _) => SQLActionBuilder(List(s"TRUNCATE TABLE $tableName"), SetUnit).asUpdate }
//    }
//
//    Await.result(truncatesFuture.flatMap(
//      truncates =>
//        dbConfig.db.run(
//          DBIO.sequence(
//            List(
//              List( sqlu"""SET FOREIGN_KEY_CHECKS = 0;"""),
//              truncates,
//              List( sqlu"""SET FOREIGN_KEY_CHECKS = 1;""")
//            ).flatten
//          )
//        )
//    ), 5.seconds)
//  }
//}
