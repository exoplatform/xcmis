package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderType;

import java.util.List;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectInFolderContainer.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface CmisObjectInFolderContainer
{

   /**
    * Gets the value of the objectInFolder property.
    * 
    * @return
    *     possible object is
    *     {@link CmisObjectInFolderType }
    *     
    */
   public abstract CmisObjectInFolder getObjectInFolder();

   /**
    * Sets the value of the objectInFolder property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisObjectInFolderType }
    *     
    */
   public abstract void setObjectInFolder(CmisObjectInFolder value);

   /**
    * Gets the value of the children property.
    * 
    * Objects of the following type(s) are allowed in the list
    * {@link CmisObjectInFolderContainerType }
    * 
    */
   public abstract List<CmisObjectInFolderContainer> getChildren();

   /**
    * Create an CmisObjectInFolderContainerType instance from current object.
    * @return CmisObjectInFolderContainerType
    */
   public CmisObjectInFolderContainerType toCmisObjectInFolderContainerType();
}