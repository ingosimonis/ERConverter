package org.ogc.io;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 *	Class to intercept output from a PrintStream and add it to a Document.
 *  The output can optionally be redirected to a different PrintStream.
 *  The text displayed in the Document can be color coded to indicate
 *  the output source.
 */
public class ConsoleOutputStream extends ByteArrayOutputStream
{
	private final String EOL = System.getProperty("line.separator");
	private List<String> messages;

	/*
	 *  Specify the option text color and PrintStream
	 */
	public ConsoleOutputStream() {
		messages = new ArrayList<String>();
	}
	
	/**
	 * Access to all messages of a given ConsoleOutputStream
	 * @return
	 */
	public String[] getMessages(){
		return messages.toArray(new String[0]);
	}

	/*
	 *  Override this method to intercept the output text. 
	 */
	public void flush()
	{
		String message = toString();

		//  check for empty messages
		if (message.length() == 0) {
			return;
		} 
		// check for CR/LF messages
		if(EOL.equals(message)) return;
		
//		System.out.println(message);
		
		// insert if proper message
		messages.add(message);

		// reset
		reset();
	}
}
