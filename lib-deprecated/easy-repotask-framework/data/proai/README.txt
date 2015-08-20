
1. set addresses correctly in
easy-tools/easy-repo-tools/dob/oai/sdep_oai_item.xml

in datastream EMD_didl:
					<!-- location sensitive. can we read these parameters from an external 
						source? -->
					<xsl:variable name="fedoraURL" select="'http://localhost:8080/fedora32/'" />
					<xsl:variable name="viewDatasetURL"
						select="'http://eof12.dans.knaw.nl/rest/datasets/'" />

in datastream WSDL, will this do?
					<wsdl:service name="N/A">
						<wsdl:port binding="this:binding" name="port">
							<http:address location="http://local.fedora.server/" />
						</wsdl:port>
					</wsdl:service>
					
2. build assembly easy-repo-tools and unpack at server.

3. run 'dob-ingester-task'

4. update proai.properties in /data/work/deployment/proai/WEB-INF/classes/proai.properties
################################################
# Fedora Driver: Metadata Format Configuration #
################################################

# driver.fedora.md.formats = oai_dc test_format formatX formatY
driver.fedora.md.formats = oai_dc didl carare

driver.fedora.md.format.carare.loc = http://www.carare.eu/carareSchema

driver.fedora.md.format.carare.uri = http://www.carare.eu/carareSchema

driver.fedora.md.format.carare.dissType = info:fedora/*/easy-sdef:oai-item1/getCarare

5. stop proai

6. clear cache

7. redeploy
