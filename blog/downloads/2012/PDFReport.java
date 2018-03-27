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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author stw
 * 
 */
public class PDFReport {

	/**
	 * Used only for testing the reporter
	 * 
	 * @param args
	 * @throws IOException
	 */
	/**
	 * Used only for testing the reporter
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileOutputStream out = new FileOutputStream(new File("/home/stw/temp/result.pdf"));

		PDFReport r = new PDFReport();
		r.fopReportFromString(out, "<text>Nothing happening here</text>",null);
		out.close();

		System.out.println("done!");

	}

	// The factory for all FopActivities
	private FopFactory	fopFactory	= null;

	/**
	 * Renders the fopString into a PDF Output
	 * 
	 * @param out
	 *            OutputStream for PDF Result
	 * @param xmlSource
	 *            containing XML Data
	 * @param xsltSource
	 *            containing a XSLT Stylesheet
	 */
	public void fopReport(OutputStream out, Source xmlSource, Source xsltSource) {
		try {
			Fop fop = this.getFop(out);
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup the transformer and execute
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(xsltSource);
			transformer.transform(xmlSource, res);

		} catch (FOPException e) {
			e.printStackTrace();
			;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders the fopString into a PDF Output
	 * 
	 * @param out
	 *            OutputStream for PDF Result
	 * @param fopString
	 *            containing fop instructions
	 */
	public void fopReport(OutputStream out, String fopString) {
		try {
			if (fopString == null) {
				fopString = this.getHelloWorld();
			}
			Fop fop = this.getFop(out);
			InputSource fopSrc = new InputSource(new StringReader(fopString));
			SAXParser parser = this.getParser();
			DefaultHandler dh = fop.getDefaultHandler();
			parser.parse(fopSrc, dh);
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders the fopString into a PDF Output
	 * 
	 * @param out
	 *            OutputStream for PDF Result
	 * @param xmlString
	 *            containing XML Data
	 * @param xsltString
	 *            containing a XSLT Stylesheet
	 */
	public void fopReportFromString(OutputStream out, String xmlString, String xsltString) {

		if (xsltString == null) {
			xsltString = this.getDefaultXSLT();
		}
		if (xmlString == null) {
			xmlString = "<text>Nothing to see here!</text>";
		}

		Source xmlSource = new StreamSource(new StringReader(xmlString));
		Source xsltSource = new StreamSource(new StringReader(xsltString));
		this.fopReport(out, xmlSource, xsltSource);
	}

	private String getDefaultXSLT() {
		StringBuilder testFo = new StringBuilder();
		testFo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		testFo.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n");
		testFo.append("xmlns:xd=\"http://www.oxygenxml.com/ns/doc/xsl\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"\n");
		testFo.append("xmlns:d=\"http://www.lotus.com/dxl\" exclude-result-prefixes=\"xd d\" version=\"1.0\">\n");

		testFo.append("<xsl:template match=\"/\">\n");
		testFo.append("<fo:root>\n");
		testFo.append("<fo:layout-master-set>\n");
		testFo.append("<fo:simple-page-master page-height=\"297mm\" page-width=\"210mm\" margin-top=\"25mm\"\n");
		testFo.append(" margin-bottom=\"25mm\" margin-left=\"25mm\" margin-right=\"25mm\"\n");
		testFo.append("master-name=\"A4portrait\">\n");
		testFo.append("<fo:region-body  margin-top=\"1.5cm\" margin-bottom=\"2.5cm\"/>\n");
		testFo.append("<fo:region-before extent=\"3.5cm\"/>\n");
		testFo.append("<fo:region-after  extent=\"2.5cm\"/>\n");
		testFo.append("</fo:simple-page-master>\n");
		testFo.append("</fo:layout-master-set>\n");

		testFo.append("<fo:page-sequence master-reference=\"A4portrait\">\n");
		testFo.append("<fo:static-content flow-name=\"xsl-region-before\" border-color=\"gray\"\n");
		testFo.append("border-style=\"solid\" border-width=\"1px\">\n");
		testFo.append("<fo:block font-size=\"24px\" font-weight=\"bold\" border-bottom-width=\"2px\">\n");
		testFo.append("<xsl:apply-templates select=\"d:database\" mode=\"header\"/>\n");
		testFo.append("</fo:block>\n");
		
		testFo.append("</fo:static-content>\n");

		testFo.append("<fo:static-content flow-name=\"xsl-region-after\" border-color=\"gray\"\n");
		testFo.append("border-style=\"solid\" border-width=\"1px\">\n");
		testFo.append("<fo:block font-size=\"12px\"> Default Notes2Fo Stylesheet for DXL Page:\n");
		testFo.append("<fo:page-number/> / <fo:page-number-citation ref-id=\"last-page\"/>\n");
		testFo.append("</fo:block>\n");
		testFo.append("</fo:static-content>\n");

		testFo.append("<fo:flow flow-name=\"xsl-region-body\">\n");
		testFo.append("<xsl:apply-templates/>\n");
		testFo.append("<fo:block id=\"last-page\"/>\n");
		testFo.append("</fo:flow>\n");
		testFo.append("</fo:page-sequence>\n");
		testFo.append("</fo:root>\n");
		testFo.append("</xsl:template>\n");

		testFo.append("<xsl:template match=\"d:database\" mode=\"header\">\n");
		testFo.append("Database <xsl:value-of select=\"@title\"/>\n");
		testFo.append("</xsl:template>\n");
		
		testFo.append("<xsl:template match=\"text\" >\n");
		testFo.append("<fo:block font-size=\"12px\" >\n");
		testFo.append("<xsl:value-of select=\".\"/>\n");
		testFo.append("</fo:block>\n");		
		testFo.append("</xsl:template>\n");

		testFo.append("<xsl:template match=\"d:database\">\n");
		testFo.append("<xsl:apply-templates/>\n");
		testFo.append("</xsl:template>\n");

		testFo.append("<xsl:template match=\"d:document\">\n");
		testFo.append("<fo:block margin-bottom=\"10px\"/>\n");
		testFo.append("<fo:block font-size=\"12px\" margin-bottom=\"10px\">Form <xsl:value-of select=\"@form\"/> (UNID <xsl:value-of select=\"d:noteinfo/@unid\"/>) </fo:block>\n");

		testFo.append("<fo:table width=\"15cm\">\n");
		testFo.append("<fo:table-header>\n");
		testFo.append("<fo:table-row>\n");
		testFo.append("<fo:table-cell>\n");
		testFo.append("<fo:block font-weight=\"bold\"> </fo:block>\n");
		testFo.append("</fo:table-cell>\n");
		testFo.append("<fo:table-cell>\n");
		testFo.append("<fo:block font-weight=\"bold\"></fo:block>\n");
		testFo.append("</fo:table-cell>\n");
		testFo.append("</fo:table-row>\n");
		testFo.append("</fo:table-header>\n");

		testFo.append("<fo:table-body>\n");
		testFo.append("<xsl:apply-templates select=\"d:item\"/>\n");
		testFo.append("</fo:table-body>\n");
		testFo.append("</fo:table>\n");

		testFo.append("<fo:block border-bottom=\"2px solid red\" margin-top=\"10px\" margin-bottom=\"10px\"/>\n");

		testFo.append("</xsl:template>\n");

		testFo.append("<xsl:template match=\"d:item\">\n");
		testFo.append("<fo:table-row>\n");
		testFo.append("<fo:table-cell>\n");
		testFo.append("<fo:block>\n");
		testFo.append("<xsl:value-of select=\"@name\"/>\n");
		testFo.append("</fo:block>\n");
		testFo.append("</fo:table-cell>\n");
		testFo.append("<fo:table-cell>\n");
		testFo.append("<fo:block>\n");
		testFo.append("<xsl:apply-templates/>\n");
		testFo.append("</fo:block>\n");
		testFo.append("</fo:table-cell>\n");
		testFo.append("</fo:table-row>\n");
		testFo.append("</xsl:template>\n");

		testFo.append("</xsl:stylesheet>\n");

		return testFo.toString();
	}

	private Fop getFop(OutputStream out) {
		if (this.fopFactory == null) {
			this.fopFactory = FopFactory.newInstance();
		}
		Fop fop = null;
		FOUserAgent ua = this.fopFactory.newFOUserAgent();
		try {
			fop = this.fopFactory.newFop(MimeConstants.MIME_PDF, ua, out);
		} catch (FOPException e) {
			e.printStackTrace();
		}
		return fop;
	}

	private String getHelloWorld() {
		StringBuilder testFo = new StringBuilder();

		testFo.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");
		testFo.append("<fo:layout-master-set>\n");
		testFo.append("<fo:simple-page-master page-height=\"297mm\" page-width=\"210mm\" margin-top=\"25mm\" ");
		testFo.append("margin-bottom=\"25mm\" margin-left=\"25mm\" margin-right=\"25mm\" master-name=\"A4\">\n");
		testFo.append("<fo:region-body/>\n");
		testFo.append("</fo:simple-page-master>\n");
		testFo.append("</fo:layout-master-set>\n");
		testFo.append("<fo:page-sequence master-reference=\"A4\">\n");
		testFo.append("<fo:flow flow-name=\"xsl-region-body\">\n");
		testFo.append("<fo:block font-size=\"64px\">Hello World!</fo:block>\n");
		testFo.append("</fo:flow>\n");
		testFo.append("</fo:page-sequence>\n");
		testFo.append("</fo:root>\n");
		return testFo.toString();
	}

	private SAXParser getParser() {

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convenience class to get access to a NotesXML Exporter
	 * 
	 * @return Notes2XML Exporter
	 */
	public Notes2XML getNotesXMLExporter() {
		return new Notes2XMLImpl();
	}

}
