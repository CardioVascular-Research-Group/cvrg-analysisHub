package edu.jhu.cvrg.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ServiceProperties {
	
	private static String PROPERTIES_PATH = "/conf/service.properties";
	private static Properties prop;
	private static ServiceProperties singleton;
	private static File propertiesFile = null;
	private static long lastChange = 0;
	
	private static Logger log = Logger.getLogger(ServiceProperties.class);
	private boolean isTest = false;
	
	private ServiceProperties() {
		prop = new Properties();
		String catalinaHome = System.getProperty("catalina.home");
		InputStream testProperties = null;
		
		if(catalinaHome == null){
			catalinaHome = "/opt/liferay/waveform3/tomcat-7.0.27";
			log.error("catalina.home not found, using the default value \""+catalinaHome+"\"");
		}
		
		testProperties = this.getClass().getResourceAsStream("/test.properties");
		if(testProperties == null){
			propertiesFile = new File(catalinaHome+PROPERTIES_PATH);
			loadProperties();
		}else{
			try {
				prop.load(testProperties);
				isTest = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static ServiceProperties getInstance(){
		if(singleton == null){
			singleton = new ServiceProperties();
		}
		return singleton;
	}
	
	public String getProperty(String propertyName){
		loadProperties();
		return prop.getProperty(propertyName);
	}
	
	private void loadProperties(){
		try {
			if(!isTest && propertiesFile.lastModified() > lastChange){
				prop.clear();
				prop.load(new FileReader(propertiesFile));
				lastChange = propertiesFile.lastModified();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final String WINE_COMMAND = "wine.command";
	public static final String CHESNOKOV_COMMAND = "chesnokov.command";
	public static final String CHESNOKOV_FILTERS = "chesnokov.filters";
	
	public static final String DATATRANSFER_SERVICE_URL = "dataTransferServiceURL";
	public static final String DATATRANSFER_SERVICE_NAME = "dataTransferServiceName";
	public static final String DATATRANSFER_SERVICE_METHOD = "dataTransferServiceMethod";
	
	public static final String TEMP_FOLDER = "temp.folder";
	public static final String LIFERAY_DB_ENDPOINT_URL = "liferay.endpoint.url.db";
	public static final String LIFERAY_FILES_ENDPOINT_URL = "liferay.endpoint.url.files";
	public static final String LIFERAY_WS_USER = "liferay.ws.user";
	public static final String LIFERAY_WS_PASSWORD = "liferay.ws.password";
		
}
