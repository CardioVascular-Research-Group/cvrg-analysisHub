package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationBasedAnalysisWrapper;

public class Ann2rrAnalysis extends AnnotationBasedAnalysisWrapper {

	private String 	intervalFormat;
	private String 	mneumonicsEnd;
	private String 	mneumonicsBegin;
	private String 	initialTimesFormat;
	private String 	finalTimesFormat;
	
	private int startTime = 0;
	private int endTime = 0;
	
	private boolean allIntervals;
	private boolean consecutive;
	private boolean finalAnnotations;
	private boolean initialAnnotations;
	private String annotator;
	
	private String path;
	private String inputFilename;
	private String outputFilename;
	private String record; 
	
	public Ann2rrAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
	}

	@Override
	public void defineInputParameters() {
		try {
			//*** The analysis algorithm should return a String array containing the full path/names of the result files.
			intervalFormat		= (String) this.getAnalysisVO().getCommandParamMap().get("i"); // -i
			mneumonicsEnd		= (String) this.getAnalysisVO().getCommandParamMap().get("p"); // -p
			mneumonicsBegin		= (String) this.getAnalysisVO().getCommandParamMap().get("P"); // -P
			initialTimesFormat	= (String) this.getAnalysisVO().getCommandParamMap().get("v"); // -v
			finalTimesFormat	= (String) this.getAnalysisVO().getCommandParamMap().get("V"); // -V
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
				startTime = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("f"));    // -f
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
				endTime	= Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("t"));    // -t (-1) defaults to end of record.	
			}
			
			allIntervals		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("A")); // -A
			consecutive			= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("c")); // -c Print intervals between consecutive valid annotations only
			finalAnnotations	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("w")); // -w
			initialAnnotations	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("W")); // -W
			
			String annotationFileName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), ".atr.qrs");
			annotator = annotationFileName.substring(annotationFileName.lastIndexOf('.')+1);
			
			//**********************************************************************
			path = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
			inputFilename = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
			
			int iIndexPeriod = inputFilename.lastIndexOf(".");
			record = inputFilename.substring(0, iIndexPeriod);
			
			outputFilename = record + '_' + this.getAnalysisVO().getJobIdNumber();
			
			debugPrintln("- sInputPath: " + path);
			debugPrintln("- sInputName: " + inputFilename);
			
		} catch (Exception e) {
			log.error(e.getMessage());
			this.getAnalysisVO().setErrorMessage(e.getMessage());
		}		
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("ann2rr()");
		debugPrintln("- sInputFile:" + inputFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- bAllInverals:" + allIntervals);
		debugPrintln("- bConsecutive:" + consecutive);
		debugPrintln("- bStartTime:" + startTime);  		
		debugPrintln("- sIntervalFormat:" + intervalFormat);
		debugPrintln("- sMneumonicsEnd:" + mneumonicsEnd);
		debugPrintln("- sMneumonicsBegin:" + mneumonicsBegin);
		debugPrintln("- iEndTime:" + endTime);
		debugPrintln("- sFinalTimesFormat:" + finalTimesFormat);
		debugPrintln("- sInitialTimesFormat:" + initialTimesFormat);
		debugPrintln("- bFinalAnnotations:" + finalAnnotations);
		debugPrintln("- bInitialAnnotations:" + initialAnnotations);
		debugPrintln("- sOutputName:" + outputFilename);

		try {

			String[] envVar = new String[0];  

			// build command string
			String command = "ann2rr -r " + path + record ; // record name
			
			command += " -a " + annotator;
			
			if (allIntervals) {
				command += " -A"; // Print all intervals between annotations. This option overrides the -c and -p options. 
			} else {
				if (consecutive) {
					command += " -c"; // Print intervals between consecutive valid annotations only.
				}

				if (mneumonicsEnd != null) {
					command += " -p " + mneumonicsEnd;
				}
			}

			if(startTime > 0) command += " -f " + startTime;

			if(intervalFormat != null && !intervalFormat.equals("")){
				command += " -i " + intervalFormat;
			}

			if(mneumonicsBegin != null &&  !mneumonicsBegin.equals("")){
				command += " -P " + mneumonicsBegin;
			}

			if(endTime > 0){ 
				command += " -t " + endTime;
			}

			if(finalTimesFormat != null && !finalTimesFormat.equals("")){
				command += " -v " + finalTimesFormat;
			}

			if(initialTimesFormat != null && !initialTimesFormat.equals("")){
				command += " -V " + initialTimesFormat;
			}

			if(finalAnnotations){
				command += " -w";
			}

			if(initialAnnotations){
				command += " -W";
			}

			bRet = executeCommand(command, envVar, WORKING_DIR);

			bRet &= stdErrorHandler();
			
			if(bRet){
				switch (this.getAnalysisVO().getResultType()) {
				case ORIGINAL_FILE:
				case CSV_FILE:
					List<String> outputFilenames = new ArrayList<String>();
					
					String outputFile;
					if(AnalysisResultType.ORIGINAL_FILE.equals(this.getAnalysisVO().getResultType())){
						outputFile = path + outputFilename + ".rr";
						stdReturnHandler(outputFile);
					}else{
						outputFile = stdCSVReturnHandler(path, outputFilename, null);	
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

		} catch (Exception e) {
			bRet = false;
			log.error(e.getMessage());
		}

		this.getAnalysisVO().setSucess(bRet);
	}

}
