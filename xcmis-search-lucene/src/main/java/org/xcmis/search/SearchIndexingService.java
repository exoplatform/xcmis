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
package org.xcmis.search;

import org.xcmis.search.index.IndexException;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 * @param <Q>
 */
public interface SearchIndexingService<Q> extends IndexingService
{

   /**
    * A string constant representing the JCR-JQOM query language.
    * 
    * @since JCR 2.0
    */
   public static final String JCR_JQOM = "JCR-JQOM";

   /**
    * A string constant representing the JCR-SQL2 query language.
    * 
    * @since JCR 2.0
    */
   public static final String JCR_SQL2 = "JCR-SQL2";

   /**
    * Return query builder.
    * 
    * @return
    * @throws IndexException
    */
   NativeQueryBuilder<Q> getQueryBuilder(String language) throws IndexException;

   /**
    * @param nativeQuery
    * @return
    * @throws IOException
    */
   List<ScoredRow> search(SingleSourceNativeQuery<Q> nativeQuery) throws IndexException;

}
