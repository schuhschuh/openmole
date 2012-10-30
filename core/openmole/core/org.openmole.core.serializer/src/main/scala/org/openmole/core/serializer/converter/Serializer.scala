/*
 * Copyright (C) 2011 romain
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
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.serializer.converter

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.SingleValueConverter
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter
import java.io.OutputStream

class Serializer {
  protected val xstream = XStreamFactory()

  protected val reflectionConverter = new ReflectionConverter(xstream.getMapper, xstream.getReflectionProvider)

  def toXML(obj: Object, outputStream: OutputStream) = xstream.toXML(obj, outputStream)

  protected def registerConverter(converter: Converter) = xstream.registerConverter(converter)
  protected def registerConverter(converter: SingleValueConverter) = xstream.registerConverter(converter)

}