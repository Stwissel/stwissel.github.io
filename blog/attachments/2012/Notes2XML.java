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
import lotus.domino.DocumentCollection;
import lotus.domino.Session;

public interface Notes2XML {

	/**
	 * Add a form definition to the exporter so more customized XML can be exported
	 * @param newForm Form2XMLDefinition
	 */
	public abstract void addForm(Form2XMLDefinition newForm);

	/**
	 * Render a Notes Document to DXL
	 * @param doc NotesDocument to be rendered
	 * @return ByteArrayOutputStream - can be toString or as source for an InputStream
	 */
	public abstract ByteArrayOutputStream renderDocument2DXL(lotus.domino.Document doc);

	/**
	 * Render a Notes Document to XML - uses a form definition if present
	 * @param doc NotesDocument to be rendered
	 * @return ByteArrayOutputStream - can be toString or as source for an InputStream
	 */
	public abstract ByteArrayOutputStream renderDocument2XML(lotus.domino.Document doc);

	/**
	 * Render a NotesDocumentCollection to DXL
	 * @param dc NotesDocumentCollection to be rendered
	 * @return ByteArrayOutputStream with render result
	 */
	public abstract ByteArrayOutputStream renderDocumentCollection2DXL(Session s, DocumentCollection dc);

	/**
	 * Render a NotesDocumentCollection to XML - uses a form definition if present
	 * @param dc NotesDocumentCollection to be rendered
	 * @return ByteArrayOutputStream with render result
	 */
	public abstract ByteArrayOutputStream renderDocumentCollection2XML(DocumentCollection dc, String rootName);

}