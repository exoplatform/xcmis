package org.xcmis.spi.object.impl;

import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectList.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectListImpl implements CmisObjectList
{

   protected List<CmisObject> objects;

   protected boolean hasMoreItems;

   protected BigInteger numItems;

   /**
    * @see org.xcmis.spi.object.CmisObjectList#getObjects()
    */
   public List<CmisObject> getObjects()
   {
      if (objects == null)
      {
         objects = new ArrayList<CmisObject>();
      }
      return this.objects;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#isHasMoreItems()
    */
   public boolean isHasMoreItems()
   {
      return hasMoreItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#setHasMoreItems(boolean)
    */
   public void setHasMoreItems(boolean value)
   {
      this.hasMoreItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#getNumItems()
    */
   public BigInteger getNumItems()
   {
      return numItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#setNumItems(java.math.BigInteger)
    */
   public void setNumItems(BigInteger value)
   {
      this.numItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectList#toCmisObjectList()
    */
   public CmisObjectListType toCmisObjectList()
   {
      CmisObjectListType result = new CmisObjectListType();
      result.setHasMoreItems(this.hasMoreItems);
      result.setNumItems(this.numItems);
      if (objects != null)
      {
         for (CmisObject obj : objects)
         {
            result.getObjects().add(obj.toCmisObjectType());
         }
      }
      return result;
   }

}
