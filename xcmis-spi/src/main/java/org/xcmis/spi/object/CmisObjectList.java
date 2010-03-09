package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectListType;

import java.math.BigInteger;
import java.util.List;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectList.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObjectList
{

   /**
    * Gets the value of the objects property.
    * 
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObject }
    * 
    */
   public abstract List<CmisObject> getObjects();

   /**
    * Gets the value of the hasMoreItems property.
    * 
    */
   public abstract boolean isHasMoreItems();

   /**
    * Sets the value of the hasMoreItems property.
    * 
    */
   public abstract void setHasMoreItems(boolean value);

   /**
    * Gets the value of the numItems property.
    * 
    * @return
    *     possible object is
    *     {@link BigInteger }
    *     
    */
   public abstract BigInteger getNumItems();

   /**
    * Sets the value of the numItems property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigInteger }
    *     
    */
   public abstract void setNumItems(BigInteger value);

   /**
    * Create an CmisObjectListType instance from current object.
    * @return CmisObjectListType
    */
   public CmisObjectListType toCmisObjectList();

}