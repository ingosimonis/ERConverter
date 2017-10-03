/**
 * 
 */
package org.ogc.er;

import static org.asciidoctor.Asciidoctor.Factory.create;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import org.ogc.controller.view.ERViewController;
import org.ogc.io.ConsoleHandler;

import x.ext.ImageInfo;


/**
 * @author isi
 * An OGC ER is nothing but a folder on the local hard drive that contains all asciidoc 
 * and other material of an ER. The EngineeringReportFolder can contain any number of 
 * subfolders, which will be processed during spell check, PNG image file check, or conversion 
 * operations. 
 */
public class EngineeringReportFolder {
		
	// base directory of the ER folder on local directory
	private File directory;
	// map with K,V: image name, interlace setting
	private Map<String,Boolean> images = new HashMap<String, Boolean>();
	
	// asciidoctor engine 		
//	Asciidoctor doc = create();
		
	// CONSTRUCTOR
	public EngineeringReportFolder(File directory) {
		this.directory = directory;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * tmp version of toPDF
	 * @param output
	 * @return
	 */
	public Report convertToPDF(File output) {
		return this.convertASCIIDOC(null, output, ERConstants.BACKEND_PDF);
	}
	
	
	/**
	 * Converts the asciidoc to HTML. All asciidoctor messages will be provided in the 
	 * AsciidoctorConversionReport. The process runs recursively if If the folder contains 
	 * further ER folders. The process checks for er.adoc files. If these are missing, the 
	 * resulting Report contains appropriate messages.
	 * @param output
	 */
	public Report convertToHTML(File output) {
		return this.convertASCIIDOC(null, output, ERConstants.BACKEND_HTML);
	}
	
	/**
	 * private operation to run process recursively if ERF is a root folder
	 * @param dir
	 * @param output
	 */
	private Report convertASCIIDOC(File dir, File output, int backend) {
		Report report = null;
		// dir equals null means that this is the start directory. In this case
		// use directory and check what can be done with it
		if(dir == null) {
			report = new Report(directory.getAbsolutePath());
			this.handleASCIIDOCConversion(directory, output, report, backend);
		} else { // recursive run, dir != null
			report = new Report(dir.getAbsolutePath());
			this.handleASCIIDOCConversion(dir, output, report, backend);
		}
		return report;
	}	
	
	/**
	 * second iteration onwards 
	 * @param file
	 * @param output
	 * @param report
	 */
	private void handleASCIIDOCConversion(File file, File output, Report report, int backend) {
		
		// check for hidden and other fancy files
		if(file.isFile() && !file.getAbsolutePath().endsWith(".adoc")) {
			return;
		}
		
		// FilenameFilter to detect er.adoc files		
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.equalsIgnoreCase("er.adoc") || 
					name.equalsIgnoreCase("userGuide.adoc")) {
					return true;
				} else {
					return false;
				}
			}
		};
				
		// We have to differentiate three cases. file can be: 
		//      (1) er.adoc or userGuide.adoc-> run conversion
		//		(2) folder with er.adoc -> get er.adoc and run conversion
		//		(3) folder with subfolders -> check each subfolder for er.adoc
		
		// case 1: check if directory equals *.adoc file 
		if(file.getAbsolutePath().endsWith("er.adoc") ||
		   file.getAbsolutePath().endsWith("userGuide.adoc")) {
			this.runASCIIDOCConversion(file, output, report, backend);
			return;
		} 
		// case 2-3: otherwise it is a directory that may contain an er.adoc file
		// case 2: check if directory contains an er.adoc file	
		File[] files = file.listFiles(textFilter); 
		if(files != null && files.length == 1) {
			// er.adoc file found and stored in files[0]
			this.runASCIIDOCConversion(files[0], output, report, backend);
		} else {// means no er.adoc found -> get all subfolders
			files = file.listFiles();
			// and call this operation for each subfolder
			for(File f : files) {
				this.handleASCIIDOCConversion(f, output, report, backend);
			}
		}	
	}
	
	
	/**
	 * runs the actual HTML conversion to allow recursive processes
	 * @param file
	 * @param output
	 */
	private void runASCIIDOCConversion(File file, File output, Report report, int backend) {
		
		ConsoleHandler console = new ConsoleHandler();
        console.redirectErr(System.err);
        
		// all ERs produce their own home folder within output
		String outputPath = output.getAbsolutePath();
		String folderName = file.getParentFile().getName();
		File targetDir = new File(outputPath + "/" + folderName);
		
		Asciidoctor doc = create();
		OptionsBuilder options = null;
		
		// switch between pdf and html backend
		// some issues here: toDir(targetDir) is always interpreted as relative path! same is true for toFile
		switch(backend) {
			case ERConstants.BACKEND_HTML: {
				Attributes attributes = AttributesBuilder.attributes()
						.attribute("source-highlighter", "coderay").get();
				options = OptionsBuilder.options()
						.compact(true)
						.safe(SafeMode.SAFE)
						.mkDirs(true)
						.attributes(attributes);
				break;
			}
			case ERConstants.BACKEND_PDF: {
				Preferences prefs = Preferences.userNodeForPackage(ERViewController.class);
				String ogcTheme = prefs.get(ERConstants.THEME_FILE, ERConstants.DEFAULT_OGC_THEME);
				String fontsDir = prefs.get(ERConstants.FONTS_DIR, ERConstants.DEFAULT_FONTS_DIR);
				Attributes attributes = AttributesBuilder.attributes()
						.attribute("source-highlighter", "coderay")
						.attribute("pdf-style", ogcTheme)
		        		.attribute("pdf-fontsdir", fontsDir).get();
				options = OptionsBuilder.options()
						.backend("pdf")
						.compact(true)
						.safe(SafeMode.SAFE)
						.mkDirs(true)
						.attributes(attributes);
				break;
			}
		}

		// run the conversion
		try {
			doc.convertFile(file, options);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		//super ugly workaround because all results end up in the home directory rather than the output directory
		String sourceFile = file.getAbsolutePath();
		String fileName = file.getName();
		switch(backend) {
			case(ERConstants.BACKEND_HTML):
				fileName = fileName.replaceAll(".adoc", ".html");
				sourceFile = sourceFile.replaceAll(".adoc", ".html");
				break;
			case(ERConstants.BACKEND_PDF): 
				fileName = fileName.replaceAll(".adoc", ".pdf");
				sourceFile = sourceFile.replaceAll(".adoc", ".pdf");
				break;
		}
		
		File s = new File(sourceFile);
		File t = new File(targetDir + "/" + fileName);

		try {
			if(!t.getParentFile().exists()) {
    			Files.createDirectory(t.getParentFile().toPath());
    		}
			Files.move(s.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//end of ugly workaround
		
		String[] messages = console.getErrMessages();
//		String[] messages = new String[0];
		// Create ConversionReports as parts of the overall report
		ConversionReport c;
		if(messages.length == 0) { //if no messages are captured, say so :)
			c = new ConversionReport(file.getAbsolutePath());
			if(backend == ERConstants.BACKEND_HTML) {
				c.addMessage("Successfully converted to HTML. ");
			} else if(backend == ERConstants.BACKEND_PDF) {
				c.addMessage("Successfully converted to PDF. ");
			} 
		} else { // in case that error messages have been captured
			c = new ConversionReport(file.getAbsolutePath(), messages);
		}
		// if this was an HTML conversion, then copy image files and add message to report
		if(backend == ERConstants.BACKEND_HTML) {
			try {
				copyImages(file, targetDir);
				c.addMessage("\nAll images copied successfully! ");
			} catch (Exception e) {
				e.printStackTrace();
				c.addMessage("\nError while copying image files!");
			}
		}
		report.addPart(c);
	}
	

	/**
	 * copies all image files to allow proper HTML rendering in target directory
	 * @param file
	 * @param output
	 */
	private void copyImages(File file, File targetDir) throws Exception {
		// first clear images store
		this.images.clear();
		// copy all images to target directory!
		this.searchImages(file.getParentFile());
		Set<String> set = images.keySet();
		if(set.isEmpty()) { // no images found!
			return;
		}
		Iterator<String> iter = set.iterator();
		File image;
		String to;
		String root = file.getParent();
		StringBuffer buffer = new StringBuffer();
		buffer.append(targetDir);
		while(iter.hasNext()) {
			image = new File(iter.next());
			// get relative path of image file to base dir: image.absPath - file.absPath
			to = image.getAbsolutePath();
			to = to.substring(root.length(), image.getAbsolutePath().length());
			buffer.append(to);
			try {
				if(!new File(buffer.toString()).exists()) {
					Files.createDirectories((new File(buffer.toString()).toPath()));
				}
				Files.copy(image.toPath(), (new File(buffer.toString()).toPath()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer.replace(0, buffer.length(), targetDir.getAbsolutePath());
		}
	}

	/**
	 * get a list of all images and their interlace settings
	 * @return
	 */
	public Map<String, Boolean> checkImages() {
		if(directory.isDirectory()) {
			searchImages(directory);
		} else {
			System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}
		
		return images;
	}
	
	
	/**
	 * get a list of all interlaced PNGs
	 * @return
	 */
	public List<String> getInterlacedPNGs() {
		List<String> png = new ArrayList<String>();
		this.checkImages();
		
		Iterator<Map.Entry<String, Boolean>> entries = images.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Boolean> entry = entries.next();
            if(entry.getValue()) {
            	png.add(entry.getKey());
            }
        }
		return png;
	}
	
	
	
	/**
	 * overwrites all interlaced PNGs with non-interlaced copies
	 * @return
	 */
	public boolean removePNGInterlace() {
		
		// create process 
		Process p;
		
		// check imagemagick settings
		Preferences prefs = Preferences.userNodeForPackage(ERViewController.class);
		String magick = prefs.get(ERConstants.IMAGE_MAGICK, "/usr/local/bin/magick");
		
//		StringBuffer output = new StringBuffer();
		
		// get all interlaced images
		List<String> png = this.getInterlacedPNGs();
		Iterator<String> iter = png.iterator();
		
		// remove interlace settings
		while(iter.hasNext()) {
			String interlacedImage = iter.next();
			System.out.println(interlacedImage);
			String[] command = { magick, "-interlace", "none", interlacedImage, interlacedImage };

			try {
				// execute imagemagick command
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				
				// read results
//				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//				String line = "";
//				while ((line = reader.readLine())!= null) {
//					output.append(line + "\n");
//				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
//		System.out.println(output.toString());
		return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * searches base directory recursively for all images and checks interlaced settings
	 * @param file
	 */
	private void searchImages(File file) {
		// iterate recursively over whole folder
		if(file.isDirectory()) {
			if(file.canRead()) {
				for(File tmp : file.listFiles()) {
					if(tmp.isDirectory()) {
						searchImages(tmp);
					} else if(this.isImage(tmp)){
						// check for images		
						try {
							ImageInfo iInfo = new ImageInfo();
					        iInfo.setInput(new RandomAccessFile(tmp, "r")); // in can be InputStream or RandomAccessFile
					        if (!iInfo.check()) {
					        	System.err.println("Not a supported image file format.");
					        	return;
					        }
					        // add absolute filename and interlace settings to map
						    images.put(tmp.getAbsoluteFile().toString(), iInfo.isProgressive());	 
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * helper method to check if given file is one of relevant image types
	 * @param file
	 * @return
	 */
	private boolean isImage(File file) {
		// currently reduced to png only, as asciidoctor can handle other types
//	    String[] extensions = {".jpg",".png",".gif", ".tif"};
	    String[] extensions = {".png"};
	    String filepath = file.getAbsoluteFile().toString();
	    for (String extension : extensions) {
	        if(filepath.endsWith(extension)){
	            return true;
	        }
	    }
	    return false;
	}
	
}
