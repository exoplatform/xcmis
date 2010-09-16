/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.xcmis.wssoap;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.xcmis.spi.UserContext;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:max.shaposhnik@exoplatform.com.ua">Max
 *         Shaposhnik</a>
 * @version $Id$ Dec 17, 2008
 */
public class UserContextInterceptor extends AbstractSoapInterceptor
{

   private final String principal;

   /**
    * Instantiates a new instance of Identity interceptor.
    */
   public UserContextInterceptor(String principal)
   {
      super(Phase.READ);
      if (principal == null)
         throw new NullPointerException();
      this.principal = principal;
   }

   /**
    * {@inheritDoc}
    */
   public void handleMessage(SoapMessage message) throws Fault
   {
      UserContext ctx = new UserContext(principal);
      UserContext.setCurrent(ctx);
   }

}
