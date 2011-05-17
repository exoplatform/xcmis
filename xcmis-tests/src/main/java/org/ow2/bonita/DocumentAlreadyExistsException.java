/**
 * Copyright (C) 2011  BonitaSoft S.A.
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
package org.ow2.bonita;


/**
 * Exception thrown when a document with the same name already exists
 * @author Nicolas Chabanoles
 *
 */
public class DocumentAlreadyExistsException extends DocumentationCreationException {

  private static final long serialVersionUID = -4313414187229317438L;
  private final String name;

  public DocumentAlreadyExistsException(final String id, final String name) {
    super(name);
    this.name = name;
  }

  public DocumentAlreadyExistsException(DocumentAlreadyExistsException e) {
    super(e);
    this.name = e.getName();
  }

  public String getName() {
    return this.name;
  }

}
