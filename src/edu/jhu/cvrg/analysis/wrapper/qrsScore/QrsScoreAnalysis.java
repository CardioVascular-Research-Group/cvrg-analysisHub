package edu.jhu.cvrg.analysis.wrapper.qrsScore;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class QrsScoreAnalysis extends AnalysisWrapper {
	
	private String headerFilename;
	private String path;
	private QRS_Score qrs;
	
	private static final String UNCHANGED_VALUE = "unchanged";
	
	public QrsScoreAnalysis(AnalysisVO vo) {
		super(vo);
		this.setDataHeaders(new String[]{"ConductionType",
										 "Scar1","Scar2","Scar3","Scar4","Scar5","Scar6","Scar7","Scar8","Scar9","Scar10","Scar11","Scar12",
										 "points_I","points_II","points_aVL","points_aVF","points_V1ant","points_V1post","points_V2ant","points_V2post","points_V3","points_V4","points_V5","points_V6",
										 "QRS_Score","Bad Parameter Count","Positive Uncertainty","Negative Uncertainty","Bad Parameter List"});
	}

	@Override
	public void defineInputParameters() {
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
		if(log.isDebugEnabled()){
			Object[] keys = this.getAnalysisVO().getCommandParamMap().keySet().toArray();
			
			for(int i=0;i<keys.length;i++){
				debugPrintln("Key: \"" + (String)keys[i] + "\" Value: \"" + this.getAnalysisVO().getCommandParamMap().get((String)keys[i]) + "\"");
			}
		}

		// WFDB files consist of a header file and one or more data files.
		// This function takes the header file as a parameter, and then uses it to look up the name(s) of the data file(s).
		String sHeaderPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(sHeaderPathName);
		headerFilename = AnalysisUtils.extractName(sHeaderPathName);
		
		debugPrintln("- sHeaderPathName: " + sHeaderPathName);
		debugPrintln("- path: " + path);
		debugPrintln("- sHeaderName: " + headerFilename);
		
		debugPrintln("- Starting QRS_Score()");
		qrs = new QRS_Score();

		int missing = 0;
		String missingList = "";
		//---------------------------- Whole record parameters ---------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("Name")!= null){  qrs.Name  = (String) this.getAnalysisVO().getCommandParamMap().get("Name");	}else{missing++; missingList += "Name/";} // Name
		if(this.getAnalysisVO().getCommandParamMap().get("ID")  != null){  qrs.ID    = (String) this.getAnalysisVO().getCommandParamMap().get("ID");	}else{missing++; missingList += "ID/";} // ID
		if(this.getAnalysisVO().getCommandParamMap().get("age") != null){  qrs.Age   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("age"));	}else{missing++; missingList += "age/";} // age
		if(this.getAnalysisVO().getCommandParamMap().get("sex") != null){ // options are "male", "female" or "Unknown"
			String sex = (String)  this.getAnalysisVO().getCommandParamMap().get("sex");
			if(sex.equalsIgnoreCase("female")){
				qrs.Sex = 1;
			}else{
				qrs.Sex = 0; // default is male, so Unknown will be treated as male. 
			}
		}else{
			qrs.Sex = 0; // default is male, so Unknown will be treated as male. 
			missing++; // sex
			missingList += "sex,";
		}
		 
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000072") != null){  qrs.qrsd   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000072"));	}else{missing++; missingList += "qrsd/";} // qrsd
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000838") != null){  qrs.qrsax  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000838"));	}else{missing++; missingList += "qrsax/";} // qrsax

		//---------------------------- Q_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_0") != null){  qrs.qa_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_0"));	}else{missing++; missingList += "qa_I/";} // qa_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_4") != null){  qrs.qa_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_4"));	}else{missing++; missingList += "qa_aVL/";} // qa_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_5") != null){  qrs.qa_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_5"));	}else{missing++; missingList += "qa_aVF/";} // qa_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6") != null){  qrs.qa_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6"));	}else{missing++; missingList += "qa_V1/";} // qa_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7") != null){  qrs.qa_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7"));	}else{missing++; missingList += "qa_V2/";} // qa_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8") != null){  qrs.qa_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8"));	}else{missing++; missingList += "qa_V3/";} // qa_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9") != null){  qrs.qa_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9"));	}else{missing++; missingList += "qa_V4/";} // qa_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10") != null){  qrs.qa_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10"));	}else{missing++; missingList += "qa_V5/";} // qa_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11") != null){  qrs.qa_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11"));	}else{missing++; missingList += "qa_V6/";} // qa_V6
		//---------------------------- Q_Wave_Duration -----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_0") != null){  qrs.qd_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_0"));	}else{missing++; missingList += "qd_I/";} // qd_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_1") != null){  qrs.qd_II  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_1"));	}else{missing++; missingList += "qd_II/";} // qd_II
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_4") != null){  qrs.qd_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_4"));	}else{missing++; missingList += "qd_aVL/";} // qd_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_5") != null){  qrs.qd_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_5"));	}else{missing++; missingList += "qd_aVF/";} // qd_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_6") != null){  qrs.qd_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_6"));	}else{missing++; missingList += "qd_V1/";} // qd_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_7") != null){  qrs.qd_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_7"));	}else{missing++; missingList += "qd_V2/";} // qd_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_8") != null){  qrs.qd_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_8"));	}else{missing++; missingList += "qd_V3/";} // qd_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_9") != null){  qrs.qd_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_9"));	}else{missing++; missingList += "qd_V4/";} // qd_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_10") != null){  qrs.qd_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_10"));	}else{missing++; missingList += "qd_V5/";} // qd_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_11") != null){  qrs.qd_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000551_11"));	}else{missing++; missingList += "qd_V6/";} // qd_V6
		
		//---------------------------- R_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_0") != null){  qrs.ra_I   = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_0"));	}else{missing++; missingList += "ra_I/";} // ra_I
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_4") != null){  qrs.ra_aVL = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_4"));	}else{missing++; missingList += "ra_aVL/";} // ra_aVL
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_5") != null){  qrs.ra_aVF = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_5"));	}else{missing++; missingList += "ra_aVF/";} // ra_aVF
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_6") != null){  qrs.ra_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_6"));	}else{missing++; missingList += "ra_V2/";} // ra_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_7") != null){  qrs.ra_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_7"));	}else{missing++; missingList += "ra_V2/";} // ra_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_8") != null){  qrs.ra_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_8"));	}else{missing++; missingList += "ra_V3/";} // ra_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_9") != null){  qrs.ra_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_9"));	}else{missing++; missingList += "ra_V4/";} // ra_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_10") != null){  qrs.ra_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_10"));	}else{missing++; missingList += "ra_V5/";} // ra_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_11") != null){  qrs.ra_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000750_11"));	}else{missing++; missingList += "ra_V6/";} // ra_V6
		//---------------------------- R_Wave_Duration -----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_6") != null){  qrs.rd_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_6"));	}else{missing++; missingList += "rd_V1/";} // rd_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_7") != null){  qrs.rd_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_7"));	}else{missing++; missingList += "rd_V2/";} // rd_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_8") != null){  qrs.rd_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000597_8"));	}else{missing++; missingList += "rd_V3/";} // rd_V3
		//---------------------------- S_Wave_Amplitude ----------------------------------------
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6") != null){  qrs.sa_V1  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_6"));	}else{missing++; missingList += "sa_V1/";} // sa_V1
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7") != null){  qrs.sa_V2  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_7"));	}else{missing++; missingList += "sa_V2/";} // sa_V2
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8") != null){  qrs.sa_V3  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_8"));	}else{missing++; missingList += "sa_V3/";} // sa_V3
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9") != null){  qrs.sa_V4  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_9"));	}else{missing++; missingList += "sa_V4/";} // sa_V4
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10") != null){  qrs.sa_V5  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_10"));	}else{missing++; missingList += "sa_V5/";} // sa_V5
		if(this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11") != null){  qrs.sa_V6  = Float.parseFloat( (String) this.getAnalysisVO().getCommandParamMap().get("ECG_000000652_11"));	}else{missing++; missingList += "sa_V6";} // sa_V6

		
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("straussSelvesterQRS_score()");
		debugPrintln("- sHeaderFile:" + headerFilename);
		debugPrintln("- sPath:" + path);
		try {
			int iIndexPeriod = headerFilename.lastIndexOf(".");
			String record = headerFilename.substring(0, iIndexPeriod);

			int[] result = qrs.calculateQRS_score_Full();
			String conductionTypeName = qrs.getConductionTypeName();
			int[] scarPercentages = qrs.getPercentLVScarBySegment();
			int badParamCount = qrs.badParameterCount;
			int posError = qrs.positiveError;
			int negError = qrs.negativeError;
			
			int[] extraValues = new int[]{badParamCount, posError, negError};

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
		    
		    int[] values = ArrayUtils.addAll(ArrayUtils.addAll(scarPercentages, result), extraValues);
		    
		    sb.append(this.writeData(conductionTypeName, values, qrs.missingParamList));
		   
		    
		    if(qrs.scode.size()==0){ 
		    	// no Marquette 12SL ECG analysis program statement codes found, so conduction type could not be determined
		    	// Therefore calculate all conductions types so that the researcher can choose the most appropriate.
		    	for(int type = 2;type<=6;type++){
		    		result = qrs.calculateQRS_score_Full(type);
					conductionTypeName = qrs.getConductionTypeName();
					scarPercentages = qrs.getPercentLVScarBySegment();
					
					values = ArrayUtils.addAll(scarPercentages, result);
					
					sb.append(this.writeData(conductionTypeName, values, null));
				}
		    }

		    
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
					sb.deleteCharAt(sb.lastIndexOf(","));
					sb.append("]}");
					this.getAnalysisVO().setOutputData(sb.toString());
					this.getAnalysisVO().setOutputFileNames(null);
					break;
			}
			
		} catch (Exception e) {
			bRet = false;
			log.error(e.getMessage());
		}
		this.getAnalysisVO().setSucess(bRet);
	}
	
	private String writeData(String conductionTypeName, int[] values, String lastColumnValue){
		StringBuilder sb = new StringBuilder();
		
		if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
			sb.append('{');	
		}
		
		for (int h = 0; h < this.getDataHeaders().length; h++) {
		
			if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
				sb.append('\"');
				sb.append(this.getDataHeaders()[h]);
				sb.append("\" : ");
			}
			if(h == 0){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(conductionTypeName).append('\"');	
				}else{
					sb.append(conductionTypeName);
				}
			}else if(h-1 < values.length){
				sb.append(values[h-1]);
			}else if(h == (this.getDataHeaders().length - 1) && lastColumnValue != null){
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(lastColumnValue).append('\"');	
				}else{
					sb.append(lastColumnValue);
				}
			}else{
				if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
					sb.append('\"').append(UNCHANGED_VALUE).append('\"');	
				}else{
					sb.append(UNCHANGED_VALUE);
				}
			}
			
			if(h < (this.getDataHeaders().length-1)){
				sb.append(',');
			}
		}
		if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
			sb.append("},\n");
		}else{
			sb.append('\n');
		}
		
		return sb.toString();
	}
}
