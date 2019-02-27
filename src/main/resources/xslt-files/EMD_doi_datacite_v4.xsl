<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:mods="http://www.loc.gov/mods/v3"
        xmlns="http://datacite.org/schema/kernel-4"
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

    <!-- Use this for registration of DOI
         Note that it is similar, but not identical, to the openaire OAI output -->
    <!-- ==================================================== -->
    <xsl:output encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="yes"/>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:variable name="date-encoding-attribute-value" select="'w3cdtf'"/>
    <!--<xsl:variable name="pid-resolver" select="'http://www.persistent-identifier.nl/?identifier='"/>-->
    <xsl:variable name="abr-type" select="document('http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd')"/>
    <xsl:variable name="audience" select="document('http://easy.dans.knaw.nl/schemas/property-list/audience.xml')"/>
    <xsl:variable name="narcis-type" select="document('http://easy.dans.knaw.nl/schemas/vocab/2015/narcis-type.xsd')"/>
    <!-- ==================================================== -->

    <xsl:template match="/">
        <xsl:call-template name="metadata-root"/>
    </xsl:template>



    <!-- ==================================================== -->
    <xsl:template name="metadata-root">
        <xsl:element name="resource">
            <!-- cosmetic -->
            <xsl:if test="//eas:subject-link">
                <xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
            </xsl:if>

            <!-- datacite version 4, previous version was 3 -->
            <xsl:attribute name="xsi:schemaLocation" select="'http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd'"/>

            <xsl:apply-templates select="emd:easymetadata"/>

        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <xsl:template match="emd:easymetadata">
        <!--  1. identifier -->
        <xsl:apply-templates select="emd:identifier/dc:identifier[@eas:scheme='DOI']"/>

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
        <xsl:apply-templates select="emd:identifier[dc:identifier[not(@eas:scheme='DOI')]]"/>

        <!-- 12. relatedIdentifier -->
        <xsl:apply-templates select="emd:relation" />

        <!-- 13. size OPTIONAL -->
        <!-- unavailable -->

        <!-- 14. format OPTIONAL -->
        <xsl:apply-templates select="emd:format[dc:format]"/>

        <!-- 15. version OPTIONAL -->
        <!-- unavailable -->

        <!-- 16. rights -->
        <xsl:element name="rightsList">
            <xsl:apply-templates select="emd:rights"/>
        </xsl:element>

        <!-- 17. description -->
        <xsl:apply-templates select="emd:description[dc:description | dcterms:abstract | dcterms:tableOfContents]" />

        <!-- 18. geoLocations -->
        <xsl:call-template name="geo"/>
    </xsl:template>


    <!-- ==================================================== -->
    <!-- dc:identifier to datacite identifier -->
    <!-- ==================================================== -->
    <xsl:template match="emd:identifier/dc:identifier[@eas:scheme='DOI']">
        <xsl:element name="identifier">
            <xsl:attribute name="identifierType" select="'DOI'"/>
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
            <xsl:call-template name="eas-organization" />
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!-- eas:organization to datacite affiliation -->
    <!-- ==================================================== -->
    <xsl:template name="eas-organization">
        <xsl:if test="eas:organization != ''">
            <xsl:element name="affiliation">
                <xsl:value-of select="eas:organization"/>
            </xsl:element>
        </xsl:if>
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
            <xsl:attribute name="contributorType" select="'Other'"/><!-- required attribute -->
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
            <xsl:choose>
                <xsl:when test="eas:role">
                    <xsl:attribute name="contributorType" select="eas:role"/><!-- required attribute -->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="contributorType" select="'Other'"/><!-- required attribute -->
                </xsl:otherwise>
            </xsl:choose>
            <xsl:element name="contributorName">
                <xsl:call-template name="eas-name" />
            </xsl:element>
            <xsl:call-template name="dai"/>
            <xsl:call-template name="eas-organization" />
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
                    <xsl:variable name="disciplineCode" select="./text()"/>
                    <xsl:variable name="narcis-enumeration" select="$narcis-type//xs:enumeration[./xs:annotation/xs:appinfo[text()=$disciplineCode]]"/>
                    <xsl:variable name="narcis-documentation" select="$narcis-enumeration/xs:annotation/xs:documentation"/>
                    <xsl:attribute name="subjectScheme">
                        <xsl:value-of select="'NARCIS-classification'"/>
                    </xsl:attribute>
                    <xsl:attribute name="schemeURI">
                        <xsl:value-of select="'http://www.narcis.nl/classification'"/>
                    </xsl:attribute>
                    <xsl:attribute name="valueURI">
                        <xsl:value-of select="concat('http://www.narcis.nl/classfication/',$narcis-enumeration/@value)"/>
                    </xsl:attribute>
                    <xsl:attribute name="xml:lang">
                        <xsl:value-of select="$narcis-documentation/@xml:lang"/>
                    </xsl:attribute>
                    <xsl:value-of select="$narcis-documentation/text()"/>
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
            <xsl:call-template name="abr-subject-schema">
                <xsl:with-param name="val" select="'ABR-complex'"/>
            </xsl:call-template>
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
            <xsl:call-template name="abr-subject-schema">
                <xsl:with-param name="val" select="'ABR-periode'"/>
            </xsl:call-template>
            <xsl:call-template name="abr-periode-to-string"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="abr-subject-schema">
        <xsl:param name="val"/>
        <xsl:attribute name="subjectScheme" select="$val"/>
        <xsl:attribute name="schemeURI" select="'http://cultureelerfgoed.nl/'"/>
        <xsl:attribute name="xml:lang" select="'nl'"/>
    </xsl:template>

    <!-- ==================================================== -->
    <!-- emd:language to datacite language -->
    <!-- ==================================================== -->
    <xsl:template match="emd:language">
        <xsl:if test="dc:language[@eas:scheme='ISO 639'][1]/text() != ''">
            <!-- even if there is more than one language code, we can only include one -->
            <xsl:variable name="lang" select="dc:language[@eas:scheme='ISO 639'][1]/text()" />
            <xsl:variable name="dataciteLang">
                <!-- NOTE we need 2 letter country codes instead, and fix for the /  -->
                <!-- Also note that we focus own GUI for four languages and ignore others -->
                <xsl:choose>
                    <xsl:when test="contains('|dut/nld|dut|nld|nl|', concat('|', $lang, '|'))">
                        <xsl:value-of select="'nl'"/>
                    </xsl:when>
                    <xsl:when test="contains('|eng|en|', concat('|', $lang, '|'))">
                        <xsl:value-of select="'en'"/>
                    </xsl:when>
                    <xsl:when test="contains('|ger/deu|ger|deu|de|', concat('|', $lang, '|'))">
                        <xsl:value-of select="'de'"/>
                    </xsl:when>
                    <xsl:when test="contains('|fre/fra|fre|fra|fr|', concat('|', $lang, '|'))">
                        <xsl:value-of select="'fr'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'-'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:if test="not($dataciteLang = '-')">
                <xsl:element name="language">
                    <xsl:value-of select="$dataciteLang"/>
                </xsl:element>
            </xsl:if>
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
        <xsl:element name="relatedIdentifiers">
            <!-- TODO no clue yet how what to map 'eas:conformsTo' to; see also below -->
            <!--<xsl:apply-templates select="eas:conformsTo[eas:subject-link != '']" />-->
            <xsl:apply-templates select="eas:hasFormat[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:hasPart[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:hasVersion[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isFormatOf[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isPartOf[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isReferencedBy[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isReplacedBy[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isRequiredBy[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:isVersionOf[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:references[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:replaces[eas:subject-link != '']" />
            <xsl:apply-templates select="eas:requires[eas:subject-link != '']" />
        </xsl:element>
    </xsl:template>

    <xsl:template name="relatedIdentifier">
        <xsl:param name="link" />
        <xsl:param name="relationType" />
        <xsl:element name="relatedIdentifier">
            <xsl:attribute name="relationType" select="$relationType" />
            <xsl:choose>
                <xsl:when test="contains($link, 'persistent-identifier.nl')">
                    <xsl:attribute name="relatedIdentifierType" select="'URN'"/>
                    <xsl:value-of select="substring-after($link, 'persistent-identifier.nl/?identifier=')"/>
                </xsl:when>
                <xsl:when test="contains($link, 'doi.org')">
                    <xsl:attribute name="relatedIdentifierType" select="'DOI'"/>
                    <xsl:value-of select="substring-after($link, 'doi.org/')"/>
                </xsl:when>
                <xsl:when test="contains($link, 'hdl.handle.net')">
                    <xsl:attribute name="relatedIdentifierType" select="'Handle'"/>
                    <xsl:value-of select="substring-after($link, 'hdl.handle.net/')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="relatedIdentifierType" select="'URL'"/>
                    <xsl:value-of select="$link"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <!-- TODO no clue what to use for the relationType here -->
    <!--<xsl:template match="eas:conformsTo[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'???'"/>
        </xsl:call-template>
    </xsl:template>-->

    <xsl:template match="eas:hasFormat[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsVariantFormatOf'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:hasPart[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'HasPart'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:hasVersion[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsSourceOf'"/> <!-- TODO change in v4.1 to HasVersion -->
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isFormatOf[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsVariantFormOf'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isPartOf[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsPartOf'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isReferencedBy[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsReferencedBy'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isReplacedBy[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsPreviousVersionOf'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isRequiredBy[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsSupplementTo'"/> <!-- TODO change in v4.1 to IsRequiredBy -->
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:isVersionOf[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsDerivedFrom'"/> <!-- TODO change in v4.1 to IsVersionOf -->
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:references[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'References'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:replaces[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsNewVersionOf'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eas:requires[eas:subject-link != '']">
        <xsl:call-template name="relatedIdentifier">
            <xsl:with-param name="link" select="eas:subject-link/text()"/>
            <xsl:with-param name="relationType" select="'IsSupplementedBy'"/> <!-- TODO change in v4.1 to Requires -->
        </xsl:call-template>
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
    <xsl:template match="emd:rights">
        <xsl:variable name="access-rights" select="dcterms:accessRights"/>
        <xsl:variable name="licenses" select="dcterms:license"/>
        <xsl:variable name="doi" select="//emd:identifier/dc:identifier[@eas:scheme='DOI']/text()"/>
        <xsl:variable name="origin-doi" select="if (starts-with($doi, '10.17026/')) then 'DANS' else 'OTHER'"/>
        <xsl:variable name="access-rights-datacite">
            <xsl:choose>
                <xsl:when test="$access-rights ='NO_ACCESS' and $origin-doi != 'DANS'">
                    <xsl:value-of select="'OPEN_ACCESS'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$access-rights"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$access-rights">
            <xsl:element name="rights">
                <xsl:choose>
                    <xsl:when test="$access-rights-datacite = 'OPEN_ACCESS_FOR_REGISTERED_USERS'">
                        <xsl:value-of select="'info:eu-repo/semantics/openAccess'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights-datacite = 'OPEN_ACCESS'">
                        <xsl:value-of select="'info:eu-repo/semantics/openAccess'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights-datacite = 'GROUP_ACCESS'">
                        <xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights-datacite = 'REQUEST_PERMISSION'">
                        <xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
                    </xsl:when>
                    <xsl:when test="$access-rights-datacite = 'NO_ACCESS'">
                        <xsl:value-of select="'info:eu-repo/semantics/closedAccess'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </xsl:if>

        <xsl:if test="$licenses">
            <xsl:for-each select="$licenses">
                <xsl:choose>
                    <xsl:when test=". = 'accept' and ../dcterms:accessRights = 'OPEN_ACCESS'">
                        <xsl:variable name="cc0" select="'http://creativecommons.org/publicdomain/zero/1.0'"/>
                        <xsl:element name="rights">
                            <xsl:attribute name="rightsURI" select="$cc0"/>
                            <xsl:value-of select="concat('License: ', $cc0)"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test=". = 'accept'"/> <!-- this case is such that the 'accept' value does not end up in the otherwise clause -->
                    <xsl:when test="starts-with(., 'http://') or starts-with(., 'https://')">
                        <xsl:element name="rights">
                            <xsl:attribute name="rightsURI" select="."/>
                            <xsl:value-of select="concat('License: ', .)"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:element name="rights">
                            <xsl:value-of select="concat('License: ', .)"/>
                        </xsl:element>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <!-- ==================================================== -->
    <!-- dc:identifier to datacite alternateIdentifier -->
    <!-- ==================================================== -->
    <xsl:template match="emd:identifier[dc:identifier[not(@eas:scheme='DOI')]]">
        <xsl:element name="alternateIdentifiers">
            <!-- we cannot tell whether identifiers other than the ones matched below
                 belong to the dataset, so we leave them out (EASY-1002) -->
            <xsl:apply-templates select="dc:identifier[@eas:scheme='DOI_OTHER_ACCESS']" />
            <xsl:apply-templates select="dc:identifier[@eas:scheme='PID']" />
            <xsl:apply-templates select="dc:identifier[@eas:scheme='DMO_ID']" />
            <xsl:apply-templates select="dc:identifier[@eas:scheme='AIP_ID']" />
        </xsl:element>
    </xsl:template>

    <xsl:template match="dc:identifier[@eas:scheme='DOI_OTHER_ACCESS']">
        <xsl:element name="alternateIdentifier">
            <xsl:attribute name="alternateIdentifierType" select="'DOI'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dc:identifier[@eas:scheme='PID']">
        <xsl:element name="alternateIdentifier">
            <xsl:attribute name="alternateIdentifierType" select="'URN'"/>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="dc:identifier[@eas:scheme='DMO_ID' or @eas:scheme='AIP_ID']">
        <xsl:element name="alternateIdentifier">
            <xsl:attribute name="alternateIdentifierType" select="@eas:scheme" />
            <xsl:value-of select="."/>
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
        <xsl:value-of select="concat($str, ' ', '(',$code-string,')')"/>
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
        <xsl:value-of select="concat($str, ' ', '(', $code-string, ')')"/>
    </xsl:template>


    <!-- ==================================================== -->
    <!-- Spatial subjects -->
    <!-- ==================================================== -->
    <xsl:template name="geo">
        <xsl:element name="geoLocations">
            <xsl:apply-templates select="emd:coverage/dcterms:spatial" />
            <xsl:apply-templates select="emd:coverage/eas:spatial"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="emd:coverage/dcterms:spatial">
        <xsl:element name="geoLocation">
            <xsl:element name="geoLocationPlace">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- eas:spatial to x,geoLocationBox -->
    <xsl:template match="emd:coverage/eas:spatial">
        <!-- Coordinates entered in degrees vary: some are in decimal, others in degree, minutes and seconds. -->
        <!-- Ignore coordinates in degrees for now. -->
        <xsl:apply-templates select="eas:point[@eas:scheme = 'RD']"/>
        <xsl:apply-templates select="eas:box[@eas:scheme = 'RD']"/>
        <xsl:apply-templates select="eas:polygon[@eas:scheme = 'RD']/eas:polygon-exterior"/>
        <xsl:apply-templates select="eas:point[@eas:scheme = 'degrees']"/>
        <xsl:apply-templates select="eas:box[@eas:scheme = 'degrees']"/>
        <xsl:apply-templates select="eas:polygon[@eas:scheme = 'degrees']/eas:polygon-exterior"/>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point[@eas:scheme = 'degrees']">
        <xsl:element name="geoLocation">
            <xsl:element name="geoLocationPoint">
                <xsl:element name="pointLatitude"><xsl:value-of select="eas:y"/></xsl:element>
                <xsl:element name="pointLongitude"><xsl:value-of select="eas:x"/></xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:point[@eas:scheme = 'RD']">
        <!-- point in WGS84 -->
        <xsl:if test="string(number(eas:x)) != 'NaN' and string(number(eas:y)) != 'NaN'">
            <xsl:element name="geoLocation">
                <xsl:element name="geoLocationPoint">
                    <xsl:element name="pointLatitude">
                        <xsl:call-template name="rd-to-lat-long-lat">
                            <xsl:with-param name="x" select="eas:x"/>
                            <xsl:with-param name="y" select="eas:y"/>
                        </xsl:call-template>
                    </xsl:element>
                    <xsl:element name="pointLongitude">
                        <xsl:call-template name="rd-to-lat-long-lon">
                            <xsl:with-param name="x" select="eas:x"/>
                            <xsl:with-param name="y" select="eas:y"/>
                        </xsl:call-template>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box[@eas:scheme = 'degrees']">
        <xsl:element name="geoLocation">
            <xsl:element name="geoLocationBox">
                <xsl:element name="northBoundLatitude"><xsl:value-of select="eas:north"/></xsl:element>
                <xsl:element name="southBoundLatitude"><xsl:value-of select="eas:south"/></xsl:element>
                <xsl:element name="westBoundLongitude"><xsl:value-of select="eas:west"/></xsl:element>
                <xsl:element name="eastBoundLongitude"><xsl:value-of select="eas:east"/></xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:box[@eas:scheme = 'RD']">
        <!-- box in WGS84 -->
        <xsl:if test="string(number(eas:north)) != 'NaN' and string(number(eas:east)) != 'NaN' and string(number(eas:south)) != 'NaN' and string(number(eas:west)) != 'NaN'">
            <xsl:element name="geoLocation">
                <xsl:element name="geoLocationBox">
                    <xsl:call-template name="box-converter">
                        <xsl:with-param name="north" select="eas:north"/>
                        <xsl:with-param name="south" select="eas:south"/>
                        <xsl:with-param name="west" select="eas:west"/>
                        <xsl:with-param name="east" select="eas:east"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:polygon[@eas:scheme = 'degrees']/eas:polygon-exterior">
        <xsl:element name="geoLocation">
            <xsl:element name="geoLocationPolygon">
                <xsl:for-each select="eas:polygon-point">
                    <xsl:element name="polygonPoint">
                        <xsl:element name="pointLatitude"><xsl:value-of select="eas:x"/></xsl:element>
                        <xsl:element name="pointLongitude"><xsl:value-of select="eas:y"/></xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <xsl:template match="eas:polygon[@eas:scheme = 'RD']/eas:polygon-exterior">
        <!-- polygon in WGS84 -->
        <xsl:element name="geoLocation">
            <xsl:element name="geoLocationPolygon">
                <xsl:for-each select="eas:polygon-point">
                    <xsl:element name="polygonPoint">
                        <xsl:element name="pointLatitude">
                            <xsl:call-template name="rd-to-lat-long-lat">
                                <xsl:with-param name="x" select="eas:x"/>
                                <xsl:with-param name="y" select="eas:y"/>
                            </xsl:call-template>
                        </xsl:element>
                        <xsl:element name="pointLongitude">
                            <xsl:call-template name="rd-to-lat-long-lon">
                                <xsl:with-param name="x" select="eas:x"/>
                                <xsl:with-param name="y" select="eas:y"/>
                            </xsl:call-template>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- ==================================================== -->
    <!-- x y to datacite pointLatitude pointLongitude -->
    <!-- ==================================================== -->
    <xsl:template name="rd-in-xy">
        <xsl:param name="xy"/>
        <xsl:variable name="temp" as="xs:string*" select="tokenize($xy,' ')"/>
        <xsl:element name="pointLatitude">
            <xsl:value-of select="$temp[1]"/>
        </xsl:element>
        <xsl:element name="pointLongitude">
            <xsl:value-of select="$temp[2]"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="rd-in-xy2">
        <xsl:param name="x"/>
        <xsl:param name="y"/>
        <xsl:element name="pointLatitude">
            <xsl:value-of select="$x"/>
        </xsl:element>
        <xsl:element name="pointLongitude">
            <xsl:value-of select="$y"/>
        </xsl:element>
    </xsl:template>


    <!-- =================================================================================== -->
    <!-- RD north east south west to lat lon lat lon                                         -->
    <!-- =================================================================================== -->
    <xsl:template name="box-converter">
        <xsl:param name="north"/>
        <xsl:param name="east"/>
        <xsl:param name="south"/>
        <xsl:param name="west"/>
        <xsl:element name="northBoundLatitude">
            <xsl:call-template name="lat-converter-">
                <xsl:with-param name="y" select="$north"/>
                <xsl:with-param name="east" select="$east"/>
                <xsl:with-param name="west" select="$west"/>
            </xsl:call-template>
        </xsl:element>
        <xsl:element name="eastBoundLongitude">
            <xsl:call-template name="lon-converter-">
                <xsl:with-param name="x" select="$east"/>
                <xsl:with-param name="north" select="$north"/>
                <xsl:with-param name="south" select="$south"/>
            </xsl:call-template>
        </xsl:element>
        <xsl:element name="southBoundLatitude">
            <xsl:call-template name="lat-converter-">
                <xsl:with-param name="y" select="$south"/>
                <xsl:with-param name="east" select="$east"/>
                <xsl:with-param name="west" select="$west"/>
            </xsl:call-template>
        </xsl:element>
        <xsl:element name="westBoundLongitude">
            <xsl:call-template name="lon-converter-">
                <xsl:with-param name="x" select="$west"/>
                <xsl:with-param name="north" select="$north"/>
                <xsl:with-param name="south" select="$south"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD y, east, west to latitude converter                                              -->
    <!-- =================================================================================== -->
    <xsl:template name="lat-converter-">
        <xsl:param name="y" as="xs:decimal"/>
        <xsl:param name="east" as="xs:decimal"/>
        <xsl:param name="west" as="xs:decimal"/>
        <xsl:variable name="x" select="($east + $west) div 2"/>
        <xsl:value-of>
            <xsl:call-template name="rd-to-lat-long-lat">
                <xsl:with-param name="x" select="$x"/>
                <xsl:with-param name="y" select="$y"/>
            </xsl:call-template>
        </xsl:value-of>
    </xsl:template>
    <!-- =================================================================================== -->
    <!-- RD x, north, south to longitude converter                                           -->
    <!-- =================================================================================== -->
    <xsl:template name="lon-converter-">
        <xsl:param name="x" as="xs:decimal"/>
        <xsl:param name="north" as="xs:decimal"/>
        <xsl:param name="south" as="xs:decimal"/>
        <xsl:variable name="y" select="($north + $south) div 2"/>
        <xsl:value-of>
            <xsl:call-template name="rd-to-lat-long-lon">
                <xsl:with-param name="x" select="$x"/>
                <xsl:with-param name="y" select="$y"/>
            </xsl:call-template>
        </xsl:value-of>
    </xsl:template>

    <xsl:template name="rd-to-lat-long-lat">
        <xsl:param name="x" as="xs:decimal"/>
        <xsl:param name="y" as="xs:decimal"/>
        <xsl:variable name="p" select="($x - 155000.00) div 100000"/>
        <xsl:variable name="q" select="($y - 463000.00) div 100000"/>

        <xsl:variable name="df"
                      select="(($q*3235.65389)+($p*$p*-32.58297)+($q*$q*-0.24750)+($p*$p*$q*-0.84978)+($q*$q*$q*-0.06550)+($p*$p*$q*$q*-0.01709)+($p*-0.00738)+($p*$p*$p*$p*0.00530)+($p*$p*$q*$q*$q*-0.00039)+($p*$p*$p*$p*$q*0.00033)+($p*$q*-0.00012)) div 3600"/>
        <xsl:value-of select="(round((52.15517440+$df)*100000000.00)) div 100000000.00"/>

    </xsl:template>

    <xsl:template name="rd-to-lat-long-lon">
        <xsl:param name="x" as="xs:decimal"/>
        <xsl:param name="y" as="xs:decimal"/>
        <xsl:variable name="p" select="($x - 155000.00) div 100000"/>
        <xsl:variable name="q" select="($y - 463000.00) div 100000"/>
        <xsl:variable name="dl"
                      select="(($p*5260.52916)+($p*$q*105.94684)+($p*$q*$q*2.45656)+($p*$p*$p*-0.81885)+($p*$q*$q*$q*0.05594)+($p*$p*$p*$q*-0.05607)+($q*0.01199)+($p*$p*$p*$q*$q*-0.00256)+($p*$q*$q*$q*$q*0.00128)+($q*$q*0.00022)+($p*$p*-0.00022)+($p*$p*$p*$p*$p*0.00026)) div 3600"/>
        <xsl:value-of select="(round((5.387206210+$dl)*100000000.00)) div 100000000.00"/>
    </xsl:template>


</xsl:stylesheet>
