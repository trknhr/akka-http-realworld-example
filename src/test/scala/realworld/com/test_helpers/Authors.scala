package realworld.com.test_helpers

import realworld.com.core.User

object Authors {
  val normalAuthor = User(1,
    "author",
    "password",
    "email",
    None,
    image = None,
    createdAt = Dates.currentWhenInserting,
    updatedAt = Dates.currentWhenInserting)

}
