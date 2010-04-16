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
import org.xcmis.soap.client.DiscoveryService;
import org.xcmis.soap.client.DiscoveryServicePort;
import org.xcmis.soap.client.RepositoryService;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class DiscoveryServicePortTest extends TestCase
{

   private static final QName SERVICE_NAME =
      new QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "DiscoveryService");

   private DiscoveryServicePort port;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      URL wsdlURL = RepositoryService.WSDL_LOCATION;

      DiscoveryService ss = new DiscoveryService(wsdlURL, SERVICE_NAME);
      port = ss.getDiscoveryServicePort();

      ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "root");
      ((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "exo");
   }

   //TODO getContentChanges is not implemented feature.
   public void testGetContentChanges() throws Exception
   {
      System.out.println("Invoking getContentChanges...");
      java.lang.String _getContentChanges_repositoryId = "cmis1";
      java.lang.String _getContentChanges_changeLogTokenVal = "*";
      javax.xml.ws.Holder<java.lang.String> _getContentChanges_changeLogToken =
         new javax.xml.ws.Holder<java.lang.String>(_getContentChanges_changeLogTokenVal);
      java.lang.Boolean _getContentChanges_includeProperties = true;
      java.lang.String _getContentChanges_filter = "*";
      java.lang.Boolean _getContentChanges_includePolicyIds = false;
      java.lang.Boolean _getContentChanges_includeACL = false;
      java.math.BigInteger _getContentChanges_maxItems = new java.math.BigInteger("0");
      org.xcmis.soap.client.CmisExtensionType _getContentChanges_extension = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisObjectListType> _getContentChanges_objects =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisObjectListType>();
      try
      {
         port.getContentChanges(_getContentChanges_repositoryId, _getContentChanges_changeLogToken,
            _getContentChanges_includeProperties, _getContentChanges_filter, _getContentChanges_includePolicyIds,
            _getContentChanges_includeACL, _getContentChanges_maxItems, _getContentChanges_extension,
            _getContentChanges_objects);

         System.out.println("getContentChanges._getContentChanges_changeLogToken="
            + _getContentChanges_changeLogToken.value);
         System.out.println("getContentChanges._getContentChanges_objects=" + _getContentChanges_objects.value);
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
      }
   }

   public void testQuery() throws Exception
   {
      System.out.println("Invoking query...");
      org.xcmis.soap.client.Query _query_parameters = new org.xcmis.soap.client.Query();
      _query_parameters.setStatement("SELECT * FROM cmis:folder");
      _query_parameters.setRepositoryId("cmis1");

      try
      {
         org.xcmis.soap.client.QueryResponse _query__return = port.query(_query_parameters);
         System.out.println("query.result=" + _query__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         e.printStackTrace();
      }
   }
}
