package org.xcmis.spi.object;

import org.xcmis.messaging.CmisObjectInFolderContainerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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

   protected List<Object> any;

   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#getAny()
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * @see org.xcmis.spi.object.CmisObjectInFolderContainer#getOtherAttributes()
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

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
      if (this.any != null)
         result.getAny().addAll(this.any);
      if (this.otherAttributes != null)
         result.getOtherAttributes().putAll(this.otherAttributes);
      return result;
   }
}
