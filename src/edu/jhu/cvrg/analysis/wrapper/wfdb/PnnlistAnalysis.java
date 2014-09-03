package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationBasedAnalysisWrapper;

public class PnnlistAnalysis extends AnnotationBasedAnalysisWrapper{

	private String annotator;
	private int startTime = 0;
	private int endTime = 0;
	private double inc = 0.0;
	private boolean percentsOfInitialIntervals;
	private boolean separateDistributions;
	
	private String path;
	private String inputFilename;
	private String outputName;
	
	
	public PnnlistAnalysis(AnalysisVO vo) throws AnalysisParameterException, AnalysisExecutionException {
		super(vo);
		this.setDataHeaders(new String[]{"NN Interval", "%"});
	}

	@Override
	protected void processReturnLine(String line) {
	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		
		String annotationFileName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), ".atr.qrs.wqrs");
		annotator = annotationFileName.substring(annotationFileName.lastIndexOf('.')+1);
		
		if(this.getAnalysisVO().getCommandParamMap() != null && !this.getAnalysisVO().getCommandParamMap().isEmpty()){
			if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
				startTime = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("f"));    // -f Stop at the specified time.
			}
			 	 
			
			if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
				endTime = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("t"));    // -t (-1) Stop at the specified time. defaults to end of record.	
			}
	
			
			if(this.getAnalysisVO().getCommandParamMap().get("i") != null){
				inc = Double.parseDouble( (String) this.getAnalysisVO().getCommandParamMap().get("i"));    // -i Compute and output pNNx for x = 0, inc, 2*inc, ... milliseconds.
			}
			
			percentsOfInitialIntervals				= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("p")); // -p Compute and output increments as percentage of initial intervals. 
			separateDistributions	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("s")); // -s Compute and output separate distributions of positive and negative intervals. 
		}
		
		path = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
		inputFilename = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
		
		debugPrintln("- path: " + path);
		debugPrintln("- inputFilename: " + inputFilename);
		
		outputName = inputFilename.substring(0, inputFilename.lastIndexOf(".")) + '_' + this.getAnalysisVO().getJobIdNumber();
		
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("pnnlist()");
		debugPrintln("- inputFilename:" + inputFilename);
		debugPrintln("- path:" + path);
		debugPrintln("- annotator:" + annotator);
		debugPrintln("- inc:" + inc);  		
		debugPrintln("- percentsOfInitialIntervals:" + percentsOfInitialIntervals);
		debugPrintln("- separateDistributions:" + separateDistributions);
		debugPrintln("- outputName:" + outputName);
		
		String[] envVar = new String[0];  
		
		// build command string
		int indexPeriod = inputFilename.lastIndexOf(".");
		String record = inputFilename.substring(0, indexPeriod);
		
		String command = "ann2rr -r " + path + record + " -a " + annotator +" -A -i s8 -w";
		
		if(startTime > 0) command += " -f " + startTime;
		if(endTime > 0) command += " -t " + endTime;
		
		command += " | pnnlist";
		
		debugPrintln("- sCommand:" + command);
		
		try {		
			bRet = executeCommand(command, envVar, WORKING_DIR);
			
			bRet &= stdErrorHandler();
			
			if(bRet){
				switch (this.getAnalysisVO().getResultType()) {
				case CSV_FILE:
					String outputFile = stdCSVReturnHandler(path, outputName, this.getDataHeaders());
					List<String> outputFilenames = new ArrayList<String>();
					debugPrintln("- outputFile:" + outputFile);
					outputFilenames.add(outputFile);
					
					this.getAnalysisVO().setOutputFileNames(outputFilenames);
					
					break;
				case JSON_DATA:
					this.setJSONOutput();
					break;
				default:
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
