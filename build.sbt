lazy val root = project.in(file("."))
  .settings(
    name := "tasty-query-lazy-implicit-term-name",
    organization := "com.example",
    scalaVersion := "3.6.3",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "ch.epfl.scala" %% "tasty-query" % "1.5.0" % Test,
    ),
    Test / sourceGenerators += Def.task {
      val file = (Test / sourceManaged).value / "Classpath.scala"
      val paths = (Runtime / fullClasspath).value.map(_.data)
      IO.write(
        file,
        s"""
        |package example
        |
        |import java.nio.file.Paths
        |
        |object Classpath {
        |  val paths = List(
        |    ${paths.map(p => s"""Paths.get("$p")""").mkString(",\n    ")}
        |  )
        |}
        |""".stripMargin.trim
      )
      Seq(file)
    },
  )
