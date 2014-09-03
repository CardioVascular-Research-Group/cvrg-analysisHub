package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
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
	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
	
		if(this.getAnalysisVO().getCommandParamMap() != null && !this.getAnalysisVO().getCommandParamMap().isEmpty()){
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
		}
		
		String headerPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(headerPathName);
		headerName = AnalysisUtils.extractName(headerPathName);
		
		debugPrintln("- headerPathName: " + headerPathName);
		debugPrintln("- path: " + path);
		debugPrintln("- headerName: " + headerName);
		
		int indexPeriod = headerName.lastIndexOf(".");
		outputName = headerName.substring(0, indexPeriod) + '_' + this.getAnalysisVO().getJobIdNumber();
		
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("sigamp()");
		debugPrintln("- headerName:" + headerName);
		debugPrintln("- path:" + path);
		debugPrintln("- begin:" + begin);
		debugPrintln("- inputAnnotator:" + inputAnnotator); // Measure QRS peak-to-peak amplitudes based on normal QRS annotations from the specified annotator. 
		debugPrintln("- deltaMeasureStart:" + deltaMeasureStart);
		debugPrintln("- deltaMeasureEnd:" + deltaMeasureEnd);
		debugPrintln("- highRez:" + highRez);
		debugPrintln("- nMax:" + nMax); // Make up to nmax measurements on each signal (default: 300). 
		debugPrintln("- time:" + time); // Process until the specified time in record (-1 to default, the end of the record) Processing will be terminated prematurely if 250 measurements are made before reaching the specified time.
		debugPrintln("- deltaTimeWin:" + deltaTimeWin); // Set RMS amplitude measurement window. Default: dtw = 1 (second). 
		//--  output adjustments
		debugPrintln("- verbose:" + verbose); // Verbose mode: print individual measurements as well as trimmed means. 
		debugPrintln("- quickmode:" + quickmode); // Quick mode: print individual measurements only. 
		debugPrintln("- printPhysUnits:" + printPhysUnits); //  print results in physical units, time in seconds, same as -ps (default: ADC units)
		debugPrintln("- printDay:" + printDay); // print physical units + time of day and date if known
		debugPrintln("- printElapsed:" + printElapsed); // print physical units + elapsed time as <hours>:<minutes>:<seconds>
		debugPrintln("- printHours:" + printHours);// print physical units + elapsed time in hours
		debugPrintln("- printMinutes:" + printMinutes); // print physical units + elapsed time in minutes
		debugPrintln("- printSeconds:" + printSeconds); // print physical units + elapsed time in seconds (default)
		debugPrintln("- printSamples:" + printSamples); // print physical units + elapsed time in sample intervals


		try {
			
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] envVar = new String[0];  
			
			// build command string
			int indexPeriod = headerName.lastIndexOf(".");
			String record = headerName.substring(0, indexPeriod);
			
			String command = "sigamp -r " + path + record; // record name (same as header file name.)
			if(begin != 0) command += " -f " + begin;
			if(inputAnnotator != null && !inputAnnotator.equals("")){
				command += " -a " + inputAnnotator; // Measure QRS peak-to-peak amplitudes based on normal QRS annotations from the specified annotator. 
			}
			// ( -d option must be used with -a;)
			if((deltaMeasureStart != 0.05) || (deltaMeasureEnd != 0.05)) command += " -a " + deltaMeasureStart + " " + deltaMeasureEnd; // Set the measurement window relative to QRS annotations. 
			if(highRez) command += " -H "; // Read the signal files in high-resolution mode (default: standard mode).
			if(nMax != 300) command += " -n " + nMax; // Make up to nmax measurements on each signal (default: 300).			
			if(time != -1) command += " -t " + time; // Process until the specified time in record (default: the end of the record). Processing will be terminated prematurely if 250 measurements are made before reaching the specified time. 
			if(deltaTimeWin != 1.0) command += " -w " + deltaTimeWin; //Set RMS amplitude measurement window in seconds. Default: dtw = 1 (second). 
			//--  output adjustments
			if(verbose) command += " -v "; //Verbose mode: print individual measurements as well as trimmed means. 
			if(quickmode) command += " -q "; // Quick mode: print individual measurements only, not trimmed means.
			if(printPhysUnits) command += " -p ";//  print results in physical units, time in seconds, same as -ps(default: ADC units)
			if(printDay) command += " -pd ";// print physical units + time of day and date if known
			if(printElapsed) command += " -pe ";//print physical units + elapsed time as <hours>:<minutes>:<seconds>
			if(printHours) command += " -ph ";// print physical units + elapsed time in hours
			if(printMinutes) command += " -pm ";//print physical units + elapsed time in minutes
			if(printSeconds) command += " -ps ";// print physical units + elapsed time in seconds (default, same as -p)
			if(printSamples) command += " -pS ";// print physical units + elapsed time in sample intervals

			bRet = executeCommand(command, envVar, WORKING_DIR);
			
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
				throw new AnalysisExecutionException("Command execution error. ["+ command+"]");
			}
			
		} catch (IOException e) {
			throw new AnalysisExecutionException("Error on "+this.getAnalysisVO().getType()+" command output handling", e);
		}

		this.getAnalysisVO().setSucess(bRet);

	}

}
