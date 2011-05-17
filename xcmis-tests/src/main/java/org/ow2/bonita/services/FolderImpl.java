package org.ow2.bonita.services;



public class FolderImpl implements Folder {

  private String id;
  private String name;
  private final String folderId;

  public FolderImpl(final String name, final String folderId) {
    this.name = name;
    this.folderId = folderId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getParentFolderId() {
    return folderId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((folderId == null) ? 0 : folderId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FolderImpl other = (FolderImpl) obj;
    if (folderId == null) {
      if (other.folderId != null)
        return false;
    } else if (!folderId.equals(other.folderId))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
