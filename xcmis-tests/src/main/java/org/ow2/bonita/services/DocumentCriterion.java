/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ow2.bonita.services;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Baptiste Mesta
 *
 */
public class DocumentCriterion implements Serializable {

  private static final long serialVersionUID = 8636952840023531275L;

  private final DocumentSearchBuilder builder;
  private final DocumentIndex fieldName;
  private Object value;
  private Object to;
  private Object from;

  private Collection<?> in;

  /**
   * @return the fieldName
   */
  public DocumentIndex getField() {
    return fieldName;
  }


  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }


  /**
   * @return the to
   */
  public Object getTo() {
    return to;
  }


  /**
   * @return the from
   */
  public Object getFrom() {
    return from;
  }


  /**
   * @param index
   * @param builder
   */
  public DocumentCriterion(DocumentIndex index, DocumentSearchBuilder builder) {
    this.fieldName = index;
    this.builder = builder;
    
  }
  

  public DocumentCriterion equalsTo(Object value) {
    this.value = value;
    return this;
  }

  public DocumentCriterion between(Object from, Object to) {
    this.from = from;
    this.to = to;
    return this;
  }
  
  public DocumentCriterion in(Collection<?> values) {
    this.in = values;
    return this;
  }


  
  public DocumentSearchBuilder rightParenthesis() {
    builder.rightParenthesis();
    return builder;
  }

  public DocumentSearchBuilder or() {
    builder.or();
    return builder;
  }

  public DocumentSearchBuilder and() {
    builder.and();
    return builder;
  }
  

  public DocumentSearchBuilder allVersion() {
    builder.allVersion();
    return builder;
  }
  
  public DocumentSearchBuilder latestVersion() {
    builder.latestVersion();
    return builder;
  }


  /**
   * @return
   */
  public Collection<?> getValues() {
    return in;
  }



}
