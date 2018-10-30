import sbt._
import Keys._

import scala.sys.process._

object BuildInfoGenerator {
  //Defining the new task:
  lazy val generateBuildInfo: TaskKey[Seq[File]] = taskKey[Seq[File]]("Generates BuildInfo")

  lazy val generateBuildInfoTask: Def.Initialize[Task[Seq[String]]] = Def.task {
    val filename: File = (resourceManaged in Compile).value / "demo" / "MyBuildInfo.scala"
    writeFile(filename)
    Seq(filename)
  }

  //Making the task available to the project
  lazy val generatorSettings: Seq[Setting[_]] = Seq(
    generateBuildInfo := generateBuildInfoTask.value, //: Def.Setting[Task[Seq[sbt.File]]]
    sourceGenerators in Compile += generateBuildInfo
  )

  //Other methods for the actual file writing
  private def writeFile(filename: File): Unit = {
    val currentCommit = determineCurrentCommit()
    val contents = fileContentsTemplate.replace("[COMMIT]", currentCommit)
    IO.write(filename, contents)
  }

  private def determineCurrentCommit(): String = "git rev-parse HEAD".!!.trim

  private val fileContentsTemplate: String =
    """
      |package example
      |
      |object BuildInfo {
      |  val gitCommit: String = "[COMMIT]"
      |}
    """.stripMargin
}
