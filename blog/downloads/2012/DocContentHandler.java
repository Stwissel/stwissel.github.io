package com.notessensei.mimeimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.Stream;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

public class DocContentHandler extends AbstractContentHandler implements
		ContentHandler {

	public static final String TEXTMIMETYPE = "text/plain";
	public static final String HTMLMIMETYPE = "text/html";
	public static final String RTFIELDNAME = "Body";

	Document doc = null;
	Session s = null;
	boolean mimeStatus = true;
	Stack<MimePartInfo> mimeParts = new Stack<MimePartInfo>();

	public DocContentHandler(Session s, Document newDoc) {
		this.doc = newDoc;
		this.s = s;
	}

	@Override
	public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
		String mtype = this.mimeParts.peek().getContentType();
		System.out.println("      Mime-Part: " + mtype);
		this.createBody(bd, is);
	}

	private void createBody(BodyDescriptor bd, InputStream is) {
		try {
			Stream notesIn = s.createStream();
			notesIn.setContents(is);
			notesIn.setPosition(0);
			MimePartInfo p = this.mimeParts.peek();
			MIMEEntity m = p.getMimepart();
			m.setContentFromBytes(notesIn, p.getContentType(), m.getEncoding());
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endBodyPart() throws MimeException {
		this.mimeParts.pop();
	}

	@Override
	public void endMessage() throws MimeException {
		try {
			doc.save();
			s.setConvertMime(this.mimeStatus);
			System.out.println("      -- Import complete");
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void field(Field rawField) throws MimeException {
		this.mimeParts.peek().createHeader(rawField.getName(),
				rawField.getBody());
	}

	@Override
	public void startBodyPart() throws MimeException {
		this.mimeParts.peek().createChildEntity(mimeParts);
	}

	@Override
	public void startMessage() throws MimeException {
		try {
			this.mimeStatus = s.isConvertMime();
			s.setConvertMime(false);
			MIMEEntity body = doc.createMIMEEntity(DocContentHandler.RTFIELDNAME);
			this.mimeParts.push(new MimePartInfo(body)); // First element on the stack
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
}