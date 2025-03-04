package example

import java.net.URI
import java.nio.file.FileSystems
import tastyquery.Contexts.Context
import tastyquery.jdk.ClasspathLoaders

object TastyQueryTest {
  val paths = FileSystems.getFileSystem(URI.create("jrt:/")).getPath("modules", "java.base") :: Classpath.paths.toList

  given ctx: Context = Context.initialize(ClasspathLoaders.read(paths))

  def main(args: Array[String]): Unit = println(ctx.findStaticTerm("example.Test.foo"))
}
