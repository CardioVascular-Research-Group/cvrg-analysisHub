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
		log.debug("++ analysisUtils +findHeaderPathName()");
		return findPathNameExt(asInputFileNames, "hea");
	}

	/** Find the first filename in the array with the specified extension.
	 * 
	 * @param asInputFileNames - array of filenames to search
	 * @param sExtension - extension to look for, without the dot(".") e.g. "hea".
	 * @return - full path/name.ext as found in the array.
	 */
	public static String findPathNameExt(List<String> asInputFileNames, String sExtension){
		log.debug("++ analysisUtils +findHeaderPathName()");
		String headerPathName="";
		int indexPeriod=0;
		
		for (String sTemp : asInputFileNames) {
			log.debug("++ analysisUtils +- asInputFileNames: " + sTemp);
			indexPeriod = sTemp.lastIndexOf(".");
			
			if( sExtension.contains(sTemp.substring(indexPeriod+1)) ){
				headerPathName = sTemp;
				break;
			}
		}
		
		log.debug("- headerPathName: " + headerPathName);
		return headerPathName;
	}
	
	public static String extractPath(String headerPathName){
		log.debug("extractPath() from: '" + headerPathName + "'");

		int indexLastSlash = headerPathName.lastIndexOf("/");
		
		String filePath = headerPathName.substring(0,indexLastSlash+1);
		
		return filePath;
	}
	
	public static String extractName(String sFilePathName){
		log.debug("extractName() from: '" + sFilePathName + "'");

		int indexLastSlash = sFilePathName.lastIndexOf("/");
		
		String fileName = sFilePathName.substring(indexLastSlash+1);

		return fileName;
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
