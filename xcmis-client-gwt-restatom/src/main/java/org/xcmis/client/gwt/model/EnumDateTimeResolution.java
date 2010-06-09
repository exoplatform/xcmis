package org.xcmis.client.gwt.model;

public enum EnumDateTimeResolution {

   YEAR("year"), DATE("date"), TIME("time");
   private final String value;

   /**
    * @param v value
    */
   EnumDateTimeResolution(String v)
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
    * @return {@link EnumDateTimeResolution} enum
    */
   public static EnumDateTimeResolution fromValue(String v)
   {
      for (EnumDateTimeResolution c : EnumDateTimeResolution.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
