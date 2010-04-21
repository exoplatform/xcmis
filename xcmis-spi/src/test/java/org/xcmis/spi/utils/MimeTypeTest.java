/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.spi.utils;

import junit.framework.TestCase;

import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class MimeTypeTest extends TestCase
{

   public void testWithoutParameters() throws Exception
   {
      String mime = "text/plain";
      MimeType mt = MimeType.fromString(mime);
      assertEquals("text", mt.getType());
      assertEquals("plain", mt.getSubType());
   }

   public void testWithParameters() throws Exception
   {
      String mime = "text/plain; \t\tcharset \t=UTF-8;   foo=\"bar\"; fooo=\"ba r\"";
      MimeType mt = MimeType.fromString(mime);
      assertEquals("text", mt.getType());
      assertEquals("plain", mt.getSubType());
      Map<String, String> params = mt.getParameters();
      assertEquals("UTF-8", params.get("charset"));
      assertEquals("bar", params.get("foo"));
      assertEquals("ba r", params.get("fooo"));
   }
}
