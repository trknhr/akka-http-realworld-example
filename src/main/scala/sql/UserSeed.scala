package sql

import realworld.com.core.User

object UserSeed {
  val userNames = Seq(
    "angel",
    "bubbles",
    "shimmer",
    "angelic",
    "bubbly",
    "glimmer",
    "baby",
    "pink",
    "little",
    "butterfly")
  def data: Seq[User] =
    userNames.zipWithIndex.map {
      case (name, index) =>
        User(
          1,
          name,
          "password",
          s"${name}@test.com",
          None,
          None,
          CreateAllData.currentWhenInserting,
          CreateAllData.currentWhenInserting)
    }
}
