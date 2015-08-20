<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
    xmlns:emd-old="http://easy.dans.knaw.nl/dms/easymetadata"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:abr="http://easy.dans.knaw.nl/easy/dc/arch/abr/"
    xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
    xmlns:fun="http://easy/migration/function">
    <xsl:import href="common/utils_01.xsl"/>
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- 
        This xslt stylesheet generates easymetadata ("http://easy.dans.knaw.nl/easy/easymetadata/")
        from EasyMetadata ("http://easy.dans.knaw.nl/dms/easymetadata") of meta/breed 'arch'.
    -->

    <xsl:template name="archConverter">
        <xsl:call-template name="arch-title"/>
        <xsl:call-template name="arch-creator"/>
        <xsl:call-template name="arch-subject"/>
        <xsl:call-template name="arch-description"/>
        <xsl:call-template name="arch-publisher"/>
        <xsl:call-template name="arch-contributor"/>
        <xsl:call-template name="arch-date"/>
        <xsl:call-template name="arch-type"/>
        <xsl:call-template name="arch-format"/>
        <xsl:call-template name="arch-identifier"/>
        <xsl:call-template name="arch-source"/>
        <xsl:call-template name="arch-language"/>
        <xsl:call-template name="arch-relation"/>
        <xsl:call-template name="arch-coverage"/>
        <xsl:call-template name="arch-rights"/>
        <xsl:call-template name="arch-audience"/>
        <xsl:call-template name="arch-other"/>
    </xsl:template>

    <xsl:template name="arch-audience">
        <xsl:if test="dcterms:audience">
            <xsl:element name="emd:audience">
                <xsl:for-each select="dcterms:audience/value">
                    <xsl:element name="dcterms:audience">
                        <xsl:attribute name="eas:scheme" select="'OI-KNAW'"/>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="arch-contributor">
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

    <xsl:template name="arch-coverage">
        <xsl:if test="dc:coverage | dcterms:temporal | dcterms:spatial | abr:temporal | dcterms:spatialPoint | dcterms:spatialBox">
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
                <xsl:for-each select="dcterms:temporal/value">
                    <xsl:element name="dcterms:temporal">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="abr:temporal/value">
                    <xsl:element name="dcterms:temporal">
                        <xsl:attribute name="eas:scheme" select="'ABR'"/>
                        <xsl:attribute name="eas:schemeId" select="'archaeology.dcterms.temporal'"/>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dcterms:spatialPoint/value">
                    <xsl:if test="@scheme!='' and x/text()!='' and y/text()!=''">
                    <xsl:element name="eas:spatial">
                        <xsl:element name="eas:point">
                            <xsl:attribute name="eas:scheme" select="@scheme"/>
                            <xsl:attribute name="eas:schemeId" select="'archaeology.eas.spatial'"/>
                            
                            <xsl:element name="eas:x">
                                <xsl:value-of select="x/text()"/>
                            </xsl:element>
                            <xsl:element name="eas:y">
                                <xsl:value-of select="y/text()"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                    </xsl:if>
                </xsl:for-each>
                <xsl:for-each select="dcterms:spatialBox/value">
                    <xsl:if test="@scheme!='' and north/text()!='' and east/text()!='' and south/text()!='' and west/text()!=''">
                    <xsl:element name="eas:spatial">
                        <xsl:element name="eas:box">
                            <xsl:attribute name="eas:scheme" select="@scheme"/>
                            <xsl:attribute name="eas:schemeId" select="'archaeology.eas.spatial'"/>
                            <xsl:element name="eas:north">
                                <xsl:value-of select="north/text()"/>
                            </xsl:element>
                            <xsl:element name="eas:east">
                                <xsl:value-of select="east/text()"/>
                            </xsl:element>
                            <xsl:element name="eas:south">
                                <xsl:value-of select="south/text()"/>
                            </xsl:element>
                            <xsl:element name="eas:west">
                                <xsl:value-of select="west/text()"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                    </xsl:if>
                </xsl:for-each>                
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="arch-creator">
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

    <xsl:template name="arch-date">
        <xsl:if test="dc:date | dcterms:created | dcterms:available">
            <xsl:element name="emd:date">
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][not(@scheme)]">
                    <xsl:element name="dc:date">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='']">
                    <xsl:element name="dc:date">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='false'][@scheme='created']">
                    <xsl:element name="dcterms:created">
                        <xsl:value-of select="stringValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
                <!-- dcterms:created has it's own element -->
                <xsl:for-each select="dcterms:created[@dcterms:W3CDTF='false']">
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
                <xsl:for-each select="dc:date/value[@dcterms:W3CDTF='true'][@scheme='created']">
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
                <!-- dcterms:created has it's own element * and has two versions: value or date element-->
                <xsl:for-each select="dcterms:created[@dcterms:W3CDTF='true']">
                    <xsl:element name="eas:created">
                        <xsl:attribute name="eas:scheme" select="'W3CDTF'"/>
                        <xsl:attribute name="eas:format" select="fun:convertDateFormat(@format)"/>
                        <xsl:choose>
                            <xsl:when test="value/text()!=''">
                                <xsl:value-of>
                                    <xsl:call-template name="normalizeDateTime">
                                        <xsl:with-param name="dateString" select="value/text()"/>
                                    </xsl:call-template>
                                </xsl:value-of>
                            </xsl:when>
                            <xsl:when test="date/text()!=''">
                                <xsl:value-of>
                                    <xsl:call-template name="normalizeDateTime">
                                        <xsl:with-param name="dateString" select="date/text()"/>
                                    </xsl:call-template>
                                </xsl:value-of>
                            </xsl:when>
                        </xsl:choose>                     
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

    <xsl:template name="arch-description">
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

    <xsl:template name="arch-format">
        <xsl:if test="dc:format | dc:formatIMT">
            <xsl:element name="emd:format">
                <xsl:for-each select="dc:format/value">
                    <xsl:element name="dc:format">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dc:formatIMT/value">
                    <xsl:element name="dc:format">
                        <xsl:attribute name="eas:scheme" select="'IMT'"/>
                        <xsl:attribute name="eas:schemeId" select="'common.dc.format'"/>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="arch-identifier">
        <xsl:if test="dc:identifier | emd-old:persistentIdentifier | archisNr">
            <xsl:element name="emd:identifier">
                <xsl:for-each select="dc:identifier/value">
                    <xsl:element name="dc:identifier">
                        <xsl:if test="not(@scheme='')">
                            <xsl:attribute name="eas:scheme" select="@scheme"/>
                        </xsl:if>
                        <xsl:if test="starts-with(string/text(), 'twips.dans.knaw.nl')">
                            <xsl:attribute name="eas:scheme" select="'AIP_ID'"/>
                        </xsl:if>
                        <xsl:value-of select="string/text()"/>
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
                <xsl:for-each select="archisNr/value">
                    <xsl:element name="dc:identifier">
                        <xsl:attribute name="eas:scheme" select="'Archis_onderzoek_m_nr'"/>
                        <xsl:attribute name="eas:schemeId" select="'archaeology.dc.identifier'"/>
                        <xsl:attribute name="eas:identification-system"
                            select="'http://archis2.archis.nl'"/>
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="arch-language">
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

    <xsl:template name="arch-other">        
        
            <xsl:element name="emd:other">
                
                <xsl:for-each select="dc:remarks/value">
                    <xsl:element name="eas:remark">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:element name="eas:application-specific">
                    <xsl:element name="eas:metadataformat">
                        <xsl:value-of select="'ARCHAEOLOGY'"/>
                    </xsl:element>
                </xsl:element>
                
                <xsl:element name="eas:etc">
                    <xsl:call-template name="getConversionProperties"/>
                </xsl:element>    
                
            </xsl:element>
       
    </xsl:template>

    <xsl:template name="arch-publisher">
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

    <xsl:template name="arch-relation">
        <xsl:if test="dc:relation">
            <xsl:element name="emd:relation">                
                
                <xsl:for-each select="dc:relation/value[@scheme='']">
                    <xsl:element name="eas:relation">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='conformsTo']">
                    <xsl:element name="eas:conformsTo">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isVersionOf']">
                    <xsl:element name="eas:isVersionOf">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='hasVersion']">
                    <xsl:element name="eas:hasVersion">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isReplacedBy']">
                    <xsl:element name="eas:isReplacedBy">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='replaces']">
                    <xsl:element name="eas:replaces">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isRequiredBy']">
                    <xsl:element name="eas:isRequiredBy">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='requires']">
                    <xsl:element name="eas:requires">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isPartOf']">
                    <xsl:element name="eas:isPartOf">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='hasPart']">
                    <xsl:element name="eas:hasPart">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isReferencedBy']">
                    <xsl:element name="eas:isReferencedBy">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='references']">
                    <xsl:element name="eas:references">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='isFormatOf']">
                    <xsl:element name="eas:isFormatOf">
                        <xsl:element name="eas:subject-title">
                            <xsl:value-of select="label/text()"/>
                        </xsl:element>
                        <xsl:element name="eas:subject-link">
                            <xsl:value-of select="normalize-space(url/text())"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
                
                <xsl:for-each select="dc:relation/value[@scheme='hasFormat']">
                    <xsl:element name="eas:hasFormat">
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

    <xsl:template name="arch-rights">
        <xsl:if test="dc:rights | dcterms:accessRights | acceptLicense">
            <xsl:element name="emd:rights">
                <xsl:for-each select="dc:rights/value">
                    <xsl:element name="dc:rights">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
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

    <xsl:template name="arch-source">
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

    <xsl:template name="arch-subject">
        <xsl:if test="dc:subject | abr:subject">
            <xsl:element name="emd:subject">
                <xsl:for-each select="dc:subject/value">
                    <xsl:element name="dc:subject">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="abr:subject/value">
                    <xsl:element name="dc:subject">
                        <xsl:attribute name="eas:scheme" select="'ABR'"/>
                        <xsl:attribute name="eas:schemeId" select="'archaeology.dc.subject'"></xsl:attribute>
                        <xsl:value-of select="optionValue/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="arch-title">
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

    <xsl:template name="arch-type">
        <xsl:if test="dc:type | dcterms:DCMIType">
            <xsl:element name="emd:type">
                <xsl:for-each select="dc:type/value">
                    <xsl:element name="dc:type">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="dcterms:DCMIType/value">
                    <xsl:element name="dc:type">
                        <xsl:attribute name="eas:scheme" select="'DCMI'"/>
                        <xsl:attribute name="eas:schemeId" select="'common.dc.type'"></xsl:attribute>
                        <xsl:value-of select="optionLabel/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>
