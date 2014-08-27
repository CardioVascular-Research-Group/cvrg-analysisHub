package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationBasedAnalysisWrapper;

public class PnnlistAnalysis extends AnnotationBasedAnalysisWrapper{

	private String annotator;
	private int startTime = 0;
	private int endTime = 0;
	private double inc = 0.0;
	private boolean percents;
	private boolean separateDistributions;
	
	private String path;
	private String inputFilename;
	private String outputName;
	
	
	public PnnlistAnalysis(AnalysisVO vo) {
		super(vo);
		this.setDataHeaders(new String[]{"NN Interval", "%"});
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void defineInputParameters() {
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
		String annotationFileName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), ".atr.qrs.wqrs");
		annotator = annotationFileName.substring(annotationFileName.lastIndexOf('.')+1);
		
		
		if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
			startTime = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("f"));    // -f Stop at the specified time.
		}
		 	 
		
		if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
			endTime = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("t"));    // -t (-1) Stop at the specified time. defaults to end of record.	
		}

		
		if(this.getAnalysisVO().getCommandParamMap().get("i") != null){
			inc = Double.parseDouble( (String) this.getAnalysisVO().getCommandParamMap().get("i"));    // -i Compute and output pNNx for x = 0, inc, 2*inc, ... milliseconds.
		}
		
		percents				= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("p")); // -p Compute and output increments as percentage of initial intervals. 
		separateDistributions	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("s")); // -s Compute and output separate distributions of positive and negative intervals. 
		
		path = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
		inputFilename = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
		
		debugPrintln("- sInputPath: " + path);
		debugPrintln("- sInputName: " + inputFilename);
		
		outputName = inputFilename.substring(0, inputFilename.lastIndexOf(".")) + '_' + this.getAnalysisVO().getJobIdNumber();
		
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("pnnlist()");
		debugPrintln("- sInputFile:" + inputFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- bAnnotator:" + annotator);
		debugPrintln("- iInc:" + inc);  		
		debugPrintln("- dQInterval:" + percents);
		debugPrintln("- iRate:" + separateDistributions);
		debugPrintln("- sOutputName:" + outputName);
		
		try {
		
		String[] asEnvVar = new String[0];  
		
		// build command string
		int iIndexPeriod = inputFilename.lastIndexOf(".");
		String sRecord = inputFilename.substring(0, iIndexPeriod);
		
		String sCommand = "ann2rr -r " + path + sRecord + " -a " + annotator +" -A -i s8 -w";
		
		if(startTime > 0) sCommand += " -f " + startTime;
		if(endTime > 0) sCommand += " -t " + endTime;
		
		sCommand += " | pnnlist";
		
		debugPrintln("- sCommand:" + sCommand);
		
		bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);
		
		bRet &= stdErrorHandler();
		
		if(bRet){
			switch (this.getAnalysisVO().getResultType()) {
			case CSV_FILE:
				String outputFile = stdCSVReturnHandler(path, outputName, this.getDataHeaders());
				List<String> outputFilenames = new ArrayList<String>();
				debugPrintln("- sOutputName:" + outputFile);
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
			debugPrintln("- Encountered errors.");
		}			
		
		} catch (Exception e) {
			bRet = false;
			log.error(e.getMessage());
		}
		
		this.getAnalysisVO().setSucess(bRet);
		
	}

}
