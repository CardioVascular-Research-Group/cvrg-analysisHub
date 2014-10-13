package edu.jhu.cvrg.analysis.wrapper;

import org.apache.log4j.Logger;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
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
	
	protected abstract void _defineInputParameters() throws AnalysisParameterException;
	protected abstract void _execute() throws AnalysisExecutionException;
	
	public void defineInputParameters() throws AnalysisParameterException{
		
		try{
			if(this.getAnalysisVO().getFileNames() != null && !this.getAnalysisVO().getFileNames().isEmpty()){
				if(!AnalysisType.WRSAMP.equals(this.getAnalysisVO().getType()) && this.getAnalysisVO().getFileNames().size() < 2){
					throw new AnalysisParameterException("Insuficient input files. 2 expected");
				}
					
				if(AnnotationBasedAnalysisWrapper.class.equals(this.getClass().getSuperclass()) && this.getAnalysisVO().getFileNames().size() < 3){
					throw new AnalysisParameterException("Insuficient input files. 3 expected");
				}
			}else{
				throw new AnalysisParameterException("Insuficient input files.");
			}
			
			_defineInputParameters();
		}catch (AnalysisParameterException e){
			log.error(e.getMessage());
			this.getAnalysisVO().setErrorMessage(e.getMessage());
			this.getAnalysisVO().setSucess(false);
			throw e;
		}catch (RuntimeException e){
			String errorMessage =  "Unexpected error at parameters definition phase [" + e.getMessage() +"]";  
			log.error(errorMessage);
			this.getAnalysisVO().setErrorMessage(errorMessage);
			this.getAnalysisVO().setSucess(false);
		}
	}
	
	public void execute() throws AnalysisExecutionException{
		try{
			_execute();	
		}catch (AnalysisExecutionException e){
			log.error(e.getMessage());
			this.getAnalysisVO().setErrorMessage(e.getMessage());
			this.getAnalysisVO().setSucess(false);
			throw e;
		}catch (RuntimeException e){
			String errorMessage =  "Unexpected error at execution phase [" + e.getMessage() +"]";  
			log.error(errorMessage);
			this.getAnalysisVO().setErrorMessage(errorMessage);
			this.getAnalysisVO().setSucess(false);
		}
		
	}
	
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
