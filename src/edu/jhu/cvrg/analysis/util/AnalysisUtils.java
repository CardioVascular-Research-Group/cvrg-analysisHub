package edu.jhu.cvrg.analysis.util;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class AnalysisUtils {
	

	private static final Logger log = Logger.getLogger(AnalysisUtils.class);
	
	public static final String SERVER_TEMP_ANALYSIS_FOLDER = ServiceProperties.getInstance().getProperty(ServiceProperties.TEMP_FOLDER)+"/a";
	
	public static String sep = File.separator;
	
	/** Find the first filename in the array with the "hea" extension.
	 * 
	 * @param asInputFileNames - array of filenames to search
	 * @return - full path/name.ext as found in the array.
	 */
	public static String findHeaderPathName(List<String> asInputFileNames){
		log.info("++ analysisUtils +findHeaderPathName()");
		return findPathNameExt(asInputFileNames, "hea");
	}

	/** Find the first filename in the array with the specified extension.
	 * 
	 * @param asInputFileNames - array of filenames to search
	 * @param sExtension - extension to look for, without the dot(".") e.g. "hea".
	 * @return - full path/name.ext as found in the array.
	 */
	public static String findPathNameExt(List<String> asInputFileNames, String sExtension){
		log.info("++ analysisUtils +findHeaderPathName()");
		String sHeaderPathName="";
		int iIndexPeriod=0;
		
		for (String sTemp : asInputFileNames) {
			log.info("++ analysisUtils +- asInputFileNames: " + sTemp);
			iIndexPeriod = sTemp.lastIndexOf(".");
			
			if( sExtension.contains(sTemp.substring(iIndexPeriod+1)) ){
				sHeaderPathName = sTemp;
				break;
			}
		}
		
		log.info("++ analysisUtils +++ analysisUtils +- ssHeaderPathName: " + sHeaderPathName);
		return sHeaderPathName;
	}
	
	public static String extractPath(String sHeaderPathName){
		log.info("extractPath() from: '" + sHeaderPathName + "'");

		String sFilePath="";
		int iIndexLastSlash = sHeaderPathName.lastIndexOf("/");
		
		sFilePath = sHeaderPathName.substring(0,iIndexLastSlash+1);
		
		return sFilePath;
	}
	
	public static String extractName(String sFilePathName){
		log.info("extractName() from: '" + sFilePathName + "'");

		String sFileName="";
		int iIndexLastSlash = sFilePathName.lastIndexOf("/");
		
		sFileName = sFilePathName.substring(iIndexLastSlash+1);

		return sFileName;
	}
	
	public static void deleteFile(String inputPath, String inputFilename) {
		deleteFile(inputPath + sep + inputFilename);
	}

	public static void deleteFile(String fullPathFileName) {
		File targetFile = new File(fullPathFileName);
		if(targetFile.exists()){
			targetFile.delete();
		}
	}

}
