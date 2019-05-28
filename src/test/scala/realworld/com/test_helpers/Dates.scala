package realworld.com.test_helpers

import java.sql.Timestamp
import java.util.Date

object Dates {
  def currentWhenInserting = new Timestamp((new Date).getTime)
}
