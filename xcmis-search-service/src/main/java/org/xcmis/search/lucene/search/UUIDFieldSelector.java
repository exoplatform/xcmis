/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.lucene.search;

import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.spi.utils.Logger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: UUIDFieldSelector.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class UUIDFieldSelector implements FieldSelector
{
   
   /** The serialVersionUID. */
   private static final long serialVersionUID = 4682829486158459956L;

   /**
    * Class logger.
    */
   private final Logger log = Logger.getLogger(getClass().getName());

   /**
    * {@inheritDoc}
    */
   public FieldSelectorResult accept(String fieldName)
   {
      if (fieldName.equals(FieldNames.UUID))
      {
         return FieldSelectorResult.LOAD_AND_BREAK;
      }
      return FieldSelectorResult.NO_LOAD;
   }
}
