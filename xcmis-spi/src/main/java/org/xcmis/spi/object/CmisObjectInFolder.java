package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectInFolderType;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectInFolder.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObjectInFolder
{

   /**
    * Gets the value of the object property.
    * 
    * @return
    *     possible object is
    *     {@link CmisObject }
    *     
    */
   public abstract CmisObject getObject();

   /**
    * Sets the value of the object property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisObject }
    *     
    */
   public abstract void setObject(CmisObject value);

   /**
    * Gets the value of the pathSegment property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public abstract String getPathSegment();

   /**
    * Sets the value of the pathSegment property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public abstract void setPathSegment(String value);

   /**
    * Create an CmisObjectInFolderType instance from current object.
    * @return CmisObjectInFolderType
    */
   public CmisObjectInFolderType toCmisObjectInFolderType();

}