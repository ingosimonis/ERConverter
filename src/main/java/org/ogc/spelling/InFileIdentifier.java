package org.ogc.spelling;

import java.io.File;

/**
 * class identifies a file, a line number, and a cursor position within this line. If 
 * no line number is specified, location equals 0.
 * @author isi
 *
 */
public class InFileIdentifier {
	
	private File file;
	private int line;
	private String selection;

	public InFileIdentifier(File file, int line) {
		this(file, line, "");
	}
	
	/**
	 * @param file
	 * @param line
	 * @param caret
	 */
	public InFileIdentifier(File file, int line, String selection) {
		this.file = file;
		this.line = line;
		this.selection = selection;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the location
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param location the location to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @return the caret
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * @param caret the caret to set
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}
	

}
