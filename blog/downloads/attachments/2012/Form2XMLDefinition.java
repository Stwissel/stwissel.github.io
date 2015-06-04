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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FormExportDefinition")
public class Form2XMLDefinition implements Serializable{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 * @param in
	 *            InputStream with the XML to load the Form Definition
	 * @return Form Definition XML
	 */
	public static Form2XMLDefinition load(InputStream in) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Form2XMLDefinition.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Form2XMLDefinition result = (Form2XMLDefinition) jaxbUnmarshaller.unmarshal(in);
			return result;

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		// If it didn't work, we return null
		return null;
	}

	private String							formName;
	private String							formTag;
	private ArrayList<Field2XMLDefinition>	members = new ArrayList<Field2XMLDefinition>();

	// Default constructor needed for reflection
	public Form2XMLDefinition() {
		this.formName = "unassigned";
	}

	public Form2XMLDefinition(String fName) {
		this.formName = fName;
		this.formTag = fName;
	}

	public Form2XMLDefinition(String fName, String fTag) {
		this.formName = fName;
		this.formTag = fTag;
	}
	
	public void addMember(Field2XMLDefinition newMember) {
		this.members.add(newMember);
	}
	
	public void addMember(String fieldName) {
		this.members.add(new Field2XMLDefinition(fieldName));
	}

	public void addMember(String fieldName, String tagName) {
		this.members.add(new Field2XMLDefinition(fieldName,tagName));
	}
	
	public String getFormName() {
		return formName;
	}
	
	public String getFormTag() {
		return formTag;
	}

	public ArrayList<Field2XMLDefinition> getMembers() {
		return members;
	}

	@XmlAttribute
	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setFormTag(String formTag) {
		this.formTag = formTag;
	}

	@XmlElement(name="field")
	public void setMembers(ArrayList<Field2XMLDefinition> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this, b);
			return b.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		// If it didn't work we return the the normal String
		return super.toString();
	}

}