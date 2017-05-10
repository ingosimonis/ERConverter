package org.ogc.er;

import java.util.ArrayList;
import java.util.List;

/**
 * SpellingReport for a directory or single file
 * @author isi
 *
 */
public class Report {

	private List<ReportPart> parts;
	private String title;
	
	public Report(String title) {
		this.title = title;
		this.parts = new ArrayList<ReportPart>();
	}


	/**
	 * @param parts the parts to set
	 */
	public void addPart(ReportPart part) {
		parts.add(part);
	}
	

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * access to all parts
	 * @return
	 */
	public List<ReportPart> getParts() {
		return parts;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// if this report has not parts then only return the title
		if(parts.isEmpty()) {
			return title; 
		}
		// if this report has only one part, then ommit the printing of the title, as it is the same as in 
		// the underlying part
		if(parts.size() == 1) {
			return parts.get(0).toString();
		}
		// if the report has multiple parts, return the title and all parts
		StringBuffer buf = new StringBuffer();
		buf.append("Report for directory: ");
		buf.append(title);
		for(ReportPart p : parts) {
			buf.append(p.toString());
		}
		return buf.toString();
	}
	
	/**
	 * prints the content to SysOut
	 */
	public void print(){
		System.out.println(this.toString());
	}
}
