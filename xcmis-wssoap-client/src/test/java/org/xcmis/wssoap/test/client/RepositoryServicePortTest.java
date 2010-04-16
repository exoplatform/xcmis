/*
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
package org.xcmis.wssoap.test.client;

import junit.framework.TestCase;

import org.xcmis.soap.client.CmisException;
import org.xcmis.soap.client.RepositoryService;
import org.xcmis.soap.client.RepositoryServicePort;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class RepositoryServicePortTest extends TestCase
{

   private static final QName SERVICE_NAME =
      new QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "RepositoryService");

   private RepositoryServicePort port;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      URL wsdlURL = RepositoryService.WSDL_LOCATION;

      RepositoryService ss = new RepositoryService(wsdlURL, SERVICE_NAME);
      port = ss.getRepositoryServicePort();
      ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "root");
      ((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "exo");
   }

   public void testGetRepositoryInfo() throws Exception
   {
      System.out.println("Invoking getRepositoryInfo...");
      java.lang.String _getRepositoryInfo_repositoryId = "cmis1";
      org.xcmis.soap.client.CmisExtensionType _getRepositoryInfo_extension = null;
      try
      {
         org.xcmis.soap.client.CmisRepositoryInfoType _getRepositoryInfo__return =
            port.getRepositoryInfo(_getRepositoryInfo_repositoryId, _getRepositoryInfo_extension);
         System.out.println("getRepositoryInfo.result=" + _getRepositoryInfo__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
      }
   }

   public void testGetRepositories() throws Exception
   {
      System.out.println("Invoking getRepositories...");
      org.xcmis.soap.client.CmisExtensionType _getRepositories_extension =
         new org.xcmis.soap.client.CmisExtensionType();
      try
      {
         java.util.List<org.xcmis.soap.client.CmisRepositoryEntryType> _getRepositories__return =
            port.getRepositories(_getRepositories_extension);
         System.out.println("getRepositories.result=" + _getRepositories__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
      }
   }
}
