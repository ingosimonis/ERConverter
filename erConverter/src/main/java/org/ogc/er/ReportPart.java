package org.ogc.er;

/**
 * ReportPart is single file part of a Spelling Report. It contains the file name as header and a 
 * Multimap<String line number, String listOfErrors
 * @author isi
 *
 */
public class ReportPart {
	protected String header;
	
	public ReportPart(String header) {
		this.header = header;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}
}
