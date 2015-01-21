package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationOutputAnalysisWrapper;

public class GalaxySqrsWqrsAnalysis extends AnnotationOutputAnalysisWrapper {

	private String 	signal;
	private Integer begin = null;
	private Integer threshold = null;
	private Integer time = null;
	private boolean	highrez;
	
	private String headerPath;
	private String headerName;
	
	public GalaxySqrsWqrsAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		
		if(this.getAnalysisVO().getCommandParamMap() != null){
			if(log.isDebugEnabled()){
				Object[] keys = this.getAnalysisVO().getCommandParamMap().keySet().toArray();
				
				for(int i=0;i<keys.length;i++){
					debugPrintln("Key: \"" + (String)keys[i] + "\" Value: \"" + this.getAnalysisVO().getCommandParamMap().get((String)keys[i]) + "\"");
				}
			}
			signal = (String) this.getAnalysisVO().getCommandParamMap().get("s");
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
				begin	= Integer.valueOf((String) this.getAnalysisVO().getCommandParamMap().get("f"));
			}
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("m") != null){
				threshold	= Integer.valueOf( (String) this.getAnalysisVO().getCommandParamMap().get("m"));	
			}
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
				time = Integer.valueOf( (String) this.getAnalysisVO().getCommandParamMap().get("t")); // -1 defaults to end of record.	
			}
			 
			highrez 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("H"));
		}
		
		debugPrintln("- Command's parameters: begin=" + begin + " highrez=" + highrez + " threshold=" + threshold + " signal=" + signal + " time=" + time);

		// WFDB files consist of a header file and one or more data files.
		// This function takes the header file as a parameter, and then uses it to look up the name(s) of the data file(s).
		String headerPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		headerPath = AnalysisUtils.extractPath(headerPathName);
		headerName = AnalysisUtils.extractName(headerPathName);
		
		debugPrintln("- headerPathName: " + headerPathName);
		debugPrintln("- headerPath: " + headerPath);
		debugPrintln("- headerName: " + headerName);
		

	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("sqrs()");
		debugPrintln("- headerName:" + headerName);
		debugPrintln("- headerPath:" + headerPath);
		debugPrintln("- begin:" + begin);
		debugPrintln("- highrez:" + highrez);
		debugPrintln("- threshold:" + threshold);
		debugPrintln("- signal:" + signal);
		debugPrintln("- time:" + time);
		try {
			
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerName.lastIndexOf(".");
			String sRecord = headerName.substring(0, iIndexPeriod);
			
			String command = "sqrs -r " + headerPath + sRecord;
			if(begin != null){ 
				command += " -f " + begin;
			}
			if(highrez){
				command += " -H ";
			}
			if(threshold != null && threshold != 500){ 
				command += " -m " + threshold;
			}
			if("0".equals(signal)){ 
				command += " -s " + signal;
			}
			if(time != null && time != -1){
				command += " -t " + time;
			}
	
			bRet = executeCommand(command, asEnvVar, WORKING_DIR);
			
			bRet = processCommandReturn(bRet,headerPath, headerName, sRecord);
			
		} catch (IOException e) {
			throw new AnalysisExecutionException("Error on "+this.getAnalysisVO().getType()+" command output handling", e);
		}
		
		this.getAnalysisVO().setSucess(bRet);
	}

	
	
	@Override
	public String getAnnotationExt() {
		return "qrs";
	}
}
