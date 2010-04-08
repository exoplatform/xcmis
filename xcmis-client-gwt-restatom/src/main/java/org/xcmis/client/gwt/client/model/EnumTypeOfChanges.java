package org.xcmis.client.gwt.client.model;

public enum EnumTypeOfChanges {

   CREATED("created"), UPDATED("updated"), DELETED("deleted"), SECURITY("security");

   /**
   * Value.
   */
   private final String value;

   /**
   * @param v value
   */
   EnumTypeOfChanges(String v)
   {
      value = v;
   }

   /**
   * @return {@link String}
   */
   public String value()
   {
      return value;
   }

   /**
   * @param v value
   * @return {@link EnumTypeOfChanges} enum value
   */
   public static EnumTypeOfChanges fromValue(String v)
   {
      for (EnumTypeOfChanges c : EnumTypeOfChanges.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
