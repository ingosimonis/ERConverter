package org.ogc.er;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConversionReport extends ReportPart {
	
	private List<String> messages;

	public ConversionReport(String header) {
		super(header);
		messages = new ArrayList<String>();
	}
	
	public ConversionReport(String header, String[]messages) {
		this(header);
		
		for(String s : messages) {
			this.messages.add(s);
		}
	}
	
	public void addMessage(String message){
		messages.add(message);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nReport for file: ");
		buf.append(header);
		buf.append("\n");
		
		Iterator<String>iter = messages.iterator();
		while(iter.hasNext()) {
			buf.append(iter.next());
		}
		buf.append("\n");
		return buf.toString();
	}
}
