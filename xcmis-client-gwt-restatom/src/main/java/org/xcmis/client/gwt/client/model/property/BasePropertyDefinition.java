/*
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

package org.xcmis.client.gwt.client.model.property;

import java.util.ArrayList;
import java.util.List;

import org.xcmis.client.gwt.client.model.Choice;
import org.xcmis.client.gwt.client.model.EnumCardinality;
import org.xcmis.client.gwt.client.model.EnumUpdatability;



/**
 * Base implementation of CMIS property definition.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: $
 */
/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 * @param <T>
 */
/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 * @param <T>
 */
public abstract class BasePropertyDefinition<T> implements PropertyDefinition<T>
{

   /**
    * Property definition id
    */
   private String id;

   /**
    * Property definition local name
    */
   private String localName;
   
   /**
    * Property definition local namespace
    */
   private String localNamespace;
   
   /**
    * Property definition query name
    */
   private String queryName;

   /**
    * Property definition display name
    */
   private String displayName;
   
   /**
    * Property definition description
    */
   private String description;
   
   /**
    * Property definition cardinality
    */
   private EnumCardinality cardinality;

   /**
    * Property definition updatability
    */
   private EnumUpdatability updatability;
   
   /**
    * Property definition inherited
    */
   private Boolean inherited;
   
   /**
    * Property definition required
    */
   private Boolean required;

   /**
    * Property definition queryable
    */
   private Boolean queryable;

   /**
    * Property definition orderable
    */
   private Boolean orderable;
   
   /**
    * Property definition choices
    */
   private List<Choice<T>> choices;
   
   /**
    * Open choice
    */
   private Boolean openChoice;
   
   /**
    * Default value
    */
   private T[] defaultValue;

   

   /**
    * Default constructor.
    */
   public BasePropertyDefinition()
   {
   }

   /**
    * @param id id 
    * @param localName local name
    * @param localNamespace local name space
    * @param queryName query name
    * @param displayName display name
    * @param description description
    * @param cardinality cardinality
    * @param updatability updatability
    * @param inherited inherited
    * @param required required
    * @param queryable queryable
    * @param orderable orderable
    * @param openChoice open choice
    * @param choices choices
    * @param defaultValue default value
    */
   public BasePropertyDefinition(String id, String localName, String localNamespace,
      String queryName, String displayName, String description, 
      EnumCardinality cardinality,EnumUpdatability updatability, 
      Boolean inherited, Boolean required, Boolean queryable, Boolean orderable, 
      Boolean openChoice, List<Choice<T>> choices, T[] defaultValue)
   {
      this.id = id;
      this.localName = localName;
      this.localNamespace = localNamespace;
      this.queryName = queryName;
      this.displayName = displayName;
      this.description = description;
      this.cardinality = cardinality;
      this.updatability = updatability;
      this.inherited = inherited;
      this.required = required;
      this.queryable = queryable;
      this.orderable = orderable;
      this.openChoice = openChoice;
      if (choices != null) {
         this.choices = new ArrayList<Choice<T>>(1);
         this.choices.add(new Choice<T>());
      }
      this.defaultValue = defaultValue;
   }


   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalName()
   {
      return localName;
   }
   
   /**
    * {@inheritDoc}
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }
   
   /**
    * {@inheritDoc}
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * {@inheritDoc}
    */
   public String getDisplayName()
   {
      return displayName;
   }
   
   /**
    * {@inheritDoc}
    */
   public String getDescription()
   {
      return description;
   }
   
   /**
    * {@inheritDoc}
    */
   public EnumCardinality getCardinality()
   {
      return cardinality;
   }
   
   /**
    * {@inheritDoc}
    */
   public EnumUpdatability getUpdatability()
   {
      return updatability;
   }
   
   /**
    * {@inheritDoc}
    */
   public Boolean isInherited()
   {
      return inherited;
   }
   
   /**
    * {@inheritDoc}
    */
   public Boolean isRequired()
   {
      return required;
   }
   
   
   public Boolean isQueryable()
   {
      return queryable;
   }
   
   /**
    * {@inheritDoc}
    */
   public Boolean isOrderable()
   {
      return orderable;
   }
   
   public List<Choice<T>> getChoices()
   {
      if (choices == null)
      {
         choices = new ArrayList<Choice<T>>();
      }
      return choices;
   }
   
   public Boolean isOpenChoice()
   {
      return openChoice;
   }
   
   /**
    * {@inheritDoc}
    */
   public T[] getDefaultValue()
   {
      return defaultValue;
   }
   
   // --- Setters.
   
   /**
    * Setter for id.
    * 
    * @param id
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * Setter for localName.
    * 
    * @param localName
    */
   public void setLocalName(String localName)
   {
      this.localName = localName;
   }
   
   /**
    * Setter for localNamespace.
    * 
    * @param localNamespace localNamespace
    */
   public void setLocalNamespace(String localNamespace)
   {
      this.localNamespace = localNamespace;
   }
   
   /**
    * Setter for displayName.
    * 
    * @param displayName
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   /**
    * Setter for queryName.
    * 
    * @param queryName queryName
    */
   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
   }
   
   /**
    * Setter for cardinality.
    * 
    * @param cardinality cardinality
    */
   public void setCardinality(EnumCardinality cardinality)
   {
      this.cardinality = cardinality;
   }
   
   /**
    * Setter for description.
    * 
    * @param description description
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * Setter for inherited.
    * 
    * @param inherited inherited
    */
   public void setInherited(Boolean inherited)
   {
      this.inherited = inherited;
   }
   
   /**
    * Setter for orderable.
    * 
    * @param orderable orderable
    */
   public void setOrderable(Boolean orderable)
   {
      this.orderable = orderable;
   }

   /**
    * Setter for queryable.
    * 
    * @param queryable queryable
    */
   public void setQueryable(Boolean queryable)
   {
      this.queryable = queryable;
   }

   /**
    * Setter for required.
    * 
    * @param required required
    */
   public void setRequired(Boolean required)
   {
      this.required = required;
   }

   /**
    * Setter for updatability.
    * 
    * @param updatability updatability
    */
   public void setUpdatability(EnumUpdatability updatability)
   {
      this.updatability = updatability;
   }
   
   /**
    * Setter for openChoice.
    * 
    * @param openChoice openChoice
    */
   public void setOpenChoice(Boolean openChoice)
   {
      this.openChoice = openChoice;
   }
   
   /**
    * Setter for choices.
    * 
    * @param choices choices
    */
   public void setChoices(List<Choice<T>> choices)
   {
      this.choices = choices;
   }
   
   /**
    * Setter for defaultValue.
    * 
    * @param defaultValue defaultValue
    */
   public void setDefaultValue(T[] defaultValue)
   {
      this.defaultValue = defaultValue;
   }

}
