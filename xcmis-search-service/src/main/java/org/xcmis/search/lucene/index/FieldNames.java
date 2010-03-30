/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.lucene.index;

/**
 * Defines field names that are used internally to store UUID, etc in the search
 * index.
 */
public class FieldNames
{

   /**
    * Name of the field that contains the UUID of the node. Terms are stored but
    * not tokenized.
    */
   public static final String UUID = "_:UUID".intern();

   /**
    * Name of the field that contains the fulltext index including terms from all
    * properties of a node. Terms are tokenized.
    */
   public static final String FULLTEXT = "_:FULLTEXT".intern();

   /**
    * Prefix for table tame field.
    */
   public static final String TABLE_NAME = "TABLE_NAME:".intern();

   /**
    * Prefix for all field names that are fulltext indexed by property name.
    */
   public static final String FULLTEXT_PREFIX = "FULL:";

   /**
    * Prefix for all field names that are indexed as single term by property
    * name.
    */
   public static final String PROPERTY_PREFIX = "PROP:";

   /**
    * Name of the field that contains the label of the node. Terms are not
    * tokenized.
    */
   public static final String LENGTH_PREFIX = "LENGTH:";

   /**
    * Name of the field that contains the UUID of the parent node. Terms are
    * stored and but not tokenized.
    */
   public static final String PARENT = "_:PARENT".intern();

   /**
    * Name of the field that contains the label of the node. Terms are not
    * tokenized.
    */
   public static final String LABEL = "_:LABEL".intern();

   /**
    * Name of the field that contains the names of multi-valued properties that
    * hold more than one value. Terms are not tokenized and not stored, only
    * indexed.
    */
   public static final String MVP = "_:MVP".intern();

   /**
    * Name of the field that contains all values of properties that are indexed
    * as is without tokenizing. Terms are prefixed with the property name.
    */
   @Deprecated
   public static final String PROPERTIES = "_:PROPERTIES".intern();

   /**
    * Name of the field that contains the names of all properties that are set on
    * an indexed node.
    */
   public static final String PROPERTIES_SET = "_:PROPERTIES_SET".intern();

   /**
    * Name of the field that contains the UUIDs of the aggregated nodes. The
    * terms are not tokenized and not stored, only indexed.
    */
   public static final String AGGREGATED_NODE_UUID = "_:AGGR_NODE_UUID".intern();

   /**
    * Private constructor.
    */
   private FieldNames()
   {
   }

   /**
    * Returns a length field name.
    * 
    * @param fieldName the property name
    * @return length field name
    */
   public static String createFieldLengthName(String fieldName)
   {
      int idx = fieldName.indexOf(':');
      return fieldName.substring(0, idx + 1) + FieldNames.LENGTH_PREFIX + fieldName.substring(idx + 1);
   }

   /**
    * Returns a named value for use as a term in the index. The named value is of
    * the form: <code>fieldName</code> + '\uFFFF' + value
    * 
    * @param fieldName the field name.
    * @param value the value.
    * @return value prefixed with field name.
    */
   public static String createFullTextFieldName(String fieldName)
   {
      int idx = fieldName.indexOf(':');
      return fieldName.substring(0, idx + 1) + FieldNames.FULLTEXT_PREFIX + fieldName.substring(idx + 1);
   }

   /**
    * Returns a named value for use as a term in the index. The named value is of
    * the form: <code>fieldName</code> + '\uFFFF' + value
    * 
    * @param fieldName the field name.
    * @param value the value.
    * @return value prefixed with field name.
    */
   @Deprecated
   public static String createNamedValue(String fieldName, String value)
   {
      return fieldName + '\uFFFF' + value;
   }

   /**
    * Returns a named value for use as a term in the index. The named value is of
    * the form: <code>fieldName</code> + '\uFFFF' + value
    * 
    * @param fieldName the field name.
    * @param value the value.
    * @return value prefixed with field name.
    * @param propertyName
    * @return
    */
   public static String createPropertyFieldName(String propertyName)
   {
      int idx = propertyName.indexOf(':');
      return propertyName.substring(0, idx + 1) + FieldNames.PROPERTY_PREFIX + propertyName.substring(idx + 1);
   }

   /**
    * Returns the length of the field prefix in <code>namedValue</code>. See also
    * {@link #createNamedValue(String, String)}. If <code>namedValue</code> does
    * not contain a name prefix, this method return 0.
    * 
    * @param namedValue the named value as created by
    *          {@link #createNamedValue(String, String)}.
    * @return the length of the field prefix including the separator char
    *         (\uFFFF).
    */
   public static int getNameLength(String namedValue)
   {
      return namedValue.indexOf('\uFFFF') + 1;
   }

}
