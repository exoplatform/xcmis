/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.spi.utils;

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MimeType.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class MimeType
{
   static class ParameterParser
   {

      /**
       * Parameter separator.
       */
      private static final char SEPARATOR = ';';

      private static final String SEPARTORS = "()<>@,;:\"\\/[]?={}";

      /**
       * Current position in the parsed string.
       */
      private int pos = 0;

      /**
       * Token's start.
       */
      private int i1 = 0;

      /**
       * Token's end.
       */
      private int i2 = 0;

      /**
       * String to be parsed.
       */
      private char[] chars = null;

      /**
       * Parsed string length.
       */
      private int length = 0;

      /**
       * Parse header string for parameters.
       *
       * @param source source header string
       * @return header parameter
       * @throws ParseException if string can't be parsed or contains illegal
       *         characters
       */
      public Map<String, String> parse(String source) throws ParseException
      {
         init(source);

         if (pos < 0)
         {
            return null;
         }

         pos++; // skip first ';'
         Map<String, String> m = null;
         while (hasChars())
         {

            String name = readToken(new char[]{'=', SEPARATOR});

            String value = null;
            if (hasChars() && chars[pos] == '=')
            {
               pos++; // skip '='
               if (chars[pos] == '"')
               {
                  value = readQuotedString();
               }
               else
               {
                  value = readToken(new char[]{SEPARATOR});
               }
            }

            if (hasChars() && chars[pos] == SEPARATOR)
            {
               pos++; // skip ';'
            }

            if (name != null && name.length() > 0)
            {
               if (m == null)
               {
                  m = new HashMap<String, String>();
               }
               m.put(name, value);
            }

         }
         return m;
      }

      /**
       * Check does char array <tt>chs</tt> contains char <tt>c</tt>.
       *
       * @param c char
       * @param chs char array
       * @return true if char array contains character <tt>c</tt>, false
       *         otherwise
       */
      private boolean checkChar(char c, char[] chs)
      {
         for (int i = 0; i < chs.length; i++)
         {
            if (c == chs[i])
            {
               return true;
            }
         }
         return false;
      }

      private String filterEscape(String token)
      {
         StringBuffer sb = new StringBuffer();
         //    boolean escape = false;
         int strlen = token.length();

         for (int i = 0; i < strlen; i++)
         {
            char c = token.charAt(i);
            //      escape = !escape && c == '\\';

            if (c == '\\' && i < strlen - 1 && token.charAt(i + 1) == '"')
            {
               continue;
            }
            sb.append(c);
         }

         return sb.toString();
      }

      /**
       * @param removeQuotes must leading and trailing quotes be skipped
       * @return parsed token
       */
      private String getToken(boolean removeQuotes)
      {
         // leading whitespace
         while ((i1 < i2) && Character.isWhitespace(chars[i1]))
         {
            i1++;
         }
         // tail whitespace
         while ((i2 > i1) && Character.isWhitespace(chars[i2 - 1]))
         {
            i2--;
         }

         // remove quotes
         if (removeQuotes && chars[i1] == '"' && chars[i2 - 1] == '"')
         {
            i1++;
            i2--;
         }

         String token = null;
         if (i2 > i1)
         {
            token = new String(chars, i1, i2 - i1);
         }

         return token;
      }

      /**
       * Check are there any character to be parsed.
       *
       * @return true if there are unparsed characters, false otherwise
       */
      private boolean hasChars()
      {
         return pos < length;
      }

      /**
       * Initialize character array for parsing.
       *
       * @param source source string for parsing
       */
      private void init(String source)
      {
         // looking for start parameters position
         // e.g. text/plain ; charsert=utf-8
         pos = source.indexOf(SEPARATOR);
         if (pos < 0)
         {
            // header string does not contains parameters
            return;
         }
         chars = source.toCharArray();
         length = chars.length;
         i1 = 0;
         i2 = 0;
      }

      private int isToken(String token)
      {
         for (int i = 0; i < token.length(); i++)
         {
            char c = token.charAt(i);
            if (c >= 127 || SEPARTORS.indexOf(c) != -1)
            {
               return i;
            }
         }

         return -1;
      }

      /**
       * Process quoted string, it minds remove escape characters for quotes.
       *
       * @see HeaderHelper#filterEscape(String)
       *
       * @return processed string
       * @throws ParseException if string can't be parsed
       */
      private String readQuotedString() throws ParseException
      {
         i1 = pos;
         i2 = pos;

         // indicate was previous character '\'
         boolean escape = false;
         // indicate is final '"' already found
         boolean qoute = false;

         while (hasChars())
         {
            char c = chars[pos];

            if (c == SEPARATOR && !qoute)
            {
               break;
            }

            if (c == '"' && !escape)
            {
               qoute = !qoute;
            }

            escape = !escape && c == '\\';
            pos++;
            i2++;
         }

         if (qoute)
         {
            throw new ParseException("String must be ended with qoute.", pos);
         }

         String token = getToken(true);
         if (token != null)
         {
            token = filterEscape(getToken(true));
         }
         return token;
      }

      /**
       * Read token from source string, token is not quoted string and does not
       * contains any separators. See <a
       * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html">HTTP1.1
       * specification</a>.
       *
       * @param terminators characters which indicate end of token
       * @return token
       * @throws ParseException if token contains illegal characters
       */
      private String readToken(char[] terminators) throws ParseException
      {
         i1 = pos;
         i2 = pos;
         while (hasChars())
         {
            char c = chars[pos];
            if (checkChar(c, terminators))
            {
               break;
            }
            pos++;
            i2++;
         }

         String token = getToken(false);
         if (token != null)
         {
            // check is it valid token
            int err = -1;
            if ((err = isToken(token)) != -1)
            {
               throw new ParseException("Token '" + token + "' contains not legal characters at " + err, err);
            }
         }

         return token;
      }

   }

   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

   private static final Pattern WHITESPACE_QOUTE_PATTERN = Pattern.compile("[\\s\"]");

   /**
    * Create instance of MimeType from the string.
    *
    * @param source string that represents media-type in form 'type/sub-type'.
    *        If <code>source</code> is <code>null</code> or empty then it is the
    *        same as pass '*&#47;*'. All parameters after ';' in
    *        <code>source</code> will be ignored.
    * @return MimeType
    */
   // TODO : support for parameter.
   public static MimeType fromString(String source)
   {
      if (source == null || source.length() == 0)
      {
         return new MimeType();
      }

      int p = source.indexOf('/');
      int col = source.indexOf(';');

      String type = null;
      String subType = null;

      if (p < 0 && col < 0)
      {
         return new MimeType(source, null);
      }
      else if (p > 0 && col < 0)
      {
         return new MimeType(removeWhitespaces(source.substring(0, p)), removeWhitespaces(source.substring(p + 1)));
      }
      else if (p < 0 && col > 0)
      { // there is no '/' but present ';'
         type = removeWhitespaces(source.substring(0, col));
         // sub-type is null
      }
      else
      { // presents '/' and ';'
         type = removeWhitespaces(source.substring(0, p));
         subType = source.substring(p + 1, col);
      }

      try
      {
         return new MimeType(type, subType, new ParameterParser().parse(source));
      }
      catch (ParseException pe)
      {
         throw new CmisRuntimeException(pe.getMessage(), pe);
      }

   }

   private static String removeWhitespaces(String s)
   {
      Matcher m = WHITESPACE_PATTERN.matcher(s);
      if (m.find())
      {
         return m.replaceAll("");
      }

      return s;
   }

   /** Type. */
   private final String type;

   /** Sub-type. */
   private final String subType;

   private final Map<String, String> parameters;

   public MimeType()
   {
      this(null, null);
   }

   /**
    * Create instance of MimeType.
    *
    * @param type the name of type
    * @param subType the name of sub-type
    */
   public MimeType(String type, String subType)
   {
      this.type =
         type == null || type.length() == 0 ? CmisConstants.WILDCARD : ((type = type.trim()).length() == 0
            ? CmisConstants.WILDCARD : type.toLowerCase());
      this.subType =
         subType == null || subType.length() == 0 ? CmisConstants.WILDCARD : ((subType = subType.trim()).length() == 0
            ? CmisConstants.WILDCARD : subType.toLowerCase());

      this.parameters = new HashMap<String, String>();
   }

   public MimeType(String type, String subType, Map<String, String> parameters)
   {
      this.type =
         type == null || type.length() == 0 ? CmisConstants.WILDCARD : ((type = type.trim()).length() == 0
            ? CmisConstants.WILDCARD : type.toLowerCase());
      this.subType =
         subType == null || subType.length() == 0 ? CmisConstants.WILDCARD : ((subType = subType.trim()).length() == 0
            ? CmisConstants.WILDCARD : subType.toLowerCase());

      if (parameters == null)
      {
         this.parameters = new HashMap<String, String>();
      }
      else
      {
         Map<String, String> map = new TreeMap<String, String>(new Comparator<String>()
         {
            public int compare(String o1, String o2)
            {
               return o1.compareToIgnoreCase(o2);
            }
         });
         for (Map.Entry<String, String> e : parameters.entrySet())
         {
            map.put(e.getKey().toLowerCase(), e.getValue());
         }
         this.parameters = Collections.unmodifiableMap(map);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object other)
   {
      if (other == null)
      {
         return false;
      }
      if (!(other instanceof MimeType))
      {
         return false;
      }
      MimeType otherMimeType = (MimeType)other;
      return type.equalsIgnoreCase(otherMimeType.type) && subType.equalsIgnoreCase(otherMimeType.subType)
         && parameters.equals(otherMimeType.parameters);
   }

   /**
    * @return get type
    */
   public String getSubType()
   {
      return subType;
   }

   /**
    * @return get sub-type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @return mime type parameters
    */
   public Map<String, String> getParameters()
   {
      return parameters;
   }

   public String getParameter(String name)
   {
      return parameters.get(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 9;
      hash = hash * 31 + type.hashCode();
      hash = hash * 31 + subType.hashCode();
      hash = hash * 31 + parameters.hashCode();
      return hash;
   }

   /**
    * Check is one mime-type compatible to other. Function is not commutative.
    * E.g. image/* compatible with image/png, image/jpeg, but image/png is not
    * compatible with image/*.
    *
    * @param other MimeType to be checked for compatible with this.
    * @return TRUE if MimeTypes compatible FALSE otherwise
    */
   public boolean match(MimeType other)
   {
      if (other == null)
      {
         return false;
      }
      return type.equals(CmisConstants.WILDCARD) //
         || (type.equalsIgnoreCase(other.type) //
         && (subType.equals(CmisConstants.WILDCARD) || subType.equalsIgnoreCase(other.subType)));
   }

   /**
    * {@inheritDoc}
    */
   public String getBaseType()
   {
      return type + "/" + subType;
   }

   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append(getBaseType());
      for (Map.Entry<String, String> entry : parameters.entrySet())
      {
         sb.append(';').append(entry.getKey()).append('=');
         appendWithQuote(sb, entry.getValue());
      }
      return sb.toString();
   }

   private void appendWithQuote(StringBuffer sb, String s)
   {
      if (s == null)
      {
         return;
      }
      Matcher m = WHITESPACE_QOUTE_PATTERN.matcher(s);

      if (m.find())
      {
         sb.append('"');
         appendEscapeQuote(sb, s);
         sb.append('"');
         return;
      }
      sb.append(s);
   }

   private void appendEscapeQuote(StringBuffer sb, String s)
   {
      for (int i = 0; i < s.length(); i++)
      {
         char c = s.charAt(i);
         if (c == '"')
         {
            sb.append('\\');
         }
         sb.append(c);
      }
   }
}
