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
package org.xcmis.search.lucene.content;

import org.xcmis.search.InvalidQueryException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Error reporter hold all error messages placed by ANTLR lexer and parser.
 * Created by The eXo Platform SAS Author : Sergey Karpenko
 * <sergey.karpenko@exoplatform.com.ua>
 * 
 * @version $Id: ErrorReporterImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ErrorReporterImpl implements ErrorReporter
{

   /**
    * List of a error messages String.
    */
   private final List<String> msgList = new ArrayList<String>();

   /**
    * List of Exceptions.
    */
   private final List<Exception> exceptionsList = new ArrayList<Exception>();

   /**
    * {@inheritDoc}
    */
   public InvalidQueryException getException()
   {

      if (hasErrors())
      {
         StringBuilder b = new StringBuilder("There is errors: \n");
         Iterator<String> it = msgList.iterator();
         while (it.hasNext())
         {
            b.append(it.next() + '\n');
         }

         Iterator<Exception> ei = exceptionsList.iterator();
         while (ei.hasNext())
         {
            b.append(ei.next().getMessage() + '\n');
         }

         InvalidQueryException ex =
            (exceptionsList.size() > 0) ? new InvalidQueryException(b.toString(), exceptionsList.get(0))
               : new InvalidQueryException(b.toString());

         return ex;
      }
      else
      {
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasErrors()
   {
      return (msgList.size() > 0 || exceptionsList.size() > 0);
   }

   /**
    * {@inheritDoc}
    */
   public void reportMessage(String msg)
   {
      msgList.add(msg);
   }

   /**
    * {@inheritDoc}
    */
   public void reportException(Exception e)
   {
      exceptionsList.add(e);
   }

   /**
    * {@inheritDoc}
    */
   public void clear()
   {
      msgList.clear();
      exceptionsList.clear();
   }

}
