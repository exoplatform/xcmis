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

package org.xcmis.core.impl.object;

import junit.framework.TestCase;

import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.spi.FilterNotValidException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RenditionFilterTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class RenditionFilterTest extends TestCase
{

   public void testGetAllRenditionsWithKind() throws Exception
   {
      String filterString = "*";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      toCheck.setKind("" + System.currentTimeMillis());
      assertTrue(filter.accept(toCheck));
      filterString = "   * \t";
      filter = new RenditionFilter(filterString);
      assertTrue(filter.accept(toCheck));
   }

   public void testGetAllRenditionsWithMimeType() throws Exception
   {
      String filterString = "*";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      toCheck.setMimetype(System.currentTimeMillis() + "/" + System.currentTimeMillis());
      assertTrue(filter.accept(toCheck));
      filterString = "   * \t";
      filter = new RenditionFilter(filterString);
      assertTrue(filter.accept(toCheck));
   }

   public void testGetAllRenditionsDefault() throws Exception
   {
      String filterString = "*";
      RenditionFilter filter = new RenditionFilter(filterString);
      // Do not set any attributes. 
      CmisRenditionType toCheck = new CmisRenditionType();
      assertTrue(filter.accept(toCheck));
      filterString = "   * \t";
      filter = new RenditionFilter(filterString);
      assertTrue(filter.accept(toCheck));
   }

   public void testGetRenditionsWithKind() throws Exception
   {
      String filterString = "     cmis:thumbnail \t ";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      toCheck.setKind("" + System.currentTimeMillis());
      assertFalse(filter.accept(toCheck));
      toCheck.setKind("cmis:thumbnail");
      assertTrue(filter.accept(toCheck));
   }

   public void testGetRenditionsWithMimeType() throws Exception
   {
      String filterString = " \t image/* ";
      RenditionFilter filter = new RenditionFilter(filterString);

      CmisRenditionType toCheck = new CmisRenditionType();

      toCheck.setMimetype(System.currentTimeMillis() + "/" + System.currentTimeMillis());
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("image/png");
      assertTrue(filter.accept(toCheck));

      toCheck.setMimetype("image/*");
      assertTrue(filter.accept(toCheck));

      filterString = " \t image/png ";
      filter = new RenditionFilter(filterString);
      // image/png is not compatible with image/*
      assertFalse(filter.accept(toCheck));
   }

   public void testExcludeAllRenditions() throws Exception
   {
      String filterString = " \t cmis:none ";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype(System.currentTimeMillis() + "/" + System.currentTimeMillis());
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("image/png");
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("image/*");
      assertFalse(filter.accept(toCheck));
   }

   public void testMultiKindRenditions() throws Exception
   {
      String filterString = " kind1 , kind2, kind3 ";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      assertFalse(filter.accept(toCheck));

      toCheck.setKind("kind1");
      assertTrue(filter.accept(toCheck));

      toCheck.setKind("kind2");
      assertTrue(filter.accept(toCheck));

      toCheck.setKind("kind3");
      assertTrue(filter.accept(toCheck));
   }

   public void testMultiMimetypeRenditions() throws Exception
   {
      String filterString = " text/*, image/png ";
      RenditionFilter filter = new RenditionFilter(filterString);
      CmisRenditionType toCheck = new CmisRenditionType();
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("image/png");
      assertTrue(filter.accept(toCheck));

      toCheck.setMimetype("image/jpeg");
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("image/*");
      assertFalse(filter.accept(toCheck));

      toCheck.setMimetype("text/*");
      assertTrue(filter.accept(toCheck));

      toCheck.setMimetype("text/plain");
      assertTrue(filter.accept(toCheck));

      toCheck.setMimetype("text/xml");
      assertTrue(filter.accept(toCheck));

      toCheck.setMimetype("application/xml");
      assertFalse(filter.accept(toCheck));
   }

   public void testIllegalCharacter1() throws Exception
   {
      String filterString = " \t cmis:thumbnail, * ";
      try
      {
         new RenditionFilter(filterString);
         fail("FilterNotValidException must be thrown.");
      }
      catch (FilterNotValidException fnv)
      {
      }
   }

   public void testIllegalCharacter2() throws Exception
   {
      String filterString = " \t cmis:thumbnail, , cmis:thumbnail2 ";
      try
      {
         new RenditionFilter(filterString);
         fail("FilterNotValidException must be thrown.");
      }
      catch (FilterNotValidException fnv)
      {
      }
   }

   public void testIllegalCharacter3() throws Exception
   {
      String filterString = " \t cmis:t humbnail, cmis:thumbnail2 ";
      try
      {
         new RenditionFilter(filterString);
         fail("FilterNotValidException must be thrown.");
      }
      catch (FilterNotValidException fnv)
      {
      }
   }

}
