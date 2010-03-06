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
package org.xcmis.search.index;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndexDataConsumer.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface IndexDataConsumer<R, D, Y>
{
   /**
    * @return index reader.
    * @throws IndexException
    * @Deprecated
    */
   R getIndexReader() throws IndexException;

   /**
    * @return index reader.
    * @throws IndexException
    */
   Y getDirectory() throws IndexException;

   /**
    * @return document
    * @throws IndexException
    */
   D getDocument(String uuid) throws IndexException;

   /**
    * @return size of index directory.
    * @throws IndexException
    */
   public long getDirectorySize(boolean includeInherited);

   /**
    * @return number of documents.
    * @throws IndexException
    */
   public long getDocumentCount();

   /**
    * @return last modification time.
    */
   public long getLastModifedTime();

}
