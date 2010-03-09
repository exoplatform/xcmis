package org.xcmis.spi.object;

import org.xcmis.core.CmisObjectType;
import org.xcmis.messaging.CmisObjectParentsType;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectParents.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObjectParents
{

   /**
    * Gets the value of the object property.
    * 
    * @return
    *     possible object is
    *     {@link CmisObjectType }
    *     
    */
   public abstract CmisObject getObject();

   /**
    * Sets the value of the object property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisObjectType }
    *     
    */
   public abstract void setObject(CmisObject value);

   /**
    * Gets the value of the relativePathSegment property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public abstract String getRelativePathSegment();

   /**
    * Sets the value of the relativePathSegment property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public abstract void setRelativePathSegment(String value);

   /**
    * Create an CmisObjectParentsType instance from current object.
    * @return CmisObjectParentsType
    */
   public abstract CmisObjectParentsType toCmisObjectParentsType();

}