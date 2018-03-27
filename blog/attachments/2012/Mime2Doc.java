package com.notessensei.mimeimport;

import java.io.IOException;
import java.io.InputStream;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;


public class Mime2Doc {
	
	public void importMail(Session s, InputStream in, Document doc) throws NotesException, MimeException, IOException {
		doc.replaceItemValue("Form", "Memo");
		MimeStreamParser parser = new MimeStreamParser();
		ContentHandler h = new DocContentHandler(s, doc);
		parser.setContentHandler(h);
		parser.parse(in);
	}
}
