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
package org.ow2.bonita;


/**
 * @author Baptiste Mesta
 *
 */
public class FolderAlreadyExistsException extends Exception {


  /**
   * 
   */
  private static final long serialVersionUID = -4879968518720277284L;
  private final String name;
  
  public FolderAlreadyExistsException(final String id, final String name) {
    super(name);
    this.name = name;
  }
  
  public FolderAlreadyExistsException(DocumentAlreadyExistsException e) {
    super(e.getMessage());
    this.name = e.getName();
  }

  /**
   * @param folderName
   * @param e
   */
  public FolderAlreadyExistsException(String folderName, Throwable t) {
    super(t);
    this.name = folderName;
  }

}
