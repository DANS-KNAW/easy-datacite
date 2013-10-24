<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      xmlns:dai="info:eu-repo/dai"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/" 
      xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      exclude-result-prefixes="xs dai dc dcterms eas emd"
      version="2.0">
    
    <!-- version 2013-01-11T12:41 -->
    <!-- ==================================================== -->
    <xsl:output encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="yes"/>
    <!-- ==================================================== -->
    
    <!-- ==================================================== -->
    <xsl:variable name="date-encoding-attribute-value" select="'w3cdtf'"/>
    <xsl:variable name="pid-resolver" select="'http://www.persistent-identifier.nl/?identifier='"/>
    <xsl:variable name="abr-type" select="document('http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd')"/>
    <xsl:variable name="audience" select="document('http://easy.dans.knaw.nl/schemas/property-list/2012/11/audience.xml')"/> 
    <!-- ==================================================== -->
    
    <xsl:template match="/">
        <xsl:call-template name="mods-root"/>
    </xsl:template>
    
    <!-- ==================================================== -->
    <xsl:template name="mods-root">
        <xsl:element name="mods:mods">
            <!-- cosmetic -->
            <xsl:if test="//eas:subject-link">
                <xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
            </xsl:if>
            <xsl:attribute name="xsi:schemaLocation" select="'http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd'"/>
            <xsl:attribute name="version" select="3.4"/>
            <xsl:apply-templates select="emd:easymetadata"/>
            
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <xsl:template match="emd:easymetadata">
        <!-- mods:titleInfo -->
        <xsl:apply-templates select="emd:title/dc:title"/>
        <xsl:apply-templates select="emd:title/dcterms:alternative"/>
        <!-- mods:name -->
        <xsl:apply-templates select="emd:creator/dc:creator"/>
        <xsl:apply-templates select="emd:creator/eas:creator" mode="name"/>
        <xsl:apply-templates select="emd:contributor/dc:contributor"/>
        <xsl:apply-templates select="emd:contributor/eas:contributor" mode="name"/>
        <xsl:apply-templates select="emd:rights/dcterms:rightsHolder"/>
        <!-- mods:typeOfResource -->
        <xsl:call-template name="mods-type-of-resource"/>
        <!-- mods:genre -->
        <xsl:call-template name="mods-genre"/>
        <!-- mods:originInfo | a wrapper element -->
        <xsl:element name="mods:originInfo">
            <xsl:apply-templates select="emd:publisher/dc:publisher"/>
            <xsl:apply-templates select="emd:date"/>
            <xsl:element name="mods:issuance">
                <xsl:value-of select="'monographic'"/>
            </xsl:element>
        </xsl:element>
        <!-- mods:language -->
        <xsl:apply-templates select="emd:language/dc:language"/>
        <!-- mods:physicalDescription | a wrapper element -->
        <xsl:element name="mods:physicalDescription">
            <xsl:apply-templates select="emd:type/dc:type"/>
            <xsl:apply-templates select="emd:format"/>
            <xsl:apply-templates select="emd:other/eas:remark"/>
        </xsl:element>
        <!-- mods:abstract -->
        <xsl:apply-templates select="emd:description/dc:description | emd:description/dcterms:abstract"/>
        <!-- mods:tableOfContents -->
        <xsl:apply-templates select="emd:description/dcterms:tableOfContents"/>
        <!-- mods:targetAudience -->
        <!-- mods:note -->
        <!-- mods:subject -->
        <xsl:call-template name="mods-subject"/>
        <!-- mods:classification -->
        <!-- mods:relatedItem -->
        <xsl:apply-templates select="emd:relation"/>
        <!-- mods:identifier -->
        <xsl:apply-templates select="emd:identifier/dc:identifier"/>
        <!-- mods:location -->
        <xsl:call-template name="mods-location"/>
        <!-- accessCondition -->
        <xsl:call-template name="mods-accessCondition"/>
        <!-- mods:extension -->
        <xsl:call-template name="mods-dai-extension"/>
        <!-- mods:recordInfo -->
        <xsl:call-template name="mods-recordInfo"/>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:subject -->
    <!-- ==================================================== -->
    <xsl:template name="mods-subject">
        <!-- mods:subject/mods:topic -->
        <xsl:apply-templates select="emd:audience/dcterms:audience"/>
        
        <xsl:if test="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]">
            <xsl:element name="mods:subject">
                <xsl:apply-templates select="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]"/>
            </xsl:element>
        </xsl:if>
        
        <xsl:apply-templates select="emd:subject/dc:subject[@eas:schemeId = 'archaeology.dc.subject' and . != '']"/>
        
        <!-- mods:subject/mods:cartographics -->
        <xsl:if test="emd:coverage/dcterms:spatial">
            <xsl:element name="mods:subject">
                <xsl:apply-templates select="emd:coverage/dcterms:spatial"/>
            </xsl:element>
        </xsl:if>
        
        <xsl:apply-templates select="emd:coverage/eas:spatial"/>
        
        <!-- mods:subject/mods:temporal -->
        <xsl:if test="emd:coverage/dcterms:temporal[not(@eas:schemeId = 'archaeology.dcterms.temporal')]">
            <xsl:element name="mods:subject">
                <xsl:apply-templates select="emd:coverage/dcterms:temporal[not(@eas:schemeId = 'archaeology.dcterms.temporal')]"/>
            </xsl:element>
        </xsl:if>
        
        <xsl:apply-templates select="emd:coverage/dcterms:temporal[@eas:schemeId = 'archaeology.dcterms.temporal' and . != '']"/>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:typeOfResource -->
    <!-- ==================================================== -->
    <xsl:template name="mods-type-of-resource">
        <xsl:element name="mods:typeOfResource">
            <xsl:attribute name="collection" select="'yes'"/>
            <xsl:value-of select="'mixed material'"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:genre -->
    <!-- ==================================================== -->
    <xsl:template name="mods-genre">
        <xsl:element name="mods:genre">
            <xsl:attribute name="xml:lang" select="'en'"/>
            <xsl:value-of select="'scientific research data'"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:location -->
    <!-- ==================================================== -->
    <xsl:template name="mods-location">
        <xsl:if test="emd:identifier/dc:identifier[@eas:scheme='PID'] != ''">
            <xsl:element name="mods:location">
                <xsl:element name="mods:url">
                    <xsl:attribute name="displayLabel" select="emd:title/dc:title[1]"/>
                    <xsl:attribute name="access" select="'object in context'"/>
                    <xsl:value-of select="concat($pid-resolver, emd:identifier/dc:identifier[@eas:scheme='PID'])"/>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:accessCondition -->
    <!-- ==================================================== -->
    <xsl:template name="mods-accessCondition">
        <xsl:variable name="access-rights" select="emd:rights/dcterms:accessRights[contains(@eas:schemeId, '.dcterms.accessrights')]"/>
        <xsl:if test="$access-rights">   
            <xsl:variable name="access-display-label">
                <xsl:choose>
                    <xsl:when test="$access-rights='OPEN_ACCESS'">
                        <xsl:value-of select="'Unrestricted access for all registered EASY users.'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights='GROUP_ACCESS'">
                        <xsl:value-of select="'Access restricted to registered members of a group.'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights='REQUEST_PERMISSION'">
                        <xsl:value-of select="'Registered EASY users, but only after depositor permission is granted.'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'The data are not available via Easy (they are either accessible in another way or elsewhere).'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:element name="mods:accessCondition">
                <xsl:attribute name="displayLabel" select="$access-display-label"/>
                <xsl:value-of select="$access-rights"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:extension dai -->
    <!-- ==================================================== -->
    <xsl:template name="mods-dai-extension">
        <xsl:if test="emd:creator/eas:creator/eas:entityId[@eas:scheme='DAI'] != ''
            or emd:contributor/eas:contributor/eas:entityId[@eas:scheme='DAI'] != ''">
            <xsl:call-template name="daiList"/>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- daiList -->
    <xsl:template name="daiList">
        <xsl:element name="mods:extension">
            <xsl:element name="dai:daiList">
                <xsl:attribute name="xsi:schemaLocation" select="'info:eu-repo/dai http://purl.org/REP/standards/dai-extension.xsd'"/>
                <xsl:apply-templates select="emd:creator/eas:creator" mode="daiList"/>
                <xsl:apply-templates select="emd:contributor/eas:contributor" mode="daiList"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- mods:extension eas:creator daiList -->
    <xsl:template match="emd:creator/eas:creator" mode="daiList">
        <xsl:call-template name="daiListIDref">
            <xsl:with-param name="marcrelator" select="'cre'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- mods:extension eas:contributor daiList -->
    <xsl:template match="emd:contributor/eas:contributor" mode="daiList">
        <xsl:call-template name="daiListIDref">
            <xsl:with-param name="marcrelator" select="'ctb'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template name="daiListIDref">
        <xsl:param name="marcrelator"/>
        <xsl:variable name="count">
            <xsl:number/>
        </xsl:variable>
        <xsl:if test="eas:entityId/@eas:scheme = 'DAI' and eas:entityId/text() != ''">
            <xsl:element name="dai:identifier">
                <xsl:attribute name="authority" select="'info:eu-repo/dai/nl'"/>
                <xsl:attribute name="IDref" select="concat($marcrelator, $count)"/>
                <xsl:value-of select="eas:entityId"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- mods:recordInfo -->
    <!-- ==================================================== -->
    <xsl:template name="mods-recordInfo">
        <xsl:variable name="utc-timestamp" select="current-dateTime()"/>
        <xsl:variable name="gmt-timestamp" select="adjust-dateTime-to-timezone($utc-timestamp, xs:dayTimeDuration('PT0H'))"/>
        <xsl:element name="mods:recordInfo">
            <xsl:element name="mods:recordContentSource">
                <xsl:value-of select="'DANS'"/>
            </xsl:element>
            <xsl:element name="mods:recordCreationDate">
                <xsl:attribute name="encoding" select="'w3cdtf'"/>
                <xsl:value-of select="$gmt-timestamp"/>
            </xsl:element>
            <xsl:element name="mods:recordOrigin">
                <xsl:value-of select="'machine generated'"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- dc:title to mods:titleInfo -->
    <xsl:template match="emd:title/dc:title">
        <xsl:element name="mods:titleInfo">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:element name="mods:title">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- dcterms:alternative to mods:titleInfo -->
    <xsl:template match="emd:title/dcterms:alternative">
        <xsl:element name="mods:titleInfo">
            <xsl:attribute name="type" select="'alternative'"/>
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:element name="mods:title">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- dc:creator to mods:name -->
    <xsl:template match="emd:creator/dc:creator">
        <xsl:call-template name="simpleNameToModsName">
            <xsl:with-param name="textrelator" select="'creator'"/>
            <xsl:with-param name="marcrelator" select="'cre'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- dc:contributor to mods:name -->
    <xsl:template match="emd:contributor/dc:contributor">
        <xsl:call-template name="simpleNameToModsName">
            <xsl:with-param name="textrelator" select="'contributor'"/>
            <xsl:with-param name="marcrelator" select="'ctb'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- dcterms:rightsHolder to mods:name -->
    <xsl:template match="emd:rights/dcterms:rightsHolder">
        <xsl:call-template name="simpleNameToModsName">
            <xsl:with-param name="textrelator" select="'rights holder'"/>
            <xsl:with-param name="marcrelator" select="'cph'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- eas:creator to mods:name -->
    <xsl:template match="emd:creator/eas:creator" mode="name">
        <xsl:call-template name="authorToMods">
            <xsl:with-param name="textrelator" select="'creator'"/>
            <xsl:with-param name="marcrelator" select="'cre'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- eas:contributor to mods:name -->
    <xsl:template match="emd:contributor/eas:contributor" mode="name">
        <xsl:call-template name="authorToMods">
            <xsl:with-param name="textrelator" select="'contributor'"/>
            <xsl:with-param name="marcrelator" select="'ctb'"/>
        </xsl:call-template>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:publisher to mods:originInfo/publisher -->
    <xsl:template match="emd:publisher/dc:publisher">
        <xsl:element name="mods:publisher">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- emd:date/x to mods:originInfo/dateX -->
    <xsl:template match="emd:date">
        <!-- first: submission date for NARCIS Harvest -->
        <xsl:for-each select="eas:dateSubmitted[1]">
            <xsl:element name="mods:dateIssued">
                <xsl:attribute name="keyDate" select="'yes'"/>
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:issued | eas:issued | dcterms:dateSubmitted | eas:dateSubmitted[position()>1]">
            <xsl:element name="mods:dateIssued">
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:created | eas:created">
            <xsl:element name="mods:dateCreated">
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:valid | eas:valid">
            <xsl:element name="mods:dateValid">
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:modified | eas:modified">
            <xsl:element name="mods:dateModified">
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:dateCopyrighted | eas:dateCopyrighted">
            <xsl:element name="mods:copyrightDate">
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:available | eas:available">
            <xsl:element name="mods:dateOther">
                <xsl:attribute name="type" select="'available'"/>
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:dateAccepted | eas:dateAccepted">
            <xsl:element name="mods:dateOther">
                <xsl:attribute name="type" select="'date accepted'"/>
                <xsl:call-template name="w3cdtfEncoding"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    <!-- mods:originInfo:dateType -->
    <xsl:template name="w3cdtfEncoding">
        <!-- xalan chokes on regex -->
        <xsl:choose>
            <xsl:when test="matches(., '\d\d\d\d-\d\d-\d\d*')">
                <xsl:attribute name="encoding" select="$date-encoding-attribute-value"/>
                <xsl:choose>
                    <xsl:when test="@eas:format = 'YEAR'">
                        <xsl:value-of select="substring(., 1, 4)"/>
                    </xsl:when>
                    <xsl:when test="@eas:format = 'MONTH'">
                        <xsl:value-of select="substring(., 1, 7)"/>
                    </xsl:when>
                    <xsl:when test="@eas:format = 'DAY'">
                        <xsl:value-of select="substring(., 1, 10)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:language to mods:language -->
    <xsl:template match="/emd:easymetadata/emd:language/dc:language">
        <xsl:element name="mods:language">
            <xsl:element name="mods:languageTerm">
                <xsl:choose>
                    <xsl:when test="@eas:schemeId = 'common.dc.language'">
                        <xsl:attribute name="type" select="'code'"/>
                        <xsl:attribute name="authority" select="'rfc3066'"/>
                        <xsl:choose>
                            <xsl:when test=". = 'dut/nld'">
                                <xsl:value-of select="'nl'"/>
                            </xsl:when>
                            <xsl:when test=". = 'eng'">
                                <xsl:value-of select="'en'"/>
                            </xsl:when>
                            <xsl:when test=". = 'ger/deu'">
                                <xsl:value-of select="'de'"/>
                            </xsl:when>
                            <xsl:when test=". = 'fre/fra'">
                                <xsl:value-of select="'fr'"/>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="type" select="'text'"/>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:type to mods:physicalDescription/mods:form -->
    <xsl:template match="emd:type/dc:type">
        <xsl:element name="mods:form">
            <xsl:if test="@eas:schemeId = 'common.dc.type'">
                <xsl:attribute name="authority" select="'http://purl.org/dc/terms/DCMIType'"/>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- emd:format to mods:internetMediaType or mods:extent -->
    <xsl:template match="emd:format">
        <xsl:for-each select="dc:format | dcterms:medium">
            <xsl:element name="mods:internetMediaType">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="dcterms:extent">
            <xsl:element name="mods:extent">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- emd:other/eas:remark to mods:note -->
    <xsl:template match="emd:other/eas:remark">
        <xsl:element name="mods:note">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:description and dcterms:abstract to mods:abstract -->
    <xsl:template match="emd:description/dc:description | emd:description/dcterms:abstract">
        <xsl:element name="mods:abstract">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dcterms:tableOfContents to mods:tableOfContents -->
    <xsl:template match="emd:description/dcterms:tableOfContents">
        <xsl:element name="mods:tableOfContents">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dcterms:audience to mods:subject/mods:topic -->
    <xsl:template match="emd:audience/dcterms:audience">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:topic">
                <xsl:choose>
                    <xsl:when test="@eas:schemeId = 'custom.disciplines' and text() != ''">
                        <xsl:attribute name="xml:lang" select="'en'"/>
                        <xsl:variable name="disciplineCode" select="."/>
                        <xsl:value-of select="$audience/properties/entry[@key = $disciplineCode]/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:subject to mods:subject/mods:topic -->
    <xsl:template match="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]">
        <xsl:element name="mods:topic">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="emd:subject/dc:subject[@eas:schemeId = 'archaeology.dc.subject' and . != '']">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:topic">
                <xsl:attribute name=" authority" select="'ABR-complex'"/>
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="mods:topic">
                <xsl:attribute name="xml:lang" select="'nl'"/>
                <xsl:value-of>
                    <xsl:call-template name="abr-complex-to-string"/>
                </xsl:value-of>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="emd:coverage/dcterms:spatial">
        <xsl:element name="mods:geographic">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- eas:spatial to mods:subject/mods:geographic -->
    <xsl:template match="emd:coverage/eas:spatial">
        <xsl:apply-templates select="eas:point"/>
        <xsl:apply-templates select="eas:box"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:cartographics">
                <xsl:element name="mods:coordinates">
                    <xsl:value-of select="concat('x=', eas:x, '; y=', eas:y, '; units=', @eas:scheme)"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point[@eas:scheme = 'RD']">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:cartographics">
                <xsl:element name="mods:projection">
                    <xsl:value-of select="'Dutch National Grid'"/>
                </xsl:element>
                <xsl:element name="mods:coordinates">
                    <xsl:value-of select="concat('x=', eas:x, '; y=', eas:y, '; units=m;')"/>
                </xsl:element>
            </xsl:element>
            <!-- same point in WGS84 -->
            <xsl:if test="string(number(eas:x)) != 'NaN' and string(number(eas:y)) != 'NaN'">
                <xsl:element name="mods:cartographics">
                    <xsl:element name="mods:projection">
                        <xsl:value-of select="'http://www.opengis.net/def/crs/EPSG/0/4326'"/>
                    </xsl:element>
                    <xsl:element name="mods:coordinates">
                        <xsl:call-template name="rd-to-lat-long">
                            <xsl:with-param name="x" select="eas:x"/>
                            <xsl:with-param name="y" select="eas:y"/>
                        </xsl:call-template>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:cartographics">
                <xsl:element name="mods:coordinates">
                    <xsl:value-of select="concat('north=', eas:north, '; east=', eas:east, '; south=', eas:south, '; west=', eas:west, '; units=', @eas:scheme)"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box[@eas:scheme = 'RD']">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:cartographics">
                <xsl:element name="mods:projection">
                    <xsl:value-of select="'Dutch National Grid'"/>
                </xsl:element>
                <xsl:element name="mods:coordinates">
                    <xsl:value-of select="concat('north=', eas:north, '; east=', eas:east, '; south=', eas:south, '; west=', eas:west, '; units=m;')"/>
                </xsl:element>
            </xsl:element>
            <!-- same box in WGS84 -->
            <xsl:if test="string(number(eas:north)) != 'NaN' and string(number(eas:east)) != 'NaN' and string(number(eas:south)) != 'NaN' and string(number(eas:west)) != 'NaN'">
                <xsl:element name="mods:cartographics">
                    <xsl:element name="mods:projection">
                        <xsl:value-of select="'http://www.opengis.net/def/crs/EPSG/0/4326'"/>
                    </xsl:element>
                    <xsl:element name="mods:coordinates">
                        <xsl:call-template name="box-converter">
                            <xsl:with-param name="north" select="eas:north"/>
                            <xsl:with-param name="east" select="eas:east"/>
                            <xsl:with-param name="south" select="eas:south"/>
                            <xsl:with-param name="west" select="eas:west"/>
                        </xsl:call-template>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dcterms:temporal to mods:subject/mods:temporal -->
    <xsl:template match="emd:coverage/dcterms:temporal[not(@eas:schemeId = 'archaeology.dcterms.temporal')]">
        <xsl:element name="mods:temporal">
            <xsl:call-template name="copy-lang-attribute"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="emd:coverage/dcterms:temporal[@eas:schemeId = 'archaeology.dcterms.temporal' and . != '']">
        <xsl:element name="mods:subject">
            <xsl:element name="mods:temporal">
                <xsl:attribute name=" authority" select="'ABR-periode'"/>
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="mods:topic">
                <xsl:attribute name="xml:lang" select="'nl'"/>
                <xsl:value-of>
                    <xsl:call-template name="abr-periode-to-string"/>
                </xsl:value-of>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- mods:relatedItem -->
    <xsl:template match="emd:relation">
        <xsl:apply-templates select="dc:relation"/>
        <xsl:apply-templates select="dcterms:*"/>
        <xsl:apply-templates select="eas:*"/>
    </xsl:template>
    <!--  -->
    <xsl:template match="dc:relation | dcterms:* | eas:*">
        <xsl:element name="mods:relatedItem">
            <xsl:if test="eas:subject-link != ''">
                <xsl:attribute name="xlink:href" select="eas:subject-link"/>
            </xsl:if>
            <xsl:if test="local-name() = 'replaces'">
                <xsl:attribute name="type" select="'preceding'"/>
            </xsl:if>
            <xsl:if test="local-name() = 'isReplacedBy'">
                <xsl:attribute name="type" select="'succeeding'"/>
            </xsl:if>
            <xsl:if test="local-name() = 'isVersionOf'">
                <xsl:attribute name="type" select="'original'"/>
            </xsl:if>
            <xsl:if test="local-name() = 'isPartOf'">
                <xsl:attribute name="type" select="'host'"/>
            </xsl:if>
            <xsl:if test="local-name() = 'hasPart'">
                <xsl:attribute name="type" select="'constituent'"/>
            </xsl:if>
            <xsl:if test="local-name() = 'isReferencedBy'">
                <xsl:attribute name="type" select="'isReferencedBy'"/>
            </xsl:if>
            <xsl:element name="mods:titleInfo">
                <xsl:element name="mods:title">
                    <xsl:choose>
                        <xsl:when test="string(namespace-uri()) = 'http://easy.dans.knaw.nl/easy/easymetadata/eas/'">
                            <xsl:value-of select="eas:subject-title"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:identifier to mods:identifier -->
    <xsl:template match="emd:identifier/dc:identifier">
        <xsl:element name="mods:identifier">
            <xsl:if test="@eas:scheme != ''">
                <xsl:choose>
                    <xsl:when test="@eas:scheme='PID'">
                        <xsl:attribute name="type" select="'URN:NBN:NL'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="type" select="@eas:scheme"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!-- author to mods:name -->
    <!-- ==================================================== -->
    <xsl:template name="authorToMods">
        <xsl:param name="textrelator"/>
        <xsl:param name="marcrelator"/>
        <xsl:variable name="count">
            <xsl:number/>
        </xsl:variable>
        <xsl:element name="mods:name">
            <!-- is there a DAI? > ID for daiList -->
            <xsl:if test="eas:entityId/@eas:scheme = 'DAI' and eas:entityId/text() != ''">
                <xsl:attribute name="ID" select="concat($marcrelator, $count)"/>
            </xsl:if>
            <!-- type of name -->
            <xsl:variable name="type">
                <xsl:choose>
                    <xsl:when test="eas:surname = ''">
                        <xsl:value-of select="'corporate'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'personal'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="type" select="$type"/>
            <!-- namePart termsOfAddress -->
            <xsl:apply-templates select="eas:title"/>
            <!-- namePart family -->
            <xsl:if test="eas:surname != ''">
                <xsl:variable name="familyName">
                    <xsl:choose>
                        <xsl:when test="eas:prefix != ''">
                            <xsl:value-of select="concat(eas:surname, ', ', eas:prefix)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="eas:surname"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="mods:namePart">
                    <xsl:attribute name="type" select="'family'"/>
                    <xsl:value-of select="$familyName"/>
                </xsl:element>
            </xsl:if>
            <!-- namePart if there is only an organization -->
            <xsl:if test="eas:surname = ''">
                <xsl:element name="mods:namePart">
                    <xsl:value-of select="eas:organization"/>
                </xsl:element>
            </xsl:if>
            <!-- namePart given -->
            <xsl:apply-templates select="eas:initials"/>
            <!-- displayForm -->
            <xsl:variable name="titles">
                <xsl:if test="eas:title and eas:title != ''">
                    <xsl:value-of select="concat(' ', eas:title)"/>
                </xsl:if>
            </xsl:variable>
            <xsl:variable name="initials">
                <xsl:if test="eas:initials and eas:initials != ''">
                    <xsl:value-of select="concat(' ', eas:initials)"/>
                </xsl:if>
            </xsl:variable>
            <xsl:variable name="prefix">
                <xsl:if test="eas:prefix and eas:prefix != ''">
                    <xsl:value-of select="concat(' ', eas:prefix)"/>
                </xsl:if>
            </xsl:variable>
            <xsl:variable name="organization">
                <xsl:if test="eas:organization and eas:organization != ''">
                    <xsl:value-of select="concat(' (', eas:organization, ')')"/>
                </xsl:if>
            </xsl:variable>
            <xsl:element name="mods:displayForm">
                <xsl:choose>
                    <xsl:when test="eas:surname = ''">
                        <xsl:value-of select="eas:organization"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat(eas:surname, ',', $titles, $initials, $prefix, $organization)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
            <!-- affiliation -->     
            <xsl:apply-templates select="eas:organization"/>
            <!-- roles -->
            <xsl:call-template name="modsNameRoles">
                <xsl:with-param name="textrelator" select="$textrelator"/>
                <xsl:with-param name="marcrelator" select="$marcrelator"/>
            </xsl:call-template>
        </xsl:element>  
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:title[not(.='')]">
        <xsl:element name="mods:namePart">
            <xsl:attribute name="type" select="'termsOfAddress'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:initials[not(.='')]">
        <xsl:element name="mods:namePart">
            <xsl:attribute name="type" select="'given'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:organization">
        <xsl:if test=". != '' and ../eas:surname != ''">
            <xsl:element name="mods:affiliation">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- simple name to mods name -->
    <!-- ==================================================== -->
    <xsl:template name="simpleNameToModsName">
        <xsl:param name="textrelator"/>
        <xsl:param name="marcrelator"/>
        <xsl:element name="mods:name">
            <xsl:element name="mods:namePart">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:element name="mods:displayForm">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:call-template name="modsNameRoles">
                <xsl:with-param name="textrelator" select="$textrelator"/>
                <xsl:with-param name="marcrelator" select="$marcrelator"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- relators to mods:roleTerm -->
    <!-- ==================================================== -->
    <xsl:template name="modsNameRoles">
        <xsl:param name="textrelator"/>
        <xsl:param name="marcrelator"/>
        <xsl:element name="mods:role">
            <xsl:element name="mods:roleTerm">
                <xsl:attribute name="type" select="'text'"/>
                <xsl:value-of select="$textrelator"/>
            </xsl:element>
            <xsl:element name="mods:roleTerm">
                <xsl:attribute name="type" select="'code'"/>
                <xsl:attribute name="authority" select="'marcrelator'"/>
                <xsl:value-of select="$marcrelator"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    <!-- Copy lang attribute -->
    <!-- ==================================================== -->
    <xsl:template name="copy-lang-attribute">
        <xsl:if test="@xml:lang">
            <xsl:attribute name="xml:lang" select="@xml:lang"/>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- ABRComplex to string                                                                -->
    <!-- =================================================================================== -->
    <xsl:template name="abr-complex-to-string">
        <xsl:variable name="code-string" select="string()"/>
        <xsl:variable name="str" select="normalize-space($abr-type/xs:schema/xs:simpleType[@name='complex']/xs:restriction/xs:enumeration[@value=$code-string]/xs:annotation/xs:documentation/text())"/>
        <xsl:value-of select="$str"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- ABRPeriode to string                                                                -->
    <!-- =================================================================================== -->
    <xsl:template name="abr-periode-to-string">
        <!-- there may be a typo in the original -->
        <xsl:variable name="code-string">
            <xsl:choose>
                <xsl:when test="string() = 'PALEOB'">
                    <xsl:value-of select="'PALEOLB'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="string()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="str" select="normalize-space($abr-type/xs:schema/xs:simpleType[@name='periode']/xs:restriction/xs:enumeration[@value=$code-string]/xs:annotation/xs:documentation/text())"/>
        <xsl:value-of select="$str"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD north east south west to lat lon lat lon                                         -->
    <!-- =================================================================================== -->
    <xsl:template name="box-converter">
        <xsl:param name="north"/>
        <xsl:param name="east"/>
        <xsl:param name="south"/>
        <xsl:param name="west"/>
        <xsl:variable name="lat-n">
            <xsl:call-template name="lat-converter">
                <xsl:with-param name="y" select="$north"/>
                <xsl:with-param name="east" select="$east"/>
                <xsl:with-param name="west" select="$west"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="lon-e">
            <xsl:call-template name="lon-converter">
                <xsl:with-param name="x" select="$east"/>
                <xsl:with-param name="north" select="$north"/>
                <xsl:with-param name="south" select="$south"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="lat-s">
            <xsl:call-template name="lat-converter">
                <xsl:with-param name="y" select="$south"/>
                <xsl:with-param name="east" select="$east"/>
                <xsl:with-param name="west" select="$west"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="lon-w">
            <xsl:call-template name="lon-converter">
                <xsl:with-param name="x" select="$west"/>
                <xsl:with-param name="north" select="$north"/>
                <xsl:with-param name="south" select="$south"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat($lat-n, ' ', $lon-e, ' ', $lat-s, ' ', $lon-w)"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD y, east, west to latitude converter                                              -->
    <!-- =================================================================================== -->
    <xsl:template name="lat-converter">
        <xsl:param name="y" as="xs:decimal"/>
        <xsl:param name="east" as="xs:decimal"/>
        <xsl:param name="west" as="xs:decimal"/>
        <xsl:variable name="x" select="($east + $west) div 2"/>
        <xsl:variable name="latlon">
            <xsl:call-template name="rd-to-lat-long">
                <xsl:with-param name="x" select="$x"/>
                <xsl:with-param name="y" select="$y"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="substring-before($latlon, ' ')"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD x, north, south to longitude converter                                           -->
    <!-- =================================================================================== -->
    <xsl:template name="lon-converter">
        <xsl:param name="x" as="xs:decimal"/>
        <xsl:param name="north" as="xs:decimal"/>
        <xsl:param name="south" as="xs:decimal"/>
        <xsl:variable name="y" select="($north + $south) div 2"/>
        <xsl:variable name="latlon">
            <xsl:call-template name="rd-to-lat-long">
                <xsl:with-param name="x" select="$x"/>
                <xsl:with-param name="y" select="$y"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="substring-after($latlon, ' ')"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD box to center point latitude longitude                                           -->
    <!-- =================================================================================== -->
    <xsl:template name="rd-box-to-lat-long">
        <xsl:param name="north" as="xs:decimal"/>
        <xsl:param name="east" as="xs:decimal"/>
        <xsl:param name="south" as="xs:decimal"/>
        <xsl:param name="west" as="xs:decimal"/>
        <xsl:variable name="x" select="($east + $west) div 2"/>
        <xsl:variable name="y" select="($north + $south) div 2"/>
        <xsl:value-of>
            <xsl:call-template name="rd-to-lat-long">
                <xsl:with-param name="x" select="$x"/>
                <xsl:with-param name="y" select="$y"/>
            </xsl:call-template>
        </xsl:value-of>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD x, y to WGS84 latitude longitude. See: http://www.regiolab-delft.nl/?q=node/36   -->
    <!-- =================================================================================== -->
    <xsl:template name="rd-to-lat-long">
        <xsl:param name="x" as="xs:decimal"/>
        <xsl:param name="y" as="xs:decimal"/>
        <xsl:variable name="p" select="($x - 155000.00) div 100000"/>
        <xsl:variable name="q" select="($y - 463000.00) div 100000"/>
        
        <xsl:variable name="df"
            select="(($q*3235.65389)+($p*$p*-32.58297)+($q*$q*-0.24750)+($p*$p*$q*-0.84978)+($q*$q*$q*-0.06550)+($p*$p*$q*$q*-0.01709)+($p*-0.00738)+($p*$p*$p*$p*0.00530)+($p*$p*$q*$q*$q*-0.00039)+($p*$p*$p*$p*$q*0.00033)+($p*$q*-0.00012)) div 3600"/>
        <xsl:variable name="dl"
            select="(($p*5260.52916)+($p*$q*105.94684)+($p*$q*$q*2.45656)+($p*$p*$p*-0.81885)+($p*$q*$q*$q*0.05594)+($p*$p*$p*$q*-0.05607)+($q*0.01199)+($p*$p*$p*$q*$q*-0.00256)+($p*$q*$q*$q*$q*0.00128)+($q*$q*0.00022)+($p*$p*-0.00022)+($p*$p*$p*$p*$p*0.00026)) div 3600"/>
        <xsl:variable name="lat" select="(round((52.15517440+$df)*100000000.00)) div 100000000.00"/>
        <xsl:variable name="lon" select="(round((5.387206210+$dl)*100000000.00)) div 100000000.00"/>
        <xsl:value-of select="concat($lat, ' ', $lon)"/>
    </xsl:template>

</xsl:stylesheet>
