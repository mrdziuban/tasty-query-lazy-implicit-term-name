package example

trait Foo[A, B]
object Foo {
  given inverse[A, B](using s: => Foo[B, A]): Foo[A, B] = new Foo[A, B] {}
}

object Test {
  val foo = summon[Foo[Int, String]]
}
