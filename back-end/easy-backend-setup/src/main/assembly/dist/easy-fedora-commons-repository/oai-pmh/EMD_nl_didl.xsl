<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dcterms="http://purl.org/dc/terms/" 
    xmlns:didl="urn:mpeg:mpeg21:2002:02-DIDL-NS" 
    xmlns:dii="urn:mpeg:mpeg21:2002:01-DII-NS" 
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/" 
    xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" 
    xmlns:fprofile="http://www.fedora.info/definitions/1/0/access/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="emd eas fprofile dc xs" version="2.0">
    
    <!-- stylesheet version 2014-01-15T13:41 -->
    <!-- ==================================================== -->
    
    <!--xsl:import href="emd-mods_1.0.xsl"/-->
    <xsl:import href="http://localhost:8080/fedora/objects/dans-xsl:1/datastreams/emd-mods.xsl/content"/>
    
    <!-- override variable 'date-encoding-attribute-value' in 'emd-mods.xsl'. -->
    <!-- nl_didl/mods requires 'iso8601', while 'w3cdtf' is more correct according to mods specs. -->
    <xsl:variable name="date-encoding-attribute-value" select="'iso8601'"/>
    
    <!-- ==================================================== -->
    <xsl:output encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="yes"/>
    <!-- ==================================================== -->
    
    <!-- === CONFIGURE contacting Fedora ==================== -->
    <!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
    <!-- ==================================================== -->
    <!--xsl:variable name="fedoraURL" select="'http://localhost:1404/fedora/'"/-->
    <xsl:variable name="fedoraURL" select="'http://localhost:8080/fedora/'"/>
    
    <!-- === CONFIGURE object location ====================== -->
    <!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
    <!-- ==================================================== -->
    <xsl:variable name="restDatasetsURL" select="'https://easy.dans.knaw.nl/ui/rest/datasets/'"/>
    
    <!-- === CONFIGURE file location ======================== -->
    <!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
    <!-- ==================================================== -->
    <xsl:variable name="fileExplorerURL" select="'https://easy.dans.knaw.nl/ui/datasets/id/'"/>
    
    <!-- ==================================================== -->
    <xsl:variable name="datasetId" select="concat(/emd:easymetadata/emd:identifier/dc:identifier[@eas:scheme='DMO_ID']/text(), '')"/>
    <xsl:variable name="datasetPage" select="concat($restDatasetsURL, $datasetId)"/>
    <xsl:variable name="access-rights" select="/emd:easymetadata/emd:rights/dcterms:accessRights[contains(@eas:schemeId, '.dcterms.accessrights')]"/>
    <!-- ==================================================== -->
    <xsl:template match="/">
        <didl:DIDL>
            <xsl:attribute name="xsi:schemaLocation">
                <xsl:call-template name="schema-locations"/>
            </xsl:attribute>
            <xsl:call-template name="item0"/>
        </didl:DIDL>
    </xsl:template>
    <!-- ==================================================== -->
    <xsl:template name="schema-locations">
        <xsl:text>urn:mpeg:mpeg21:2002:02-DIDL-NS </xsl:text>
        <xsl:text>http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/did/didl.xsd </xsl:text>
        <xsl:text>urn:mpeg:mpeg21:2002:01-DII-NS </xsl:text>
        <xsl:text>http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/dii/dii.xsd </xsl:text>
        <xsl:text>http://purl.org/dc/terms/ </xsl:text>
        <xsl:text>http://dublincore.org/schemas/xmls/qdc/dcterms.xsd</xsl:text>
        <!--                                 no space in last entry ^ !! -->
    </xsl:template>
    <!-- ==================================================== -->
    <!-- top-level Item element -->
    <xsl:template name="item0">
        <xsl:variable name="urlGetObjectXML" select="concat($fedoraURL, 'objects/', $datasetId, '?format=xml')"/>
        <xsl:variable name="lastModified" select="document($urlGetObjectXML)/fprofile:objectProfile/fprofile:objLastModDate/text()"/>
        <xsl:variable name="perssistent-identifier" select="/emd:easymetadata/emd:identifier/dc:identifier[@eas:scheme='PID']/text()"/>
        
        <xsl:element name="didl:Item">
            <!-- Identification, mandatory -->
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="'application/xml'"/>
                    <xsl:element name="dii:Identifier">
                        <xsl:value-of select="$perssistent-identifier"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <!-- Modification date, mandatory --> 
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="'application/xml'"/>
                    <xsl:element name="dcterms:modified">
                        <xsl:value-of select="$lastModified"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <!-- Type of resource -->
            <!-- It may be a URI, but a definition of what they mean with 'dataset' is nowhere to be found.
                <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                <xsl:attribute name="mimeType" select="'application/xml'"/>
                <xsl:element name="rdf:type">
                <xsl:attribute name="rdf:resource" select="'info:eu-repo/semantics/dataset'"/>
                </xsl:element>
                </xsl:element>
                </xsl:element>
            -->
            <!-- accessRights -->
            <!-- SurfShare afspraken en harvester van NARCIS willen accessRights in second-level-item
                with objectFile.
                <xsl:if test="$access-rights">
                <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                <xsl:attribute name="mimeType" select="'application/xml'"/>
                <xsl:element name="dcterms:accessRights">
                <xsl:choose>
                <xsl:when test="$access-rights = 'FREELY_AVAILABLE'">
                <xsl:value-of select="'http://purl.org/eprint/accessRights/OpenAccess'"/>
                </xsl:when>
                <xsl:when test="$access-rights = 'NO_ACCESS' or $access-rights = 'ACCESS_ELSEWHERE'">
                <xsl:value-of select="'http://purl.org/eprint/accessRights/ClosedAccess'"/>
                </xsl:when>
                <xsl:otherwise>
                <xsl:value-of select="'http://purl.org/eprint/accessRights/RestrictedAccess'"/>
                </xsl:otherwise>
                </xsl:choose>
                </xsl:element>
                </xsl:element>
                </xsl:element>
                </xsl:if>
            -->
            <!-- Location, mandatory (for BRI) -->
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="'text/html'"/><!-- Modified for issue 645 -->
                    <xsl:attribute name="ref">
                        <xsl:value-of select="$datasetPage"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:element>
            <!-- the rest is in second-level item elements -->
            <xsl:call-template name="metadata-item"/>
            <xsl:call-template name="object-file"/>
            <xsl:call-template name="humanStartpage-item"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- metadata item -->
    <!-- ==================================================== -->
    <xsl:template name="metadata-item">
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="'application/xml'"/>
                    <xsl:element name="rdf:type">
                        <xsl:attribute name="rdf:resource">
                            <xsl:value-of select="'info:eu-repo/semantics/descriptiveMetadata'"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="'application/xml'"/>
                    <xsl:apply-imports/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- objectFile -->
    <!-- ==================================================== -->
    <xsl:template name="object-file">
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <!-- xsl:attribute name="mimeType" select="'application/xml'"/ --><!-- Removed for issue 645 -->
	                <xsl:element name="didl:Statement">
	                    <xsl:attribute name="mimeType" select="'application/xml'"/>
	                <xsl:element name="rdf:type">
	                    <xsl:attribute name="rdf:resource">
	                        <xsl:value-of select="'info:eu-repo/semantics/objectFile'"/>
	                    </xsl:attribute>
	                </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:if test="$access-rights">
                <xsl:element name="didl:Descriptor">
                    <xsl:element name="didl:Statement">
                        <xsl:attribute name="mimeType" select="'application/xml'"/>
                        <xsl:element name="dcterms:accessRights">
                            <xsl:choose>
                                <xsl:when test="$access-rights = 'OPEN_ACCESS'">
                                    <xsl:value-of select="'http://purl.org/eprint/accessRights/OpenAccess'"/>
                                </xsl:when>
                                <xsl:when test="$access-rights = 'FREELY_AVAILABLE'">
                                    <xsl:value-of select="'http://purl.org/eprint/accessRights/OpenAccess'"/>
                                </xsl:when>
                                <xsl:when test="$access-rights = 'NO_ACCESS' or $access-rights = 'ACCESS_ELSEWHERE'">
                                    <xsl:value-of select="'http://purl.org/eprint/accessRights/ClosedAccess'"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="'http://purl.org/eprint/accessRights/RestrictedAccess'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="'multipart/mixed'"/>
                    <xsl:attribute name="ref" select="concat($fileExplorerURL, $datasetId, '/tab/2')"/> 
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- humanStartPage -->
    <!-- ==================================================== -->
    <xsl:template name="humanStartpage-item">
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="'application/xml'"/>
                    <xsl:element name="rdf:type">
                        <xsl:attribute name="rdf:resource">
                            <xsl:value-of select="'info:eu-repo/semantics/humanStartPage'"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="ref" select="$datasetPage"></xsl:attribute>
                    <xsl:attribute name="mimeType" select="'text/html'"></xsl:attribute><!-- Modified for issue 645 -->
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
</xsl:stylesheet>
