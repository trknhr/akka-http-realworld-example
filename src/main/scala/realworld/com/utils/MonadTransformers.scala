package realworld.com.utils

import scala.concurrent.{ ExecutionContext, Future }

object MonadTransformers {
  implicit class FutureOptionMonadTransformer[A](t: Future[Option[A]])(
      implicit
      executionContext: ExecutionContext
  ) {
    def filterT(f: A => Boolean): Future[Option[A]] =
      t.map {
        case Some(data) if f(data) =>
          Some(data)
        case _ =>
          None
      }

    def mapT[B](f: A => B): Future[Option[B]] =
      t.map(_.map(f))

    def flatMapTFuture[B](f: A => Future[B]): Future[Option[B]] =
      t.flatMap {
        case Some(data) =>
          f(data).map(Some(_))
        case None =>
          Future.successful(None)
      }
  }
}
