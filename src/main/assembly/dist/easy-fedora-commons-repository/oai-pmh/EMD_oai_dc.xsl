<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
      xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
      xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      exclude-result-prefixes="xs dcterms emd eas"
      version="2.0">
    
    <!-- =================================================================================== -->
    <xsl:output encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="yes"/>
    <!-- =================================================================================== -->
    
    <xsl:variable name="abr-type" select="document('http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd')"/>
    <xsl:variable name="audience" select="document('http://easy.dans.knaw.nl/schemas/property-list/2012/11/audience.xml')"/>  
    
    <xsl:template match="/">
        <oai_dc:dc 
            xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
            xmlns:dc="http://purl.org/dc/elements/1.1/" 
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
            xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/dc.xsd">
            
            <xsl:apply-templates select="emd:easymetadata"/>
        </oai_dc:dc>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <xsl:template name="language-attr">
        <xsl:if test="@xml:lang and @xml:lang != ''">
            <xsl:attribute name="xml:lang" select="@xml:lang"/>
        </xsl:if>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <xsl:template match="emd:easymetadata">
        <xsl:apply-templates select="emd:title/*"/>
        <xsl:apply-templates select="emd:creator/*"/>
        <xsl:apply-templates select="emd:audience/*"/>
        <xsl:apply-templates select="emd:subject/*"/>
        <xsl:apply-templates select="emd:description/*"/>
        <xsl:apply-templates select="emd:publisher/*"/>
        <xsl:apply-templates select="emd:contributor/*"/>
        <xsl:apply-templates select="emd:date/*"/>
        <xsl:apply-templates select="emd:type/*"/>
        <xsl:apply-templates select="emd:format/*"/>
        <xsl:apply-templates select="emd:identifier/*"/>
        <xsl:apply-templates select="emd:source/*"/>
        <xsl:apply-templates select="emd:language/*"/>
        <xsl:apply-templates select="emd:relation/*"/>
        <xsl:apply-templates select="emd:coverage/*"/>
        <xsl:apply-templates select="emd:rights/*"/>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <xsl:template match="dc:title | dcterms:alternative">
        <dc:title>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:title>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <xsl:template match="dc:creator">
        <xsl:call-template name="creator">
            <xsl:with-param name="value" select="."/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="eas:creator">
        <xsl:call-template name="creator">
            <xsl:with-param name="value">
                <xsl:call-template name="author-to-string"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="creator">
        <xsl:param name="value"/>
        <dc:creator>
            <xsl:value-of select="$value"/>
        </dc:creator>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <xsl:template match="dcterms:audience">
        <xsl:choose>
            <xsl:when test="@eas:schemeId = 'custom.disciplines'">
                <xsl:call-template name="subject">
                    <xsl:with-param name="lang" select="'en'"/>
                    <xsl:with-param name="value">
                        <xsl:call-template name="audience-to-string"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="subject">
                    <xsl:with-param name="value" select="."/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="dc:subject">
        <dc:subject>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:subject>
    </xsl:template>
    
    <xsl:template match="dc:subject[@eas:schemeId = 'archaeology.dc.subject' and . != '']">
        <xsl:call-template name="subject">
            <xsl:with-param name="lang" select="'nl'"/>
            <xsl:with-param name="value">
                <xsl:call-template name="abr-complex-to-string"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="subject">
        <xsl:param name="lang"/>
        <xsl:param name="value"/>
        <dc:subject>
            <xsl:if test="$lang">
                <xsl:attribute name="xml:lang" select="$lang"/>
            </xsl:if>
            <xsl:value-of select="$value"/>
        </dc:subject>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:description | dcterms:abstract | dcterms:tableOfContents">
        <dc:description>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:description>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:publisher">
        <dc:publisher>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:publisher>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:contributor">
        <xsl:call-template name="contributor">
            <xsl:with-param name="value" select="."/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="eas:contributor">
        <xsl:call-template name="contributor">
            <xsl:with-param name="value">
                <xsl:call-template name="author-to-string"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="contributor">
        <xsl:param name="value"/>
        <dc:contributor>
            <xsl:value-of select="$value"/>
        </dc:contributor>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="*:date | *:created | *:valid | *:available | *:issued | *:modified | *:dateAccepted | *:dateCopyrighted | *:dateSubmitted">
        <dc:date>
            <xsl:call-template name="date-to-string"/>
        </dc:date>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:type">
        <dc:type>
            <xsl:value-of select="replace(., '_', ' ')"/>
        </dc:type>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:format | dcterms:extent | dcterms:medium">
        <dc:format>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:format>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:identifier">
        <dc:identifier>
            <xsl:call-template name="identifier-to-string"/>
        </dc:identifier>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:source">
        <dc:source>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:source>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:language">
        <dc:language>
            <xsl:call-template name="language-to-string"/>
        </dc:language>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:relation | dcterms:conformsTo | dcterms:isVersionOf | dcterms:hasVersion | dcterms:isReplacedBy | dcterms:replaces | dcterms:isRequiredBy | dcterms:requires | dcterms:isPartOf | dcterms:hasPart | dcterms:isReferencedBy | dcterms:references | dcterms:isFormatOf | dcterms:hasFormat">
        <dc:relation>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:relation>
    </xsl:template>
    
    <xsl:template match="eas:relation | eas:conformsTo | eas:isVersionOf | eas:hasVersion | eas:isReplacedBy | eas:replaces | eas:isRequiredBy | eas:requires | eas:isPartOf | eas:hasPart | eas:isReferencedBy | eas:references | eas:isFormatOf | eas:hasFormat">
        <dc:relation>
            <xsl:if test="eas:subject-title/@xml:lang != ''">
                <xsl:attribute name="xml:lang" select="eas:subject-title/@xml:lang"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="eas:subject-link !=''">
                    <xsl:value-of select="concat('title=', eas:subject-title, '; URI=', eas:subject-link)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="eas:subject-title"/>
                </xsl:otherwise>
            </xsl:choose>
        </dc:relation>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:coverage | dcterms:spatial | dcterms:temporal">
        <dc:coverage>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:coverage>
    </xsl:template>
    
    <xsl:template match="dcterms:temporal[@eas:schemeId = 'archaeology.dcterms.temporal']">
        <dc:coverage>
            <xsl:call-template name="abr-periode-to-string"/>
        </dc:coverage>
    </xsl:template>
    
    <xsl:template match="eas:spatial">
        <xsl:apply-templates select="eas:point"/>
        <xsl:apply-templates select="eas:box"/>
    </xsl:template>
    
    <xsl:template match="eas:point">
        <dc:coverage>
            <xsl:value-of select="concat('east=', eas:x, '; north=', eas:y, '; units=', @eas:scheme, ';')"/>
        </dc:coverage>
    </xsl:template>
    
    <xsl:template match="eas:point[@eas:scheme = 'RD']">
        <dc:coverage>
            <xsl:value-of select="concat('east=', eas:x, '; north=', eas:y, '; units=m; projection=Dutch National Grid;')"/>
        </dc:coverage>
        <!-- same point in WGS84 -->
        <xsl:if test="string(number(eas:x)) != 'NaN' and string(number(eas:y)) != 'NaN'">
            <xsl:variable name="latlon">
                <xsl:call-template name="rd-to-lat-long">
                    <xsl:with-param name="x" select="eas:x"/>
                    <xsl:with-param name="y" select="eas:y"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="source" select="concat('RD ', eas:x, ' ', eas:y)"/>
            <dc:coverage>
                <xsl:value-of select="concat('&#966;&#955;=', $latlon, '; projection=http://www.opengis.net/def/crs/EPSG/0/4326; units=decimal; source=', $source, ';')"/>
            </dc:coverage>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="eas:box">
        <dc:coverage>
            <xsl:value-of select="concat('northlimit=', eas:north, '; eastlimit=', eas:east, '; southlimit=', eas:south, '; westlimit=', eas:west, '; units=', @eas:scheme, ';')"/>
        </dc:coverage>
    </xsl:template>
    
    <xsl:template match="eas:box[@eas:scheme = 'RD']">
        <dc:coverage>
            <xsl:value-of select="concat('northlimit=', eas:north, '; eastlimit=', eas:east, '; southlimit=', eas:south, '; westlimit=', eas:west, '; units=m; projection=Dutch National Grid;')"/>
        </dc:coverage>
        <!-- center of box in WGS84 -->
        <xsl:if test="string(number(eas:north)) != 'NaN' and string(number(eas:east)) != 'NaN' and string(number(eas:south)) != 'NaN' and string(number(eas:west)) != 'NaN'">
            <xsl:variable name="latlon">
                <xsl:call-template name="rd-box-to-lat-long">
                    <xsl:with-param name="north" select="eas:north"/>
                    <xsl:with-param name="east" select="eas:east"/>
                    <xsl:with-param name="south" select="eas:south"/>
                    <xsl:with-param name="west" select="eas:west"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="height" select="number(eas:north) - number(eas:south)"/>
            <xsl:variable name="width" select="number(eas:east) - number(eas:west)"/>
            <xsl:variable name="position" select="concat('center of box ', $height, ' x ', $width, ' m')"/>
            <xsl:variable name="source" select="concat('RD ', eas:north, ' ', eas:east, ' ', eas:south, ' ', eas:west)"/>
            <dc:coverage>
                <xsl:value-of select="concat('&#966;&#955;=', $latlon, '; projection=http://www.opengis.net/def/crs/EPSG/0/4326; units=decimal; source=', $source, '; position=', $position, ';')"/>
            </dc:coverage>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="dc:rights | dcterms:rightsHolder | dcterms:accessRights | dcterms:license">
        <dc:rights>
            <xsl:call-template name="language-attr"/>
            <xsl:value-of select="."/>
        </dc:rights>
    </xsl:template>
    
    <xsl:template match="dcterms:license[. = 'accept']">
        <dc:rights>
            <xsl:value-of select="'http://www.dans.knaw.nl/en/content/dans-licence-agreement-deposited-data'"/>
        </dc:rights>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <!-- Author to string                                                                    -->
    <!-- =================================================================================== -->
    <xsl:template name="author-to-string">
        <!-- displayForm -->
        <xsl:variable name="titles">
            <xsl:if test="eas:title and eas:title != ''">
                <xsl:value-of select="concat(' ', eas:title)"/>
            </xsl:if>
        </xsl:variable>
        <!--  -->
        <xsl:variable name="initials">
            <xsl:if test="eas:initials and eas:initials != ''">
                <xsl:value-of select="concat(' ', eas:initials)"/>
            </xsl:if>
        </xsl:variable>
        <!--  -->
        <xsl:variable name="prefix">
            <xsl:if test="eas:prefix and eas:prefix != ''">
                <xsl:value-of select="concat(' ', eas:prefix)"/>
            </xsl:if>
        </xsl:variable>
        <!--  -->
        <xsl:variable name="organization">
            <xsl:if test="eas:organization and eas:organization != ''">
                <xsl:value-of select="concat(' (', eas:organization, ')')"/>
            </xsl:if>
        </xsl:variable>
        <!--  -->
        <xsl:variable name="dai">
            <xsl:if test="eas:entityId != ''">
                <xsl:choose>
                    <xsl:when test="eas:entityId/@eas:scheme = 'DAI'">
                        <xsl:value-of select="concat(' DAI=info:eu-repo/dai/nl/', eas:entityId)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat(' ', eas:entityId/@eas:scheme, '=', eas:entityId)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:variable>
        <!--  -->
        <xsl:choose>
            <xsl:when test="eas:surname = ''">
                <xsl:value-of select="eas:organization"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat(eas:surname, ',', $titles, $initials, $prefix, $organization, $dai)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- ABRComplex to string                                                                -->
    <!-- =================================================================================== -->
    <xsl:template name="abr-complex-to-string">
        <xsl:variable name="code-string" select="string()"/>
        <xsl:variable name="scheme" select="'scheme=ABR-complex'"/>
        <xsl:variable name="code" select="concat(' code=', .)"/>
        <xsl:variable name="str" select="normalize-space($abr-type/xs:schema/xs:simpleType[@name='complex']/xs:restriction/xs:enumeration[@value=$code-string]/xs:annotation/xs:documentation/text())"/>
        <xsl:variable name="value" select="concat(' value=', $str)"/>
        <xsl:value-of select="concat($scheme, ';', $code, ';', $value, ';')"/>
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
        <xsl:variable name="scheme" select="'scheme=ABR-periode'"/>
        <xsl:variable name="code" select="concat(' code=', $code-string)"/>
        <xsl:variable name="str" select="normalize-space($abr-type/xs:schema/xs:simpleType[@name='periode']/xs:restriction/xs:enumeration[@value=$code-string]/xs:annotation/xs:documentation/text())"/>
        <xsl:variable name="value" select="concat(' value=', $str)"/>
        <xsl:value-of select="concat($scheme, ';', $code, ';', $value, ';')"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- audience to string                                                                  -->
    <!-- =================================================================================== -->
    <xsl:template name="audience-to-string">
        <xsl:variable name="disciplineCode" select="string()"/>
        <xsl:value-of select="$audience/properties/entry[@key = $disciplineCode]/text()"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- date to string                                                                      -->
    <!-- =================================================================================== -->
    <xsl:template name="date-to-string">
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
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- identifier to string                                                                -->
    <!-- =================================================================================== -->
    <xsl:template name="identifier-to-string">
        <xsl:choose>
            <xsl:when test="@eas:scheme = 'ISBN'">
                <xsl:value-of select="concat('ISBN=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'ISSN'">
                <xsl:value-of select="concat('ISSN=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'bibliographicCitation'">
                <xsl:value-of select="concat('Bibliographic citation=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'Archis_onderzoek'">
                <xsl:value-of select="concat('Archis onderzoek=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'Archis_vondstmelding'">
                <xsl:value-of select="concat('Archis vondstmelding=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'Archis_art.41'">
                <xsl:value-of select="concat('Archis art.41=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'Archis_waarneming'">
                <xsl:value-of select="concat('Archis waarneming=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'Archis_monument'">
                <xsl:value-of select="concat('Archis monument=', .)"/>
            </xsl:when>
            <xsl:when test="@eas:scheme = 'eDNA-project'">
                <xsl:value-of select="concat('eDNA-project=', .)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- language to string                                                                  -->
    <!-- =================================================================================== -->
    <xsl:template name="language-to-string">
        <xsl:choose>
            <xsl:when test="@eas:schemeId = 'common.dc.language'">
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
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
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
