package org.ogc.maven.erConverter;

import java.io.File;
import java.util.Calendar;

import org.im4java.process.ProcessStarter;
import org.ogc.er.EngineeringReportFolder;
import org.ogc.er.Report;


/**
 * Class used for testing without GUI
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	long t = Calendar.getInstance().getTimeInMillis();
    	System.out.println("Process started!\n");
    	
    
    	
    	
    	String myPath="/usr/local/bin";
    	ProcessStarter.setGlobalSearchPath(myPath);
        
        String dir = "/DS001-VectorTiles/er.adoc";
        File output = new File("/test/compiled");
        
//        ConsoleHandler console = new ConsoleHandler();
//        console.redirectErr(System.err);
        
        
        EngineeringReportFolder erf = new EngineeringReportFolder(new File(dir));
        Report report = erf.convertToPDF(output);

        System.out.println(report.toString());
        
//        String[] m = console.getErrMessages();
//        System.out.println(m[0]);

       
        long msec = (Calendar.getInstance().getTimeInMillis() - t);
        System.out.println("\n\n-- completed in " + msec + "ms");
    }
}
		