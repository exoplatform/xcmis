package org.xcmis.spi.object.impl;

import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.spi.object.CmisObjectInFolder;
import org.xcmis.spi.object.CmisObjectInFolderList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectInFolderListImpl.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectInFolderListImpl implements CmisObjectInFolderList
{

   protected List<CmisObjectInFolder> objects;

   protected boolean hasMoreItems;

   protected BigInteger numItems;

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#getObjects()
    */
   public List<CmisObjectInFolder> getObjects()
   {
      if (objects == null)
      {
         objects = new ArrayList<CmisObjectInFolder>();
      }
      return this.objects;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#isHasMoreItems()
    */
   public boolean isHasMoreItems()
   {
      return hasMoreItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#setHasMoreItems(boolean)
    */
   public void setHasMoreItems(boolean value)
   {
      this.hasMoreItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#getNumItems()
    */
   public BigInteger getNumItems()
   {
      return numItems;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#setNumItems(java.math.BigInteger)
    */
   public void setNumItems(BigInteger value)
   {
      this.numItems = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderList#toCmisObjectInFolderListType()
    */
   public CmisObjectInFolderListType toCmisObjectInFolderListType()
   {
      CmisObjectInFolderListType result = new CmisObjectInFolderListType();
      result.setHasMoreItems(this.hasMoreItems);
      result.setNumItems(this.numItems);
      if (objects != null)
      {
         for (CmisObjectInFolder obj : objects)
         {
            result.getObjects().add(obj.toCmisObjectInFolderType());
         }
      }
      return result;
   }

}
