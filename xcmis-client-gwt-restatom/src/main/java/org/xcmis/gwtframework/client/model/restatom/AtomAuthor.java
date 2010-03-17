/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * 
 */
package org.xcmis.gwtframework.client.model.restatom;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class AtomAuthor
{
   /**
    * Name.
    */
   private String name;

   /**
    * URI.
    */
   private String uri;

   /**
    * Email.
    */
   private String email;

   /**
    * Accessor for author name.
    * 
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Mutator for author name.
    * 
    * @param name
    *            the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Accessor for author URI.
    * 
    * @return the uri
    */
   public String getUri()
   {
      return uri;
   }

   /**
    * Mutator for author URI.
    * 
    * @param uri
    *            the uri to set
    */
   public void setUri(String uri)
   {
      this.uri = uri;
   }

   /**
    * Accessor for author e-mail.
    * 
    * @return the email
    */
   public String getEmail()
   {
      return email;
   }

   /**
    * Mutator for author e-mail address.
    * 
    * @param email
    *            the email to set
    */
   public void setEmail(String email)
   {
      this.email = email;
   }

}
