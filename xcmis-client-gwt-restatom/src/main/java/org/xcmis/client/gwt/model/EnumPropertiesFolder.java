package org.xcmis.client.gwt.model;

public enum EnumPropertiesFolder {

   CMIS_PARENT_ID("cmis:parentId"), CMIS_ALLOWED_CHILD_OBJECT_TYPE_IDS("cmis:allowedChildObjectTypeIds"), CMIS_PATH(
      "cmis:path");

   /**
   * Value.
   */
   private final String value;

   /**
   * @param v value
   */
   EnumPropertiesFolder(String v)
   {
      value = v;
   }

   /**
   * @return {@link String} value
   */
   public String value()
   {
      return value;
   }

   /**
   * @param v value
   * @return {@link EnumPropertiesFolder} enum value
   */
   public static EnumPropertiesFolder fromValue(String v)
   {
      for (EnumPropertiesFolder c : EnumPropertiesFolder.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
