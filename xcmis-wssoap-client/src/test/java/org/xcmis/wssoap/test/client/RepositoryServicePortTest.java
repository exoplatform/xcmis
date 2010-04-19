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
public class RepositoryServicePortTest extends BaseTest
{

   private static final QName REPOSITORY_SERVICE_NAME =
      new QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "RepositoryService");

   private RepositoryServicePort repository_port;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      URL wsdlURL = RepositoryService.WSDL_LOCATION;
      RepositoryService ss = new RepositoryService(wsdlURL, REPOSITORY_SERVICE_NAME);
      repository_port = ss.getRepositoryServicePort();
      ((BindingProvider)repository_port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
      ((BindingProvider)repository_port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
      super.setUp();
   }

   public void testGetRepositoryInfo() throws Exception
   {
      System.out.println("Invoking getRepositoryInfo...");
      org.xcmis.soap.client.CmisExtensionType _getRepositoryInfo_extension = null;
      try
      {
         org.xcmis.soap.client.CmisRepositoryInfoType _getRepositoryInfo__return =
            repository_port.getRepositoryInfo(cmisRepositoryId, _getRepositoryInfo_extension);
         System.out.println("getRepositoryInfo.result=" + _getRepositoryInfo__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
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
            repository_port.getRepositories(_getRepositories_extension);
         System.out.println("getRepositories.result=" + _getRepositories__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testGetTypeChildren() throws Exception
   {
      System.out.println("Invoking getTypeChildren...");
      java.lang.String _getTypeChildren_typeId = "cmis:folder";
      java.lang.Boolean _getTypeChildren_includePropertyDefinitions = Boolean.TRUE;
      java.math.BigInteger _getTypeChildren_maxItems = new java.math.BigInteger("10");
      java.math.BigInteger _getTypeChildren_skipCount = new java.math.BigInteger("0");
      org.xcmis.soap.client.CmisExtensionType _getTypeChildren_extension = null;
      try
      {
         org.xcmis.soap.client.CmisTypeDefinitionListType _getTypeChildren__return =
            repository_port.getTypeChildren(cmisRepositoryId, _getTypeChildren_typeId,
               _getTypeChildren_includePropertyDefinitions, _getTypeChildren_maxItems, _getTypeChildren_skipCount,
               _getTypeChildren_extension);
         System.out.println("getTypeChildren.result=" + _getTypeChildren__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testGetTypeDescendants() throws Exception
   {
      java.lang.String _getTypeDescendants_typeId = "cmis:folder";
      java.math.BigInteger _getTypeDescendants_depth = new java.math.BigInteger("10");
      java.lang.Boolean _getTypeDescendants_includePropertyDefinitions = Boolean.TRUE;
      org.xcmis.soap.client.CmisExtensionType _getTypeDescendants_extension = null;
      try
      {
         java.util.List<org.xcmis.soap.client.CmisTypeContainer> _getTypeDescendants__return =
            repository_port
               .getTypeDescendants(cmisRepositoryId, _getTypeDescendants_typeId,
                  _getTypeDescendants_depth, _getTypeDescendants_includePropertyDefinitions,
                  _getTypeDescendants_extension);
         System.out.println("getTypeDescendants.result=" + _getTypeDescendants__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

   public void testGetTypeDefinition() throws Exception
   {
      System.out.println("Invoking getTypeDefinition...");
      java.lang.String _getTypeDefinition_typeId = "cmis:folder";
      org.xcmis.soap.client.CmisExtensionType _getTypeDefinition_extension = null;
      try
      {
         org.xcmis.soap.client.CmisTypeDefinitionType _getTypeDefinition__return =
            repository_port.getTypeDefinition(cmisRepositoryId, _getTypeDefinition_typeId,
               _getTypeDefinition_extension);
         System.out.println("getTypeDefinition.result=" + _getTypeDefinition__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
      }
   }

}
