package org.xcmis.client.gwt.client.model;

public enum EnumSupportedPermissions {

   BASIC("basic"), REPOSITORY("repository"), BOTH("both");
   
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumSupportedPermissions(String v)
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
    * @param v
    * @return {@link EnumSupportedPermissions} enum value
    */
   public static EnumSupportedPermissions fromValue(String v)
   {
      for (EnumSupportedPermissions c : EnumSupportedPermissions.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
