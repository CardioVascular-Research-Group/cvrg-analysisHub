package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationBasedAnalysisWrapper;

public class IhrAnalysis extends AnnotationBasedAnalysisWrapper{

	private String 	startTime;
	private String 	type;
	private String 	endTime;
	private Integer tolerance = null;
	private boolean printSummary;
	private boolean includeIntervals;
	private boolean excludeIntervals;
	private String annotationFileName;
	private String annotator;
	private String showTotalNumberBy = null;
	
	
	private String path;
	private String inputFilename;
	private String outputName; 
	
	public IhrAnalysis(AnalysisVO vo) throws AnalysisParameterException, AnalysisExecutionException {
		super(vo);
		setDataHeaders(new String[]{"Elapsed time (in seconds)", "Instantaneous heart rate (in beats per minute)", "Interval type"});
	}

	@Override
	protected void processReturnLine(String line) {
	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		
		if(this.getAnalysisVO().getCommandParamMap() != null && !this.getAnalysisVO().getCommandParamMap().isEmpty()){
			startTime		= (String) this.getAnalysisVO().getCommandParamMap().get("f"); // -f
			//we can use more than one -p on command, but for now we will not support
			type				= (String) this.getAnalysisVO().getCommandParamMap().get("p"); // -p
			endTime				= (String) this.getAnalysisVO().getCommandParamMap().get("t"); // -t
			
			if(this.getAnalysisVO().getCommandParamMap().get("d") != null){
				tolerance	= Integer.valueOf((String) this.getAnalysisVO().getCommandParamMap().get("d")); // -c
			}
			
			//TODO Implement the showTotalNumberBy
			
			printSummary	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("h")); // -h
			includeIntervals	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("i")); // -v
			excludeIntervals = Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("x")); // -x
		}
		
		annotationFileName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), ".atr.qrs.wqrs");
		annotator = annotationFileName.substring(annotationFileName.lastIndexOf('.')+1);
		
		//**********************************************************************
		path = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
		inputFilename = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
		
		outputName = inputFilename.substring(0, inputFilename.lastIndexOf(".")) + '_' + this.getAnalysisVO().getJobIdNumber();
		
		debugPrintln("- path: " + path);
		debugPrintln("- inputFilename: " + inputFilename);
		
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("ihr()");
		debugPrintln("- inputName:" + inputFilename);
		debugPrintln("- path:" + path);
		debugPrintln("- annotator:" + annotator);
		debugPrintln("- tolerance:" + tolerance);
		debugPrintln("- startTime:" + startTime);
		debugPrintln("- printSummary:" + printSummary);
		debugPrintln("- includeIntervals:" + includeIntervals);
		debugPrintln("- endTime:" + endTime);
		debugPrintln("- showTotalNumberBy:" + showTotalNumberBy);
		debugPrintln("- excludeIntervals:" + excludeIntervals);
		debugPrintln("- type:" + type);
		debugPrintln("- outputName:" + outputName);
		
		String[] envVar = new String[0];  
		
		// build command string
		int iIndexPeriod = inputFilename.lastIndexOf(".");
		String sRecord = inputFilename.substring(0, iIndexPeriod);
		
		String command = "ihr -r " + path + sRecord ; // record name
		
		command += " -a " + annotator;
		
		if (tolerance != null && !tolerance.equals("")) {
			command += " -d " + tolerance; //Reject beat-to-beat heart rate changes exceeding tolerance (in beats per minute; default: 10). Any intervals for which the calculated heart rate would differ by more than the specified tolerance are simply excluded from the output series. To disable this behavior, use a large value for tolerance (e.g., 10000). 
		} 
		
		if(startTime != null && !startTime.equals("")){ 
			command += " -f " + startTime; //Begin at the specified time in record (default: the beginning of record).
		}
		
		if(endTime != null && !endTime.equals("")){ 
			command += " -t " + endTime; //Process until the specified time in record (default: the end of the record).
		}
		
		if(printSummary){
			command += " -h"; //Print a usage summary. 
		}
		
		if(includeIntervals){
			command += " -i"; //Include all intervals bounded by QRS annotations (default: include intervals bounded by consecutive supraventricular beats only). 
		}
		
		if(excludeIntervals){
			command += " -x"; //Exclude the interval immediately following each rejected interval. (Rejected intervals are those bounded by excluded beats on at least one end, and those that do not satisfy the tolerance criterion). By default, intervals following rejected intervals are included (unless they are rejected by the tolerance criterion), and a third column is used to flag these intervals (a zero in the third column means the interval is normal, a one means it follows an excluded interval). 
		}
		
		if(showTotalNumberBy != null && !showTotalNumberBy.equals("")){
			command += " -"+showTotalNumberBy; //Print the elapsed times from the beginning of the record to the annotations that begin each interval, as sample number (using -v), or in seconds (using -vs), minutes (using -vm), or hours (using -vh) before each heart rate value. The options -V, -Vs, -Vm, and -Vh work in the same way, but the printed times are those for the annotations that end the intervals. Only one of these options can be used at a time; if none is chosen, -vs mode is used by default. 
		}
		
		if(type != null && !type.equals("")){
			command += " -p " + type; //Include intervals bounded by annotations of the specified types only. The type arguments should be annotation mnemonics (e.g., N) as normally printed by rdann(1) in the third column. More than one -p option may be used in a single command, and each -p option may have more than one type argument following it. If type begins with ‘‘-’’, however, it must immediately follow -p (standard annotation mnemonics do not begin with ‘‘-’’, but modification labels in an annotation file may define such mnemonics).
		}
		
		try {
			bRet = executeCommand(command, envVar, WORKING_DIR);
			bRet &= stdErrorHandler();
			
			if(bRet){
				
				switch (this.getAnalysisVO().getResultType()) {
					case CSV_FILE:
						String outputFile = stdCSVReturnHandler(path, outputName, this.getDataHeaders());
						
						//set first output file.
						List<String> outputFilenames = new ArrayList<String>();
						debugPrintln("- outputFile:" + outputFile);
						outputFilenames.add(outputFile);
						
						this.getAnalysisVO().setOutputFileNames(outputFilenames);
						
						break;
					case JSON_DATA:
						this.setJSONOutput();
						break;
						
					default:
						throw new AnalysisExecutionException("Unexpected output format ["+this.getAnalysisVO().getResultType()+"] for this analysis ["+this.getAnalysisVO().getType()+"]");
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
