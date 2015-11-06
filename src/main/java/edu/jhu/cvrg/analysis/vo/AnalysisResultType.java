package edu.jhu.cvrg.analysis.vo;

public enum AnalysisResultType {

	ORIGINAL_FILE, 
	CSV_FILE,
	JSON_DATA;
	
	public static AnalysisResultType getType(String typeStr) {
		for (AnalysisResultType t : AnalysisResultType.values()) {
			if(typeStr.equals(t.name())){
				return t;
			}
		}
		return null;
	}
}
