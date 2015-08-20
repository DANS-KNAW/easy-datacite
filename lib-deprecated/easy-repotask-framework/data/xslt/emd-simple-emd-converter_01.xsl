<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
    xmlns:emd-old="http://easy.dans.knaw.nl/dms/easymetadata"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
    xmlns:fun="http://easy/migration/function">
    <xsl:import href="common/utils_01.xsl"/>
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- 
        This xslt stylesheet generates easymetadata ("http://easy.dans.knaw.nl/easy/easymetadata/")
        from EasyMetadata ("http://easy.dans.knaw.nl/dms/easymetadata") of meta/breed 'simple'.
    -->

    <xsl:template name="simpleConverter">
        <xsl:call-template name="title"/>
        <xsl:call-template name="creator"/>
        <xsl:call-template name="subject"/>
        <xsl:call-template name="description"/>
        <xsl:call-template name="publisher"/>
        <xsl:call-template name="contributor"/>
        <xsl:call-template name="date"/>
        <xsl:call-template name="type"/>
        <xsl:call-template name="format"/>
        <xsl:call-template name="identifier"/>
        <xsl:call-template name="source"/>
        <xsl:call-template name="language"/>
        <xsl:call-template name="relation"/>
        <xsl:call-template name="coverage"/>
        <xsl:call-template name="rights"/>
        <xsl:call-template name="audience"/>
        <xsl:call-template name="other"/>
    </xsl:template>

    <xsl:template name="audience">
        <xsl:if test="dcterms:audience">
            <xsl:element name="emd:audience">
                <xsl:for-each select="dcterms:audience/value">
                    <xsl:element name="dcterms:audience">
                        <xsl:attribute name="eas:scheme" select="'OI-KNAW'"/>
                        <xsl:attribute name="eas:schemeId" select="'common.dcterms.audience'"/>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="contributor">
        <xsl:if test="dc:contributor">
            <xsl:element name="emd:contributor">
                <xsl:for-each select="dc:contributor/value">
                    <xsl:element name="dc:contributor">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="coverage">
        <xsl:if test="dc:coverage | dcterms:spatial | dc:temporal">
            <xsl:element name="emd:coverage">
                <xsl:for-each select="dc:coverage/value">
                    <xsl:element name="dc:coverage">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dcterms:spatial/value">
                    <xsl:element name="dcterms:spatial">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:temporal/value">
                    <xsl:element name="dcterms:temporal">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="creator">
        <xsl:if test="dc:creator">
            <xsl:element name="emd:creator">
                <xsl:for-each select="dc:creator/value">
                    <xsl:element name="dc:creator">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="date">
        <xsl:if test="dc:date | dcterms:created | dcterms:available">
            <xsl:element name="emd:date">
            	<xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][not(@scheme)]">
                    <xsl:element name="dc:date">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='created'][stringValue/text()!='']">
                    <xsl:element name="dcterms:created">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <!-- dcterms:created has it's own element -->
                <xsl:for-each select="dcterms:created/value[@dcterms:W3CDTF='false']">
                    <xsl:element name="dcterms:created">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='valid']">
                    <xsl:element name="dcterms:valid">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='available']">
                    <xsl:element name="dcterms:available">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <!-- dcterms:available -->
                <xsl:for-each select="dcterms:available/value[@dcterms:W3CDTF='false']">                   
                    <xsl:element name="dcterms:available">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='issued']">
                    <xsl:element name="dcterms:issued">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='modified']">
                    <xsl:element name="dcterms:modified">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='dateAccepted']">
                    <xsl:element name="dcterms:dateAccepted">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='dateCopyrighted']">
                    <xsl:element name="dcterms:dateCopyrighted">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='dateSubmitted']">
                    <xsl:element name="dcterms:dateSubmitted">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <!-- eas elements for W3CDTF dates -->
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][not(@scheme)]">
                    <xsl:element name="eas:date">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='created'][stringValue/text()!='']">
                    <xsl:element name="eas:created">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <!-- dcterms:created has it's own element, but is different from arch-format!!! -->
                <xsl:for-each select="dcterms:created/value[@dcterms:W3CDTF='true']">
                    <xsl:element name="eas:created">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>                   
                </xsl:for-each>
                
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='valid']">
                    <xsl:element name="eas:valid">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='available']">
                    <xsl:element name="eas:available">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <!-- dcterms:available -->
                <xsl:for-each select="dcterms:available/value[@dcterms:W3CDTF='true']">
                    <xsl:element name="eas:available">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="value/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <!-- another form of dcterms:available -->
                <xsl:for-each select="dcterms:available[@dcterms:W3CDTF='true']">
                    <xsl:element name="eas:available">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="value/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                 </xsl:for-each>
                
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='issued']">
                    <xsl:element name="eas:issued">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='modified']">
                    <xsl:element name="eas:modified">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='dateAccepted']">
                    <xsl:element name="eas:dateAccepted">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='dateCopyrighted']">
                    <xsl:element name="eas:dateCopyrighted">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='dateSubmitted']">
                    <xsl:element name="eas:dateSubmitted">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:value-of>
                            <xsl:call-template name="normalizeDateTime">
                                <xsl:with-param name="dateString" select="date/text()"/>
                            </xsl:call-template>
                        </xsl:value-of>
                    </xsl:element>
                </xsl:for-each> 
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="description">
        <xsl:if test="dc:description">
            <xsl:element name="emd:description">
                <xsl:for-each select="dc:description/value">
                    <xsl:element name="dc:description">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="format">
        <xsl:if test="dc:format">
            <xsl:element name="emd:format">
                <xsl:for-each select="dc:format/value">
                    <xsl:element name="dc:format">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="identifier">
        <xsl:if test="dcterms:identifier | emd-old:persistentIdentifier">
            <xsl:element name="emd:identifier">
                <xsl:for-each select="dcterms:identifier/value">
                    <xsl:element name="dc:identifier">
                        <xsl:if test="starts-with(text(), 'twips.dans.knaw.nl')">
                            <xsl:attribute name="eas:scheme" select="'AIP_ID'"/>
                        </xsl:if>
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="emd-old:persistentIdentifier/value">
                    <xsl:element name="dc:identifier">
                        <xsl:attribute name="eas:scheme" select="'PID'"/>
                        <xsl:attribute name="eas:identification-system"
                            select="'http://www.persistent-identifier.nl'"/>
                        <xsl:value-of select="label"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="language">
        <xsl:if test="dc:language | dc:languageISO">
            <xsl:element name="emd:language">
                <xsl:for-each select="dc:language/value">
                    <xsl:element name="dc:language">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:languageISO/value">
                    <xsl:element name="dc:language">
                        <xsl:attribute name="eas:scheme" select="'ISO 639'"/>
                        <xsl:attribute name="eas:schemeId" select="'common.dc.language'"/>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="other">

            <xsl:element name="emd:other">
                <xsl:for-each select="dc:remarks/value">
                    <xsl:element name="eas:remark">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:element name="eas:application-specific">
                    <xsl:element name="eas:metadataformat">
                        <xsl:value-of select="'SOCIOLOGY'"/>
                    </xsl:element>
                </xsl:element>
                
                <xsl:element name="eas:etc">
                    <xsl:call-template name="getConversionProperties"/>
                </xsl:element> 
                
            </xsl:element>

    </xsl:template>

    <xsl:template name="publisher">
        <xsl:if test="dc:publisher">
            <xsl:element name="emd:publisher">
                <xsl:for-each select="dc:publisher/value">
                    <xsl:element name="dc:publisher">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="relation">
        <xsl:if test="dc:relation">
            <xsl:element name="emd:relation">
                <xsl:for-each select="dc:relation/value">
                    <xsl:element name="eas:relation">
                        <xsl:if test="@scheme='emphasize'">
                            <xsl:attribute name="eas:emphasis" select="'true'"/>
                        </xsl:if>
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="rights">
        <xsl:if test="dcterms:accessRights | acceptLicense">
            <xsl:element name="emd:rights">
                <xsl:for-each select="dcterms:accessRights/value">
                    <xsl:element name="dcterms:accessRights">
                        <!--xsl:attribute name="eas:scheme" select="'EASY'"/-->
                        <xsl:attribute name="eas:schemeId" select="'common.dcterms.accessrights'"/>
                        <xsl:call-template name="getDatasetAccessCategory">
                            <xsl:with-param name="oldValue">
                                <xsl:value-of select="optionValue/text()"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="acceptLicense/value">
                    <xsl:element name="dcterms:license">
                        <xsl:attribute name="eas:scheme" select="'EASY version 1'"/>
                        <xsl:if test="optionValue/text()='accept'">
                            <xsl:value-of select="'accept'"/>
                        </xsl:if>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="source">
        <xsl:if test="dc:source">
            <xsl:element name="emd:source">
                <xsl:for-each select="dc:source/value">
                    <xsl:element name="dc:source">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="subject">
        <xsl:if test="dc:subject">
            <xsl:element name="emd:subject">
                <xsl:for-each select="dc:subject/value">
                    <xsl:element name="dc:subject">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="title">
        <xsl:if test="dc:title | dcterms:alternative">
            <xsl:element name="emd:title">
                <xsl:for-each select="dc:title/value">
                    <xsl:element name="dc:title">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dcterms:alternative/value">
                    <xsl:element name="dcterms:alternative">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="type">
        <xsl:if test="dc:type">
            <xsl:element name="emd:type">
                <xsl:for-each select="dc:type/value">
                    <xsl:element name="dc:type">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
