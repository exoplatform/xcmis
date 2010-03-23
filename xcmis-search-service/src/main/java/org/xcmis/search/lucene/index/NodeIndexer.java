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
package org.xcmis.search.lucene.index;

import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.poi.util.LongField;
import org.exoplatform.services.document.DocumentReadException;
import org.exoplatform.services.document.DocumentReader;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.document.HandlerNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.Property;
import org.xcmis.search.content.Property.BinaryValue;
import org.xcmis.search.content.Property.ContentValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

/**
 * Create {@link Document} from {@link ContentEntry}
 */
public class NodeIndexer
{
   /**
    * Content extractor.
    */
   private final DocumentReaderService extractor;

   /**
    * @param extractor
    */
   public NodeIndexer(DocumentReaderService extractor)
   {
      super();
      this.extractor = extractor;
   }

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(NodeIndexer.class);

   public Document createDocument(ContentEntry contentEntry)
   {
      final Document doc = new Document();

      //  UUID
      doc.add(new Field(FieldNames.UUID, contentEntry.getIdentifier(), Field.Store.YES,
         Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO));

      //parent uuids
      for (int i = 0; i < contentEntry.getParentIdentifiers().length; i++)
      {
         String parentIdetifier = contentEntry.getParentIdentifiers()[i];
         // parent UUID
         if (parentIdetifier == null)
         {
            // root node
            doc.add(new Field(FieldNames.PARENT, "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));
            doc.add(new Field(FieldNames.LABEL, "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));
         }
         else
         {
            doc.add(new Field(FieldNames.PARENT, parentIdetifier, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));

            doc.add(new Field(FieldNames.LABEL, contentEntry.getName(), Field.Store.YES,
               Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO));
         }
      }
      for (int i = 0; i < contentEntry.getProperties().length; i++)
      {
         Property property = contentEntry.getProperties()[i];
         if (isIndexed(property.getName()))
         {
            addProperty(doc, property);
         }
      }
      return doc;
   }

   /**
    * Adds the non binary property.
    * 
    * @param doc the doc
    * @param propertyData the property data
    * @throws RepositoryException the repository exception
    */
   @SuppressWarnings("unchecked")
   public void addProperty(final Document doc, final Property propertyData)
   {
      final String propName = propertyData.getName();

      addPropertyName(doc, propName);

      Collection<ContentValue> data = propertyData.getValue();
      for (ContentValue value : data)
      {
         switch (propertyData.getType())
         {
            case BINARY :
               addBinaryProperty(doc, propName, ((BinaryValue)value));
               break;
            case BOOLEAN :
               addBooleanValue(doc, propName, ((ContentValue<Boolean>)value).getValue());
               break;
            case NAME :
            case PATH :
            case STRING :
               this.addStringValue(doc, propName, ((ContentValue<String>)value).getValue(), true);
               break;
            case LONG :
               addLongValue(doc, propName, ((ContentValue<Long>)value).getValue());
               break;
            case DOUBLE :
               addDoubleValue(doc, propName, ((ContentValue<Double>)value).getValue());
               break;
            case DATE :
               addCalendarValue(doc, propName, ((ContentValue<Calendar>)value).getValue());
               break;

            default :
               throw new NotImplementedException();
         }
         addLengthField(doc, propName, value);
      }

      if (data.size() > 1)
      {
         // real multi-valued
         addMVPName(doc, propName);
      }
   }

   private void addBinaryProperty(final Document doc, String propName, BinaryValue data)
   {
      try
      {
         final DocumentReader dreader = extractor.getDocumentReader(data.getMimeType());
         // ok, have a reader
         // if the prop obtainer from cache it will contains a values,
         // otherwise read prop with values from DM
         if (data == null)
         {
            LOG.warn("null value found at property " + propName);
         }
         final InputStream is = data.getValue();
         String content = "";

         try
         {
            if (data.getEncoding() != null)
            {
               content = dreader.getContentAsText(is, data.getEncoding());
            }
            else
            {
               content = dreader.getContentAsText(is);
            }
            final Field f =
               new Field(FieldNames.createFullTextFieldName(propName), content, Field.Store.NO, Field.Index.ANALYZED,
                  Field.TermVector.NO);
            doc.add(f);
         }
         finally
         {
            if (is != null)
            {
               is.close();
            }
         }
      }
      catch (final HandlerNotFoundException e)
      {
         // no handler - no index
         if (LOG.isDebugEnabled())
         {
            LOG.warn("This content is not readable " + e);
         }
      }
      catch (IOException e)
      {
         // no data - no index
         if (LOG.isDebugEnabled())
         {
            LOG.warn("Binary value indexer IO error " + e, e);
         }
      }
      catch (DocumentReadException e)
      {
         // no data - no index
         if (LOG.isDebugEnabled())
         {
            LOG.warn("Binary value indexer IO error " + e, e);
         }
      }

   }

   /**
    * Adds a {@link FieldNames#MVP} field to <code>doc</code> with the resolved
    * <code>name</code> using the internal search index namespace mapping.
    * 
    * @param doc the lucene document.
    * @param propName the name of the multi-value property.
    * @throws RepositoryException if any repository errors
    */
   private void addMVPName(final Document doc, final String propName)
   {
      doc.add(new Field(FieldNames.MVP, propName, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
   }

   /**
    * Adds the property name to the lucene _:PROPERTIES_SET field.
    * 
    * @param doc the document.
    * @param name the name of the property.
    * @throws RepositoryException if any repository errors
    */
   private void addPropertyName(final Document doc, final String propertyName)
   {
      doc.add(new Field(FieldNames.PROPERTIES_SET, propertyName, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
   }

   /**
    * Adds the string representation of the boolean value to the document as the
    * named field.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addBooleanValue(final Document doc, final String fieldName, final Boolean internalValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, internalValue.toString(), false));
   }

   /**
    * Creates a document field name as prefixed <code>fieldName</code> with the
    * value of <code>
    * internalValue</code> . The created field is indexed without norms.
    * 
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    * @param store <code>true</code> if the value should be stored,
    *          <code>false</code> otherwise
    *  @return field  Field 
    */
   private Field createFieldWithoutNorms(final String fieldName, final String internalValue, final boolean store)
   {

      final Field field =
         new Field(FieldNames.createPropertyFieldName(fieldName), internalValue, Field.Store.YES,
            Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO);
      return field;
   }

   /**
    * Adds the calendar value to the document as the named field. The calendar
    * value is converted to an indexable string value using the {@link DateTools}
    * class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param value The value for the field to add to the document.
    */
   private void addCalendarValue(final Document doc, final String fieldName, final Calendar value)
   {

      doc.add(createFieldWithoutNorms(fieldName, DateTools.dateToString(value.getTime(),
         DateTools.Resolution.MILLISECOND), false));
   }

   /**
    * Adds the double value to the document as the named field. The double value
    * is converted to an indexable string value using the {@link DoubleField}
    * class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addDoubleValue(final Document doc, final String fieldName, final Double doubleValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, ExtendedNumberTools.doubleToString(doubleValue), false));
   }

   /**
    * Adds the long value to the document as the named field. The long value is
    * converted to an indexable string value using the {@link LongField} class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param longValue The value for the field to add to the document.
    */
   private void addLongValue(final Document doc, final String fieldName, final Long longValue)
   {

      doc.add(createFieldWithoutNorms(fieldName, NumberTools.longToString(longValue), false));
   }

   /**
   * Returns <code>true</code> if the property with the given name should be indexed.
   *
   * @param propertyName name of a property.
   * @return <code>true</code> if the property should be fulltext indexed;   <code>false</code>
   * otherwise.
   */
   private boolean isIndexed(final String propertyName)
   {
      return true;
   }

   /** Adds the string value to the document both as the named field and
   * optionally for full text indexing if <code>tokenized</code> is
   * <code>true</code>.
   * 
   * @param doc The document to which to add the field
   * @param fieldName The name of the field to add
   * @param internalValue The value for the field to add to the document.
   * @param tokenized If <code>true</code> the string is also tokenized and
   *          fulltext indexed.
   */
   private void addStringValue(final Document doc, final String fieldName, final String stringValue,
      final boolean tokenized)
   {
      // simple String
      doc.add(createFieldWithoutNorms(fieldName, stringValue, false));
      if (tokenized)
      {
         if (stringValue.length() != 0)
         {
            // create fulltext index on property
            doc.add(new Field(FieldNames.createFullTextFieldName(fieldName), stringValue, Field.Store.NO,
               Field.Index.ANALYZED, Field.TermVector.NO));
         }
      }
   }

   /**
    * Adds the length field.
    * @param doc
    * @param propName - property name.
    * @param value 
    */
   private void addLengthField(Document doc, String propName, ContentValue value)
   {
      doc.add(new Field(FieldNames.createFieldLengthName(propName), //
         NumberTools.longToString(value.getLength()), //
         Store.YES, //
         Index.NOT_ANALYZED_NO_NORMS));

   }

}
