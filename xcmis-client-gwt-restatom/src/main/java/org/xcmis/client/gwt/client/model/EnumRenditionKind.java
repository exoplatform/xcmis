package org.xcmis.client.gwt.client.model;

public enum EnumRenditionKind {

   CMIS_THUMBNAIL("cmis:thumbnail");
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumRenditionKind(String v)
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
    * @return {@link EnumRenditionKind} enum value
    */
   public static EnumRenditionKind fromValue(String v)
   {
      for (EnumRenditionKind c : EnumRenditionKind.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
