package edu.jhu.cvrg.analysis.wrapper;

import java.lang.reflect.InvocationTargetException;

import edu.jhu.cvrg.analysis.vo.AnalysisVO;

public abstract class AnnotationBasedAnalysisWrapper extends ApplicationWrapper {

	private AnnotationOutputAnalysisWrapper annotationBased = null;
	
	public AnnotationBasedAnalysisWrapper(AnalysisVO vo) {
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
							vo.getFileNames().add(this.getAnnotationBased().getOutputFilename());
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

	
}
