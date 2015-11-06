package edu.jhu.cvrg.analysis.vo;

import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;
import edu.jhu.cvrg.analysis.wrapper.AnnotationOutputAnalysisWrapper;
import edu.jhu.cvrg.analysis.wrapper.chesnokov.ChesnokovAnalysis;
import edu.jhu.cvrg.analysis.wrapper.chesnokov.ChesnokovAnalysisUnix;
import edu.jhu.cvrg.analysis.wrapper.qrsScore.QrsScoreAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.Ann2rrAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.IhrAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.NguessAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.PnnlistAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.RdsampAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.SigampAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.SqrsAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.TachAnalysis;
import edu.jhu.cvrg.analysis.wrapper.wfdb.WqrsAnalysis;


public enum AnalysisType {

	ANN2RR("ann2rr", 					"ann2rrWrapperType2", 		Ann2rrAnalysis.class,			null),
	NGUESS("nguess", 					"nguessWrapperType2", 		NguessAnalysis.class, 			null),
	PNNLIST("pnnlist/pNNx",			 	"pnnlistWrapperType2", 		PnnlistAnalysis.class,			null),
	RDSAMP("rdsamp", 					"rdsampWrapperType2", 		RdsampAnalysis.class, 			null),
	SIGAAMP("sigamp", 					"sigampWrapperType2", 		SigampAnalysis.class, 			null),
	SQRS("sqrs", 						"sqrsWrapperType2", 		SqrsAnalysis.class, 			null),
	SQRS4IHR("sqrs4ihr", 				"sqrs4ihrWrapperType2", 	IhrAnalysis.class, 				SqrsAnalysis.class),
	SQRS4PNNLIST("sqrs4pnnlist/pNNx", 	"sqrs4pnnlistWrapperType2",	PnnlistAnalysis.class, 			SqrsAnalysis.class),
	IHR("sqrs4ihr", 					"sqrs4ihrWrapperType2", 	IhrAnalysis.class, 				null),
	TACH("tach", 						"tachWrapperType2", 		TachAnalysis.class, 			null),
	WQRS("wqrs", 						"wqrsWrapperType2", 		WqrsAnalysis.class, 			null),
	WQRS4IHR("wqrs4ihr", 				"wqrs4ihrWrapperType2", 	IhrAnalysis.class, 				WqrsAnalysis.class),
	WQRS4PNNLIST("wqrs4pnnlist/pNNx", 	"wqrs4pnnlistWrapperType2",	PnnlistAnalysis.class, 			WqrsAnalysis.class),
	WRSAMP("wrsamp", 					"wrsampWrapperType2", 		null, 							null), 
	CHESNOKOV("QT Screening", 			"chesnokovWrapperType2", 	ChesnokovAnalysis.class,		null),
	CHESNOKOVUNIX("QT Screening", 		"chesnokovWrapperUnixType2",ChesnokovAnalysisUnix.class,	null),
	QRS_SCORE("QRS_SCORE", 				"qrs_scoreWrapperType2", 	QrsScoreAnalysis.class,			null);
	
	private String name;
	private String omeName; 
	private Class<? extends AnalysisWrapper> wrapper;
	private Class<? extends AnnotationOutputAnalysisWrapper> annotationBase;
	
	AnalysisType(String name, String omeName, Class<? extends AnalysisWrapper> wrapper, Class<? extends AnnotationOutputAnalysisWrapper> annotationBase){
		this.omeName = omeName;
		this.name = name;
		this.wrapper = wrapper;
		this.annotationBase = annotationBase;
	}
	
	public String getOmeName(){
		return omeName;
	}

	public String getName() {
		return name;
	}
	
	public static AnalysisType getTypeByName(String name){
		
		for (AnalysisType m : values()) {
			if(m.getName().equals(name) || m.toString().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	public static AnalysisType getTypeByOmeName(String name){
		
		for (AnalysisType m : values()) {
			if(m.getOmeName().equals(name)){
				return m;
			}
		}
		return null;
	}

	public Class<? extends AnalysisWrapper> getWrapper() {
		return wrapper;
	}

	public Class<? extends AnnotationOutputAnalysisWrapper> getAnnotationBase() {
		return annotationBase;
	}
}
