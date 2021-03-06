/*
 * Copyright (C) 2011 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.tools.io

import java.io.File
import FileUtil._
import org.openmole.tool.tar.TarOutputStream
import org.scalatest.FlatSpec
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.io.FileOutputStream
import java.io.FileWriter
import java.nio.file.FileSystems
import java.nio.file.Files
import scala.io.Source
import FileUtil._

class TarArchiverSpec extends FlatSpec with Matchers {
  "Archive" should "preserve symbolic links" in {
    val tmpDir = Files.createTempDirectory("testArch").toFile
    val file = new File(tmpDir, "file")
    file.createNewFile

    val fs = FileSystems.getDefault

    Files.createSymbolicLink(fs.getPath(tmpDir.getAbsolutePath, "link"), fs.getPath("file"))
    // Files.createSymbolicLink(fs.getPath(tmpDir.getAbsolutePath, "linkNoDest"), fs.getPath(file.getAbsolutePath))

    val archive = Files.createTempFile("archiveTest", ".tar").toFile

    archive.archiveDirWithRelativePathNoVariableContent(tmpDir)
    val extractDir = Files.createTempDirectory("testArchExtract").toFile
    archive.extractDirArchiveWithRelativePath(extractDir)

    Files.isSymbolicLink(fs.getPath(extractDir.getAbsolutePath, "link")) should equal(true)
    Files.isSameFile(fs.getPath(extractDir.getAbsolutePath, "link").toRealPath(), fs.getPath(extractDir.getAbsolutePath, "file")) should equal(true)

    extractDir.recursiveDelete
    archive.delete
    tmpDir.recursiveDelete
  }

  "Archive" should "preserve file permissions" in {
    val tmpDir = Files.createTempDirectory("testArch").toFile
    val file1 = new File(tmpDir, "file1")
    val file2 = new File(tmpDir, "file2")
    val file3 = new File(tmpDir, "category/file1")
    file3.getParentFile.mkdirs

    file1.createNewFile
    file2.createNewFile
    file3.createNewFile

    file1.setExecutable(true)
    file1.setReadable(true)
    file1.setWritable(true)

    file2.setExecutable(false)
    file2.setReadable(true)
    file2.setWritable(false)

    file3.setExecutable(true)
    file3.setReadable(true)
    file3.setWritable(true)

    val archive = Files.createTempFile("archiveTest", ".tar").toFile
    archive.archiveDirWithRelativePathNoVariableContent(tmpDir)

    val extractDir = Files.createTempDirectory("testArchExtract").toFile
    archive.extractDirArchiveWithRelativePath(extractDir)

    val file1Arch = new File(extractDir, "file1")
    val file2Arch = new File(extractDir, "file2")
    val file3Arch = new File(extractDir, "category/file1")

    file1Arch.canExecute should equal(true)
    file1Arch.canRead should equal(true)
    file1Arch.canWrite should equal(true)

    file2Arch.canExecute should equal(false)
    file2Arch.canRead should equal(true)
    file2Arch.canWrite should equal(false)

    file3Arch.canExecute should equal(true)
    file3Arch.canRead should equal(true)
    file3Arch.canWrite should equal(true)

    tmpDir.recursiveDelete
    extractDir.recursiveDelete
  }

  "Archive addFile method" should "preserve file permissions" in {
    val file1 = Files.createTempFile("testArch", ".tmp").toFile

    file1.setExecutable(true)
    file1.setReadable(true)
    file1.setWritable(false)

    val archive = Files.createTempFile("archiveTest", ".tar").toFile
    val tos = new TarOutputStream(new FileOutputStream(archive))
    tos.addFile(file1, file1.getName)
    tos.close

    val extractDir = Files.createTempDirectory("testArchExtract").toFile
    archive.extractDirArchiveWithRelativePath(extractDir)

    val extracted = new File(extractDir, file1.getName)
    extracted.canExecute should equal(true)
    extracted.canRead should equal(true)
    extracted.canWrite should equal(false)

    file1.delete
    extractDir.recursiveDelete
  }

}
