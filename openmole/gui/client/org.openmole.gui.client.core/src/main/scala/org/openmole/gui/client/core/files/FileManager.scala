package org.openmole.gui.client.core.files

import org.scalajs.dom.raw._
import FileExtension._

/*
 * Copyright (C) 29/04/15 // mathieu.leclaire@openmole.org
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

sealed trait FileTransferState {
  def ratio: Int = 0

  def display: String = ""
}

case class Transfering(override val ratio: Int) extends FileTransferState {
  override def display: String = "Transferring... " + ratio + " %"
}

case class Finalizing(override val ratio: Int = 100,
                      override val display: String = "Finalizing...") extends FileTransferState

case class Transfered(override val ratio: Int = 100) extends FileTransferState

case class Standby() extends FileTransferState

object FileManager {

  def fileNames(fileList: FileList): Seq[String] = {
    var nameList = Seq[String]()
    for (i ← 0 to fileList.length - 1) {
      nameList = nameList :+ fileList(i).name
    }
    nameList
  }

  def upload(fileList: FileList,
             destinationPath: String,
             fileTransferState: FileTransferState ⇒ Unit) = {
    var formData = new FormData

    for (i ← 0 to fileList.length - 1) {
      val file = fileList(i)
      formData.append(destinationPath + "/" + file.name, file)
    }

    val xhr = new XMLHttpRequest

    xhr.upload.onprogress = (e: ProgressEvent) ⇒ {
      fileTransferState(Transfering((e.loaded.toDouble * 100 / e.total).toInt))
    }

    xhr.upload.onloadend = (e: ProgressEvent) ⇒ {
      fileTransferState(Finalizing())
    }

    xhr.onloadend = (e: ProgressEvent) ⇒ {
      fileTransferState(Transfered())
    }

    xhr.open("POST", "uploadfiles", true)
    xhr.send(formData)
  }

  def download(treeNode: TreeNode,
               saveFile: Boolean,
               fileTransferState: FileTransferState ⇒ Unit,
               onLoadEnded: String ⇒ Unit) = {

    val (fileName, fileType) = FileExtension(treeNode)

    val formData = new FormData
    formData.append("path", treeNode.canonicalPath())
    formData.append("saveFile", saveFile)
    val xhr = new XMLHttpRequest

    xhr.onprogress = (e: ProgressEvent) ⇒ {
      fileTransferState(Transfering((e.loaded.toDouble * 100 / treeNode.size).toInt))
    }

    xhr.onloadend = (e: ProgressEvent) ⇒ {
      fileTransferState(Transfered())
      fileType match {
        case df: DisplayableFile ⇒ onLoadEnded(xhr.responseText)
        case _                   ⇒
      }
    }

    xhr.open("POST", "downloadedfiles", true)

    fileType match {
      case df: DisplayableFile ⇒
      case _ ⇒ {
        println("blob !!!")
        xhr.responseType = "blob"
      }
    }

    xhr.send(formData)
  }

}

