<?xml version="1.0" encoding="UTF-8"?>    
<xsl:stylesheet 
    exclude-result-prefixes="emd eas dc"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dcterms="http://purl.org/dc/terms/" 
    xmlns:didl="urn:mpeg:mpeg21:2002:02-DIDL-NS"
    xmlns:dii="urn:mpeg:mpeg21:2002:01-DII-NS" 
    xmlns:dip="urn:mpeg:mpeg21:2005:01-DIP-NS" 
    xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
    xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:variable name="fedoraURL" select="&apos;http://localhost:8080/fedora32/&apos;"/>
    <xsl:variable name="viewDatasetURL" select="&apos;http://eof12.dans.knaw.nl/rest/datasets/&apos;"/>
    <xsl:template match="emd:easymetadata">
            <didl:DIDL>
                <xsl:attribute name="xsi:schemaLocation">
                    <xsl:call-template name="schema-locations"/>
                </xsl:attribute>
                <xsl:call-template name="item0"/>
            </didl:DIDL>
    </xsl:template>
    <xsl:template name="schema-locations">
        <xsl:text>urn:mpeg:mpeg21:2002:02-DIDL-NS </xsl:text>
        <xsl:text>http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/did/didl.xsd </xsl:text>
        <xsl:text>urn:mpeg:mpeg21:2002:01-DII-NS </xsl:text>
        <xsl:text>http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/dii/dii.xsd </xsl:text>
        <xsl:text>urn:mpeg:mpeg21:2005:01-DIP-NS </xsl:text>
        <xsl:text>http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/dip/dip.xsd </xsl:text>
        <xsl:text>http://purl.org/dc/terms/ </xsl:text>
        <xsl:text>http://dublincore.org/schemas/xmls/qdc/dcterms.xsd </xsl:text>
    </xsl:template>
    <xsl:template name="item0">
        <xsl:variable name="datasetId" select="//emd:identifier/dc:identifier[@eas:scheme=&apos;DMO_ID&apos;]/text()"/>
        <xsl:variable name="datasetPage" select="concat($viewDatasetURL, $datasetId)"/>
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;application/xml&apos;"/>
                    <xsl:element name="dii:Identifier">
                        <xsl:value-of select="//emd:identifier/dc:identifier[@eas:scheme=&apos;PID&apos;]/text()"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;text/xml&apos;"/>
                    <xsl:element name="dcterms:modified">
                        <xsl:value-of select="//emd:easymetadata/emd:date/eas:dateSubmitted[1]/text()"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="&apos;application/html&apos;"/>
                    <xsl:attribute name="ref">
                        <xsl:value-of select="$datasetPage"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:element>
            <xsl:call-template name="oai_dc">
                <xsl:with-param name="datasetId" select="$datasetId"/>
            </xsl:call-template>
            <xsl:call-template name="mods"/>
            <xsl:call-template name="humanStartpage">
                <xsl:with-param name="datasetPage" select="$datasetPage"/>
            </xsl:call-template>
            <xsl:call-template name="objectFile">
                <xsl:with-param name="datasetPage" select="$datasetPage"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>
    <xsl:template name="oai_dc">
        <xsl:param name="datasetId"/>
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;application/xml&apos;"/>
                    <xsl:element name="dip:ObjectType">
                        <xsl:value-of select="&apos;info:eu-repo/semantics/descriptiveMetadata&apos;"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="&apos;application/xml&apos;"/>
                    <xsl:variable name="dcUrl"
                        select="concat($fedoraURL, &apos;get/&apos;, $datasetId, &apos;/easy-sdef:oai-item1/getOAI_DC&apos;)"/>
                    <xsl:copy-of select="document($dcUrl)/*"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="mods">
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;application/xml&apos;"/>
                    <xsl:element name="dip:ObjectType">
                        <xsl:value-of select="&apos;info:eu-repo/semantics/descriptiveMetadata&apos;"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="mimeType" select="&apos;application/xml&apos;"/>
                    <xsl:element name="mods">
                        <xsl:attribute name="version" select="3.4"/>
                        <xsl:attribute name="xsi:schemaLocation"
                            select="&apos;http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd&apos;"/>
                        <xsl:element name="titleInfo">
                            <xsl:for-each select="//emd:easymetadata/emd:title/dc:title">
                                <xsl:element name="title">
                                    <xsl:call-template name="extract"/>
                                </xsl:element>
                            </xsl:for-each>
                        </xsl:element>
                        <xsl:if test="//emd:easymetadata/emd:title/dcterms:alternative">
                            <xsl:element name="titleInfo">
                                <xsl:attribute name="type" select="&apos;alternative&apos;"/>
                                <xsl:for-each select="//emd:easymetadata/emd:title/dcterms:alternative">
                                    <xsl:element name="title">
                                        <xsl:call-template name="extract"/>
                                    </xsl:element>
                                </xsl:for-each>
                            </xsl:element>
                        </xsl:if>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="extract">
        <xsl:if test="@xml:lang">
            <xsl:attribute name="xml:lang" select="@xml:lang"/>
        </xsl:if>
        <xsl:value-of select="text()"/>
    </xsl:template>
    <xsl:template name="humanStartpage">
        <xsl:param name="datasetPage"/>
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;application/html&apos;"/>
                    <xsl:element name="dip:ObjectType">
                        <xsl:value-of select="&apos;info:eu-repo/semantics/humanStartPage&apos;"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="ref" select="$datasetPage"/>
                    <xsl:attribute name="mimeType" select="&apos;application/html&apos;"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="objectFile">
        <xsl:param name="datasetPage"/>
        <xsl:element name="didl:Item">
            <xsl:element name="didl:Descriptor">
                <xsl:element name="didl:Statement">
                    <xsl:attribute name="mimeType" select="&apos;application/html&apos;"/>
                    <xsl:element name="dip:ObjectType">
                        <xsl:value-of select="&apos;info:eu-repo/semantics/objectFile&apos;"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="didl:Component">
                <xsl:element name="didl:Resource">
                    <xsl:attribute name="ref" select="$datasetPage"/>
                    <xsl:attribute name="mimeType" select="&apos;application/html&apos;"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>


</xsl:stylesheet>
