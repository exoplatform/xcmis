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

package org.xcmis.sp.jcr.exo.object;

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.EnumBasicPermissions;
import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.impl.core.value.BooleanValue;
import org.exoplatform.services.jcr.impl.core.value.DateValue;
import org.exoplatform.services.jcr.impl.core.value.DoubleValue;
import org.exoplatform.services.jcr.impl.core.value.LongValue;
import org.exoplatform.services.jcr.impl.core.value.StringValue;
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.object.Entry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Value;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: EntryTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class EntryTest extends BaseTest
{

   public void testAddPermission() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);

      CmisAccessControlEntryType aceRead = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("exo1");
      aceRead.setPrincipal(principal1);
      aceRead.getPermission().add(EnumBasicPermissions.CMIS_READ.value());

      CmisAccessControlEntryType aceAll = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal2 = new CmisAccessControlPrincipalType();
      principal2.setPrincipalId("root");
      aceAll.setPrincipal(principal2);
      aceAll.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());

      List<CmisAccessControlEntryType> acl = new ArrayList<CmisAccessControlEntryType>();
      acl.add(aceRead);
      acl.add(aceAll);
      entry.addPermissions(acl);
      entry.save();

      // ACE must be added to back-end node.
      AccessControlList result = ((ExtendedNode)node).getACL();
      assertEquals(2, result.getPermissions("exo1").size());
      assertEquals(4, result.getPermissions("root").size());
      assertTrue(result.getPermissions("exo1").contains("add_node"));
      assertTrue(result.getPermissions("exo1").contains("read"));
      assertTrue(result.getPermissions("root").contains("add_node"));
      assertTrue(result.getPermissions("root").contains("read"));
      assertTrue(result.getPermissions("root").contains("set_property"));
      assertTrue(result.getPermissions("root").contains("remove"));
   }

   public void testAddPolicy() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      EntryImpl policy1 = createPolicy(testRootFolderId, "policy1", "");
      entry.applyPolicy(policy1);
      entry.save();
      assertEquals(1, policy1.getNode().getReferences().getSize());
   }

   public void testGetBoolean() throws Exception
   {
      Node node = createNode();
      node.setProperty("boolean", true);
      session.save();
      assertTrue(new EntryImpl(node).getBoolean("boolean"));
   }

   public void testGetBooleans() throws Exception
   {
      Node node = createNode();
      Value[] values = new Value[]{new BooleanValue(true), new BooleanValue(false)};
      node.setProperty("booleans", values);
      session.save();
      boolean[] res = new EntryImpl(node).getBooleans("booleans");
      assertEquals(2, res.length);
      assertTrue(res[0]);
      assertFalse(res[1]);
   }

   public void testGetDate() throws Exception
   {
      Node node = createNode();
      Calendar value = Calendar.getInstance();
      node.setProperty("date", value);
      session.save();
      assertEquals(value, new EntryImpl(node).getDate("date"));
   }

   public void testGetDates() throws Exception
   {
      Node node = createNode();
      Value[] values = new Value[]{new DateValue(Calendar.getInstance())};
      node.setProperty("dates", values);
      session.save();
      Calendar[] res = new EntryImpl(node).getDates("dates");
      assertEquals(1, res.length);
   }

   public void testGetDecimal() throws Exception
   {
      Node node = createNode();
      node.setProperty("decimal", 1D);
      session.save();
      assertEquals(1D, new EntryImpl(node).getDecimal("decimal").doubleValue());
   }

   public void testGetDecimals() throws Exception
   {
      Node node = createNode();
      Value[] values = new Value[]{new DoubleValue(1D), new DoubleValue(2D)};
      node.setProperty("decimals", values);
      session.save();
      BigDecimal[] res = new EntryImpl(node).getDecimals("decimals");
      assertEquals(2, res.length);
      assertEquals(1D, res[0].doubleValue());
      assertEquals(2D, res[1].doubleValue());
   }

   public void testGetHTML() throws Exception
   {
      Node node = createNode();
      String html = "<html><head></head><body><h1>to be or not to be</h1></body></html>";
      node.setProperty("html", html);
      session.save();
      String result = new EntryImpl(node).getHTML("html");
      assertEquals(html, result);
   }

   public void testGetHTMLs() throws Exception
   {
      Node node = createNode();
      String html1 = "<html><head></head><body><h1>to be or not to be</h1></body></html>";
      String html2 = "<html><head></head><body><h2>to be or not to be</h2></body></html>";
      Value[] values = new Value[]{new StringValue(html1), new StringValue(html2)};
      node.setProperty("htmls", values);
      session.save();
      String[] res = new EntryImpl(node).getHTMLs("htmls");
      assertEquals(2, res.length);
      assertEquals(html1, res[0]);
      assertEquals(html2, res[1]);
   }

   public void testGetInteger() throws Exception
   {
      Node node = createNode();
      node.setProperty("integer", 1L);
      session.save();
      assertEquals(1L, new EntryImpl(node).getInteger("integer").longValue());
   }

   public void testGetIntegers() throws Exception
   {
      Node node = createNode();
      Value[] values = new Value[]{new LongValue(1L), new LongValue(2L)};
      node.setProperty("integers", values);
      session.save();
      BigInteger[] res = new EntryImpl(node).getIntegers("integers");
      assertEquals(2, res.length);
      assertEquals(1L, res[0].longValue());
      assertEquals(2L, res[1].longValue());
   }

   public void testGetPermission() throws Exception
   {
      Node node = createNode();
      node.addMixin("exo:privilegeable");
      ((ExtendedNode)node).setPermission("exo1", new String[]{"add_node", "read", "remove", "set_property"});
      node.save();
      Entry entry = new EntryImpl(node);
      List<CmisAccessControlEntryType> result = entry.getPermissions();
      // Created for "exo1" and default for "any" was created by default.  
      assertEquals(2, result.size());
      checkACL(result, "exo1", EnumBasicPermissions.CMIS_ALL.value());
   }

   public void testGetString() throws Exception
   {
      Node node = createNode();
      node.setProperty("string", "hello");
      session.save();
      assertEquals("hello", new EntryImpl(node).getString("string"));
   }

   public void testGetStrings() throws Exception
   {
      Node node = createNode();
      Value[] values = new Value[]{new StringValue("hello"), new StringValue("world")};
      node.setProperty("strings", values);
      session.save();
      String[] res = new EntryImpl(node).getStrings("strings");
      assertEquals(2, res.length);
      assertEquals("hello", res[0]);
      assertEquals("world", res[1]);
   }

   public void testGetURI() throws Exception
   {
      Node node = createNode();
      node.setProperty("uri", "http://localhost:8080/a/b/c/d");
      session.save();
      assertEquals("http://localhost:8080/a/b/c/d", new EntryImpl(node).getURI("uri").toString());
   }

   public void testGetURIs() throws Exception
   {
      Node node = createNode();
      Value[] values =
         new Value[]{new StringValue("http://localhost:8080/a"), new StringValue("http://localhost:8080/a/b")};
      node.setProperty("uris", values);
      session.save();
      URI[] res = new EntryImpl(node).getURIs("uris");
      assertEquals(2, res.length);
      assertEquals("http://localhost:8080/a", res[0].toString());
      assertEquals("http://localhost:8080/a/b", res[1].toString());
   }

   public void testRemovePermissions() throws Exception
   {
      Node node = createNode();
      node.addMixin("exo:privilegeable");
      ((ExtendedNode)node).setPermission("exo1", new String[]{"add_node", "read", "remove", "set_property"});
      node.save();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("exo1");

      CmisAccessControlEntryType aceRead = new CmisAccessControlEntryType();
      aceRead.setPrincipal(principal1);
      aceRead.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      List<CmisAccessControlEntryType> acl = new ArrayList<CmisAccessControlEntryType>();
      acl.add(aceRead);

      Entry entry = new EntryImpl(node);
      entry.removePermissions(acl);
      entry.save();

      // ACE must be removed from back-end node.
      AccessControlList result = ((ExtendedNode)node).getACL();
      assertEquals(2, result.getPermissions("exo1").size());
      assertTrue(result.getPermissions("exo1").contains("set_property"));
      assertTrue(result.getPermissions("exo1").contains("remove"));
   }

   public void testSetBoolean() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setBoolean("boolean", true);
      entry.save();
      assertTrue(node.getProperty("boolean").getBoolean());
   }

   public void testSetBooleans() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setBooleans("booleans", new boolean[]{true, false});
      entry.save();
      Value[] values = node.getProperty("booleans").getValues();
      assertEquals(2, values.length);
   }

   public void testSetDate() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      Calendar value = Calendar.getInstance();
      entry.setDate("date", value);
      entry.save();
      assertEquals(value, node.getProperty("date").getDate());
   }

   public void testSetDates() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setDates("dates", new Calendar[]{Calendar.getInstance()});
      entry.save();
      Value[] values = node.getProperty("dates").getValues();
      assertEquals(1, values.length);

   }

   public void testSetDecimal() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setDecimal("decimal", BigDecimal.valueOf(1D));
      entry.save();
      assertEquals(1D, node.getProperty("decimal").getDouble());
   }

   public void testSetDecimals() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setDecimals("decimals", new BigDecimal[]{BigDecimal.valueOf(1D), BigDecimal.valueOf(2D)});
      entry.save();
      Value[] values = node.getProperty("decimals").getValues();
      assertEquals(2, values.length);
      assertEquals(1D, values[0].getDouble());
      assertEquals(2D, values[1].getDouble());
   }

   public void testSetHTML() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      String html = "<html><head></head><body><h1>to be or not to be</h1></body></html>";
      entry.setHTML("html", html);
      entry.save();
      assertEquals(html, node.getProperty("html").getString());
   }

   public void testSetHTMLs() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      String html1 = "<html><head></head><body><h1>to be or not to be</h1></body></html>";
      String html2 = "<html><head></head><body><h2>to be or not to be</h2></body></html>";
      entry.setHTMLs("htmls", new String[]{html1, html2});
      entry.save();
      Value[] values = node.getProperty("htmls").getValues();
      assertEquals(2, values.length);
      assertEquals(html1, values[0].getString());
      assertEquals(html2, values[1].getString());
   }

   public void testSetInteger() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setInteger("integer", BigInteger.valueOf(1L));
      entry.save();
      assertEquals(1L, node.getProperty("integer").getLong());
   }

   public void testSetIntegers() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setIntegers("integers", new BigInteger[]{BigInteger.valueOf(1L), BigInteger.valueOf(2L)});
      entry.save();
      Value[] values = node.getProperty("integers").getValues();
      assertEquals(2, values.length);
      assertEquals(1L, values[0].getLong());
      assertEquals(2L, values[1].getLong());
   }

   public void testSetString() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setString("string", "hello");
      entry.save();
      assertEquals("hello", node.getProperty("string").getString());
   }

   public void testSetStrings() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setStrings("strings", new String[]{"hello", "world"});
      entry.save();
      Value[] values = node.getProperty("strings").getValues();
      assertEquals(2, values.length);
      assertEquals("hello", values[0].getString());
      assertEquals("world", values[1].getString());
   }

   public void testSetURI() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setURI("uri", new URI("http://localhost:8080/a/b/c/d"));
      entry.save();
      assertEquals("http://localhost:8080/a/b/c/d", node.getProperty("uri").getString());
   }

   public void testSetURIs() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      entry.setURIs("uris", new URI[]{new URI("http://localhost:8080/a"), new URI("http://localhost:8080/a/b")});
      Value[] values = node.getProperty("uris").getValues();
      assertEquals(2, values.length);
      assertEquals("http://localhost:8080/a", values[0].getString());
      assertEquals("http://localhost:8080/a/b", values[1].getString());
   }

   private void checkACL(List<CmisAccessControlEntryType> aces, String principal, String... permissions)
      throws Exception
   {
      List<String> notFound = new ArrayList<String>();
      for (CmisAccessControlEntryType ace : aces)
      {
         if (ace.getPrincipal().getPrincipalId().equals(principal))
         {
            List<String> values = ace.getPermission();
            assertEquals(permissions.length, values.size());
            for (String perm : permissions)
            {
               if (!values.contains(perm))
                  notFound.add(perm);
            }
         }
      }
      if (notFound.size() > 0)
         fail("Not found expected permissions " + notFound + " for principal " + principal);
   }

   protected abstract Node createNode() throws Exception;

}
