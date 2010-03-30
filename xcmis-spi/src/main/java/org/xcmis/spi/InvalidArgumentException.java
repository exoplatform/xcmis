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
 * Should thrown by CMIS service it got not valid argument.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InvalidArgumentException.java 232 2010-02-18 10:27:51Z andrew00x $
 */
public class InvalidArgumentException extends CmisRuntimeException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 16179547915588323L;

   /**
    * Construct instance <tt>InvalidArgumentException</tt> without message.
    */
   public InvalidArgumentException()
   {
      super();
   }

   /**
    * Construct instance <tt>InvalidArgumentException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public InvalidArgumentException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>InvalidArgumentException</tt> with message and cause
    * exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public InvalidArgumentException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>InvalidArgumentException</tt> with cause exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public InvalidArgumentException(Throwable cause)
   {
      super(cause);
   }

}
