package org.xcmis.spi.object.impl;

import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.spi.object.CmisObjectInFolder;
import org.xcmis.spi.object.CmisObjectInFolderContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisObjectInFolderContainerImpl.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisObjectInFolderContainerImpl implements CmisObjectInFolderContainer
{

   protected CmisObjectInFolder objectInFolder;

   protected List<CmisObjectInFolderContainer> children;

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#getObjectInFolder()
    */
   public CmisObjectInFolder getObjectInFolder()
   {
      return objectInFolder;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#setObjectInFolder(org.xcmis.spi.object.CmisObjectInFolder)
    */
   public void setObjectInFolder(CmisObjectInFolder value)
   {
      this.objectInFolder = value;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#getChildren()
    */
   public List<CmisObjectInFolderContainer> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<CmisObjectInFolderContainer>();
      }
      return this.children;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#toCmisObjectInFolderContainerType()
    */
   public CmisObjectInFolderContainerType toCmisObjectInFolderContainerType()
   {
      CmisObjectInFolderContainerType result = new CmisObjectInFolderContainerType();
      if (children != null)
      {
         for (CmisObjectInFolderContainer obj : children)
         {
            result.getChildren().add(obj.toCmisObjectInFolderContainerType());
         }
      }
      result.setObjectInFolder(this.objectInFolder.toCmisObjectInFolderType());
      return result;
   }
}
