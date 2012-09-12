/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.xcmis.sp.inmemory.query;

import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.utils.MimeType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class TestQueryWithQueryNames extends BaseQueryTest
{
   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      propertyDefinitions.put("cmis:team-rating", new PropertyDefinition<String>("cmis:team-rating",
         "RATING", "cmis:team-rating", null, "cmis:team-rating", "cmis:team-rating",
         PropertyType.DECIMAL, Updatability.READWRITE, true, false, false, false, false, true, null, null));
      storage.addType(new TypeDefinition("cmis:article-football", BaseType.DOCUMENT, "FOOTBALL",
         "cmis:article-football", "", "cmis:article-sports", "cmis:article-football", "cmis article about football",
         true, true, true, true, true, true, true, true, null, null,
         ContentStreamAllowed.ALLOWED, propertyDefinitions));
   }

   public void testQueryNames() throws Exception
   {
      DocumentData mu = createDocument((FolderData)storage.getObjectByPath("/"),
         "MU", storage.getTypeDefinition("cmis:article-football", true), new byte[0], new MimeType());
      mu.setProperty(new DecimalProperty("cmis:team-rating", "RATING", "cmis:team-rating", "cmis:team-rating",
         BigDecimal.valueOf(9.9)));
      DocumentData bfc = createDocument((FolderData)storage.getObjectByPath("/"),
         "BFC", storage.getTypeDefinition("cmis:article-football", true), new byte[0], new MimeType());
      bfc.setProperty(new DecimalProperty("cmis:team-rating", "RATING", "cmis:team-rating", "cmis:team-rating",
         BigDecimal.valueOf(7.5)));
      String sql = "SELECT * FROM FOOTBALL WHERE RATING > 9";
      checkResult(sql, new ObjectData[]{mu});
   }
}
