/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search;

/**
 * General Search service exception. 
 */
public class SearchServiceException extends Exception
{

   /** The  serialVersionUID. */
   private static final long serialVersionUID = 5595901452426227420L;

   /**
    * Constructor.
    */
   public SearchServiceException()
   {
      
   }

   /**
    * @param message String message
    */
   public SearchServiceException(String message)
   {
      super(message);
      
   }

   /**
    * @param cause Throwable cause
    */
   public SearchServiceException(Throwable cause)
   {
      super(cause);
      
   }

   /**
    * @param message String message
    * @param cause Throwable cause
    */
   public SearchServiceException(String message, Throwable cause)
   {
      super(message, cause);
      
   }

}
