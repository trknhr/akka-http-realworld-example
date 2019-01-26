package realworld.com.utils

trait Monad[T[_]] {
  def map[A, B](value: T[A])(f: A => B): T[B]

  def flatMap[A, B](value: T[A])(f: A => T[B]): T[B]

  def pure[A](x: A): T[A]
}

