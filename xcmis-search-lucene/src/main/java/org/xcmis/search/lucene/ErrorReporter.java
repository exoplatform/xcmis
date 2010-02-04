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
package org.xcmis.search.lucene;

import javax.jcr.query.InvalidQueryException;

/**
 * Error reporter hold all error messages placed by ANTLR lexer and parser.
 * Created by The eXo Platform SAS Author : Sergey Karpenko
 * <sergey.karpenko@exoplatform.com.ua>
 * 
 * @version $Id$
 */
public interface ErrorReporter
{

   /**
    * Return all error messages as a single InvalidQueryException.
    * 
    * @return InvalidQueryException.
    */
   public InvalidQueryException getException();

   /**
    * Return true if ErrorReporter hold any error message.
    * 
    * @return boolean
    */
   public boolean hasErrors();

   /**
    * Save error message in ErrorReporter.
    * 
    * @param msg Error message.
    */
   public void reportMessage(String msg);

   /**
    * Save exception.
    * 
    * @param e Exception
    */
   public void reportException(Exception e);

   /**
    * Clear all message and exceptions lists.
    */
   public void clear();
}
