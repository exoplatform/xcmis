/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.xcmis.client.gwt.restatom.sample.client.application;

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.property.BooleanProperty;
import org.xcmis.client.gwt.client.model.property.CmisProperties;
import org.xcmis.client.gwt.client.model.property.DateTimeProperty;
import org.xcmis.client.gwt.client.model.property.DecimalProperty;
import org.xcmis.client.gwt.client.model.property.IdProperty;
import org.xcmis.client.gwt.client.model.property.IntegerProperty;
import org.xcmis.client.gwt.client.model.property.Property;
import org.xcmis.client.gwt.client.model.property.StringProperty;
import org.xcmis.client.gwt.client.model.property.UriProperty;
import org.xcmis.client.gwt.client.model.repository.CmisRepositoryInfo;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class SampleForm extends FlowPanel implements SamplePresenter.Display
{
   private VerticalPanel repositoryInfoPanel;

   private VerticalPanel mainPanel;

   private DecoratedTabPanel tabPanel;

   private Button createFolderButton;

   private Button createDocumentButton;

   private Button deleteButton;

   private HorizontalPanel toolBarPanel;

   private int selectedItem = -1;

   private SamplePresenter presenter;

   /**
    * @param eventBus
    */
   public SampleForm(final HandlerManager eventBus)
   {
      setWidth("100%");
      setHeight("100%");

      mainPanel = new VerticalPanel();
      mainPanel.setSpacing(20);

      repositoryInfoPanel = new VerticalPanel();
      Label title = new Label();
      title.setText("REPOSITORIES:");
      repositoryInfoPanel.add(title);
      mainPanel.add(repositoryInfoPanel);

      toolBarPanel = new HorizontalPanel();
      toolBarPanel.setSpacing(10);
      createFolderButton = createButton("Add Folder");
      createDocumentButton = createButton("Add Document");
      deleteButton = createButton("Delete");
      mainPanel.add(toolBarPanel);

      createTabPanel();

      add(mainPanel);
      RootPanel.get().add(this);

      presenter = new SamplePresenter(eventBus);
      presenter.bindDisplay(this);
   }

   /**
    * Create new button element with pointed title
    * 
    * @param title
    * @return {@link Button}
    */
   private Button createButton(String title)
   {
      Button button = new Button(title);
      button.setWidth("120px");
      button.setHeight("25px");
      button.setEnabled(false);
      toolBarPanel.add(button);
      return button;
   }

   /**
    * Create tab panel for displaying objects
    */
   private void createTabPanel()
   {
      tabPanel = new DecoratedTabPanel();
      tabPanel.setWidth("100%");
      tabPanel.setHeight("100%");

      tabPanel.addSelectionHandler(new SelectionHandler<Integer>()
      {

         public void onSelection(SelectionEvent<Integer> event)
         {
            selectedItem = event.getSelectedItem();
            deleteButton.setEnabled(true);
         }

      });

      mainPanel.add(tabPanel);
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#showObjects(java.util.List)
    */
   public void showObjects(List<AtomEntry> entries)
   {
      for (AtomEntry entry : entries)
      {
         addObject(entry);
      }
      tabPanel.setVisible(true);
   }

   /**
    * Adds object to tab panel
    * 
    * @param entry
    */
   public void addObject(AtomEntry entry)
   {
      showObjectProperties(entry);
   }

   /**
    * Display all properties of the object
    * 
    * @param entry
    */
   private void showObjectProperties(AtomEntry entry)
   {
      String name = "";
      EnumBaseObjectTypeIds baseTypeId = null;
      VerticalPanel propertyPanel = new VerticalPanel();
      CmisProperties properties = entry.getObject().getProperties();
      for (Property<?> property : properties.getProperties().values())
      {
         String propertyName = (property.getDisplayName() == null) ? property.getId() : property.getDisplayName();
         String propertyValue = "";

         if (property instanceof StringProperty)
         {
            propertyValue = properties.getString(property.getId());

            if (propertyName.equals(CMIS.CMIS_NAME))
            {
               name = propertyValue;
            }
         }
         else if (property instanceof IntegerProperty)
         {
            propertyValue = String.valueOf(properties.getInteger(property.getId()));
         }
         else if (property instanceof DateTimeProperty)
         {
            propertyValue = String.valueOf(properties.getDate(property.getId()));
         }
         else if (property instanceof IdProperty)
         {
            propertyValue = properties.getId(property.getId());

            if (propertyName.equals(CMIS.CMIS_BASE_TYPE_ID))
            {
               baseTypeId = EnumBaseObjectTypeIds.fromValue(propertyValue);
            }
         }
         else if (property instanceof BooleanProperty)
         {
            propertyValue = String.valueOf(properties.getBoolean(property.getId()));
         }
         else if (property instanceof DecimalProperty)
         {
            propertyValue = String.valueOf(properties.getDecimal(property.getId()));
         }
         else if (property instanceof UriProperty)
         {
            propertyValue = properties.getString(property.getId());
         }
         propertyPanel.add(createPropertyPanel(propertyName, propertyValue));
      }

      String url = presenter.getUrlForDelete(baseTypeId, entry.getEntryInfo().getLinks());
      propertyPanel.setTitle(url);
      tabPanel.add(propertyPanel, name);

      int lastTab = tabPanel.getElement().getChildNodes().getLength() - 1;
      tabPanel.selectTab(lastTab);
   }

   /**
    * Creates one property panel with property name and value
    * 
    * @param name
    * @param value
    * @return {@link HorizontalPanel}
    */
   private HorizontalPanel createPropertyPanel(String name, String value)
   {
      HorizontalPanel hPanel = new HorizontalPanel();
      Label label = new Label();
      label.setText(name);
      label.setWidth("250px");

      TextBox propertyBox = new TextBox();
      propertyBox.setWidth("300px");
      propertyBox.setText(value);

      hPanel.add(label);
      hPanel.add(propertyBox);

      return hPanel;
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#displayRepository(org.xcmis.gwtframework.client.model.repository.CmisRepositoryInfo)
    */
   public void displayRepository(CmisRepositoryInfo repositoryInfo)
   {
      Label label = new Label();
      label.setText("- " + repositoryInfo.getRepositoryName());
      repositoryInfoPanel.add(label);
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#getAddFolderButton()
    */
   public HasClickHandlers getCreateFolderButton()
   {
      return createFolderButton;
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#addNewObject(org.xcmis.gwtframework.client.model.restatom.AtomEntry)
    */
   public void addNewObject(AtomEntry entry)
   {
      addObject(entry);
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#getCreateDocumentButton()
    */
   public HasClickHandlers getCreateDocumentButton()
   {
      return createDocumentButton;
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#removeObject()
    */
   public String removeObject()
   {
      String url = tabPanel.getWidget(selectedItem).getTitle();
      tabPanel.remove(selectedItem);

      if (tabPanel.getWidgetCount() > 0)
      {
         tabPanel.selectTab(0);
      }
      else
      {
         deleteButton.setEnabled(false);
      }
      return url;
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#setEnableCreateButtons(boolean)
    */
   public void setEnableCreateButtons(boolean enable)
   {
      createDocumentButton.setEnabled(enable);
      createFolderButton.setEnabled(enable);
   }

   /**
    * @see org.xcmis.client.gwt.restatom.sample.client.application.SamplePresenter.Display#setEnableDeleteButton(boolean)
    */
   public void setEnableDeleteButton(boolean enable)
   {
      deleteButton.setEnabled(enable);
   }

}
