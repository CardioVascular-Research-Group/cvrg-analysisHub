package edu.jhu.cvrg.analysis.wrapper;

import org.apache.log4j.Logger;

import edu.jhu.cvrg.analysis.vo.AnalysisVO;

public abstract class AnalysisWrapper {
	
	private AnalysisVO analysisVO;
	private String[] dataHeaders;
	protected Logger log;
	
	protected static final String WORKING_DIR = "/";

	public AnalysisWrapper(AnalysisVO vo) {
		this.analysisVO = vo;
		log = Logger.getLogger(this.getClass());
	}
	
	public abstract void defineInputParameters();
	public abstract void execute();
	
	protected AnalysisVO getAnalysisVO() {
		return analysisVO;
	}

	protected void setAnalysisVO(AnalysisVO analysisVO) {
		this.analysisVO = analysisVO;
	}
	
	protected void debugPrintln(String text){
		log.debug("- AnalysisWrapper - " + text);
	}

	protected String[] getDataHeaders() {
		return dataHeaders;
	}

	protected void setDataHeaders(String[] dataHeaders) {
		this.dataHeaders = dataHeaders;
	}
	
	
}
