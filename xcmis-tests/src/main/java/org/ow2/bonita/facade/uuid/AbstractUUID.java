/**
 * Copyright (C) 2006  Bull S. A. S.
 * Bull, Rue Jean Jaures, B.P.68, 78340, Les Clayes-sous-Bois
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
package org.ow2.bonita.facade.uuid;

import java.io.Serializable;

/**
 * Abstract parent class for all UUID classes.
 */
public abstract class AbstractUUID implements Serializable {

  private static final long serialVersionUID = 2564196915744578620L;
  protected String value;
  protected static final String SEPARATOR = "--";

  protected AbstractUUID() { }

  protected AbstractUUID(AbstractUUID src) {
    this.value = src.getValue();
  }

  AbstractUUID(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj.getClass().equals(this.getClass()))) {
      return false;
    }
    AbstractUUID other = (AbstractUUID)obj;
    if (other.value == null) {
      return value == null;
    }
    return other.value.equals(value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  public String getValue() {
    return value;
  }

}
