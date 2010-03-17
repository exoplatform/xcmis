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

package org.xcmis.sp.jcr.exo.query.index;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.poi.util.LongField;
import org.exoplatform.services.document.DocumentReader;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.document.HandlerNotFoundException;
import org.exoplatform.services.jcr.core.ExtendedPropertyType;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.value.ExtendedValue;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.jcr.datamodel.ValueData;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.query.lucene.DoubleField;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.AbstractPersistedValueData;
import org.exoplatform.services.jcr.impl.dataflow.TransientValueData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.index.ExtendedNumberTools;
import org.xcmis.search.lucene.index.FieldNames;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.NamespaceException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndependentNodeIndexer.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class IndependentNodeIndexer
{
   /**
    * The default boost for a lucene field: 1.0f.
    */
   private static final float DEFAULT_BOOST = 1.0f;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(IndependentNodeIndexer.class);

   /**
    * Content extractor.
    */
   private final DocumentReaderService extractor;

   /**
    * Name and Path resolver.
    */
   private final LocationFactory resolver;

   //   /**
   //    * The indexing configuration or <code>null</code> if none is available.
   //    */
   //   private IndexingConfiguration indexingConfig;
   //   /**
   //    * If set to <code>true</code> the fulltext field is stored and and a term
   //    * vector is created with offset information.
   //    */
   //   private final boolean supportHighlighting = false;

   /** The v factory. */
   private final ValueFactoryImpl vFactory;

   /**
    * @param resolver
    * @param extractor
    */
   public IndependentNodeIndexer(final NamespaceAccessor nsRegistry, final DocumentReaderService extractor)
   {
      super();
      resolver = new LocationFactory(nsRegistry);
      this.extractor = extractor;
      vFactory = new ValueFactoryImpl(resolver);
   }

   /**
    * Adds the document property.
    * 
    * @param doc the doc
    * @param propertyData the property data
    * @param mimeType the mime type
    * @param encoding the encoding
    * @throws RepositoryException the repository exception
    */
   public void addDocumentProperty(final Document doc, final PropertyData propertyData, final String mimeType,
      final String encoding) throws RepositoryException
   {
      final int propType = propertyData.getType();
      final String propName = resolver.createJCRName(propertyData.getQPath().getName()).getAsString();
      if (propType != PropertyType.BINARY)
      {
         throw new UnsupportedRepositoryOperationException("Wrong method use addNonBinaryProperty");
      }
      addPropertyName(doc, propertyData.getQPath().getName());

      final List<ValueData> data = propertyData.getValues();

      addLengthField(doc, propName, data, propType);
      // TODO 'check' if node is of type nt:resource
      if (!Constants.JCR_DATA.equals(propertyData.getQPath().getName()))
      {
         return; // don't know how to index
      }

      if (mimeType != null)
      {
         // index if have jcr:mimeType sibling for this binary property only
         try
         {
            final DocumentReader dreader = extractor.getDocumentReader(mimeType);
            // ok, have a reader
            // if the prop obtainer from cache it will contains a values,
            // otherwise read prop with values from DM
            if (data == null)
            {
               LOG.warn("null value found at property " + propertyData.getQPath().getAsString());
            }

            for (final ValueData pvd : data)
            {
               final InputStream is = pvd.getAsStream();
               String content = "";
               try
               {
                  if (encoding != null)
                  {
                     content = dreader.getContentAsText(is, encoding);
                  }
                  else
                  {
                     content = dreader.getContentAsText(is);
                  }
               }
               finally
               {
                  is.close();
               }
               final Field f =
                  new Field(FieldNames.createFullTextFieldName(propName), content, Field.Store.NO,
                     Field.Index.ANALYZED, Field.TermVector.NO);
               doc.add(f);
            }

            if (data.size() > 1)
            {
               // real multi-valued
               addMVPName(doc, propertyData.getQPath().getName());
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
         catch (final IOException e)
         {
            // no data - no index
            if (LOG.isDebugEnabled())
            {
               LOG.warn("Binary value indexer IO error " + e, e);
            }
         }
         catch (final Exception e)
         {
            LOG.error("Binary value indexer error " + e, e);
         }

      }
   }

   /**
    * Adds the non binary property.
    * 
    * @param doc the doc
    * @param propertyData the property data
    * @throws RepositoryException the repository exception
    */
   public void addNonBinaryProperty(final Document doc, final PropertyData propertyData) throws RepositoryException
   {
      final int propType = propertyData.getType();
      final String propName = resolver.createJCRName(propertyData.getQPath().getName()).getAsString();
      if (propType == PropertyType.BINARY || doc == null)
      {
         throw new UnsupportedRepositoryOperationException("Wrong method use addBinaryProperty");
      }

      addPropertyName(doc, propertyData.getQPath().getName());
      // if the prop obtainer from cache it will contains a values, otherwise
      // read prop with values from DM
      // WARN. DON'T USE access item BY PATH - it's may be a node in case of
      // residual definitions in NT
      final List<ValueData> data = propertyData.getValues();

      if (data == null)
      {
         LOG.warn("null value found at property " + propertyData.getQPath().getAsString());
      }

      ExtendedValue val = null;
      final InternalQName name = propertyData.getQPath().getName();
      for (final ValueData value : data)
      {

         val = (ExtendedValue)vFactory.loadValue(value, propType);

         switch (propType)
         {
            case PropertyType.BOOLEAN :
               if (isIndexed(name))
               {
                  addBooleanValue(doc, propName, Boolean.valueOf(val.getBoolean()));
               }
               break;
            case PropertyType.DATE :
               if (isIndexed(name))
               {
                  addCalendarValue(doc, propName, val.getDate());
               }
               break;
            case PropertyType.DOUBLE :
               if (isIndexed(name))
               {
                  addDoubleValue(doc, propName, new Double(val.getDouble()));
               }
               break;
            case PropertyType.LONG :
               if (isIndexed(name))
               {
                  addLongValue(doc, propName, new Long(val.getLong()));
               }
               break;
            // case PropertyType.WEAKREFERENCE:
            case PropertyType.REFERENCE :
               if (isIndexed(name))
               {
                  addReferenceValue(doc, propName, val.getString());
               }
               break;
            case PropertyType.PATH :
               if (isIndexed(name))
               {
                  addPathValue(doc, propName, val.getString());
               }
               break;
            // case PropertyType.URI:
            case PropertyType.STRING :
               if (isIndexed(name))
               {
                  // never fulltext index jcr:uuid String
                  if (name.equals(Constants.JCR_UUID))
                  {
                     this.addStringValue(doc, propName, val.getString(), false, false, DEFAULT_BOOST);
                  }
                  else
                  {
                     this.addStringValue(doc, propName, val.getString(), true, isIncludedInNodeIndex(name),
                        getPropertyBoost(name));
                  }
               }
               break;
            case PropertyType.NAME :
               // jcr:primaryType and jcr:mixinTypes are required for correct
               // node type resolution in queries
               if (isIndexed(name) || name.equals(Constants.JCR_PRIMARYTYPE) || name.equals(Constants.JCR_MIXINTYPES))
               {
                  addNameValue(doc, propName, val.getString());
               }
               break;
            case ExtendedPropertyType.PERMISSION :
               break;
            //            case PropertyType.DECIMAL :
            //               if (isIndexed(name))
            //                  addDecimalValue(doc, propName, val.getDecimal());
            //               break;
            default :
               throw new IllegalArgumentException("illegal internal value type " + propType);
         }
      }
      addLengthField(doc, propName, data, propType);
      if (data.size() > 1)
      {
         // real multi-valued
         addMVPName(doc, propertyData.getQPath().getName());
      }

   }

   /**
    * Creates the document.
    * 
    * @param nodeData the node data
    * @return the document
    * @throws RepositoryException the repository exception
    */
   public Document createDocument(final NodeData nodeData) throws RepositoryException
   {
      final Document doc = new Document();
      // doc.setBoost(getNodeBoost());

      // special fields UUID
      doc.add(new Field(FieldNames.UUID, nodeData.getIdentifier(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
         Field.TermVector.NO));
      try
      {
         // parent UUID
         if (nodeData.getParentIdentifier() == null)
         {
            // root node
            doc.add(new Field(FieldNames.PARENT, "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));
            doc.add(new Field(FieldNames.LABEL, "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));
         }
         else
         {
            doc.add(new Field(FieldNames.PARENT, nodeData.getParentIdentifier(), Field.Store.YES,
               Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO));

            // String name =
            // resolver.createJCRName(node.getQPath().getName()).getAsString();

            final QPathEntry[] qe = nodeData.getQPath().getEntries();
            final String name = resolver.formatPathElement(qe[qe.length - 1]);

            doc.add(new Field(FieldNames.LABEL, name, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS,
               Field.TermVector.NO));
         }
      }
      catch (final NamespaceException e)
      {
         // will never happen, because this.mappings will dynamically add
         // unknown uri<->prefix mappings
      }
      return doc;
   }

   /**
    * Removes the property.
    * 
    * @param doc the doc
    * @param propertyData the property data
    * @throws RepositoryException the repository exception
    */
   public void removeProperty(final Document doc, final PropertyData propertyData) throws RepositoryException
   {
      final int propType = propertyData.getType();
      final String propName = resolver.createJCRName(propertyData.getQPath().getName()).getAsString();
      if (/* propType == PropertyType.BINARY || */doc == null)
      {
         throw new UnsupportedRepositoryOperationException("Wrong method use removeNonBinaryProperty");
      }

      final Field[] props_set = doc.getFields(FieldNames.PROPERTIES_SET);
      doc.removeFields(FieldNames.PROPERTIES_SET);

      for (int i = 0; i < props_set.length; i++)
      {
         if (!propName.equals(props_set[i].stringValue()))
         {
            doc.add(props_set[i]);
         }
      }
      // remove mvp flag
      final Field[] props_mvp = doc.getFields(FieldNames.MVP);
      doc.removeFields(FieldNames.MVP);

      for (int i = 0; i < props_mvp.length; i++)
      {
         if (!propName.equals(props_mvp[i].stringValue()))
         {
            doc.add(props_mvp[i]);
         }
      }

      doc.removeFields(FieldNames.createPropertyFieldName(propName));
      doc.removeField(FieldNames.createFieldLengthName(propName));
      if (/* propType == PropertyType.URI || */propType == PropertyType.STRING || propType == PropertyType.BINARY)
      {
         doc.removeFields(FieldNames.createFullTextFieldName(propName));
      }
   }

   /**
    * Rename.
    * 
    * @param doc the doc
    * @param nodeData the node data
    * @throws RepositoryException the repository exception
    */
   public void rename(final Document doc, final NodeData nodeData) throws RepositoryException
   {
      final QPathEntry[] qe = nodeData.getQPath().getEntries();
      final String name = resolver.formatPathElement(qe[qe.length - 1]);
      doc.getField(FieldNames.LABEL).setValue(name);
   }

   /**
    * Update property.
    * 
    * @param doc the doc
    * @param propertyData the property data
    * @throws RepositoryException the repository exception
    */
   public void updateProperty(final Document doc, final PropertyData propertyData) throws RepositoryException
   {
      final int propType = propertyData.getType();
      final String propName = resolver.createJCRName(propertyData.getQPath().getName()).getAsString();
      if (/* propType == PropertyType.BINARY || */doc == null)
      {
         throw new UnsupportedRepositoryOperationException("Wrong method use removeNonBinaryProperty");
      }

      // remove mvp flag
      // !!!!No need to update MVP flag, it newer change
      // Field[] props_mvp = doc.getFields(FieldNames.MVP);
      // doc.removeFields(FieldNames.MVP);
      //
      // for (int i = 0; i < props_mvp.length; i++) {
      // if (!propName.equals(props_mvp[i].stringValue()))
      // doc.add(props_mvp[i]);
      // }

      // remove value
      doc.removeFields(FieldNames.createPropertyFieldName(propName));
      // remove length
      doc.removeField(FieldNames.createFieldLengthName(propName));

      if (/* propType == PropertyType.URI || */propType == PropertyType.STRING || propType == PropertyType.BINARY)
      {
         doc.removeFields(FieldNames.createFullTextFieldName(propName));
      }

      final List<ValueData> data = propertyData.getValues();

      if (data == null)
      {
         LOG.warn("null value found at property " + propertyData.getQPath().getAsString());
      }

      ExtendedValue val = null;
      final InternalQName name = propertyData.getQPath().getName();
      for (final ValueData value : data)
      {
         val = (ExtendedValue)vFactory.loadValue(((AbstractPersistedValueData)value).createTransientCopy(), propType);

         switch (propType)
         {
            case PropertyType.BOOLEAN :
               if (isIndexed(name))
               {
                  addBooleanValue(doc, propName, Boolean.valueOf(val.getBoolean()));
               }
               break;
            case PropertyType.DATE :
               if (isIndexed(name))
               {
                  addCalendarValue(doc, propName, val.getDate());
               }
               break;
            case PropertyType.DOUBLE :
               if (isIndexed(name))
               {
                  addDoubleValue(doc, propName, new Double(val.getDouble()));
               }
               break;
            case PropertyType.LONG :
               if (isIndexed(name))
               {
                  addLongValue(doc, propName, new Long(val.getLong()));
               }
               break;
            // case PropertyType.WEAKREFERENCE:
            case PropertyType.REFERENCE :
               if (isIndexed(name))
               {
                  addReferenceValue(doc, propName, val.getString());
               }
               break;
            case PropertyType.PATH :
               if (isIndexed(name))
               {
                  addPathValue(doc, propName, val.getString());
               }
               break;
            // case PropertyType.URI:
            case PropertyType.STRING :
               if (isIndexed(name))
               {
                  // never fulltext index jcr:uuid String
                  if (name.equals(Constants.JCR_UUID))
                  {
                     this.addStringValue(doc, propName, val.getString(), false, false, DEFAULT_BOOST);
                  }
                  else
                  {
                     this.addStringValue(doc, propName, val.getString(), true, isIncludedInNodeIndex(name),
                        getPropertyBoost(name));
                  }
               }
               break;
            case PropertyType.NAME :
               // jcr:primaryType and jcr:mixinTypes are required for correct
               // node type resolution in queries
               if (isIndexed(name) || name.equals(Constants.JCR_PRIMARYTYPE) || name.equals(Constants.JCR_MIXINTYPES))
               {
                  addNameValue(doc, propName, val.getString());
               }
               break;
            case ExtendedPropertyType.PERMISSION :
               break;
            //            case PropertyType.DECIMAL :
            //               if (isIndexed(name))
            //                  addDecimalValue(doc, propName, val.getDecimal());
            //               break;
            default :
               throw new IllegalArgumentException("illegal internal value type " + propType);
         }
      }
      addLengthField(doc, propName, data, propType);
      if (data.size() > 1)
      {
         // real multi-valued
         addMVPName(doc, propertyData.getQPath().getName());
      }
   }

   /**
    * Adds the string representation of the boolean value to the document as the
    * named field.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addBooleanValue(final Document doc, final String fieldName, final Object internalValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, internalValue.toString(), false));
   }

   /**
    * Adds the calendar value to the document as the named field. The calendar
    * value is converted to an indexable string value using the {@link DateTools}
    * class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addCalendarValue(final Document doc, final String fieldName, final Object internalValue)
   {
      final Calendar value = (Calendar)internalValue;
      // long millis = value.getTimeInMillis();
      // doc.add(createFieldWithoutNorms(fieldName,
      // DateTools.timeToString(millis), false));
      doc.add(createFieldWithoutNorms(fieldName, DateTools.dateToString(value.getTime(),
         DateTools.Resolution.MILLISECOND), false));
   }

   //   /**
   //    * Adds the double value to the document as the named field. The double value
   //    * is converted to an indexable string value using the {@link DoubleField}
   //    * class.
   //    * 
   //    * @param doc The document to which to add the field
   //    * @param fieldName The name of the field to add
   //    * @param internalValue The value for the field to add to the document.
   //    */
   //   private void addDecimalValue(final Document doc, final String fieldName, final Object internalValue)
   //   {
   //      doc.add(createFieldWithoutNorms(fieldName, JCRNumberTools.bigDecimalToString((BigDecimal)internalValue), false));
   //   }

   /**
    * Adds the double value to the document as the named field. The double value
    * is converted to an indexable string value using the {@link DoubleField}
    * class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addDoubleValue(final Document doc, final String fieldName, final Object internalValue)
   {
      final double doubleVal = ((Double)internalValue).doubleValue();
      doc.add(createFieldWithoutNorms(fieldName, ExtendedNumberTools.doubleToString(doubleVal), false));
   }

   /**
    * Adds the length field.
    * 
    * @param doc the doc
    * @param fieldName the field name
    * @param data the data
    * @param type the type
    * @throws RepositoryException the repository exception
    */
   private void addLengthField(final Document doc, final String fieldName, final List<ValueData> data, final int type)
      throws RepositoryException
   {
      for (final ValueData vData : data)
      {
         TransientValueData trData = null;
         if (vData instanceof TransientValueData)
         {
            trData = (TransientValueData)vData;
         }
         else
         {
            trData = ((AbstractPersistedValueData)vData).createTransientCopy();
         }

         // TODO : check it
         //         final Value value = vFactory.loadValue(trData, type);
         doc.add(new Field(FieldNames.createFieldLengthName(fieldName), //
            NumberTools.longToString(trData.getLength()), //
            Store.YES, //
            Index.NOT_ANALYZED_NO_NORMS));
         // computed by 3.6.7 Value Length
         //         if (type == PropertyType.BINARY)
         //            doc.add(new Field(FieldNames.createFieldLengthName(fieldName), //
         //               NumberTools.longToString(trData.getLength()), //
         //               Store.YES, //
         //               Index.NOT_ANALYZED_NO_NORMS));
         //         else
         //            doc.add(new Field(FieldNames.createFieldLengthName(fieldName), //
         //               NumberTools.longToString(value.getString().length()), //
         //               Store.YES, //
         //               Index.NOT_ANALYZED_NO_NORMS));
      }
   }

   /**
    * Adds the long value to the document as the named field. The long value is
    * converted to an indexable string value using the {@link LongField} class.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addLongValue(final Document doc, final String fieldName, final Object internalValue)
   {
      final long longVal = ((Long)internalValue).longValue();
      // doc.add(createFieldWithoutNorms(fieldName,
      // LongField.longToString(longVal), false));
      doc.add(createFieldWithoutNorms(fieldName, NumberTools.longToString(longVal), false));
   }

   /**
    * Adds a {@link FieldNames#MVP} field to <code>doc</code> with the resolved
    * <code>name</code> using the internal search index namespace mapping.
    * 
    * @param doc the lucene document.
    * @param name the name of the multi-value property.
    * @throws RepositoryException if any repository errors
    */
   private void addMVPName(final Document doc, final InternalQName name) throws RepositoryException
   {
      final String propName = resolver.createJCRName(name).getAsString();
      doc.add(new Field(FieldNames.MVP, propName, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
   }

   /**
    * Adds the name value to the document as the named field. The name value is
    * converted to an indexable string treating the internal value as a qualified
    * name and mapping the name space using the name space mappings with which
    * this class has been created.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addNameValue(final Document doc, final String fieldName, final Object internalValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, internalValue.toString(), false));
   }

   /**
    * Adds the path value to the document as the named field. The path value is
    * converted to an indexable string value using the name space mappings with
    * which this class has been created.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addPathValue(final Document doc, final String fieldName, final Object internalValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, internalValue.toString(), false));
   }

   /**
    * Adds the property name to the lucene _:PROPERTIES_SET field.
    * 
    * @param doc the document.
    * @param name the name of the property.
    * @throws RepositoryException if any repository errors
    */
   private void addPropertyName(final Document doc, final InternalQName name) throws RepositoryException
   {
      final String fieldName = resolver.createJCRName(name).getAsString();
      doc.add(new Field(FieldNames.PROPERTIES_SET, fieldName, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
   }

   //   /**
   //    * Creates a field of name <code>fieldName</code> with the value of <code>
   //    * internalValue</code>
   //    * . The created field is indexed without norms.
   //    * 
   //    * @param fieldName The name of the field to add
   //    * @param internalValue The value for the field to add to the document.
   //    * @param store <code>true</code> if the value should be stored,
   //    *          <code>false</code> otherwise
   //    */
   //   private Field createFieldWithoutNorms(String fieldName, String internalValue, boolean store)
   //   {
   //      Field field =
   //         new Field(FieldNames.PROPERTIES, FieldNames.createNamedValue(fieldName, internalValue), true ? Field.Store.YES
   //            : Field.Store.NO, Field.Index.NO_NORMS, Field.TermVector.NO);
   //      return field;
   //   }

   /**
    * Adds the reference value to the document as the named field. The value's
    * string representation is added as the reference data. Additionally the
    * reference data is stored in the index.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    */
   private void addReferenceValue(final Document doc, final String fieldName, final Object internalValue)
   {
      doc.add(createFieldWithoutNorms(fieldName, internalValue.toString(), true));
   }

   //   /**
   //   * Creates a fulltext field for the reader <code>value</code>.
   //   *
   //   * @param value the reader value.
   //   * @return a lucene field.
   //   * @deprecated
   //   */
   //   private Field createFulltextField(Reader value)
   //   {
   //      if (supportHighlighting)
   //      {
   //         // need to create a string value
   //         StringBuffer textExtract = new StringBuffer();
   //         char[] buffer = new char[1024];
   //         int len;
   //         try
   //         {
   //            while ((len = value.read(buffer)) > -1)
   //               textExtract.append(buffer, 0, len);
   //         }
   //         catch (IOException e)
   //         {
   //            LOG.warn("Exception reading value for fulltext field: " + e.getMessage());
   //            LOG.debug("Dump:", e);
   //         }
   //         finally
   //         {
   //            try
   //            {
   //               value.close();
   //            }
   //            catch (IOException e)
   //            {
   //               // ignore
   //            }
   //         }
   //         return createFulltextField(textExtract.toString());
   //      }
   //      else
   //         return new Field(FieldNames.FULLTEXT, value);
   //   }
   //
   //   /**
   //   * Creates a fulltext field for the string <code>value</code>.
   //   *
   //   * @param value the string value.
   //   * @return a lucene field.
   //   * @deprecated
   //   */
   //   private Field createFulltextField(String value)
   //   {
   //      if (supportHighlighting)
   //      {
   //         // store field compressed if greater than 16k
   //         Field.Store stored = Field.Store.YES;
   //         // TODO make the stored parameter be configurable. COMPRESS or only
   //         // Store.YES
   //         if (value.length() > 0x4000)
   //            stored = Field.Store.COMPRESS;
   //         else
   //            stored = Field.Store.YES;
   //         return new Field(FieldNames.FULLTEXT, value, stored, Field.Index.ANALYZED, Field.TermVector.WITH_OFFSETS);
   //      }
   //      else
   //         return new Field(FieldNames.FULLTEXT, value, Field.Store.NO, Field.Index.ANALYZED);
   //   }
   //
   //   /**
   //    * Adds the string value to the document both as the named field and
   //    * optionally for full text indexing if <code>tokenized</code> is
   //    * <code>true</code>.
   //    * 
   //    * @param doc The document to which to add the field
   //    * @param fieldName The name of the field to add
   //    * @param internalValue The value for the field to add to the document.
   //    * @param tokenized If <code>true</code> the string is also tokenized and
   //    *          fulltext indexed.
   //    */
   //   private void addStringValue(final Document doc, final String fieldName, final Object internalValue,
   //      final boolean tokenized)
   //   {
   //      this.addStringValue(doc, fieldName, internalValue, tokenized, true, DEFAULT_BOOST);
   //   }

   /**
    * Adds the string value to the document both as the named field and
    * optionally for full text indexing if <code>tokenized</code> is
    * <code>true</code>.
    * 
    * @param doc The document to which to add the field
    * @param fieldName The name of the field to add
    * @param internalValue The value for the field to add to the document.
    * @param tokenized If <code>true</code> the string is also tokenized and
    *          fulltext indexed.
    * @param includeInNodeIndex If <code>true</code> the string is also tokenized
    *          and added to the node scope fulltext index.
    * @param boost the boost value for this string field.
    */
   private void addStringValue(final Document doc, final String fieldName, final Object internalValue,
      final boolean tokenized, final boolean includeInNodeIndex, final float boost)
   {
      // simple String
      final String stringValue = (String)internalValue;
      doc.add(createFieldWithoutNorms(fieldName, stringValue, false));
      if (tokenized)
      {
         if (stringValue.length() == 0)
         {
            return;
         }

         // create fulltext index on property
         final Field f =
            new Field(FieldNames.createFullTextFieldName(fieldName), stringValue, Field.Store.NO, Field.Index.ANALYZED,
               Field.TermVector.NO);
         f.setBoost(boost);
         doc.add(f);
         //         if (includeInNodeIndex)
         //            // also create fulltext index of this value
         //            doc.add(createFulltextField(stringValue));
      }
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
      //      final Field field =
      //         new Field(FieldNames.createPropertyFieldName(fieldName), internalValue, true ? Field.Store.YES
      //            : Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO);
      final Field field =
         new Field(FieldNames.createPropertyFieldName(fieldName), internalValue, Field.Store.YES,
            Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.NO);
      return field;
   }

   //   /**
   //   * @return the boost value for this {@link #node} state.
   //   */
   //   private float getNodeBoost()
   //   {
   //      /*      if (indexingConfig == null)
   //               return DEFAULT_BOOST;
   //            else
   //               return indexingConfig.getNodeBoost(node);
   //      */
   //      return DEFAULT_BOOST;
   //
   //   }

   /**
   * Returns the boost value for the given property name.
   *
   * @param propertyName the name of a property.
   * @return the boost value for the given property name.
   */
   private float getPropertyBoost(final InternalQName propertyName)
   {
      //      if (indexingConfig == null)
      //         return DEFAULT_BOOST;
      //      else
      //         return indexingConfig.getPropertyBoost(node, propertyName);
      return DEFAULT_BOOST;
   }

   /**
   * Returns <code>true</code> if the property with the given name should also
   be added to the
   node
   * scope index.
   *
   * @param propertyName the name of a property.
   * @return <code>true</code> if it should be added to the node scope index;
   <code>false</code>
   * otherwise.
   */
   private boolean isIncludedInNodeIndex(final InternalQName propertyName)
   {
      //      if (indexingConfig == null)
      //         return true;
      //      else
      //         return indexingConfig.isIncludedInNodeScopeIndex(node, propertyName);
      return true;
   }

   /**
   * Returns <code>true</code> if the property with the given name should be
   indexed.
   *
   * @param propertyName name of a property.
   * @return <code>true</code> if the property should be fulltext indexed;
   <code>false</code>
   * otherwise.
   */
   private boolean isIndexed(final InternalQName propertyName)
   {
      return true;
   }
}
