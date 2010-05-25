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

package org.xcmis.sp.jcr.exo;

import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class PWC extends DocumentDataImpl {
	static final Set<String> checkinCheckoutSkip = new HashSet<String>();
	static {
		checkinCheckoutSkip.add(CmisConstants.NAME);
		checkinCheckoutSkip.add(CmisConstants.OBJECT_ID);
		checkinCheckoutSkip.add(CmisConstants.OBJECT_TYPE_ID);
		checkinCheckoutSkip.add(CmisConstants.BASE_TYPE_ID);
		checkinCheckoutSkip.add(CmisConstants.CREATED_BY);
		checkinCheckoutSkip.add(CmisConstants.CREATION_DATE);
		checkinCheckoutSkip.add(CmisConstants.LAST_MODIFIED_BY);
		checkinCheckoutSkip.add(CmisConstants.LAST_MODIFICATION_DATE);
		checkinCheckoutSkip.add(CmisConstants.CHANGE_TOKEN);
		checkinCheckoutSkip.add(CmisConstants.IS_IMMUTABLE);
		checkinCheckoutSkip.add(CmisConstants.VERSION_SERIES_ID);
		checkinCheckoutSkip.add(CmisConstants.IS_LATEST_VERSION);
		checkinCheckoutSkip.add(CmisConstants.IS_MAJOR_VERSION);
		checkinCheckoutSkip.add(CmisConstants.IS_LATEST_MAJOR_VERSION);
		checkinCheckoutSkip.add(CmisConstants.VERSION_LABEL);
		checkinCheckoutSkip.add(CmisConstants.CHECKIN_COMMENT);
		checkinCheckoutSkip.add(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT);
		checkinCheckoutSkip.add(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID);
		checkinCheckoutSkip.add(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY);
		checkinCheckoutSkip.add("xcmis:latestVersionId");
		checkinCheckoutSkip.add(CmisConstants.CONTENT_STREAM_FILE_NAME);
		checkinCheckoutSkip.add(CmisConstants.CONTENT_STREAM_ID);
		checkinCheckoutSkip.add(CmisConstants.CONTENT_STREAM_LENGTH);
		checkinCheckoutSkip.add(CmisConstants.CONTENT_STREAM_MIME_TYPE);
	}

	/** Latest version of document. */
	private final DocumentDataImpl document;

	public PWC(DocumentData document, Session session, Node node,
			IndexListener indexListener) {
		super(document.getTypeDefinition(), null, session, node, null,
				indexListener);
		this.document = (DocumentDataImpl) document;
	}

	public PWC(TypeDefinition type, Node node, DocumentData document,
			IndexListener indexListener) {
		super(type, node, indexListener);
		this.document = (DocumentDataImpl) document;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelCheckout() throws StorageException {
		try {
			Node docNode = document.getNode();

			docNode.setProperty(CmisConstants.IS_LATEST_VERSION, true);
			docNode.setProperty(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT,
					false);
			docNode.setProperty(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID,
					(Value) null);
			docNode.setProperty(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY,
					(Value) null);

			node.getParent().remove();

			session.save();

		} catch (RepositoryException re) {
			throw new StorageException("Unable cancel checkout Document. "
					+ re.getMessage(), re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentData checkin(boolean major, String checkinComment,
			Map<String, Property<?>> properties, ContentStream content,
			List<AccessControlEntry> addACL,
			List<AccessControlEntry> removeACL, Collection<ObjectData> policies)
			throws ConstraintException, StorageException {
		try {
			Node docNode = document.getNode();

			docNode.checkin();
			docNode.checkout();

			docNode.setProperty(CmisConstants.IS_LATEST_VERSION, true);
			docNode.setProperty(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT,
					false);
			docNode.setProperty(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID,
					(Value) null);
			docNode.setProperty(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY,
					(Value) null);
			// Update creation date & last modification date
			// to emulate creation new version.
			docNode.setProperty(CmisConstants.CREATED_BY, session.getUserID());
			docNode.setProperty(CmisConstants.CREATION_DATE, Calendar
					.getInstance());
			// docNode.setProperty(CmisConstants.LAST_MODIFIED_BY,
			// session.getUserID());
			// docNode.setProperty(CmisConstants.LAST_MODIFICATION_DATE,
			// Calendar.getInstance());
			//
			docNode.setProperty(CmisConstants.IS_MAJOR_VERSION, major);
			if (checkinComment != null) {
				docNode.setProperty(CmisConstants.CHECKIN_COMMENT,
						checkinComment);
			}

			// Copy the other properties from document.
			for (PropertyDefinition<?> def : type.getPropertyDefinitions()) {
				String pId = def.getId();
				if (!checkinCheckoutSkip.contains(pId)) {
					setProperty(docNode, properties.get(pId));
				}
			}

			String name = getName();
			if (!document.getName().equals(name)) {
				document.setName(name);
			}

			if (content != null) {
				try {
					// TODO : Need to check if contents are the same then not
					// was
					// not updated not need to change
					setContentStream(docNode, content);
				} catch (IOException ioe) {
					throw new CmisRuntimeException(
							"Unable copy content for new document. "
									+ ioe.getMessage(), ioe);
				}
			}

			if ((addACL != null && addACL.size() > 0)
					|| (removeACL != null && removeACL.size() > 0)) {
				List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(
						document.getACL(false), addACL, removeACL);
				if (mergedACL != null && mergedACL.size() > 0) {
					BaseObjectData.setACL(docNode, mergedACL);
				}
			}

			if (policies != null && policies.size() > 0) {
				for (ObjectData aPolicy : policies) {
					BaseObjectData.applyPolicy(docNode, (PolicyData) aPolicy);
				}
			}

			// document.save() will save this change
			node.getParent().remove();

//			session.save();
			document.save(null);

			return document;
		} catch (RepositoryException re) {
			throw new StorageException("Unable checkin Document. "
					+ re.getMessage(), re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void delete() throws StorageException {
		cancelCheckout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FolderData getParent() throws ConstraintException {
		return document.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<FolderData> getParents() {
		return document.getParents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPWC() {
		return true;
	}

	   /**
	    * {@inheritDoc}
	    */
	   public void setContentStream(ContentStream contentStream) throws ConstraintException, IOException
	   {
	      try
	      {
	         setContentStream(node, contentStream);
	      }
	      catch (RepositoryException re)
	      {
	         throw new CmisRuntimeException("Unable save document content. " + re.getMessage(), re);
	      }
	      save(null);
	   }
	   
		/**
		 * {@inheritDoc}
		 */
		public void setName(String name) throws NameConstraintViolationException {
			// TODO Do we need still this method? Zavizionov
			this.name = name;
			save(null);
		}
}
