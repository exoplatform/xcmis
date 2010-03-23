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

package org.xcmis.restatom.abdera;

import junit.framework.TestCase;

import org.xcmis.restatom.AbderaFactory;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.NamespaceResolver;
import org.xcmis.spi.Precision;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.impl.ChoiceImpl;
import org.xcmis.spi.impl.PropertyDefinitionImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyDefinitionElementTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PropertyDefinitionElementTest extends TestCase
{

   public void testWriteBoolean() throws Exception
   {
      PropertyDefinitionImpl<Boolean> def = new PropertyDefinitionImpl<Boolean>();
      addCommonAttributes(def);

      def.setPropertyType(PropertyType.BOOLEAN);

      ChoiceImpl<Boolean> bc1 = new ChoiceImpl<Boolean>();
      bc1.setDisplayName("key1");
      bc1.setValues(new Boolean[]{true});

      ChoiceImpl<Boolean> bc2 = new ChoiceImpl<Boolean>();
      bc2.setDisplayName("key2");
      bc2.setValues(new Boolean[]{true});

      def.getChoices().add(bc1);
      def.getChoices().add(bc2);

      def.setDefaultValue(new Boolean[]{true});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_BOOLEAN_DEFINITION);
      el.build(def);

      //      System.out.println("PropertyBooleanDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyBooleanDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate(baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("boolean", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceBoolean)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
   }

   public void testWriteDateTime() throws Exception
   {
      PropertyDefinitionImpl<Calendar> def = new PropertyDefinitionImpl<Calendar>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.DATETIME);

      ChoiceImpl<Calendar> dtc1 = new ChoiceImpl<Calendar>();
      dtc1.setDisplayName("key1");
      dtc1.setValues(new Calendar[]{Calendar.getInstance()});

      ChoiceImpl<Calendar> dtc2 = new ChoiceImpl<Calendar>();
      dtc2.setDisplayName("key2");
      dtc2.setValues(new Calendar[]{Calendar.getInstance()});

      def.getChoices().add(dtc1);
      def.getChoices().add(dtc2);

      def.setDefaultValue(new Calendar[]{Calendar.getInstance()});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_DATE_TIME_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyDateTimeDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyDateTimeDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("datetime", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceDateTime)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteDecimal() throws Exception
   {
      PropertyDefinitionImpl<BigDecimal> def = new PropertyDefinitionImpl<BigDecimal>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.DECIMAL);
      def.setPrecision(Precision.Bit32);

      ChoiceImpl<BigDecimal> d1 = new ChoiceImpl<BigDecimal>();
      d1.setDisplayName("key1");
      d1.setValues(new BigDecimal[]{BigDecimal.ZERO});

      ChoiceImpl<BigDecimal> d2 = new ChoiceImpl<BigDecimal>();
      d2.setDisplayName("key2");
      d2.setValues(new BigDecimal[]{BigDecimal.ONE});

      def.getChoices().add(d1);
      def.getChoices().add(d2);

      def.setDefaultValue(new BigDecimal[]{BigDecimal.valueOf(1)});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_DECIMAL_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyDecimalDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyDecimalDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("decimal", r);
      r = (String)xp.evaluate("/" + baseElement + "/cmis:precision", xmlDoc, XPathConstants.STRING);
      assertEquals("32", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceDecimal)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteId() throws Exception
   {
      PropertyDefinitionImpl<String> def = new PropertyDefinitionImpl<String>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.ID);

      ChoiceImpl<String> idc1 = new ChoiceImpl<String>();
      idc1.setDisplayName("key1");
      idc1.setValues(new String[]{"id1"});

      ChoiceImpl<String> idc2 = new ChoiceImpl<String>();
      idc2.setDisplayName("key2");
      idc2.setValues(new String[]{"id2"});

      def.getChoices().add(idc1);
      def.getChoices().add(idc2);

      def.setDefaultValue(new String[]{"id:1"});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(), AtomCMIS.PROPERTY_ID_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyIdDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyIdDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("id", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceId)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("id:1", r);
   }

   public void testWriteInteger() throws Exception
   {
      PropertyDefinitionImpl<BigInteger> def = new PropertyDefinitionImpl<BigInteger>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.INTEGER);
      def.setMinInteger(BigInteger.valueOf(Long.MIN_VALUE));
      def.setMaxInteger(BigInteger.valueOf(Long.MAX_VALUE));

      ChoiceImpl<BigInteger> ic1 = new ChoiceImpl<BigInteger>();
      ic1.setDisplayName("key1");
      ic1.setValues(new BigInteger[]{BigInteger.ONE});

      ChoiceImpl<BigInteger> ic2 = new ChoiceImpl<BigInteger>();
      ic2.setDisplayName("key2");
      ic2.setValues(new BigInteger[]{BigInteger.ONE});

      def.getChoices().add(ic1);
      def.getChoices().add(ic2);

      def.setDefaultValue(new BigInteger[]{BigInteger.ONE});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_INTEGER_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyIntegerDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyIntegerDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("integer", r);
      r = (String)xp.evaluate("/" + baseElement + "/cmis:minValue", xmlDoc, XPathConstants.STRING);
      assertEquals(Long.toString(Long.MIN_VALUE), r);
      r = (String)xp.evaluate("/" + baseElement + "/cmis:maxValue", xmlDoc, XPathConstants.STRING);
      assertEquals(Long.toString(Long.MAX_VALUE), r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceInteger)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteString() throws Exception
   {
      PropertyDefinitionImpl<String> def = new PropertyDefinitionImpl<String>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.STRING);
      def.setMaxLength(Integer.valueOf(65536));

      ChoiceImpl<String> sc1 = new ChoiceImpl<String>();
      sc1.setDisplayName("key1");
      sc1.setValues(new String[]{"string1"});

      ChoiceImpl<String> sc2 = new ChoiceImpl<String>();
      sc2.setDisplayName("key2");
      sc2.setValues(new String[]{"string2"});

      def.getChoices().add(sc1);
      def.getChoices().add(sc2);

      def.setDefaultValue(new String[]{"hello"});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_STRING_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyStringDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyStringDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("string", r);
      r = (String)xp.evaluate("/" + baseElement + "/cmis:maxLength", xmlDoc, XPathConstants.STRING);
      assertEquals("65536", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceString)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("hello", r);
   }

   public void testWriteUri() throws Exception
   {
      PropertyDefinitionImpl<String> def = new PropertyDefinitionImpl<String>();
      addCommonAttributes(def);
      def.setPropertyType(PropertyType.URI);

      ChoiceImpl<String> bc1 = new ChoiceImpl<String>();
      bc1.setDisplayName("key1");
      bc1.setValues(new String[]{"http://host1/a/b/c/d"});

      ChoiceImpl<String> bc2 = new ChoiceImpl<String>();
      bc2.setDisplayName("key2");
      bc2.setValues(new String[]{"http://host2/a/b/c/d"});

      def.getChoices().add(bc1);
      def.getChoices().add(bc2);

      def.setDefaultValue(new String[]{"http://host2/a"});

      PropertyDefinitionTypeElement el =
         new PropertyDefinitionTypeElement(AbderaFactory.getInstance().getFactory(), AtomCMIS.PROPERTY_URI_DEFINITION);
      el.build(def);

      //    System.out.println("PropertyUriDefinition: " + el);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      el.writeTo(out);

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String baseElement = "cmis:propertyUriDefinition";
      checkCommonAttributes(xp, xmlDoc, baseElement);

      String r = (String)xp.evaluate("/" + baseElement + "/cmis:propertyType", xmlDoc, XPathConstants.STRING);
      assertEquals("uri", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:choiceUri)", xmlDoc, XPathConstants.STRING);
      assertEquals("2", r);
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r =
         (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("http://host2/a", r);
   }

   private void addCommonAttributes(PropertyDefinitionImpl<?> def)
   {
      def.setDescription("description");
      def.setDisplayName("displayName");
      def.setId("id");
      def.setLocalName("localName");
      def.setOrderable(true);
      def.setLocalNamespace("localNamespace");
      def.setQueryable(true);
      def.setQueryName("queryName");
      def.setMultivalued(false);
      def.setRequired(true);
      def.setOpenChoice(new Boolean(true));
      def.setUpdatability(Updatability.READONLY);
   }

   private void checkCommonAttributes(XPath xp, org.w3c.dom.Document xmlDoc, String baseElement) throws Exception
   {
      String r = (String)xp.evaluate("/" + baseElement + "/cmis:name", xmlDoc, XPathConstants.STRING);
      r = (String)xp.evaluate(baseElement + "/cmis:cardinality", xmlDoc, XPathConstants.STRING);
      assertEquals("single", r);
      r = (String)xp.evaluate(baseElement + "/cmis:updatability", xmlDoc, XPathConstants.STRING);
      assertEquals("readonly", r);
      r = (String)xp.evaluate(baseElement + "/cmis:queryName", xmlDoc, XPathConstants.STRING);
      assertEquals("queryName", r);
      r = (String)xp.evaluate(baseElement + "/cmis:localName", xmlDoc, XPathConstants.STRING);
      assertEquals("localName", r);
      r = (String)xp.evaluate(baseElement + "/cmis:localNamespace", xmlDoc, XPathConstants.STRING);
      assertEquals("localNamespace", r);
      r = (String)xp.evaluate(baseElement + "/cmis:displayName", xmlDoc, XPathConstants.STRING);
      assertEquals("displayName", r);
      r = (String)xp.evaluate(baseElement + "/cmis:description", xmlDoc, XPathConstants.STRING);
      assertEquals("description", r);
      r = (String)xp.evaluate(baseElement + "/cmis:inherited", xmlDoc, XPathConstants.STRING);
      assertEquals("false", r);
      r = (String)xp.evaluate(baseElement + "/cmis:required", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(baseElement + "/cmis:queryable", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(baseElement + "/cmis:orderable", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(baseElement + "/cmis:openChoice", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
   }
}
