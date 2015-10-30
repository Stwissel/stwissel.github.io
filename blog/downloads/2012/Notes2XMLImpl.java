/** ========================================================================= *
 * Copyright (C) 2012  Stephan H. Wissel ( http://www.wissel.net/ )           * 
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package com.notessensei.fop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.DxlExporter;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import biz.taoconsulting.xmltools.SimpleXMLDoc;

public class Notes2XMLImpl implements Serializable, Notes2XML {

	private static final long					serialVersionUID	= 1L;

	private HashMap<String, Form2XMLDefinition>	forms				= new HashMap<String, Form2XMLDefinition>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.notessensei.fop.Notes2XML#addForm(com.notessensei.fop.Form2XMLDefinition
	 * )
	 */
	public void addForm(Form2XMLDefinition newForm) {
		this.forms.put(newForm.getFormName(), newForm);
	}

	private String getNotesFormName(lotus.domino.Document nDoc) {
		try {
			if (nDoc.hasItem("Form")) {
				return nDoc.getItemValueString("Form");
			}
		} catch (NotesException e) {
			// No action here
		}
		return "None";
	}

	private String getProperTagName(String name) {
		// The $ sign isn't allowed in XML Element names
		return name.replace("$", "d_");

	}

	@SuppressWarnings("unchecked")
	private void processDefaultDocument(lotus.domino.Document nDoc, SimpleXMLDoc result) {
		String tagName = this.getNotesFormName(nDoc);
		result.openTag(tagName);
		try {
			Vector allItems = nDoc.getItems();
			for (int i = 0; i < allItems.size(); i++) {
				Item curItem = (Item) allItems.get(i);
				String itemFieldName = curItem.getName();
				String itemTagName = this.getProperTagName(itemFieldName);
				processField(nDoc, result, itemFieldName, itemTagName);
				curItem.recycle();
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		result.closeTag(1);

	}

	// Processes one document and renders results out
	private void processDocument(lotus.domino.Document nDoc, SimpleXMLDoc result) {
		String key = this.getNotesFormName(nDoc);
		if (!this.forms.containsKey(key)) {
			this.processDefaultDocument(nDoc, result);
			return;
		}

		Form2XMLDefinition form = this.forms.get(key);
		this.processDocumentWithFormDefinition(nDoc, result, form);

	}

	private void processDocumentWithFormDefinition(Document nDoc, SimpleXMLDoc result, Form2XMLDefinition form) {
		result.openTag(form.getFormTag());

		for (Field2XMLDefinition def : form.getMembers()) {
			String itemTagName = def.getTagName();			
			if (def.isFieldGroup()) {
				result.openTag(def.getTagName());
				this.processFieldGroup(nDoc, result, def);
				result.closeTag(1);
			} else {
				String itemFieldName = def.getFieldName();
				this.processField(nDoc, result, itemFieldName, itemTagName);
			}
		}

		result.closeTag(1);
	}

	@SuppressWarnings("unchecked")
	private void processField(Document nDoc, SimpleXMLDoc result, String itemFieldName, String itemTagName) {
		try {
			if (nDoc.hasItem(itemFieldName)) {
				Item curItem = nDoc.getFirstItem(itemFieldName);
				Vector values = curItem.getValues();
				for (int j = 0; j < values.size(); j++) {
					result.addSimpleTag(itemTagName, values.get(j).toString());
				}
				curItem.recycle();
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	private void processFieldGroup(Document nDoc, SimpleXMLDoc result, Field2XMLDefinition groupDef) {
		for (Field2XMLDefinition def : groupDef.getMembers()) {
			String itemTagName = def.getTagName();
			if (def.isFieldGroup()) {
				result.openTag(itemTagName);
				this.processFieldGroup(nDoc, result, def);
				result.closeTag(1);
			} else {
				String itemFieldName = def.getFieldName();
				this.processField(nDoc, result, itemFieldName, itemTagName);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.notessensei.fop.Notes2XML#renderDocument2DXL(lotus.domino.Document)
	 */
	public ByteArrayOutputStream renderDocument2DXL(lotus.domino.Document doc) {
		// Part 1: assemble the document in memory
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter w = new PrintWriter(out);
		try {
			doc.generateXML(w);
		} catch (NotesException e) {
			// No Action
		} catch (IOException e) {
			// No Action
		}
		w.flush();
		w.close();
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.notessensei.fop.Notes2XML#renderDocument2XML(lotus.domino.Document)
	 */
	public ByteArrayOutputStream renderDocument2XML(lotus.domino.Document doc) {
		// Part 1: assemble the document in memory
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SimpleXMLDoc result = new SimpleXMLDoc();
		result.setOut(out);
		this.processDocument(doc, result);
		result.closeDocument();
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.notessensei.fop.Notes2XML#renderDocumentCollection2DXL(lotus.domino
	 * .DocumentCollection)
	 */
	public ByteArrayOutputStream renderDocumentCollection2DXL(Session s, DocumentCollection dc) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);
		try {
			DxlExporter exporter = s.createDxlExporter();
			exporter.setExitOnFirstFatalError(false);
			exporter.setOmitMiscFileObjects(true);
			exporter.setForceNoteFormat(false);
			exporter.setConvertNotesBitmapsToGIF(true);
			exporter.setOutputDOCTYPE(false); // We omit the DocType to make
			// sure XSLT doesn't complain
			pw.append(exporter.exportDxl(dc));

		} catch (NotesException e) {
			e.printStackTrace();
		}
		pw.close();
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.notessensei.fop.Notes2XML#renderDocumentCollection2XML(lotus.domino
	 * .DocumentCollection, java.lang.String)
	 */
	public ByteArrayOutputStream renderDocumentCollection2XML(DocumentCollection dc, String rootName) {
		// Part 1: assemble the document in memory
		lotus.domino.Document nDoc = null;
		lotus.domino.Document nDocNext = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SimpleXMLDoc result = new SimpleXMLDoc();
		result.setOut(out);
		result.openTag(rootName);
		try {
			nDoc = dc.getFirstDocument();
			while (nDoc != null) {
				nDocNext = dc.getNextDocument(nDoc);
				this.processDocument(nDoc, result);
				nDoc.recycle();
				nDoc = nDocNext;
			}
			if (nDocNext != null) {
				nDocNext.recycle();
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		result.closeDocument();
		return out;

	}
}
