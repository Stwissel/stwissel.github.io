package com.notessensei.mimeimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.james.mime4j.MimeException;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.Database;

public class Tester {

	public static void main(String[] args) {

		// Change these for your own test
		final String WORKDIR = "/home/stw/Desktop/fortest/";
		final String EXTENSION = ".eml";
		final String DBNAME = "DeleteMe.nsf";
		final String SERVER = "";

		NotesThread.sinitThread();
		try {
			Session s = NotesFactory.createSession();
			Database db = s.getDatabase(SERVER, DBNAME);
			File folder = new File(WORKDIR);
			File[] tobeImported = folder.listFiles();
			Mime2Doc md = new Mime2Doc();
			for (int i = 0; i < tobeImported.length; i++) {
				File f = tobeImported[i];
				if (f.isFile() && f.getName().endsWith(EXTENSION)) {
					System.out.println("File: "+f.getName());
					Document doc = db.createDocument();
					FileInputStream in = new FileInputStream(f);
					md.importMail(s,in, doc);
					doc.recycle();
				}
			}
			System.out.println("--- DONE ---");
			// Cleanup
			db.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			NotesThread.stermThread();
		}
	}
}
