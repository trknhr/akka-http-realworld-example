package realworld.com.utils

import scala.concurrent.{ ExecutionContext, Future }

case class FutureOptional[+A](future: Future[Option[A]]) extends AnyVal {
  def flatMap[B](f: A => FutureOptional[B])(
    implicit
    ec: ExecutionContext): FutureOptional[B] = {
    val newFuture = future.flatMap {
      case Some(a) => f(a).future
      case None => Future.successful(None)
    }
    FutureOptional(newFuture)
  }

  def map[B](f: A => B)(implicit ec: ExecutionContext): FutureOptional[B] = {
    FutureOptional(future.map(option => option map f))
  }
}
