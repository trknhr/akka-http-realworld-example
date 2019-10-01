package realworld.com.utils

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

case class ISO8601()
object ISO8601 {
  val millisFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  def apply(s: String) =
    new SimpleDateFormat(millisFormatString).parse(s)

  def apply(d: Date) =
    new SimpleDateFormat(millisFormatString) {
      setTimeZone(TimeZone.getTimeZone("UTC"))
    }.format(d)
}
