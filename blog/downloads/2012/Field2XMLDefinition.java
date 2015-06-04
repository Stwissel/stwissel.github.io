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
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Field2XMLDefinition implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String fieldName;
	private String tagName;

	@XmlAttribute
	public void setFieldGroup(boolean fieldGroup) {
		// Ignored - only needed for serializaion
	}

	private ArrayList<Field2XMLDefinition> members = new ArrayList<Field2XMLDefinition>();
	
	//Default constructor needed for reflection
	public Field2XMLDefinition() {
		
	}

	public Field2XMLDefinition(String fName, String tName) {
		this.fieldName = fName;
		this.tagName = tName;
	}
	
	public Field2XMLDefinition(String fName) {
		this.fieldName = fName;
		this.tagName = fName;
	}
	
	public void addMember(Field2XMLDefinition newMember) {
		this.members.add(newMember);
	}
		
	public void addMember(String memberName, String memberTab) {
		Field2XMLDefinition newMember = new Field2XMLDefinition(memberName,memberTab);
		this.members.add(newMember);
	}
	
	public void addMember(String memberName) {
		Field2XMLDefinition newMember = new Field2XMLDefinition(memberName);
		this.members.add(newMember);
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public ArrayList<Field2XMLDefinition> getMembers() {
		return this.members;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public boolean isFieldGroup() {
		return (this.members.size()) > 0;
	}


	@XmlAttribute
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@XmlElement(name="field")
	public void setMembers(ArrayList<Field2XMLDefinition> members) {
		this.members = members;
	}

	@XmlAttribute
	public void setTagName(String tagName) {
		this.tagName = tagName;
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