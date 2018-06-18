//package common.model
//
//import java.sql.Timestamp
//import java.util.Date
//
//import slick.jdbc.JdbcProfile
//
//import utils.DatabaseConnector
//
//
///**
//  * Created by kunihiro on 2018/06/18.
//  */
//case class Common(updatedAt: Timestamp, createdAt: Timestamp)
//
//trait Commons { this: Table[_] =>
//  protected val databaseConnector: DatabaseConnector
//  import databaseConnector.profile.api._
//
//  def currentWhenInserting = new Timestamp((new Date).getTime)
//
//  def updatedAt = column[Timestamp]("updated_at", O.Default(currentWhenInserting))
//
//  def createdAt = column[Timestamp]("created_at", O.Default(currentWhenInserting))
//
//
//  def common = (updatedAt, createdAt).<> [Meta, (Timestamp, Timestamp)] (
//    r => {
//      val (updatedAt, createdAt) = r
//      Meta(updatedAt, createdAt)
//    },
//    o => Some(o.updatedAt, o.createdAt)
//  )
//
//}
