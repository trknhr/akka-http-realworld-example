package realworld.com.test_helpers

import realworld.com.articles.comments.Comment

object Comments {
  val comments = Seq(
    Comment(
      1,
      "first comment",
      2,
      3,
      Dates.currentWhenInserting,
      Dates.currentWhenInserting
    ),
    Comment(
      2,
      "second comment",
      2,
      4,
      Dates.currentWhenInserting,
      Dates.currentWhenInserting
    ),
    Comment(
      3,
      "third comment",
      2,
      5,
      Dates.currentWhenInserting,
      Dates.currentWhenInserting
    )
  )
}
