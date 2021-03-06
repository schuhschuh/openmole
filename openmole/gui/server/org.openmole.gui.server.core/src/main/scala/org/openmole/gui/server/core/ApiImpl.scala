package org.openmole.gui.server.core

import org.openmole.gui.misc.utils.Utils._
import org.openmole.tool.file._
import org.openmole.core.workspace.Workspace
import org.openmole.gui.shared._
import org.openmole.gui.ext.data.{ ScriptData, TreeNodeData }
import java.io.{ File, PrintStream }
import java.nio.file._
import org.openmole.console._
import scala.util.{ Failure, Success, Try }
import org.openmole.gui.ext.data._
import org.openmole.console.ConsoleVariables
import org.openmole.core.workflow.mole.{ MoleExecution, ExecutionContext }
import org.openmole.core.workflow.puzzle.Puzzle

/*
 * Copyright (C) 21/07/14 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object ApiImpl extends Api {

  // FILES
  def addDirectory(treeNodeData: TreeNodeData, directoryName: String): Boolean = new File(treeNodeData.canonicalPath, directoryName).mkdirs

  def addFile(treeNodeData: TreeNodeData, fileName: String): Boolean = new File(treeNodeData.canonicalPath, fileName).createNewFile

  def deleteFile(treeNodeData: TreeNodeData): Unit = new File(treeNodeData.canonicalPath).recursiveDelete

  def fileSize(treeNodeData: TreeNodeData): Long = new File(treeNodeData.canonicalPath).length

  def listFiles(tnd: TreeNodeData): Seq[TreeNodeData] = Utils.listFiles(tnd.canonicalPath)

  def renameFile(treeNodeData: TreeNodeData, name: String): Boolean = {
    val canonicalFile = new File(treeNodeData.canonicalPath)
    val (source, target) = (canonicalFile, new File(canonicalFile.getParent, name))

    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
    target.exists
  }

  def saveFile(path: String, fileContent: String): Unit = new File(path).content = fileContent

  def workspacePath(): String = Utils.workspaceProjectFile.getCanonicalPath()

  // EXECUTIONS

  def allExecutionStates(): Seq[(ExecutionId, ExecutionInfo)] = Execution.allStates

  def cancelExecution(id: ExecutionId): Unit = Execution.cancel(id)

  def removeExecution(id: ExecutionId): Unit = Execution.remove(id)

  def runScript(scriptData: ScriptData): Unit = {
    val id = getUUID
    val projectsPath = Utils.workspaceProjectFile
    val console = new Console
    val repl = console.newREPL(ConsoleVariables(
      inputDirectory = new File(projectsPath, scriptData.inputDirectory),
      outputDirectory = new File(projectsPath, scriptData.outputDirectory)
    ))

    val execId = ExecutionId(scriptData.scriptName, System.currentTimeMillis, id)
    def error(t: Throwable) = Execution.add(execId, Failed(ErrorBuilder(t)))

    Try(repl.eval(scriptData.script)) match {
      case Failure(e) ⇒ error(e)
      case Success(o) ⇒
        o match {
          case puzzle: Puzzle ⇒
            val output = new File(projectsPath, scriptData.output)
            val outputStream = new PrintStream(output.bufferedOutputStream())
            Try(puzzle.toExecution(executionContext = ExecutionContext(out = outputStream))) match {
              case Success(ex) ⇒
                Try(ex.start) match {
                  case Failure(e)  ⇒ error(e)
                  case Success(ex) ⇒ Execution.add(execId.copy(startDate = ex.startTime.get), ex)
                }
              case Failure(e) ⇒ error(e)
            }
          case Failure(e) ⇒ error(e)
        }
    }
  }
}
