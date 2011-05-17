/**
 * Copyright (C) 2010  BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA  02110-1301, USA.
 **/
package org.ow2.bonita.services;

/**
 * 
 * @author Matthieu Chaffotte
 *
 */
class QueryFormatter {

  static final String OR = "OR";
  static final String AND = "AND";
  static final String TO = "TO";
  static final String L_PARENTHESIS = "(";
  static final String R_PARENTHESIS = ")";
  static final String L_SQUARE_BRACKET = "[";
  static final String R_SQUARE_BRACKET = "]";
  static final String L_CURLY_BRACKET = "{";
  static final String R_CURLY_BRACKET = "}";
  static final String FIELD_VALUE_SEPARATOR = ":";
  static final String WILDCARD = "*";

  protected static String[] specialCharacters =
    new String[] {"\\", "+", "-", "&&", "||", "!",
    L_PARENTHESIS, R_PARENTHESIS, L_CURLY_BRACKET, R_CURLY_BRACKET, L_SQUARE_BRACKET, R_SQUARE_BRACKET,
    "^", "\"", "~", WILDCARD, "?", FIELD_VALUE_SEPARATOR};

  protected static String escapeSpecialCharacters(String value) {
    String temp = value;
    for (String specialCharacter : specialCharacters) {
      temp = temp.replace(specialCharacter, "\\" + specialCharacter);
    }
    return temp;
  }

  protected static String field(String fieldName, String value) {
    StringBuilder field = new StringBuilder(fieldName);
    field.append(FIELD_VALUE_SEPARATOR).append(value);
    return field.toString();
  }

  protected static String startsWith(String value) {
    String formattedValue = escapeSpecialCharacters(value);
    StringBuilder starts = new StringBuilder(formattedValue);
    starts.append(WILDCARD);
    return starts.toString();
  }

  protected static String equals(String value) {
    String formattedValue = escapeSpecialCharacters(value);
    return formattedValue;
  }

  protected static String ranges(final String from, final String to, boolean exclusive) {
    StringBuilder range = new StringBuilder();
    String left = L_SQUARE_BRACKET;
    String right = R_SQUARE_BRACKET;
    if (exclusive) {
      left = L_CURLY_BRACKET;
      right = R_CURLY_BRACKET;
    }
    String formattedFrom = escapeSpecialCharacters(from);
    String formattedTo = escapeSpecialCharacters(to);
    range.append(left).append(formattedFrom).append(" ")
    .append(TO).append(" ").append(formattedTo).append(right);
    return range.toString();
  }

}
