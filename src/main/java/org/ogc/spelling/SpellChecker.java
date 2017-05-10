/**
 * 
 */
package org.ogc.spelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.ogc.er.Report;
import org.ogc.er.SpellingReportPart;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author isi
 * test for spell checks
 */
public class SpellChecker {
	
	protected JLanguageTool langTool;
		
	// CONSTRUCTOR
	public SpellChecker(File ignoreFile) throws FileNotFoundException {
		List<String> wordsToIgnore = this.loadIgnoreList(ignoreFile);
		langTool = new JLanguageTool(new AmericanEnglish());
		for (Rule rule : langTool.getAllRules()) {
		  if (!rule.isDictionaryBasedSpellingRule()) {
		    langTool.disableRule(rule.getId());
		  }
		  if (rule instanceof SpellingCheckRule) {
			    ((SpellingCheckRule)rule).addIgnoreTokens(wordsToIgnore);
			  }
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Generates a SpellingReport for the File file. If file is a directory, SpellingReport will contain all 
	 * elements recursively
	 * @param file
	 * @return SpellingReport
	 * @throws IOException
	 */
	public Report getSpellingReport(File file) throws IOException {
		Report report = new Report(file.getAbsolutePath());
		this.addReportParts(report, file);
		return report;
	}
	
	
	private void addReportParts(Report report, File file) {
		
		if(file.isDirectory()) { // if file is a directory then generate ReportParts for all sub-dirs/files
			for(File f : file.listFiles()) {
				if(f.isDirectory()) {
					this.addReportParts(report, f);
				}
				if(this.isAdocFile(f)) {
					this.addReportParts(report, f);
				}
			}
		} else { // if file is a single file
			if(this.isAdocFile(file)) {
				report.addPart(this.spellCheckFile(file));
			}
		}
	}
	
	/**
	 * generates a SpellingReportPart with a Multimap<Integer lineNumber, String listOfMisspelledWords>
	 * @param text array of Strings as read from file
	 */
	private SpellingReportPart spellCheckFile(File file) {
		
		String[] text = this.loadText(file);
		Multimap<Integer, String> misspelled = ArrayListMultimap.create();
		
		List<RuleMatch> matches;
		int line = 1;
		try {
			for(String s : text) {
				matches = langTool.check(s);
				for(RuleMatch match : matches) {
					misspelled.put(Integer.valueOf(line), s.substring(match.getFromPos(), match.getToPos()));
//					System.out.println(s.substring(match.getFromPos(), match.getToPos()));
				}
				line++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new SpellingReportPart(file.getAbsolutePath(), misspelled);
	}
	
	

	/**
	 * gets the number of errors in a given String
	 * @param text
	 * @return
	 */
	public int getNumberOfErrors(String text) {
		List<RuleMatch> matches;
		int errors = 0;
		try {
			matches = langTool.check(text);
			for (RuleMatch match : matches) {
				errors++;
				  System.out.print("Potential typo at characters " +
				      match.getFromPos() + "-" + match.getToPos() + ": " +
				      match.getMessage());
				  System.out.println(": " +text.substring(match.getFromPos(), match.getToPos()));
//				  System.out.println("Suggested correction(s): " +
//				      match.getSuggestedReplacements());
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return errors;
	}
	
	/**
	 * gets the number of Errors in a given file
	 * @param file .file to be checked
	 * @param load flag to indicate if all imported files should be loaded
	 * @return number of Errors in that file
	 */
	public int getNumberOfErrors(File file, boolean load) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));;
		StringBuffer sb = new StringBuffer();
		String line = null;
		List<File> filesToCheck = new ArrayList<File>();
		
		// get the path of the master file
		String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")+1);
		
		// check if only the given file or all imported files should be checked
		// case: all included files
		if(load) {
			// iterate over all entries in file
			while((line = br.readLine()) != null) {
				sb.append(line);
				
				// check includes and load all included files
				if(line.startsWith("include::")) {
					filesToCheck.add(new File(path + line.trim().substring(9, line.trim().length()-2)));
				}
			}
			br.close();
			int totalErrors = 0;
			for(File f : filesToCheck) {
				System.out.println("\nchecking file: " + f.getName());
				totalErrors += this.getNumberOfErrors(f, false);
			}
			return totalErrors;
		} 
		// case that only the given file should be checked
		else {
			// iterate over all entries in file
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		}
		return getNumberOfErrors(sb.toString());
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
		
	private String[] loadText(File file) {
		List<String> textLines = new ArrayList<String>();

		BufferedReader br = null;
		String line = null;
		StringBuffer buf = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				buf.append(line);
				buf.append(" ");
				textLines.add(buf.toString());
				buf.setLength(0);
			}
			br.close();
		} catch(Exception e) {
			return textLines.toArray(new String[0]);
		}
		return textLines.toArray(new String[textLines.size()]);
	}	
	
	/**
	 * check if a given file is asciidoc
	 * @param file
	 * @return
	 */
	private boolean isAdocFile(File file){
		String s = file.getAbsolutePath();
		if(s.endsWith(".adoc")) {
			return true;
		} else return false;
	}
	
	
	/**
	 * private method to load all elements from the ignore file into the spell checker
	 * @param file
	 * @return List<String> with terms to be ignored
	 * @throws FileNotFoundException
	 */
	private List<String> loadIgnoreList(File file) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<String> words = new ArrayList<String>();
		String line = null;
		try {
			while((line = br.readLine()) != null) {
				if(!line.isEmpty()) {
					words.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return words;
	}
	
}
