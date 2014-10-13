package edu.jhu.cvrg.analysis.wrapper;

import java.lang.reflect.InvocationTargetException;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;

public abstract class AnnotationBasedAnalysisWrapper extends ApplicationWrapper {

	private AnnotationOutputAnalysisWrapper annotationBased = null;
	private String tempFile = null;
	
	public AnnotationBasedAnalysisWrapper(AnalysisVO vo) throws AnalysisParameterException, AnalysisExecutionException {
		super(vo);
		
		if(vo.getType().getAnnotationBase() != null){
		
			try {
				vo.setRename(false);
				
				annotationBased = vo.getType().getAnnotationBase().getConstructor(AnalysisVO.class).newInstance(vo);
				
				if(this.getAnnotationBased() != null){
					
					this.getAnnotationBased().defineInputParameters();
					this.getAnnotationBased().execute();
					
					if(this.getAnnotationBased().isSuccess()){
						if(this.getAnnotationBased().getOutputFilename() != null){
							tempFile = this.getAnnotationBased().getOutputFilename();
							vo.getFileNames().add(tempFile);
						}
					}else{
						//TODO THROW EXCEPTION
					}
					
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public AnnotationOutputAnalysisWrapper getAnnotationBased() {
		return annotationBased;
	}

	@Override
	public void execute() throws AnalysisExecutionException {
		super.execute();
		if(tempFile != null){
			AnalysisUtils.deleteFile(tempFile);
		}
	}
	
}
