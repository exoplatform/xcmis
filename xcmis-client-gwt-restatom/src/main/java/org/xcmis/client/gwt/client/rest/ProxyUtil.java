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

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProxyUtil
{

   /**
    * @return {@link String} proxy service context
    */
   private static native String getProxyServiceContext() /*-{
        return $wnd.proxyServiceContext;
     }-*/;

   /**
    * @return {@link String} current host
    */
   private static String getCurrentHost()
   {
      String currentHost = Location.getProtocol() + "//" + Location.getHost();
      return currentHost;
   }

   /**
    * @param url url
    * @return {@link String} full url
    */
   public static String getCheckedURL(String url)
   {
      String proxyServiceContext = getProxyServiceContext();
      if (proxyServiceContext == null || "".equals(proxyServiceContext))
      {
         return url;
      }

      if (!(url.startsWith("http://") || url.startsWith("https://")))
      {
         return url;
      }

      String currentHost = getCurrentHost();
      if (url.startsWith(currentHost))
      {
         return url;
      }
      return proxyServiceContext + "?url=" + URL.encodeComponent(url);
   }

}
