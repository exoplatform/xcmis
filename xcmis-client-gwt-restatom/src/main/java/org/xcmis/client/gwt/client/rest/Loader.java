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

package org.xcmis.client.gwt.client.rest;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class Loader
{

   protected String DEFAULT_MESSAGE = "Loading data...";

   private String message = DEFAULT_MESSAGE;

   private static Loader instance;

   public static Loader getInstance()
   {
      return instance;
   }

   protected Loader()
   {
      instance = this;
   }

   /**
    * Show data loader
    */
   public abstract void show();

   /**
    * Hide data loader
    */
   public abstract void hide();

   protected String getMessage()
   {
      String m = message;
      message = DEFAULT_MESSAGE;
      return m;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

}
