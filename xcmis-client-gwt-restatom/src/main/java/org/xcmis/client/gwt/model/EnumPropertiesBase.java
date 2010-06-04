package org.xcmis.client.gwt.model;

public enum EnumPropertiesBase {

   CMIS_NAME("cmis:name"), CMIS_OBJECT_ID("cmis:objectId"), CMIS_OBJECT_TYPE_ID("cmis:objectTypeId"), CMIS_BASE_TYPE_ID(
      "cmis:baseTypeId"), CMIS_CREATED_BY("cmis:createdBy"), CMIS_CREATION_DATE("cmis:creationDate"), CMIS_LAST_MODIFIED_BY(
      "cmis:lastModifiedBy"), CMIS_LAST_MODIFICATION_DATE("cmis:lastModificationDate"), CMIS_CHANGE_TOKEN(
      "cmis:changeToken");
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumPropertiesBase(String v)
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
    * @return {@link EnumPropertiesBase} enum value
    */
   public static EnumPropertiesBase fromValue(String v)
   {
      for (EnumPropertiesBase c : EnumPropertiesBase.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
