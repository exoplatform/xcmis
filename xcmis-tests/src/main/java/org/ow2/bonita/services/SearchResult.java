package org.ow2.bonita.services;

import java.util.List;

import org.ow2.bonita.services.Document;

public class SearchResult {

  private final int pageNumItems;
  private final List<Document> documents;

  /**
   * @param documents
   * @param pageNumItems
   */
  public SearchResult(List<Document> documents, int pageNumItems) {
    this.documents = documents;
    this.pageNumItems = pageNumItems;
  }

  public List<Document> getDocuments() {
    return documents;
  }

  public int getCount() {
    return pageNumItems;
  }

}
