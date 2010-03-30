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
 * If action fails cause to some constraint.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ConstraintException.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public final class ConstraintException extends InvalidArgumentException
{
   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 7433963970361873580L;

   /**
    * Construct instance <tt>ConstraintViolationException</tt> without message.
    */
   public ConstraintException()
   {
      super();
   }

   /**
    * Construct instance <tt>ConstraintViolationException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public ConstraintException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>ConstraintViolationException</tt> with message and
    * cause exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public ConstraintException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>ConstraintViolationException</tt> with cause
    * exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public ConstraintException(Throwable cause)
   {
      super(cause);
   }

}
