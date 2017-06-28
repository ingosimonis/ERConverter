package org.ogc.io;

import java.io.PrintStream;


public class ConsoleHandler {
	private ConsoleOutputStream outStream;
	private ConsoleOutputStream errStream;

	public ConsoleHandler() {
		this.outStream = null;
		this.errStream = null;
	}
	
	/**
	 * get current Std.out messages
	 * @return
	 */
	public String[] getOutMessages() {
		return outStream.getMessages();
	}
	
	/**
	 * get current Std.err messages
	 * @return
	 */
	public String[] getErrMessages() {
		return errStream.getMessages();
	}

	
	/*
	 *  Redirect the output from the standard output to this ConsoleHandler
	 */
	public void redirectOut()
	{
		redirectOut(null);
	}

	/*
	 *  Redirect the output from the standard output to the ConsoleHandler 
	 *  and the specified PrintStream; e.g. System.out to allow parallel output
	 */
	public void redirectOut(PrintStream printStream)
	{
		outStream = new ConsoleOutputStream();
		System.setOut( new PrintStream(outStream, true) );
	}
	
	/**
	 * cancel the redirection for Std.out
	 */
	public void cancelOutRedirection() {
		System.setOut(null);
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the default text color and null PrintStream
	 */
	public void redirectErr()
	{
		redirectErr(null);
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	public void redirectErr(PrintStream printStream)
	{
		errStream = new ConsoleOutputStream();
		System.setErr( new PrintStream(errStream, true) );
	}

	/**
	 * cancel the redirection for Std.err
	 */
	public void cancelErrRedirection() {
		System.setErr(System.err);
	}
}
