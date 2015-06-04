package com.notessensei.mimeimport;

import java.util.Stack;

import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;

public class MimePartInfo {

	public static final String	CONTENT_TYPE	= "Content-Type";
	public static final String	CONTENT_TRANSFER_ENCODING	= "Content-Transfer-Encoding";
	private MIMEEntity mimepart;
	private String contentType;
	private String contentEncoding;

	public MimePartInfo(MIMEEntity newPart) {
		this.mimepart = newPart;
	}

	public void createChildEntity(Stack<MimePartInfo> mimeParts) {
		try {
			mimeParts.push(new MimePartInfo(this.mimepart.createChildEntity()));
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public void createHeader(String hName, String hValue) {
		try {
			this.mimepart.createHeader(hName).setHeaderVal(hValue);
		} catch (NotesException e) {
			e.printStackTrace();
		}

		// We need to check for some special entities
		if (hName.equalsIgnoreCase(MimePartInfo.CONTENT_TRANSFER_ENCODING)) { // Encoding
			this.contentEncoding = hValue;
		} else if (hName.equalsIgnoreCase(MimePartInfo.CONTENT_TYPE)) { // mime-type and charset
			this.contentType = hValue;
		}
	}

	public String getContentEncoding() {
		return this.contentEncoding;
	}

	public String getContentType() {
		if (this.contentType==null) {
			return "text/plain";
		}
		return this.contentType;
	}

	public MIMEEntity getMimepart() {
		return this.mimepart;
	}
}
