package edu.jhu.cvrg.analysis.wrapper.qrsScore;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class QrsScoreAnalysis extends AnalysisWrapper {
	
	private String headerFilename;
	private String path;
	private QRS_Score qrs;
	
	public QrsScoreAnalysis(AnalysisVO vo) {
		super(vo);
		this.setDataHeaders(new String[]{"ConductionType", "QRS_Score",
										 "Scar1","Scar2","Scar3","Scar4","Scar5","Scar6","Scar7","Scar8","Scar9","Scar10","Scar11","Scar12",
										 "points_I","points_II","points_aVL","points_aVF","points_V1ant","points_V1post","points_V2ant","points_V2post","points_V3","points_V4","points_V5","points_V6",
										 "Bad Q Parameter Count", "Bad R Parameter Count", "Bad S Parameter Count", "Bad Q Parameter List", "Bad R Parameter List", "Bad S Parameter List","Positive Uncertainty","Negative Uncertainty"});
	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
		if(log.isDebugEnabled()){
			Object[] keys = this.getAnalysisVO().getCommandParamMap().keySet().toArray();
			
			for(int i=0;i<keys.length;i++){
				debugPrintln("Key: \"" + (String)keys[i] + "\" Value: \"" + this.getAnalysisVO().getCommandParamMap().get((String)keys[i]) + "\"");
			}
		}

		// WFDB files consist of a header file and one or more data files.
		// This function takes the header file as a parameter, and then uses it to look up the name(s) of the data file(s).
		String headerPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(headerPathName);
		headerFilename = AnalysisUtils.extractName(headerPathName);
		
		debugPrintln("- headerPathName: " + headerPathName);
		debugPrintln("- path: " + path);
		debugPrintln("- headerFilename: " + headerFilename);
		
		debugPrintln("- Starting QRS_Score()");
		qrs = new QRS_Score();

		int missingQ = 0, missingR = 0, missingS = 0, missingOther = 0;
		String missingQList = "",missingRList = "",missingSList = "", missingOtherList = "";
		
		//---------------------------- Whole record parameters ---------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("Name")!= null){  qrs.Name  = (String) this.getAnalysisVO().getCommandParamMap().get("Name");	}else{missingOther++; missingOtherList += "Name/";} // Name
		if(this.getAnalysisVO().getCommandParamMap().get("ID")  != null){  qrs.ID    = (String) this.getAnalysisVO().getCommandParamMap().get("ID");	}else{missingOther++; missingOtherList += "ID/";} // ID
		if(this.getAnalysisVO().getCommandParamMap().get("age") != null){  qrs.Age   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("age"));	}else{missingOther++; missingOtherList += "age/";} // age
		if(this.getAnalysisVO().getCommandParamMap().get("sex") != null){ // options are "male", "female" or "Unknown"
			String sex = (String)  this.getAnalysisVO().getCommandParamMap().get("sex");
			if(sex.equalsIgnoreCase("female")){
				qrs.Sex = 1;
			}else{
				qrs.Sex = 0; // default is male, so Unknown will be treated as male. 
			}
		}else{
			qrs.Sex = 0; // default is male, so Unknown will be treated as male. 
			missingOther++; // sex
			missingOtherList += "sex,";
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000072") != null){  qrs.qrsd   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000072"));	}else{missingQ++; missingQList += "qrsd/";} // qrsd
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000838") != null){  qrs.qrsax  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000838"));	}else{missingQ++; missingQList += "qrsax/";} // qrsax

		//---------------------------- Q_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_0") != null){  qrs.qa_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_0"));	}else{missingQ++; missingQList += "qa_I/";} // qa_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_4") != null){  qrs.qa_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_4"));	}else{missingQ++; missingQList += "qa_aVL/";} // qa_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_5") != null){  qrs.qa_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_5"));	}else{missingQ++; missingQList += "qa_aVF/";} // qa_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6") != null){  qrs.qa_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6"));	}else{missingQ++; missingQList += "qa_V1/";} // qa_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7") != null){  qrs.qa_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7"));	}else{missingQ++; missingQList += "qa_V2/";} // qa_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8") != null){  qrs.qa_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8"));	}else{missingQ++; missingQList += "qa_V3/";} // qa_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9") != null){  qrs.qa_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9"));	}else{missingQ++; missingQList += "qa_V4/";} // qa_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10") != null){  qrs.qa_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10"));	}else{missingQ++; missingQList += "qa_V5/";} // qa_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11") != null){  qrs.qa_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11"));	}else{missingQ++; missingQList += "qa_V6/";} // qa_V6
		//---------------------------- Q_Wave_Duration -----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_0") != null){  qrs.qd_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_0"));	}else{missingQ++; missingQList += "qd_I/";} // qd_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_1") != null){  qrs.qd_II  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_1"));	}else{missingQ++; missingQList += "qd_II/";} // qd_II
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_4") != null){  qrs.qd_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_4"));	}else{missingQ++; missingQList += "qd_aVL/";} // qd_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_5") != null){  qrs.qd_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_5"));	}else{missingQ++; missingQList += "qd_aVF/";} // qd_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_6") != null){  qrs.qd_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_6"));	}else{missingQ++; missingQList += "qd_V1/";} // qd_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_7") != null){  qrs.qd_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_7"));	}else{missingQ++; missingQList += "qd_V2/";} // qd_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_8") != null){  qrs.qd_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_8"));	}else{missingQ++; missingQList += "qd_V3/";} // qd_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_9") != null){  qrs.qd_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_9"));	}else{missingQ++; missingQList += "qd_V4/";} // qd_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_10") != null){  qrs.qd_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_10"));	}else{missingQ++; missingQList += "qd_V5/";} // qd_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_11") != null){  qrs.qd_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_11"));	}else{missingQ++; missingQList += "qd_V6/";} // qd_V6
		
		//---------------------------- R_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_0") != null){  qrs.ra_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_0"));	}else{missingR++; missingRList += "ra_I/";} // ra_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_4") != null){  qrs.ra_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_4"));	}else{missingR++; missingRList += "ra_aVL/";} // ra_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_5") != null){  qrs.ra_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_5"));	}else{missingR++; missingRList += "ra_aVF/";} // ra_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_6") != null){  qrs.ra_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_6"));	}else{missingR++; missingRList += "ra_V2/";} // ra_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_7") != null){  qrs.ra_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_7"));	}else{missingR++; missingRList += "ra_V2/";} // ra_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_8") != null){  qrs.ra_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_8"));	}else{missingR++; missingRList += "ra_V3/";} // ra_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_9") != null){  qrs.ra_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_9"));	}else{missingR++; missingRList += "ra_V4/";} // ra_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_10") != null){  qrs.ra_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_10"));	}else{missingR++; missingRList += "ra_V5/";} // ra_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_11") != null){  qrs.ra_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_11"));	}else{missingR++; missingRList += "ra_V6/";} // ra_V6
		//---------------------------- R_Wave_Duration -----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_6") != null){  qrs.rd_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_6"));	}else{missingR++; missingRList += "rd_V1/";} // rd_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_7") != null){  qrs.rd_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_7"));	}else{missingR++; missingRList += "rd_V2/";} // rd_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_8") != null){  qrs.rd_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_8"));	}else{missingR++; missingRList += "rd_V3/";} // rd_V3
		//---------------------------- S_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6") != null){  qrs.sa_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6"));	}else{missingS++; missingSList += "sa_V1/";} // sa_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7") != null){  qrs.sa_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7"));	}else{missingS++; missingSList += "sa_V2/";} // sa_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8") != null){  qrs.sa_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8"));	}else{missingS++; missingSList += "sa_V3/";} // sa_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9") != null){  qrs.sa_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9"));	}else{missingS++; missingSList += "sa_V4/";} // sa_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10") != null){  qrs.sa_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10"));	}else{missingS++; missingSList += "sa_V5/";} // sa_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11") != null){  qrs.sa_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11"));	}else{missingS++; missingSList += "sa_V6";}  // sa_V6

		if(missingQ+missingR+missingS+missingOther > 5){
			throw new AnalysisParameterException(" Missing " + (missingQ+missingR+missingS+missingOther) + " critical parameters.");
		}
		
		if( (missingQList.length()>0) || (missingRList.length()>0) || (missingSList.length()>0) || (missingOtherList.length()>0) ){
			debugPrintln("Missing Q parameter List: \"" + missingQList + "\"");
			debugPrintln("Missing R parameter List: \"" + missingRList + "\"");
			debugPrintln("Missing S parameter List: \"" + missingSList + "\"");
			debugPrintln("Missing \"Other\" parameter List: \"" + missingOtherList + "\"");
		}
		
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("straussSelvesterQRS_score()");
		debugPrintln("- sHeaderFile:" + headerFilename);
		debugPrintln("- sPath:" + path);
		
		int iIndexPeriod = headerFilename.lastIndexOf(".");
		String record = headerFilename.substring(0, iIndexPeriod);

		int[] result;

		if(qrs.scode.size()==0){ 
			this.setDataHeaders(new String[]{"LBBB_QRS_Score", "RBBB+LAFB_QRS_Score", "RBBB_QRS_Score", "LAFB_QRS_Score", "LVH_QRS_Score", "No_confounders_QRS_Score",
											 
											 "LBBB_Scar1", 		"LBBB_Scar2", 		"LBBB_Scar3", 		"LBBB_Scar4", 		"LBBB_Scar5", 		"LBBB_Scar6", 		"LBBB_Scar7", 		"LBBB_Scar8", 		"LBBB_Scar9", 		"LBBB_Scar10", 		"LBBB_Scar11", 		"LBBB_Scar12",		"LBBB_points_I", 		"LBBB_points_II", 		"LBBB_points_aVL", 		"LBBB_points_aVF", 		"LBBB_points_V1ant", 		"LBBB_points_V1post", 		"LBBB_points_V2ant", 		"LBBB_points_V2post", 		"LBBB_points_V3", 		"LBBB_points_V4",		"LBBB_points_V5", 		"LBBB_points_V6",
											 "RBBB+LAFB_Scar1", "RBBB+LAFB_Scar2", 	"RBBB+LAFB_Scar3", 	"RBBB+LAFB_Scar4", 	"RBBB+LAFB_Scar5", 	"RBBB+LAFB_Scar6", 	"RBBB+LAFB_Scar7", 	"RBBB+LAFB_Scar8", 	"RBBB+LAFB_Scar9", 	"RBBB+LAFB_Scar10", "RBBB+LAFB_Scar11", "RBBB+LAFB_Scar12",	"RBBB+LAFB_points_I", 	"RBBB+LAFB_points_II", 	"RBBB+LAFB_points_aVL", "RBBB+LAFB_points_aVF", "RBBB+LAFB_points_V1ant", 	"RBBB+LAFB_points_V1post", 	"RBBB+LAFB_points_V2ant", 	"RBBB+LAFB_points_V2post", 	"RBBB+LAFB_points_V3", 	"RBBB+LAFB_points_V4", 	"RBBB+LAFB_points_V5", 	"RBBB+LAFB_points_V6",
											 "RBBB_Scar1", 		"RBBB_Scar2", 		"RBBB_Scar3", 		"RBBB_Scar4", 		"RBBB_Scar5", 		"RBBB_Scar6", 		"RBBB_Scar7", 		"RBBB_Scar8", 		"RBBB_Scar9", 		"RBBB_Scar10", 		"RBBB_Scar11", 		"RBBB_Scar12",		"RBBB_points_I", 		"RBBB_points_II", 		"RBBB_points_aVL", 		"RBBB_points_aVF", 		"RBBB_points_V1ant", 		"RBBB_points_V1post", 		"RBBB_points_V2ant", 		"RBBB_points_V2post", 		"RBBB_points_V3", 		"RBBB_points_V4", 		"RBBB_points_V5", 		"RBBB_points_V6",
											 "LAFB_Scar1", 		"LAFB_Scar2", 		"LAFB_Scar3", 		"LAFB_Scar4", 		"LAFB_Scar5", 		"LAFB_Scar6", 		"LAFB_Scar7", 		"LAFB_Scar8", 		"LAFB_Scar9", 		"LAFB_Scar10", 		"LAFB_Scar11", 		"LAFB_Scar12",		"LAFB_points_I", 		"LAFB_points_II", 		"LAFB_points_aVL",		"LAFB_points_aVF", 		"LAFB_points_V1ant", 		"LAFB_points_V1post", 		"LAFB_points_V2ant", 		"LAFB_points_V2post", 		"LAFB_points_V3", 		"LAFB_points_V4", 		"LAFB_points_V5", 		"LAFB_points_V6",
											 "LVH_Scar1", 		"LVH_Scar2", 		"LVH_Scar3",		"LVH_Scar4", 		"LVH_Scar5", 		"LVH_Scar6", 		"LVH_Scar7", 		"LVH_Scar8", 		"LVH_Scar9", 		"LVH_Scar10", 		"LVH_Scar11", 		"LVH_Scar12", 		"LVH_points_I", 		"LVH_points_II", 		"LVH_points_aVL", 		"LVH_points_aVF", 		"LVH_points_V1ant", 		"LVH_points_V1post", 		"LVH_points_V2ant", 		"LVH_points_V2post", 		"LVH_points_V3", 		"LVH_points_V4", 		"LVH_points_V5", 		"LVH_points_V6",
					
											 "No_confounders_Scar1", "No_confounders_Scar2", "No_confounders_Scar3", "No_confounders_Scar4", "No_confounders_Scar5", "No_confounders_Scar6", "No_confounders_Scar7", "No_confounders_Scar8", "No_confounders_Scar9", "No_confounders_Scar10", "No_confounders_Scar11", "No_confounders_Scar12",
											 "No_confounders_points_I", "No_confounders_points_II", "No_confounders_points_aVL", "No_confounders_points_aVF", "No_confounders_points_V1ant", "No_confounders_points_V1post", "No_confounders_points_V2ant", "No_confounders_points_V2post", "No_confounders_points_V3", "No_confounders_points_V4", "No_confounders_points_V5", "No_confounders_points_V6",			    
					
											 "Bad Q Parameter Count", "Bad R Parameter Count", "Bad S Parameter Count", "Bad Q Parameter List", "Bad R Parameter List", "Bad S Parameter List", "Positive Uncertainty", "Negative Uncertainty"});
		   
		}
		
	    StringBuilder sb = new StringBuilder();
	    if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
		    sb.append("{ \"results\" : [ \n");
	    }else{
	    	for (int i = 0; i < this.getDataHeaders().length; i++) {
		    	 sb.append(this.getDataHeaders()[i]);
		    	 if(i < this.getDataHeaders().length){
		    		 sb.append(',');
		    	 }
			}
	    	sb.deleteCharAt(sb.lastIndexOf(","));
		    sb.append('\n');
	    }

	    List<Integer> QRSScoreBuff = new ArrayList<Integer>();
    	List<Integer> scarPointBuff = new ArrayList<Integer>();
    	String conductionTypeName = null;
    	
    	if(this.qrs.scode.size() == 0){
		    
	    	for(int type = 2;type<=7;type++){
	    		result = qrs.calculateQRS_score_Full(type);
				extractData(result, QRSScoreBuff, scarPointBuff);
	    	}
	    	
		}else{
	    	// some Marquette 12SL ECG analysis program statement codes found, so conduction type is assumed to be amongst the codes.
			result = qrs.calculateQRS_score_Full();
			conductionTypeName = qrs.getConductionTypeName();
			extractData(result, QRSScoreBuff, scarPointBuff);
	    }

	    scarPointBuff.add(qrs.badQParameterCount);
	    scarPointBuff.add(qrs.badRParameterCount);
	    scarPointBuff.add(qrs.badSParameterCount);
	    
	    sb.append(this.writeData(conductionTypeName, QRSScoreBuff, scarPointBuff, qrs.missingQParamList, qrs.missingRParamList,qrs.missingSParamList , qrs.positiveError, qrs.negativeError));
		    
		switch (this.getAnalysisVO().getResultType()) {
			case ORIGINAL_FILE:
			case CSV_FILE:
				BufferedWriter  writer = null;
				try {
					String fileName = path + record + "_" + this.getAnalysisVO().getJobId() + ".csv";
				    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
				    
				    writer.write(sb.toString());
				    
				    if(bRet){
						ArrayList<String> outputFilenames = new ArrayList<String>();
						outputFilenames.add(fileName);
						this.getAnalysisVO().setOutputFileNames(outputFilenames);
					}else{
						debugPrintln("- Encountered errors.");
					}
				} catch (IOException ex) {
					  bRet = false;
				} finally {
				   try {
					   writer.close();
				   }catch (Exception ex) {
					   bRet = false;
				   }
				}
				break;

			case JSON_DATA:
				sb.append("]}");
				this.getAnalysisVO().setOutputData(sb.toString());
				this.getAnalysisVO().setOutputFileNames(null);
				break;
		}
		
		this.getAnalysisVO().setSucess(bRet);
	}

	private void extractData(int[] result, List<Integer> QRSScoreBuff,
			List<Integer> scarPointBuff) {
		int[] scarPercentages = qrs.getPercentLVScarBySegment();
		
		QRSScoreBuff.add(result[12]); // element 12 is QRS-Score
		System.out.println(qrs.getConductionTypeName() + ": score = " + result[12]);
		
		for(int sp:scarPercentages){
			scarPointBuff.add(sp);
		}
		for(int point=0;point<12;point++){ // f0 through 11 are intermediate point calculations, element 12 is QRS-Score					    	
			scarPointBuff.add(result[point]);
		}
	}
	
	private String writeData(String conductionTypeName, List<Integer> qRSScoreBuff, List<Integer> scarPointBuff, String missingQParamList, String missingRParamList, String missingSParamList, int positiveError, int negativeError) {
		StringBuilder sb = new StringBuilder();
		
		if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
			sb.append('{');	
		}
		
		int conductionTypeIndex = 0;
		for (int h = 0; h < this.getDataHeaders().length; h++) {
		
			if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
				sb.append('\"');
				sb.append(this.getDataHeaders()[h]);
				sb.append("\" : ");
			}
			
			if(h == 0 && conductionTypeName != null){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(conductionTypeName).append('\"');	
				}else{
					sb.append(conductionTypeName);
				}
				conductionTypeIndex++;
			}else if(h-(conductionTypeIndex) < qRSScoreBuff.size()){
				sb.append(qRSScoreBuff.get(h-(conductionTypeIndex)));
			}else if(h-(conductionTypeIndex + qRSScoreBuff.size()) < scarPointBuff.size()){
				sb.append(scarPointBuff.get(h-(conductionTypeIndex + qRSScoreBuff.size())));
				
			}else if(h == this.getDataHeaders().length - 5){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(missingQParamList).append('\"');	
				}else{
					sb.append(missingQParamList);
				}
			}else if(h == this.getDataHeaders().length - 4){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(missingRParamList).append('\"');	
				}else{
					sb.append(missingRParamList);
				}	
			}else if(h == this.getDataHeaders().length - 3){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(missingSParamList).append('\"');	
				}else{
					sb.append(missingSParamList);
				}	
			}else if(h == this.getDataHeaders().length - 2){
				sb.append(positiveError);
			}else if(h == this.getDataHeaders().length - 1){
				sb.append(negativeError);
			}
			
			if(h < (this.getDataHeaders().length-1)){
				sb.append(',');
			}
		}
		if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
			sb.append('}');
		}
		return sb.toString();
	}
}
