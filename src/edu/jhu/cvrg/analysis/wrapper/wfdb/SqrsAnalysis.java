package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;

import edu.jhu.cvrg.analysis.util.AnalysisFailureException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationOutputAnalysisWrapper;

public class SqrsAnalysis extends AnnotationOutputAnalysisWrapper {

	private String 	signal;
	private Integer begin = null;
	private Integer threshold = null;
	private Integer time = null;
	private boolean	highrez;
	
	private String headerPath;
	private String headerName;
	
	public SqrsAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub

	}

	@Override
	public void defineInputParameters() {
		debugPrintln("executeV2_sqrs()");
		
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
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
		 
		boolean	bHighrez 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("H"));
		
		debugPrintln("- Command's parameters: iBegin=" + begin + " bHighrez=" + bHighrez + " iThreshold=" + threshold + " sSignal=" + signal + " iTime=" + time);

		// WFDB files consist of a header file and one or more data files.
		// This function takes the header file as a parameter, and then uses it to look up the name(s) of the data file(s).
		String sHeaderPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		headerPath = AnalysisUtils.extractPath(sHeaderPathName);
		headerName = AnalysisUtils.extractName(sHeaderPathName);
		
		debugPrintln("- sHeaderPathName: " + sHeaderPathName);
		debugPrintln("- sHeaderPath: " + headerPath);
		debugPrintln("- sHeaderName: " + headerName);
		

	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("sqrs()");
		debugPrintln("- sHeaderFile:" + headerName);
		debugPrintln("- sPath:" + headerPath);
		debugPrintln("- iBegin:" + begin);
		debugPrintln("- bHighrez:" + highrez);
		debugPrintln("- iThreshold:" + threshold);
		debugPrintln("- sSignal:" + signal);
		debugPrintln("- iTime:" + time);
		try {
			
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerName.lastIndexOf(".");
			String sRecord = headerName.substring(0, iIndexPeriod);
			
			String sCommand = "sqrs -r " + headerPath + sRecord;
			if(begin != null){ 
				sCommand += " -f " + begin;
			}
			if(highrez){
				sCommand += " -H ";
			}
			if(threshold != null && threshold != 500){ 
				sCommand += " -m " + threshold;
			}
			if("0".equals(signal)){ 
				sCommand += " -s " + signal;
			}
			if(time != null && time != -1){
				sCommand += " -t " + time;
			}
	
			bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);
			
			bRet = processCommandReturn(bRet,headerPath, headerName, sRecord);
			
		} catch (IOException e) {
			bRet = false;
			log.error(e.getMessage());
		} catch (AnalysisFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getAnalysisVO().setSucess(bRet);
	}

	
	
	@Override
	public String getAnnotationExt() {
		return "qrs";
	}
}
