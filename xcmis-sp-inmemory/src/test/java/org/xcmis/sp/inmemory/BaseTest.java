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

package org.xcmis.sp.inmemory;

import com.sun.corba.se.impl.activation.RepositoryImpl;
import junit.framework.TestCase;

import org.exoplatform.services.log.LogConfigurator;
import org.exoplatform.services.log.impl.Log4JConfigurator;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseTest extends TestCase
{
   protected Repository repository;

   protected final String repositoryId = "cmis_simple";

   public void setUp() throws Exception
   {
      super.setUp();
      HashMap<String, Object> properties = new HashMap<String, Object>();
      properties.put("exo.cmis.changetoken.feature", false);
      repository = new RepositoryImpl(new CMISRepositoryConfiguration(repositoryId, properties));
      
      LogConfigurator lc = new Log4JConfigurator();
      Properties props = new Properties();
      props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/log4j.properties"));
      lc.configure(props);
   }
}
