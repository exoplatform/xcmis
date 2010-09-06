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
 * Should be thrown for any others errors that are not expressible by another
 * CMIS exception. Used as base class for any CMIS unchecked exceptions.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: CmisRuntimeException.java 232 2010-02-18 10:27:51Z andrew00x $
 */
public class CmisRuntimeException extends RuntimeException
{
   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 2382756988364004750L;

   /**
    * Construct instance <tt>CmisRuntimeException</tt> without message.
    */
   public CmisRuntimeException()
   {
      super();
   }

   /**
    * Construct instance <tt>CmisRuntimeException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public CmisRuntimeException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>CmisRuntimeException</tt> with message and cause
    * exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public CmisRuntimeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>CmisRuntimeException</tt> with cause exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public CmisRuntimeException(Throwable cause)
   {
      super(cause);
   }

}
