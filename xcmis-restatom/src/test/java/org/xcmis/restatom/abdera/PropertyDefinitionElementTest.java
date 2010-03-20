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
import org.xcmis.spi.object.impl.IdProperty;
import org.xcmis.spi.utils.CmisUtils;

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
      CmisPropertyBooleanDefinitionType def = new CmisPropertyBooleanDefinitionType();
      addCommonAttributes(def);

      def.setPropertyType(EnumPropertyType.BOOLEAN);

      CmisChoiceBoolean bc1 = new CmisChoiceBoolean();
      bc1.setDisplayName("key1");
      bc1.getValue().add(new Boolean(true));

      CmisChoiceBoolean bc2 = new CmisChoiceBoolean();
      bc2.setDisplayName("key2");
      bc2.getValue().add(new Boolean(true));

      def.getChoice().add(bc1);
      def.getChoice().add(bc2);

      CmisPropertyBoolean defaultValue = new CmisPropertyBoolean();
      defaultValue.getValue().add(true);
      def.setDefaultValue(defaultValue);

      PropertyBooleanDefinitionTypeElement el =
         new PropertyBooleanDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
   }

   public void testWriteDateTime() throws Exception
   {
      CmisPropertyDateTimeDefinitionType def = new CmisPropertyDateTimeDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.DATETIME);

      CmisChoiceDateTime dtc1 = new CmisChoiceDateTime();
      dtc1.setDisplayName("key1");
      dtc1.getValue().add(CmisUtils.fromCalendar(Calendar.getInstance()));

      CmisChoiceDateTime dtc2 = new CmisChoiceDateTime();
      dtc2.setDisplayName("key2");
      dtc2.getValue().add(CmisUtils.fromCalendar(Calendar.getInstance()));

      def.getChoice().add(dtc1);
      def.getChoice().add(dtc2);

      CmisPropertyDateTime defaultValue = new CmisPropertyDateTime();
      defaultValue.getValue().add(CmisUtils.fromCalendar(Calendar.getInstance()));
      def.setDefaultValue(defaultValue);

      PropertyDateTimeDefinitionTypeElement el =
         new PropertyDateTimeDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteDecimal() throws Exception
   {
      CmisPropertyDecimalDefinitionType def = new CmisPropertyDecimalDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.DECIMAL);
      def.setPrecision(BigInteger.valueOf(32));

      CmisChoiceDecimal d1 = new CmisChoiceDecimal();
      d1.setDisplayName("key1");
      d1.getValue().add(new BigDecimal(0));

      CmisChoiceDecimal d2 = new CmisChoiceDecimal();
      d2.setDisplayName("key2");
      d2.getValue().add(new BigDecimal(1));

      def.getChoice().add(d1);
      def.getChoice().add(d2);

      CmisPropertyDecimal defaultValue = new CmisPropertyDecimal();
      defaultValue.getValue().add(BigDecimal.valueOf(1));
      def.setDefaultValue(defaultValue);

      PropertyDecimalDefinitionTypeElement el =
         new PropertyDecimalDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteId() throws Exception
   {
      CmisPropertyIdDefinitionType def = new CmisPropertyIdDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.ID);

      CmisChoiceId idc1 = new CmisChoiceId();
      idc1.setDisplayName("key1");
      idc1.getValue().add("id1");

      CmisChoiceId idc2 = new CmisChoiceId();
      idc2.setDisplayName("key2");
      idc2.getValue().add("id2");

      def.getChoice().add(idc1);
      def.getChoice().add(idc2);

      IdProperty defaultValue = new IdProperty();
      defaultValue.getValue().add("id:1");
      def.setDefaultValue(defaultValue);

      PropertyIdDefinitionTypeElement el =
         new PropertyIdDefinitionTypeElement(AbderaFactory.getInstance().getFactory(), AtomCMIS.PROPERTY_ID_DEFINITION);
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("id:1", r);
   }

   public void testWriteInteger() throws Exception
   {
      CmisPropertyIntegerDefinitionType def = new CmisPropertyIntegerDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.INTEGER);
      def.setMinValue(BigInteger.valueOf(Long.MIN_VALUE));
      def.setMaxValue(BigInteger.valueOf(Long.MAX_VALUE));

      CmisChoiceInteger ic1 = new CmisChoiceInteger();
      ic1.setDisplayName("key1");
      ic1.getValue().add(BigInteger.valueOf(1));

      CmisChoiceInteger ic2 = new CmisChoiceInteger();
      ic2.setDisplayName("key2");
      ic2.getValue().add(BigInteger.valueOf(1));

      def.getChoice().add(ic1);
      def.getChoice().add(ic2);

      CmisPropertyInteger defaultValue = new CmisPropertyInteger();
      defaultValue.getValue().add(BigInteger.valueOf(1));
      def.setDefaultValue(defaultValue);

      PropertyIntegerDefinitionTypeElement el =
         new PropertyIntegerDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
   }

   public void testWriteString() throws Exception
   {
      CmisPropertyStringDefinitionType def = new CmisPropertyStringDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.STRING);
      def.setMaxLength(BigInteger.valueOf(65536));

      CmisChoiceString sc1 = new CmisChoiceString();
      sc1.setDisplayName("key1");
      sc1.getValue().add("string1");

      CmisChoiceString sc2 = new CmisChoiceString();
      sc2.setDisplayName("key2");
      sc2.getValue().add("string2");

      def.getChoice().add(sc1);
      def.getChoice().add(sc2);

      CmisPropertyString defaultValue = new CmisPropertyString();
      defaultValue.getValue().add("hello");
      def.setDefaultValue(defaultValue);

      PropertyStringDefinitionTypeElement el =
         new PropertyStringDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("hello", r);
   }

   public void testWriteUri() throws Exception
   {
      CmisPropertyUriDefinitionType def = new CmisPropertyUriDefinitionType();
      addCommonAttributes(def);
      def.setPropertyType(EnumPropertyType.URI);

      CmisChoiceUri bc1 = new CmisChoiceUri();
      bc1.setDisplayName("key1");
      bc1.getValue().add("htt://host1/a/b/c/d");

      CmisChoiceUri bc2 = new CmisChoiceUri();
      bc2.setDisplayName("key2");
      bc2.getValue().add("htt://host2/a/b/c/d");

      def.getChoice().add(bc1);
      def.getChoice().add(bc2);

      CmisPropertyUri defaultValue = new CmisPropertyUri();
      defaultValue.getValue().add("htt://host2/a");
      def.setDefaultValue(defaultValue);

      PropertyUriDefinitionTypeElement el =
         new PropertyUriDefinitionTypeElement(AbderaFactory.getInstance().getFactory(),
            AtomCMIS.PROPERTY_URI_DEFINITION);
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
      r = (String)xp.evaluate("count(/" + baseElement + "/cmis:defaultValue/cmis:value)", xmlDoc, XPathConstants.STRING);
      assertEquals("1", r);
      r = (String)xp.evaluate(baseElement + "/cmis:defaultValue/cmis:value", xmlDoc, XPathConstants.STRING);
      assertEquals("htt://host2/a", r);
   }

   private void addCommonAttributes(CmisPropertyDefinitionType def)
   {
      def.setDescription("description");
      def.setDisplayName("displayName");
      def.setId("id");
      def.setLocalName("localName");
      def.setOrderable(true);
      def.setLocalNamespace("localNamespace");
      def.setQueryable(true);
      def.setQueryName("queryName");
      def.setCardinality(EnumCardinality.SINGLE);
      def.setRequired(true);
      def.setOpenChoice(new Boolean(true));
      def.setUpdatability(EnumUpdatability.READONLY);
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
