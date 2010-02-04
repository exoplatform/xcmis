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

package org.xcmis.spi;

/**
 * If there is no permission for operation.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public final class PermissionDeniedException extends RuntimeException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 6697062000543256046L;

   /**
    * Construct instance <tt>PermissionDeniedException</tt> without message.
    */
   public PermissionDeniedException()
   {
      super();
   }

   /**
    * Construct instance <tt>PermissionDeniedException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public PermissionDeniedException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>PermissionDeniedException</tt> with message and cause
    * exception. 
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public PermissionDeniedException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>PermissionDeniedException</tt> with cause exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public PermissionDeniedException(Throwable cause)
   {
      super(cause);
   }

}
