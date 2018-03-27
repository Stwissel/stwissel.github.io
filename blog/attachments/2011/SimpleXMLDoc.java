/** ========================================================================= *
 * Copyright (C) 2006, 2007 TAO Consulting Pte <http://www.taoconsulting.sg/> *
 * Copyright (C) 2011       IBM Corporation ( http://www.ibm.com/ )           *
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
package biz.taoconsulting.xmltools;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Convenience class to write out an XML document using a stream and a SAX
 * parser. A stack is used to keep track of open and closed tag and on document
 * close all tags are properly closed. It doesn't guarantee the XML you might
 * want or need, but it will be valid XML. The SAX Parser esures correct
 * encoding of the tags and values
 * 
 * @author stw
 * 
 */
public class SimpleXMLDoc {

	private TransformerHandler hd = null; // Handler for SAX output
	private String docTypeSystem = null; // XML System type (optional)
	private String docTypePublic = null; // XML Public type (optional)
	private boolean documentStarted = false; // Flag for the document
	private String xmlStyleSheet = null; // Stylesheet (optional)
	private OutputStream out = null; // Provided by XPages
	private Stack<String> xmlTagStack = new Stack<String>(); //Keeping track of all open / closed XML tags
	private ArrayList<String> deferedComments; // We can't write comments before we have the first element

	public SimpleXMLDoc() { }

	public void addCdataTag(String tagName, String tagValue) {
		try {
			this.openTag(tagName);
			hd.startCDATA();
			this.addTagContent(tagValue);
			hd.endCDATA();
			this.closeTag(1);
		} catch (SAXException e) {
			// TODO Better error handling
			e.printStackTrace();
		}
	}

	/**
	 * Adds a comment to the result. Comments are only written after the root
	 * element, so we cache them if necessary
	 * 
	 * @param comment
	 *            The comment text
	 */
	public void addComment(String comment) {

		if (!this.documentStarted) {
			if (this.deferedComments == null) {
				this.deferedComments = new ArrayList<String>();
			}
			this.deferedComments.add(comment);
		} else {
			try {
				hd.comment(comment.toCharArray(), 0, comment.length());
			} catch (SAXException e) {
				// Ends in the log
				e.printStackTrace();
			}
		}
	}

	public void addEmptyTag(String tagName)
			throws TransformerConfigurationException, SAXException {
		this.addSimpleTag(tagName, null, null);
	}

	public void addEmptyTag(String tagName, Map<String, String> attributes)
			throws TransformerConfigurationException, SAXException {
		this.addSimpleTag(tagName, null, attributes);
	}

	public void addSimpleTag(String tagName, String tagValue) {
		this.addSimpleTag(tagName, tagValue, null);
	}

	public void addSimpleTag(String tagName, String tagValue,
			Map<String, String> attributes) {
		this.openTag(tagName, attributes);
		this.addTagContent(tagValue);
		this.closeTag(1);
	}

	private void addTagContent(String content) {
		if (content != null) {
			try {
				hd.characters(content.toCharArray(), 0, content.length());
			} catch (SAXException e) {
				// TODO Better errors
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the document and ensures that all tags are closed
	 * @throws SAXException
	 */
	public void closeDocument() {
		this.closeTag(-1); // Make sure all tages are closes
		// Closing of the document,
		try {
			hd.endDocument();
		} catch (SAXException e) {
			// No error captured here
		}
	}

	public void closeTag(int howMany) {
		if (howMany < 0) {
			while (!this.xmlTagStack.empty()) {
				String closeTag = this.xmlTagStack.pop();
				try {
					hd.endElement("", "", closeTag);
				} catch (SAXException e) {
					// We don't capture that error
				}
			}
		} else {
			for (int i = 0; i < howMany; i++) {
				if (!this.xmlTagStack.empty()) {
					String closeTag = this.xmlTagStack.pop();
					try {
						hd.endElement("", "", closeTag);
					} catch (SAXException e) {
						// We don't capture that error
					}
				} else {
					break; // No point looping
				}
			}
		}
	}

	public boolean closeTag(String lastTagToClose) {
		boolean result = false;
		while (!this.xmlTagStack.empty()) {
			String closeTag = this.xmlTagStack.pop();
			try {
				hd.endElement("", "", closeTag);
			} catch (SAXException e) {
				// We don't capture that error
			}
			if (closeTag.equals(lastTagToClose)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void dateTag(String TagName, Date date)
			throws TransformerConfigurationException, SAXException {
		if (date == null) {
			return;
		}
		// "Sat, 26 Mar 2005 11:22:20 GMT";
		String lastmodFormat = "EE', 'd' 'MMM' 'yyyy' 'H':'m':'s' 'z";
		SimpleDateFormat fmt = new SimpleDateFormat(lastmodFormat, Locale.UK);
		String datestring = fmt.format(date);
		this.addSimpleTag(TagName, datestring);
	}

	public String getDocTypePublic() {
		return docTypePublic;
	}

	public String getDocTypeSystem() {
		return docTypeSystem;
	}

	public String getXmlStyleSheet() {
		return xmlStyleSheet;
	}

	/**
	 * Starts a document if not yet there
	 */
	private void initializeDoc() {
		PrintWriter pw = new PrintWriter(this.out); // out comes from outside
		StreamResult streamResult = new StreamResult(pw);
		// Factory pattern at work
		SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		try {
			hd = tf.newTransformerHandler();

			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.INDENT, "no");
			if (this.docTypeSystem != null) {
				serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
						this.docTypeSystem);
			}
			if (this.docTypePublic != null) {
				serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
						this.docTypePublic);
			}

			hd.setResult(streamResult);
			// This creates the empty document
			hd.startDocument();

			// Get a processing instruction
			if (this.xmlStyleSheet != null) {
				hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\""+ this.xmlStyleSheet + "\"");
			}
			this.documentStarted = true; // We memorise that
		} catch (TransformerConfigurationException e) {
			// Better error handling?
			e.printStackTrace();
			this.documentStarted = false;
		} catch (SAXException e) {
			// Better error handling?
			e.printStackTrace();
			this.documentStarted = false;
		}
	}

	public void openTag(String tagName) {
		this.openTag(tagName, null);
	}

	public void openTag(String tagName, Map<String, String> attributes) {

		AttributesImpl atts = null;
		boolean needToStartDocument = !this.documentStarted;

		if (needToStartDocument) {
			this.initializeDoc();
		}
		// This creates attributes that go inside the element, all encoding is
		// taken care of
		if (attributes != null) {
			atts = new AttributesImpl();
			for (Entry<String, String> curAtt : attributes.entrySet()) {
				atts.addAttribute("", "", curAtt.getKey(), "CDATA", curAtt
						.getValue());
			}
		}
		// This creates the element with the previously defined attributes
		try {
			hd.startElement("", "", tagName, atts);
			this.xmlTagStack.push(tagName); // Memorize that we opened it!

			// If there are defered commends process them
			if (needToStartDocument && this.deferedComments != null) {
				for (String curComment : this.deferedComments) {
					this.addComment(curComment);
				}
			}

		} catch (SAXException e) {
			// TODO Better error handling
			e.printStackTrace();
		}
	}

	public void setDocTypePublic(String docTypePublic) {
		this.docTypePublic = docTypePublic;
	}

	public void setDocTypeSystem(String docTypeSystem) {
		this.docTypeSystem = docTypeSystem;
	}

	/**
	 * Define the output stream where the XML document should be written
	 * @param out OutputStream from server response
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}

	public void setXmlStyleSheet(String xmlStyleSheet) {
		this.xmlStyleSheet = xmlStyleSheet;
	}
}
