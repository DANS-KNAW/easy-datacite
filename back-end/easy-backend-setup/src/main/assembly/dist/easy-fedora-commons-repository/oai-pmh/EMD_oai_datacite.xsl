<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns="http://datacite.org/schema/kernel-2.2"
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
    <!--<xsl:variable name="pid-resolver" select="'http://www.persistent-identifier.nl/?identifier='"/>-->
    <xsl:variable name="abr-type" select="document('http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd')"/>
    <xsl:variable name="audience" select="document('http://easy.dans.knaw.nl/schemas/property-list/2012/11/audience.xml')"/> 
    <!-- ==================================================== -->
    
    <xsl:template match="/">
        <xsl:call-template name="openaire-root"/>
    </xsl:template>
    
   
       
    <!-- ==================================================== -->
    <xsl:template name="openaire-root">
        <xsl:element name="resource">
            <!-- cosmetic -->
            <xsl:if test="//eas:subject-link">
                <xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
            </xsl:if>
            <xsl:attribute name="xsi:schemaLocation" select="'http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd'"/>
            
            <xsl:apply-templates select="emd:easymetadata"/>
            
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->
    
    <xsl:template match="emd:easymetadata">
        <!--  1. identifier -->
        <xsl:apply-templates select="emd:identifier/dc:identifier[@eas:scheme='PID']"/>
        
        <!--  2. creators -->
        <xsl:apply-templates select="emd:creator"/>
        
        <!--  3. title -->
        <xsl:apply-templates select="emd:title"/>
        
        <!--  4. publisher -->
        <xsl:call-template name="publisher"/>
        
        <!--  5. publicationYear -->
        <xsl:call-template name="publication-year"/>
        
        <!--  6. subject -->
        <xsl:call-template name="subject"/>
         
        <!--  7. contributor -->
        <xsl:call-template name="contributor"/>
         
        <!--  8. date -->
        <xsl:apply-templates select="emd:date" />
         
        <!--  9. language -->
        <xsl:apply-templates select="emd:language" />
        
        <!-- 10. resourceType -->
        <xsl:call-template name="type" />
        
        <!-- 11. alternateIdentifier OPTIONAL -->
        <xsl:apply-templates select="emd:identifier[dc:identifier[not(@eas:scheme='PID')]]"/>
        
        <!-- 12. relatedIdentifier -->
        <xsl:apply-templates select="emd:relation" />
        
        <!-- 13. size OPTIONAL -->
        <!-- unavailable -->
        
        <!-- 14. format OPTIONAL -->
        <xsl:apply-templates select="emd:format[dc:format]"/>
        
        <!-- 15. version OPTIONAL -->
        <!-- unavailable -->
        
        <!-- 16. rights -->
        <xsl:apply-templates select="emd:rights/dcterms:accessRights" />
        
        <!-- 17. description -->
        <xsl:apply-templates select="emd:description[dc:description | dcterms:abstract | dcterms:tableOfContents]" />
        
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:identifier to datacite identifier -->
    <!-- ==================================================== -->
    <xsl:template match="emd:identifier/dc:identifier[@eas:scheme='PID']">
        <xsl:element name="identifier">
            <xsl:attribute name="identifierType" select="'URN'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- creator to datacite creators -->
    <!-- ==================================================== -->
    <xsl:template match="emd:creator">
        <xsl:element name="creators">
            <xsl:apply-templates select="eas:creator"/>
            <xsl:apply-templates select="dc:creator"/>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:creator to datacite name -->
    <!-- ==================================================== -->
    <xsl:template match="dc:creator">
        <xsl:element name="creator">
            <xsl:element name="creatorName">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
   
    <!-- ==================================================== -->
    <!-- eas:creator to datacite creator -->
    <!-- ==================================================== -->
    <xsl:template match="eas:creator">
        <xsl:element name="creator">
            <xsl:element name="creatorName">
                <xsl:call-template name="eas-name" />
            </xsl:element>
            <xsl:call-template name="dai"/>
        </xsl:element>  
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- contributor to datacite contributors -->
    <!-- ==================================================== -->
    <xsl:template name="contributor">
        <xsl:element name="contributors">
            <xsl:apply-templates select="emd:contributor/eas:contributor"/>
            <xsl:apply-templates select="emd:contributor/dc:contributor"/>
            <xsl:apply-templates select="emd:rights/dcterms:rightsHolder"/>
            <xsl:apply-templates select="emd:rights/dc:rights"/>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:contributor to datacite contributor -->
    <!-- ==================================================== -->
    <xsl:template match="dc:contributor">
        <xsl:element name="contributor">
            <xsl:element name="contributorName">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
   
    <!-- ==================================================== -->
    <!-- eas:contributor to datacite contributor -->
    <!-- ==================================================== -->
    <xsl:template match="eas:contributor">
        <xsl:element name="contributor">
            <xsl:element name="contributorName">
                <xsl:call-template name="eas-name" />
            </xsl:element>
            <xsl:call-template name="dai"/>
        </xsl:element>  
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- eas: creator/contributor name contents -->
    <!-- ==================================================== -->
    <xsl:template name="eas-name">
        <!-- only eas:organization => use organization as creatorName -->
        <!-- surname and initials => surname, initials[, prefix] -->
        <xsl:variable name="familyName">
            <xsl:if test="eas:surname != ''">
                <xsl:choose>
                    <xsl:when test="eas:prefix != ''">
                        <xsl:value-of select="concat(eas:surname, ', ', eas:prefix)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="eas:surname"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:variable>
        
        <!-- creatorName -->
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
        
        <xsl:choose>
            <xsl:when test="not(eas:surname) or eas:surname = ''">
                <xsl:value-of select="eas:organization"/>
            </xsl:when>
            <xsl:when test="eas:surname != '' and (not(eas:initials) or eas:initials = '')">
                <xsl:value-of select="$familyName"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat(eas:surname, ',', $initials, $prefix)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- DAI -> nameIdentifier -->
    <!-- ==================================================== -->
    <xsl:template name="dai">
        <!-- is there a DAI? > ID for daiList -->
        <xsl:if test="eas:entityId/@eas:scheme = 'DAI' and eas:entityId/text() != ''">
            <xsl:element name="nameIdentifier">
                <xsl:attribute name="nameIdentifierScheme" select="'info:eu-repo/dai'" />
                <xsl:value-of select="eas:entityId/text()" />
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    
    <!-- ==================================================== -->
    <!-- dcterms:rightsHolder to datacite contributor -->
    <!-- ==================================================== -->
    <xsl:template match="emd:rights/dcterms:rightsHolder | emd:rights/dc:rights">
        <xsl:element name="contributor">
            <xsl:attribute name="contributorType" select="'RightsHolder'"/>
            <xsl:element name="contributorName">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
   
    
    <!-- ==================================================== -->
    <!-- title to datacite titles -->
    <!-- ==================================================== -->
    <xsl:template match="emd:title">
        <xsl:element name="titles">
            <xsl:apply-templates select="dc:title"/>
            <xsl:apply-templates select="dcterms:alternative"/>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:title to datacite title -->
    <!-- ==================================================== -->
    <xsl:template match="dc:title">
        <xsl:element name="title">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    
    <!-- ==================================================== -->
    <!-- dcterms:alternative to datacite title (alternative) -->
    <!-- ==================================================== -->
    <xsl:template match="dcterms:alternative">
        <xsl:element name="title">
            <xsl:attribute name="titleType" select="'AlternativeTitle'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:publisher to publisher -->
    <!-- ==================================================== -->
    <xsl:template name="publisher">
        <xsl:element name="publisher">
            <xsl:choose>
                <xsl:when test="emd:publisher/dc:publisher">
                    <xsl:value-of select="emd:publisher/dc:publisher"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'Data Archiving and Networked Services (DANS)'" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- Publication year -->
    <!-- ==================================================== -->
    <xsl:template name="publication-year">
        <xsl:element name="publicationYear">
            <xsl:choose>
                <!-- we assume there is only one date available... -->
                <xsl:when test="emd:date/eas:available != ''">
                    <xsl:variable name="avdate">
                        <xsl:apply-templates select="emd:date/eas:available"/>
                    </xsl:variable>
                    <xsl:value-of select="substring($avdate, 1, 4)"/>
                </xsl:when>
                <!-- ... or no date available -->
                <xsl:otherwise>
                    <!-- pick the date farthest into the future, but not a date modified -->
                    <xsl:variable name="dates" as="xs:string+" select="emd:date/eas:*[not(contains(name(),'modified'))]"/>
                    <xsl:value-of select="year-from-dateTime(xs:dateTime(max($dates)))"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="emd:date/eas:available">
        <xsl:call-template name="w3cdtfEncoding"/>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <!-- emd:date/x to date[@dateType=x] -->
    <!-- ==================================================== -->
    <xsl:template match="emd:date">
        <xsl:element name="dates">
            <!-- We don't do dcterms dates, as they are free text and impossible to convert;
            we might lose some dates here. -->
            <xsl:for-each select="eas:dateSubmitted">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Submitted'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:issued">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Issued'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:created">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Created'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:valid">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Valid'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:modified">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Updated'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:dateCopyrighted">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Copyrighted'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:available">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Available'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="eas:dateAccepted">
                <xsl:element name="date">
                    <xsl:attribute name="dateType" select="'Accepted'"/>
                    <xsl:call-template name="w3cdtfEncoding"/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
    <!-- w3cdtfEncoding -->
    <xsl:template name="w3cdtfEncoding">
        <!-- xalan chokes on regex -->
        <xsl:choose>
            <xsl:when test="matches(., '\d\d\d\d-\d\d-\d\d*')">
                <!--<xsl:attribute name="encoding" select="$date-encoding-attribute-value"/>-->
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
    
    <!-- ==================================================== -->
    <!-- emd:audience, emd:subject, emd:coverage => subject 
           - there are special subjects for archaeology datasets
             in a scheme 'archaeology.dc.subject' -->
    <!-- ==================================================== -->
    <xsl:template name="subject">
        <xsl:element name="subjects">
            <!-- Audience terms -->
            <xsl:apply-templates select="emd:audience/dcterms:audience"/>
            
            <!-- General subject terms -->
            <xsl:if test="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]">
                <xsl:apply-templates select="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]"/>
            </xsl:if>
            
            <xsl:apply-templates select="emd:subject/dc:subject[@eas:schemeId = 'archaeology.dc.subject' and . != '']"/>
            
            <!-- Spatial subjects (we lose something, maybe) -->
            <xsl:for-each select="emd:coverage/dcterms:spatial">
                <xsl:element name="subject">
                    <xsl:apply-templates select="."/>
                </xsl:element>
            </xsl:for-each>
            
            <xsl:apply-templates select="emd:coverage/eas:spatial"/>
            
            <!-- Temporal subjects -->
            <xsl:if test="emd:coverage/dcterms:temporal[not(@eas:schemeId = 'archaeology.dcterms.temporal')]">
                <xsl:for-each select="emd:coverage/dcterms:temporal[not(@eas:schemeId = 'archaeology.dcterms.temporal')]">
                    <xsl:element name="subject">
                        <xsl:variable name="temporal">
                        Temporal coverage: 
                        <xsl:call-template name="free-temporal-coverage"/>
                        </xsl:variable>
                        <xsl:value-of select="normalize-space($temporal)" />
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            
            <!-- TODO: Inconsistent coding style -->
            <xsl:apply-templates select="emd:coverage/dcterms:temporal[@eas:schemeId = 'archaeology.dcterms.temporal' and . != '']"/>
        </xsl:element>
    </xsl:template>
    
    <!-- =================================================================================== -->
    <!-- dcterms:audience to subject -->
    <!-- ==================================================== -->
    <xsl:template match="emd:audience/dcterms:audience">
        <xsl:element name="subject">
            <xsl:choose>
                <xsl:when test="@eas:schemeId = 'custom.disciplines' and text() != ''">
                    <xsl:variable name="disciplineCode" select="."/>
                    <xsl:value-of select="$audience/properties/entry[@key = $disciplineCode]/text()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dc:subject to subject -->
    <!-- ==================================================== -->
    <xsl:template match="emd:subject/dc:subject[not(@eas:schemeId = 'archaeology.dc.subject')]">
        <xsl:element name="subject">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="emd:subject/dc:subject[@eas:schemeId = 'archaeology.dc.subject' and . != '']">
        <xsl:element name="subject">
            <xsl:value-of>
                <xsl:call-template name="abr-complex-to-string"/>
            </xsl:value-of>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- dcterms:temporal to subject -->
    <xsl:template name="free-temporal-coverage">
        <xsl:value-of select="string-join(., ' ')"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="emd:coverage/dcterms:temporal[@eas:schemeId = 'archaeology.dcterms.temporal' and . != '']">
        <xsl:element name="subject">
            <xsl:attribute name="subjectScheme" select="'ABR-periode'"/>
            <xsl:value-of select="."/>
        </xsl:element>
        <xsl:element name="subject">
            <xsl:attribute name="subjectScheme" select="'ABR-periode-label'"/>
            <xsl:variable name="periodestring">Periode: 
                <xsl:call-template name="abr-periode-to-string"/>
            </xsl:variable>
            <xsl:value-of select="normalize-space($periodestring)" />
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:language to datacite language -->
    <!-- ==================================================== -->
    <xsl:template match="emd:language">
        <xsl:if test="dc:language[@eas:scheme='ISO 639'][1]/text() != ''">
            <xsl:element name="language">
                <!-- even if there is more than one language code, we can only include one -->
                <xsl:variable name="lang" select="dc:language[@eas:scheme='ISO 639'][1]/text()" />
                <xsl:choose>
                    <xsl:when test="$lang = 'dut/nld'">
                        <xsl:value-of select="'nld'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$lang"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:type to datacite resourceType -->
    <!-- ==================================================== -->
    <xsl:template name="type">
        <xsl:element name="resourceType">
            <xsl:attribute name="resourceTypeGeneral" select="'Dataset'" />
            <xsl:value-of select="'Dataset'" />
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:relation to datacite relatedIdentifiers -->
    <!-- ==================================================== -->
    <xsl:template match="emd:relation">
        <xsl:if test="eas:*[eas:subject-link != '']">
            <xsl:element name="relatedIdentifiers">
                <xsl:for-each select="eas:*[eas:subject-link != '']">
                    <xsl:variable name="rawid" select="eas:subject-link/text()" />
                    <xsl:choose>
                        <xsl:when test="starts-with($rawid, 'http://persistent-identifier.nl/?identifier=')">
                            <xsl:element name="relatedIdentifier">
                                <xsl:attribute name="relatedIdentifierType" select="'URN'"/>
                                <xsl:value-of select="substring-after($rawid, 'http://persistent-identifier.nl/?identifier=')"/>
                            </xsl:element>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:element name="relatedIdentifier">
                                <xsl:attribute name="relatedIdentifierType" select="'URL'"/>
                                <xsl:value-of select="$rawid"/>
                            </xsl:element>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:format to formats -->
    <!-- ==================================================== -->
    <xsl:template match="emd:format[dc:format]">
        <xsl:element name="formats">
            <xsl:for-each select="dc:format">
                <xsl:element name="format">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:rights/dct:accessRights to datacite rights -->
    <!-- ==================================================== -->
    <xsl:template match="emd:rights/dcterms:accessRights">
        <!-- 
         info:eu-repo/semantics/closedAccess
         info:eu-repo/semantics/embargoedAccess
         info:eu-repo/semantics/restrictedAccess
         info:eu-repo/semantics/openAccess
        -->
        <xsl:element name="rights">
            <xsl:choose>
                <xsl:when test=". = 'OPEN_ACCESS_FOR_REGISTERED_USERS'">
                    <xsl:value-of select="'info:eu-repo/semantics/openAccess'"/>
                </xsl:when>
                <xsl:when test=". = 'GROUP_ACCESS'">
                    <xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
                </xsl:when>
                <xsl:when test=". = 'REQUEST_PERMISSION'">
                    <xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
                </xsl:when>
                <xsl:when test=". = 'NO_ACCESS'">
                    <xsl:value-of select="'info:eu-repo/semantics/closedAccess'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- dc:identifier to datacite alternateIdentifier -->
    <!-- ==================================================== -->
    <xsl:template match="emd:identifier[dc:identifier[not(@eas:scheme='PID')]]">
        <xsl:element name="alternateIdentifiers">
            <xsl:for-each select="dc:identifier[not(@eas:scheme='PID')]">
                <xsl:element name="alternateIdentifier">
                    <xsl:choose>
                        <xsl:when test="@eas:scheme != ''">
                            <xsl:attribute name="alternateIdentifierType" select="@eas:scheme"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="alternateIdentifierType" select="'Unknown'"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
    <!-- ==================================================== -->
    <!-- emd:description to datacite description -->
    <!-- ==================================================== -->
    <xsl:template match="emd:description[dc:description | dcterms:abstract | dcterms:tableOfContents]">
        <xsl:element name="descriptions">
            <xsl:for-each select="dc:description">
                <xsl:element name="description">
                    <xsl:attribute name="descriptionType" select="'Other'" />
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="dcterms:abstract">
                <xsl:element name="description">
                    <xsl:attribute name="descriptionType" select="'Abstract'" />
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="dcterms:tableOfContents">
                <xsl:element name="description">
                    <xsl:attribute name="descriptionType" select="'TableOfContents'" />
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
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
    
    <!-- eas:spatial to mods:subject/mods:geographic -->
    <xsl:template match="emd:coverage/eas:spatial">
        <xsl:apply-templates select="eas:point"/>
        <xsl:apply-templates select="eas:box"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point">
        <xsl:element name="subject">
            <xsl:value-of select="concat('Spatial coverage: ', 'x=', eas:x, '; y=', eas:y, '; units=', @eas:scheme)"/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point[@eas:scheme = 'RD']">
        <xsl:element name="subject">
            <xsl:value-of select="concat('Spatial coverage: ', 'x=', eas:x, '; y=', eas:y, '; units=m; (Dutch National Grid projection)')"/>
        </xsl:element>
        <!-- same point in WGS84 -->
        <xsl:if test="string(number(eas:x)) != 'NaN' and string(number(eas:y)) != 'NaN'">
            <xsl:element name="subject">
                <xsl:variable name="transformed">
                    Spatial coverage: 
                    <xsl:call-template name="rd-to-lat-long">
                        <xsl:with-param name="x" select="eas:x"/>
                        <xsl:with-param name="y" select="eas:y"/>
                    </xsl:call-template>
                    (EPSG projection; see http://www.opengis.net/def/crs/EPSG/0/4326)
                </xsl:variable>
                <xsl:value-of select="normalize-space($transformed)"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box">
        <xsl:element name="subject">
            <xsl:value-of select="concat('Spatial coverage: ', 'north=', eas:north, '; east=', eas:east, '; south=', eas:south, '; west=', eas:west, '; units=', @eas:scheme)"/>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box[@eas:scheme = 'RD']">
        <xsl:element name="subject">
            <xsl:value-of select="concat('north=', eas:north, '; east=', eas:east, '; south=', eas:south, '; west=', eas:west, '; units=m; (Dutch National Grid projection)')"/>
        </xsl:element>
        <!-- same box in WGS84 -->
        <xsl:if test="string(number(eas:north)) != 'NaN' and string(number(eas:east)) != 'NaN' and string(number(eas:south)) != 'NaN' and string(number(eas:west)) != 'NaN'">
            <xsl:element name="subject">
                <xsl:variable name="transformed">
                    Spatial coverage: 
                    <xsl:call-template name="box-converter">
                        <xsl:with-param name="north" select="eas:north"/>
                        <xsl:with-param name="east" select="eas:east"/>
                        <xsl:with-param name="south" select="eas:south"/>
                        <xsl:with-param name="west" select="eas:west"/>
                    </xsl:call-template>
                    (EPSG projection; see http://www.opengis.net/def/crs/EPSG/0/4326)
                </xsl:variable>
                <xsl:value-of select="normalize-space($transformed)"/>
            </xsl:element>
        </xsl:if>
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
