package edu.jhu.cvrg.analysis.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;


public abstract class AnnotationOutputAnalysisWrapper extends ApplicationWrapper{

	public AnnotationOutputAnalysisWrapper(AnalysisVO vo) {
		super(vo);
		this.setDataHeaders(new String[]{"Seconds", "Minutes", "Hours", "Type", "Sub", "Chan", "Num", "Aux"});
	}

	public boolean isSuccess() {
		return this.getAnalysisVO().isSucess();
	}
	
	public String getOutputFilename() {
		if(this.getAnalysisVO().getOutputFileNames() != null && this.getAnalysisVO().getOutputFileNames().size() == 1){
			return this.getAnalysisVO().getOutputFileNames().get(0);
		}else{
			return null;	
		}
	}
	
	public abstract String getAnnotationExt();
	
	protected void execute_rdann(String path, String headerFilename, AnalysisResultType format) throws AnalysisExecutionException{
		
		String[] asEnvVar = new String[0];   
		int iIndexPeriod = headerFilename.lastIndexOf(".");
		String sRecord = headerFilename.substring(0, iIndexPeriod);
		 
		try { 
			// rdann -v -x -r twa01 -a wqrs
			String sCommand = "rdann -x -r " + path + sRecord + " -a " + this.getAnnotationExt();
			this.executeCommand(sCommand, asEnvVar, AnalysisWrapper.WORKING_DIR);
			
			switch (format) {
				case CSV_FILE:
					String outputFile = stdCSVReturnHandler(path, sRecord + '_'+ this.getAnalysisVO().getJobId(), this.getDataHeaders());
					
					List<String> outputFilenames = new ArrayList<String>();
					
					outputFilenames.add(outputFile);
				
					this.getAnalysisVO().setOutputFileNames(outputFilenames);
					
					break;
	
				case JSON_DATA:
					this.setJSONOutput();
					break;
				default:
					break;
			}
			
			stdReturnMethodHandler();
		    log.info("--- execute_rdann() found " + lineNum + " annotations");
		    
		} catch (IOException e) {
			throw new AnalysisExecutionException("Error on RDANN command output handling", e);
		}finally{
			AnalysisUtils.deleteFile(path+sRecord+'.'+this.getAnnotationExt());
		}
	}
	
	protected boolean processCommandReturn(boolean bRet, String path, String headerName, String record) throws IOException, AnalysisExecutionException {
		
		bRet &= stdErrorHandler();
		
		File originFile = new File(path + record+ "." + this.getAnnotationExt());
		bRet = originFile.exists();
		
		if(bRet){
			
			stdReturnHandler();
			
			AnalysisResultType resultType = this.getAnalysisVO().getResultType();
			if(!this.getAnalysisVO().isRename()){
				resultType = AnalysisResultType.ORIGINAL_FILE;
			}
			
			switch (resultType) {
				case ORIGINAL_FILE:
					List<String> outputFilenames = new ArrayList<String>();
					if(this.getAnalysisVO().isRename()){
						String finalName = path + record + '_'+ this.getAnalysisVO().getJobId() + "." + this.getAnnotationExt() ;
						originFile.renameTo(new File(finalName));
						outputFilenames.add(finalName);
					}else{
						outputFilenames.add(originFile.getAbsolutePath());
					}
					this.getAnalysisVO().setOutputFileNames(outputFilenames);
					
					break;
				case CSV_FILE:
				case JSON_DATA:
					this.execute_rdann(path, headerName, this.getAnalysisVO().getResultType());
					break;
				default:
					throw new AnalysisExecutionException("Unexpected format ["+this.getAnalysisVO().getResultType()+"] for this analysis ["+this.getAnalysisVO().getType()+"]");
			}
			
		}else{
			throw new AnalysisExecutionException("Annotation file not generated");
		}
		return bRet;
	}
	
}
