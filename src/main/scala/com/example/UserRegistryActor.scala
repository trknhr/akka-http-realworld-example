package com.example

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, Props }

//#user-case-classes
final case class User_(name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User_])
//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User_)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  //インメモリでごにょごにょやってる
  //ここんのアクセスをDBにするイメージでいいのかな
  //マイグレーションはSQLのコマンドファイルを一発容易ですます（めんどくさいので）
  //必要最低限機能だけ容易する
  // 基本的にFunctionalプログラミングに則る(他のリポジトリも見ながら)
  var users = Set.empty[User_]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(name) =>
      sender() ! users.find(_.name == name)
    case DeleteUser(name) =>
      users.find(_.name == name) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User ${name} deleted.")
  }
}
//#user-registry-actor