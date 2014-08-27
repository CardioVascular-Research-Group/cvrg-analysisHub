<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
    	<html>
    		<xsl:text>{"results":[</xsl:text>
    		<xsl:for-each select="/autoQRSResults/LeadResults/Lead">
	        	<xsl:text>&#10;</xsl:text> <!--  New Line -->
	        	<xsl:text>{ "File Analyzed" : "</xsl:text>
				<xsl:value-of select="/autoQRSResults/FileAnalyzed"/>
				<xsl:text>", "Length" : "</xsl:text>
				<xsl:value-of select="/autoQRSResults/Length"/>
				<xsl:text>", "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000000150|Lead" : "</xsl:text>
			 	<xsl:value-of select="../Lead"/>
				<xsl:text>", "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001083|Total Beat Count" : </xsl:text>
				<xsl:choose>
					<xsl:when test="number(../TotalBeatCount) = ../TotalBeatCount">
						<xsl:value-of select="../TotalBeatCount"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../TotalBeatCount"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001084|Ectopic Beat Count" : </xsl:text>
				<xsl:choose>
					<xsl:when test="number(../EctopicBeatCount) = ../EctopicBeatCount">
						<xsl:value-of select="../EctopicBeatCount"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../EctopicBeatCount"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000000701|QT Corrected Bazett" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTCorrected_Bazett) = ../QTCorrected_Bazett">
						<xsl:value-of select="../QTCorrected_Bazett"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTCorrected_Bazett"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001070|QTVI log" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTVI_log) = ../QTVI_log">
						<xsl:value-of select="../QTVI_log"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTVI_log"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001078|QT Dispersion" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QT_Dispersion) = ../QT_Dispersion">
						<xsl:value-of select="../QT_Dispersion"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QT_Dispersion"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001086|RR Interval Count" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRIntervalCount) = ../RRIntervalResults/RRIntervalCount">
						<xsl:value-of select="../RRIntervalResults/RRIntervalCount"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRIntervalCount"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_0000001088|RR Mean" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRMean) = ../RRIntervalResults/RRMean">
						<xsl:value-of select="../RRIntervalResults/RRMean"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRMean"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001091|RR Minimum" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRMin) = ../RRIntervalResults/RRMin">
						<xsl:value-of select="../RRIntervalResults/RRMin"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRMin"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001090|RR Maximum" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRMax) = ../RRIntervalResults/RRMax">
						<xsl:value-of select="../RRIntervalResults/RRMax"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRMax"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001094|RR Variance" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRVariance) = ../RRIntervalResults/RRVariance">
						<xsl:value-of select="../RRIntervalResults/RRVariance"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRVariance"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001096|RR Standard Deviation" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../RRIntervalResults/RRStandardDeviation) = ../RRIntervalResults/RRStandardDeviation">
						<xsl:value-of select="../RRIntervalResults/RRStandardDeviation"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../RRIntervalResults/RRStandardDeviation"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001085|QT Interval Count" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTIntervalCount) = ../QTIntervalResults/QTIntervalCount">
						<xsl:value-of select="../QTIntervalResults/QTIntervalCount"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTIntervalCount"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001089|QT Mean" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTMean) = ../QTIntervalResults/QTMean">
						<xsl:value-of select="../QTIntervalResults/QTMean"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTMean"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001093|QT Minimum" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTMin) = ../QTIntervalResults/QTMin">
						<xsl:value-of select="../QTIntervalResults/QTMin"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTMin"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001092|QT Maximum" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTMax) = ../QTIntervalResults/QTMax">
						<xsl:value-of select="../QTIntervalResults/QTMax"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTMax"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001095|QT Variance" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTVariance) = ../QTIntervalResults/QTVariance">
						<xsl:value-of select="../QTIntervalResults/QTVariance"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTVariance"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, "http://purl.bioontology.org/ontology/ECGT/ECGTermsv1:ECG_000001097|QT Standard Deviation" : </xsl:text>
			 	<xsl:choose>
					<xsl:when test="number(../QTIntervalResults/QTStandardDeviation) = ../QTIntervalResults/QTStandardDeviation">
						<xsl:value-of select="../QTIntervalResults/QTStandardDeviation"/> 
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>"</xsl:text>
						<xsl:value-of select="../QTIntervalResults/QTStandardDeviation"/>
						<xsl:text>"</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			 	<xsl:text>},</xsl:text>
			 </xsl:for-each>
			 <xsl:text>]}</xsl:text>
		</html>
	</xsl:template>

</xsl:stylesheet>