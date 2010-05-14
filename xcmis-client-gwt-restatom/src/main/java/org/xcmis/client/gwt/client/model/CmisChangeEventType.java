package org.xcmis.client.gwt.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CmisChangeEventType
{

   /**
   * Change type.
   */
   protected EnumTypeOfChanges changeType;

   /**
   * Change time.
   */
   protected Date changeTime;

   /**
    * Any other object.
    */
   protected List<Object> any;

   /**
   * @return {@link EnumTypeOfChanges}
   */
   public EnumTypeOfChanges getChangeType()
   {
      return changeType;
   }

   /**
   * @param value type of change
   */
   public void setChangeType(EnumTypeOfChanges value)
   {
      this.changeType = value;
   }

   /**
   * @return Date time of change
   */
   public Date getChangeTime()
   {
      return changeTime;
   }

   /**
   * @param value time of change
   */
   public void setChangeTime(Date value)
   {
      this.changeTime = value;
   }

   /**
   * @return List<Object> any object
   */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

}
