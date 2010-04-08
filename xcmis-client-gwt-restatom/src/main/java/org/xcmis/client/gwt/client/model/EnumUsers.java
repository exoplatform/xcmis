package org.xcmis.client.gwt.client.model;

public enum EnumUsers {

   /**
    * 
    * 						This user can be used on setting ACLs to specify
    * 						the permission this
    * 						user context should have.
    * 			
    * 
    */
   CMIS_USER("cmis:user");
   
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumUsers(String v)
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
    * @return {@link EnumUsers} enum value
    */
   public static EnumUsers fromValue(String v)
   {
      for (EnumUsers c : EnumUsers.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
