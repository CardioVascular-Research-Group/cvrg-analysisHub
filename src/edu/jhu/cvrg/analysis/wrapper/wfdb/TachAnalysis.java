package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.util.RandomString;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationBasedAnalysisWrapper;

public class TachAnalysis extends AnnotationBasedAnalysisWrapper {

	private String annotator;
	
	private int startTime = 0;
	private int	endTime = 0;
	private int	frequency = 0;
	private int rate = 0;
	private int duration = 0;
	private int outputSamples = 0;
	private int smoothing = 0;
	
	private boolean outlier;
	private boolean sampleNumber;
	private boolean outputSeconds1;	
	private boolean outputMinutes;
	private boolean outputHours;
	
	private String path;
	private String inputFilename;
	private String outputFile;
	
	public TachAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
	}

	@Override
	public void defineInputParameters() {
		try {
			//*** The analysis algorithm should return a String array containing the full path/names of the result files.
			//String sAnnotator = "tach";
			String annotationFileName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), ".atr.qrs");
			annotator = annotationFileName.substring(annotationFileName.lastIndexOf('.')+1);
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
				startTime = Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("f")); // -f Begin at the specified time 
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
				endTime = Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("t")); // -t (-1) defaults to end of record.	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("F") != null){
				frequency = Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("F")); // -F Produce output at the specified sampling frequency (default: 2 Hz).	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("i") != null){
				rate = Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("i")); // -i For outlier detection, assume an initial rate of rate bpm.	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("l") != null){
				duration = Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("l")); // -l Process the record for the specified duration	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("n") != null){
				outputSamples	= Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("n")); // -n Produce exactly n output samples	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("s") != null){
				smoothing		= Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("s")); // -s Set the smoothing constant to k (default: 1; k must be positive).	
			}
			 
			outlier 		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("O")); // -O Disable outlier rejection.  
			sampleNumber	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("v")); // -v Print the output sample number before each output sample value. 
			outputSeconds1	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("Vs")); // -Vs Print the output sample time in seconds 
			outputMinutes	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("Vm")); // -Vm Print the output sample time in minutes 
			outputHours 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("Vh")); // -Vh Print the output sample time in hours
			
			path = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
			inputFilename = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
			
			debugPrintln("- sInputPath: " + path);
			debugPrintln("- sInputName: " + inputFilename);
			
			int iIndexPeriod = inputFilename.lastIndexOf(".");
			outputFile = inputFilename.substring(0, iIndexPeriod) + "_" + this.getAnalysisVO().getJobIdNumber();  // this should be the same name as the input file for this particular function
			
		} catch (Exception e) {
			log.error(e.getMessage());
			this.getAnalysisVO().setErrorMessage(e.getMessage());
		}		
	
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("tach()");
		debugPrintln("- sInputFile:" + inputFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- bAnnotator:" + annotator);
		debugPrintln("- bStartTime:" + startTime);  
		debugPrintln("- iSampleFrequency:" + frequency);
		debugPrintln("- iRate:" + rate);
		debugPrintln("- iDuration:" + duration); 
		debugPrintln("- iSamples:" + outputSamples); 
		debugPrintln("- bOutlier:" + outlier); 
		debugPrintln("- iSmoothing:" + smoothing);
		debugPrintln("- iEndTime:" + endTime);  
		debugPrintln("- bSampleNumber:" + sampleNumber);
		debugPrintln("- bOutputSeconds1:" + outputSeconds1);
		debugPrintln("- bOutputMinutes:" + outputMinutes);	
		debugPrintln("- bOutputHours:" + outputHours);	
		debugPrintln("- sOutputName:" + outputFile);
		
		try {
		
		String[] asEnvVar = new String[0];  
		
		// build command string
		int iIndexPeriod = inputFilename.lastIndexOf(".");
		String sRecord = inputFilename.substring(0, iIndexPeriod);
		
		//sOutputFile = sRecord;
		
		String sCommand = "tach -r " + path + sRecord; // record name
		
		sCommand += " -a " + annotator;
		
		if(startTime > 0) sCommand += " -f " + startTime;
		if( (frequency != 2) && (frequency > 0) ) {
			sCommand += " -F " + frequency;	
		}
		if( (rate != 2) && (rate > 0) ) sCommand += " -i " + rate;
		if(duration > 0) sCommand += "-l " + duration;
		
		if(outputSamples > 0) sCommand += " -n " + outputSamples;
		
		// the -o is a required parameter, the default is the input filename.
		if(outputFile.equals("")){
			outputFile = sRecord;
		}
		
		//Problems with the output file path size, it's so big. 
		// (path + filename) must have 33 characters at maximum 
		String finalPathName = path + outputFile;
		String tempName = RandomString.newString(5);
		String tempPathName = AnalysisUtils.SERVER_TEMP_ANALYSIS_FOLDER + File.separator + tempName;
		
		sCommand += " -o " + tempPathName;
		
		if(outlier) sCommand += " -O";
			
		if(smoothing > 1) sCommand += " -s " + smoothing; 
		if(endTime > 0) sCommand += " -t " + endTime;
		if(sampleNumber) sCommand += " -v";
		if(outputSeconds1) sCommand += " -Vs";
		if(outputMinutes) sCommand += " -Vm";
		if(outputHours) sCommand += " -Vh";
		
		bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);
		
		bRet &= stdErrorHandler();
		
		if(bRet){
			switch (this.getAnalysisVO().getResultType()) {
			case ORIGINAL_FILE:
				new File(tempPathName+".dat").renameTo(new File(finalPathName + ".dat"));
				
				File tmpHea = new File(tempPathName + ".hea");
				File finalHea = new File(finalPathName + ".hea");
				
				finalHea.createNewFile();
				
				BufferedReader reader = new BufferedReader(new FileReader(tmpHea));
				BufferedWriter writer = new BufferedWriter(new FileWriter(finalHea));
				
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.replaceAll(tempPathName, outputFile);
					writer.write(line);
					writer.newLine();
				}
				
				reader.close();
				writer.flush();
				writer.close();
				
				tmpHea.delete();
				RandomString.release(tempName);
				
				//set first output file to output generated by the sigamp command
				List<String>outputFilenames = new ArrayList<String>();
				debugPrintln("- sOutputName:" + outputFile);
				outputFilenames.add(finalPathName + ".dat");
				outputFilenames.add(finalPathName + ".hea");
				
				this.getAnalysisVO().setOutputFileNames(outputFilenames);
				break;

			default:
				bRet = false;
				debugPrintln("This analysis does not support the selected output format.");	
				break;
			}
			
		}else{
			debugPrintln("- Encountered errors.");
		}			
		
		} catch (Exception e) {
			bRet = false;
			log.error(e.getMessage());
		}
		
		this.getAnalysisVO().setSucess(bRet);
	}

}
