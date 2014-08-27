package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.ApplicationWrapper;

public class SigampAnalysis extends ApplicationWrapper {
	
	private String 	inputAnnotator;
	private int begin = 0;
	private int time = -1;
	private int nMax = 300;
	private double 	deltaMeasureStart = 0.05;
	private double 	deltaMeasureEnd = 0.05;
	private double 	deltaTimeWin = 1.0;
	
	private boolean highRez;
	private boolean verbose;
	private boolean quickmode;
	private boolean printPhysUnits;	
	private boolean printDay;
	private boolean printElapsed;
	private boolean printHours	;
	private boolean printMinutes;
	private boolean printSeconds;
	private boolean printSamples;
	
	private String path;
	private String headerName;
	private String outputName;
	
	public SigampAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub

	}

	@Override
	public void defineInputParameters() {
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.

		inputAnnotator	= (String) this.getAnalysisVO().getCommandParamMap().get("a"); // -a
		
		if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
			begin = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("f"));    // -f
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
			time = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("t"));    // -t (-1) defaults to end of record.
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("n") != null){
			nMax = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("n"));    // -n Make up to nmax measurements on each signal (default: 300).
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("dt1") != null){
			deltaMeasureStart = Double.parseDouble( (String) this.getAnalysisVO().getCommandParamMap().get("dt1"));// -d Set the measurement window relative to QRS annotations. Defaults: dt1 = 0.05 (seconds before the annotation);
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("dt2") != null){
			deltaMeasureEnd = Double.parseDouble((String) this.getAnalysisVO().getCommandParamMap().get("dt2"));    // -d Set the measurement window relative to QRS annotations. Defaults: dt2 = 0.05 (seconds after the annotation).	
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("w") != null){
			deltaTimeWin = Double.parseDouble((String) this.getAnalysisVO().getCommandParamMap().get("w"));    // -w Set RMS amplitude measurement window. Default: dtw = 1 (second).
		}
		
		highRez		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("H")); // -H  high-resolution mode
		verbose		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("v")); // -v Verbose mode: print individual measurements as well as trimmed means. 
		quickmode		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("q")); // -q  Quick mode: print individual measurements only. 
		printPhysUnits	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("p")); //  Print results in physical units (default: ADC units).
		printDay		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("pd")); // -pd time of day and date if known
		printElapsed	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("pe")); // -pe (elapsed time in hours, minutes, and seconds
		printHours		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("ph")); // -ph (elapsed time in hours)
		printMinutes	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("pm")); // -pm (elapsed time in minutes)
		printSeconds	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("ps")); //  -ps (elapsed time in seconds (default))
		printSamples	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("pS")); // -pS (elapsed time in sample intervals).
		//**********************************************************************
		String sHeaderPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(sHeaderPathName);
		headerName = AnalysisUtils.extractName(sHeaderPathName);
		
		debugPrintln("- sHeaderPathName: " + sHeaderPathName);
		debugPrintln("- sHeaderPath: " + path);
		debugPrintln("- sHeaderName: " + headerName);
		
		int iIndexPeriod = headerName.lastIndexOf(".");
		outputName = headerName.substring(0, iIndexPeriod) + '_' + this.getAnalysisVO().getJobIdNumber();
		
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("sigamp()");
		debugPrintln("- sHeaderFile:" + headerName);
		debugPrintln("- sPath:" + path);
		debugPrintln("- iBegin:" + begin);
		debugPrintln("- sInputAnnotator:" + inputAnnotator); // Measure QRS peak-to-peak amplitudes based on normal QRS annotations from the specified annotator. 
		debugPrintln("- fdt1:" + deltaMeasureStart);
		debugPrintln("- fdt2:" + deltaMeasureEnd);
		debugPrintln("- bHighRez:" + highRez);
		debugPrintln("- iNmax:" + nMax); // Make up to nmax measurements on each signal (default: 300). 
		debugPrintln("- iTime:" + time); // Process until the specified time in record (-1 to default, the end of the record) Processing will be terminated prematurely if 250 measurements are made before reaching the specified time.
		debugPrintln("- fDeltaTimeWin:" + deltaTimeWin); // Set RMS amplitude measurement window. Default: dtw = 1 (second). 
		//--  output adjustments
		debugPrintln("- bVerbose:" + verbose); // Verbose mode: print individual measurements as well as trimmed means. 
		debugPrintln("- bQuickmode:" + quickmode); // Quick mode: print individual measurements only. 
		debugPrintln("- bPrintPhysicalUnits:" + printPhysUnits); //  print results in physical units, time in seconds, same as -ps (default: ADC units)
		debugPrintln("- bPrintDay:" + printDay); // print physical units + time of day and date if known
		debugPrintln("- bPrintElapsed:" + printElapsed); // print physical units + elapsed time as <hours>:<minutes>:<seconds>
		debugPrintln("- bPrintHours:" + printHours);// print physical units + elapsed time in hours
		debugPrintln("- bPrintMinutes:" + printMinutes); // print physical units + elapsed time in minutes
		debugPrintln("- bPrintSeconds:" + printSeconds); // print physical units + elapsed time in seconds (default)
		debugPrintln("- bPrintSamples:" + printSamples); // print physical units + elapsed time in sample intervals


		try {
			
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerName.lastIndexOf(".");
			String sRecord = headerName.substring(0, iIndexPeriod);
			
			String sCommand = "sigamp -r " + path + sRecord; // record name (same as header file name.)
			if(begin != 0) sCommand += " -f " + begin;
			if(inputAnnotator != null && !inputAnnotator.equals("")){
				sCommand += " -a " + inputAnnotator; // Measure QRS peak-to-peak amplitudes based on normal QRS annotations from the specified annotator. 
			}
			// ( -d option must be used with -a;)
			if((deltaMeasureStart != 0.05) || (deltaMeasureEnd != 0.05)) sCommand += " -a " + deltaMeasureStart + " " + deltaMeasureEnd; // Set the measurement window relative to QRS annotations. 
			if(highRez) sCommand += " -H "; // Read the signal files in high-resolution mode (default: standard mode).
			if(nMax != 300) sCommand += " -n " + nMax; // Make up to nmax measurements on each signal (default: 300).			
			if(time != -1) sCommand += " -t " + time; // Process until the specified time in record (default: the end of the record). Processing will be terminated prematurely if 250 measurements are made before reaching the specified time. 
			if(deltaTimeWin != 1.0) sCommand += " -w " + deltaTimeWin; //Set RMS amplitude measurement window in seconds. Default: dtw = 1 (second). 
			//--  output adjustments
			if(verbose) sCommand += " -v "; //Verbose mode: print individual measurements as well as trimmed means. 
			if(quickmode) sCommand += " -q "; // Quick mode: print individual measurements only, not trimmed means.
			if(printPhysUnits) sCommand += " -p ";//  print results in physical units, time in seconds, same as -ps(default: ADC units)
			if(printDay) sCommand += " -pd ";// print physical units + time of day and date if known
			if(printElapsed) sCommand += " -pe ";//print physical units + elapsed time as <hours>:<minutes>:<seconds>
			if(printHours) sCommand += " -ph ";// print physical units + elapsed time in hours
			if(printMinutes) sCommand += " -pm ";//print physical units + elapsed time in minutes
			if(printSeconds) sCommand += " -ps ";// print physical units + elapsed time in seconds (default, same as -p)
			if(printSamples) sCommand += " -pS ";// print physical units + elapsed time in sample intervals

			bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);
			
			bRet &= stdErrorHandler();
			
			if(bRet){
				
				switch (this.getAnalysisVO().getResultType()) {
				case ORIGINAL_FILE:
				case CSV_FILE:
					List<String> outputFilenames = new ArrayList<String>();
					String outputFile;
					//set first output file to output generated by the sigamp command
					if(AnalysisResultType.ORIGINAL_FILE.equals(this.getAnalysisVO().getResultType())){
						outputFile = path + outputName + ".txt";
						stdReturnHandler(outputFile);	
					}else{
						outputFile = stdCSVReturnHandler(path, outputName, null);	
					}
					outputFilenames.add(outputFile);
					
					this.getAnalysisVO().setOutputFileNames(outputFilenames);	
					break;
				case JSON_DATA:
					this.setJSONOutput();
					break;
				}
				
				
				
				
			}else{
				debugPrintln("- Encountered errors.");
			}
			
		} catch (IOException e) {
			bRet = false;
			log.error(e.getMessage());
		}

		this.getAnalysisVO().setSucess(bRet);

	}

}
