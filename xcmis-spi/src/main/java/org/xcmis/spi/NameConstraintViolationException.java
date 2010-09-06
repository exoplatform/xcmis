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
 * The repository is not able to store the object that the user is
 * creating/updating due to a name constraint violation.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: NameConstraintViolationException.java 2 2010-02-04 17:21:49Z
 *          andrew00x $
 */
public class NameConstraintViolationException extends CmisException
{

   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 3396861814213057410L;

   /**
    * Construct instance <tt>NameConstraintException</tt> without message.
    */
   public NameConstraintViolationException()
   {
      super();
   }

   /**
    * Construct instance <tt>NameConstraintException</tt> with message.
    * 
    * @param message the detail message about exception
    * @see Throwable#getMessage()
    */
   public NameConstraintViolationException(String message)
   {
      super(message);
   }

   /**
    * Construct instance <tt>NameConstraintException</tt> with message and cause
    * exception.
    * 
    * @param message the detail message about exception
    * @param cause the cause exception
    * @see Throwable#getCause()
    * @see Throwable#getMessage()
    */
   public NameConstraintViolationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Construct instance <tt>NameConstraintException</tt> with cause exception.
    * 
    * @param cause the cause exception
    * @see Throwable#getCause()
    */
   public NameConstraintViolationException(Throwable cause)
   {
      super(cause);
   }
}
