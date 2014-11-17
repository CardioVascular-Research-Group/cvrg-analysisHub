package edu.jhu.cvrg.analysis.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisVO {

	private String jobId;
	
	private AnalysisType type;
	private AnalysisResultType resultType;
	
	private List<String> inputFileNames;
	private Map<String, Object> commandParamMap;
	
	private String outputData;
	private List<String> outputFileNames;
	
	private boolean sucess;
	private String errorMessage;
	
	//By default we have to rename the output file to identify the analysis job 
	private boolean rename = true;
	
	private String tempFolder;
	
	public AnalysisVO(String jobId, AnalysisType type, AnalysisResultType resultType, List<String> inputFileNames, Map<String, Object> commandParamMap) {
		super();
		this.jobId = jobId;
		this.type = type;
		this.resultType = resultType;
		this.inputFileNames = inputFileNames;
		if(commandParamMap == null){
			this.commandParamMap = new HashMap<String, Object>();
		}else{
			this.commandParamMap = commandParamMap;	
		}
	}

	public AnalysisType getType() {
		return type;
	}

	public List<String> getFileNames() {
		return inputFileNames;
	}

	public Map<String, Object> getCommandParamMap() {
		return commandParamMap;
	}

	public String getJobId() {
		return jobId;
	}

	public String getJobIdNumber(){
		return this.getJobId().replaceAll("\\D", "");
	}

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isRename() {
		return rename;
	}

	public void setRename(boolean rename) {
		this.rename = rename;
	}

	public List<String> getOutputFileNames() {
		return outputFileNames;
	}

	public void setOutputFileNames(List<String> outputFileNames) {
		this.outputFileNames = outputFileNames;
	}

	public AnalysisResultType getResultType() {
		return resultType;
	}

	public String getOutputData() {
		return outputData;
	}

	public void setOutputData(String outputData) {
		this.outputData = outputData;
	}

	public String getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}

}
