<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:oai="http://www.openarchives.org/OAI/2.0/"
      xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
      xmlns:dc="http://purl.org/dc/elements/1.1/" 
      xmlns:oai-id="http://www.openarchives.org/OAI/2.0/oai-identifier"
      xmlns:format="metadata:format:prefixes"
      exclude-result-prefixes="xs xsl xsi oai oai_dc dc format oai-id">
    
    <!-- 
        Features based on the (string) syntax of some of the dc-values.
        This is highly repository-specific. 
    -->   
    <xsl:import href="dc-plugin.xsl"/>
    <xsl:import href="xml-html.xsl"/>
    <!-- 
        ======================================== 
    -->   
    <!-- 
        A list of metadataPrefixes of formats available from this repository. 
        Attribute 'integral' specifies whether the metadata format is available
        for all records. 
    -->
    <format:prefixes>
        <prefix integral="true">oai_dc</prefix>
        <prefix integral="true">oai_datacite</prefix>
        <prefix integral="true">nl_didl</prefix>
        <prefix integral="false">carare</prefix>
    </format:prefixes>
    <!-- 
        Optional variables for composing the URL of a jump off page for items.
    -->
    <xsl:variable name="jumpoffURL" select="'http://easy.dans.knaw.nl/ui/rest/datasets/'"/>
    <xsl:variable name="sidPrefix" select="'easy-dataset:'"/>
    <xsl:variable name="oaiPrefix" select="'oai:easy.dans.knaw.nl:'"/>
    <!-- 
        ======================================== 
    -->
    <xsl:variable name="request" select="oai:OAI-PMH/oai:request"/>
    <xsl:variable name="verb" select="oai:OAI-PMH/oai:request/@verb"/>
    <xsl:variable name="identifier" select="oai:OAI-PMH/oai:request/@identifier"/>
    <!-- 
        ======================================== 
    -->
    <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
    <!-- 
        ======================================== 
    -->    
    <xsl:template match="/">
        <xsl:variable name="titlePrefix">
            <xsl:choose>
                <xsl:when test="oai:OAI-PMH/oai:error">
                    <xsl:value-of select="'Error'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$verb"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <html>
            <head>
                <title><xsl:value-of select="$titlePrefix"/> - DANS OAI-PMH 2.0</title>
                <xsl:call-template name="cssStyle"/>
                <link rel="shortcut icon" href="static/d.gif"/>
            </head>
            <body id="top">
                <xsl:call-template name="navigation"/>
                <xsl:call-template name="main-content"/>
                <div id="bottom"/>
            </body>
        </html>
    </xsl:template>
    <!-- 
        ======================================== 
    -->  
    <xsl:template name="main-content">
        <div id="content">
            <xsl:call-template name="render-message"/>
            <xsl:apply-templates select="/oai:OAI-PMH"/>
            <xsl:call-template name="render-message"/>
            <xsl:call-template name="vendor-message"/>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    -->  
    <xsl:template match="/oai:OAI-PMH">
        <div id="header">
            <table><tr>
                <td class="key">Datestamp of response</td>
                <td class="value"><xsl:value-of select="oai:responseDate"/></td>
            </tr><tr>
                <td class="key">Request URL</td>
                <td class="value"><xsl:value-of select="oai:request"/></td>
            </tr></table>
        </div>
        <div id="response">
            <xsl:apply-templates select="oai:error"/>
            <xsl:apply-templates select="oai:Identify"/>
            <xsl:apply-templates select="oai:ListMetadataFormats"/>
            <xsl:apply-templates select="oai:ListSets"/>
            <xsl:apply-templates select="oai:ListIdentifiers"/>
            <xsl:apply-templates select="oai:ListRecords"/>
            <xsl:apply-templates select="oai:GetRecord"/>
        </div>      
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:GetRecord">
        <div id="get-record">
            <xsl:apply-templates select="oai:record"/>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:ListRecords">
        <div id="list-records">
            <xsl:apply-templates select="oai:record"/>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:record">
        <div class="oai-record">
            <xsl:apply-templates select="oai:header"/>
            <xsl:apply-templates select="oai:metadata"/>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:metadata">
        <div class="oai-metadata">
            <xsl:apply-templates select="*"/>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:metadata/*" priority="-100">
        <xsl:call-template name="xmlToHtml">
            <xsl:with-param name="print-xml-declaration" select="true()"/>
        </xsl:call-template>
    </xsl:template> 

    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:metadata/oai_dc:dc">
        <xsl:choose>
            <xsl:when test="contains($request, 'xml')">
                <xsl:call-template name="xmlToHtml">
                    <xsl:with-param name="print-xml-declaration" select="true()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="dc-table"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="dc-table">
        <div class="oai-dc">
            <table class="dcdata">
                <xsl:apply-templates select="dc:title"/>
                <xsl:apply-templates select="dc:creator"/>
                <xsl:apply-templates select="dc:subject"/>
                <xsl:apply-templates select="dc:description"/>
                <xsl:apply-templates select="dc:publisher"/>
                <xsl:apply-templates select="dc:contributor"/>
                <xsl:apply-templates select="dc:date"/>
                <xsl:apply-templates select="dc:type"/>
                <xsl:apply-templates select="dc:format"/>
                <xsl:apply-templates select="dc:identifier"/>
                <xsl:apply-templates select="dc:source"/>
                <xsl:apply-templates select="dc:language"/>
                <xsl:apply-templates select="dc:relation"/>
                <xsl:apply-templates select="dc:coverage"/>
                <xsl:apply-templates select="dc:rights"/>
            </table>
        </div>
    </xsl:template>
    
    <xsl:template match="oai_dc:dc/dc:*">
        <tr>
            <td class="key">
                <xsl:value-of select="name()"/>
                <xsl:if test="@xml:lang">
                    <xsl:value-of select="concat(' (', @xml:lang, ')')"/>
                </xsl:if>
            </td> 
            <td class="value {substring-after(name(), ':')}">
                <xsl:element name="div">
                    <xsl:attribute name="class">dcvalue</xsl:attribute>
                    <xsl:if test="@xml:lang">
                        <xsl:attribute name="lang"><xsl:value-of select="@xml:lang"/></xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when test="name() = 'dc:description' or name() = 'dc:source'">
                            <xsl:call-template name="createParagraphs">
                                <xsl:with-param name="text" select="."/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
                <!-- =========== plugin port =========== -->
                <xsl:apply-imports/>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template name="createParagraphs">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="contains($text,'&#xA;')">
                <p>
                <xsl:value-of select="substring-before($text,'&#xA;')"/>
                </p>
                <xsl:call-template name="createParagraphs">
                    <xsl:with-param name="text">
                        <xsl:value-of select="substring-after($text,'&#xA;')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <p>
                <xsl:value-of select="$text"/>
                </p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:ListIdentifiers">
        <div id="list-ids">
            <xsl:apply-templates select="oai:header"/>   
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:header">
        <xsl:variable name="oaiId" select="oai:identifier"/>
        <div class="oai-header">
            <h2><xsl:value-of select="oai:identifier"/></h2>
            <div class="buttons">
                <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
                    <xsl:if test="./@integral='true'">
                        <a class="oai" href="?verb=GetRecord&amp;metadataPrefix={.}&amp;identifier={$oaiId}" title="GetRecord, format {.}"><xsl:value-of select="."/></a>
                    </xsl:if>
                </xsl:for-each>
                <xsl:if test="document('')/xsl:stylesheet/format:prefixes/prefix[@integral = 'false']">
                    <a class="oai" href="?verb=ListMetadataFormats&amp;identifier={$oaiId}" title="ListMetadataFormats for {$oaiId}">all&#160;formats</a>
                </xsl:if>
                <xsl:if test="$jumpoffURL">
                    <xsl:variable name="sid" select="substring-after($oaiId, $oaiPrefix)"/>
                    <xsl:if test="starts-with($sid, $sidPrefix)">
                        <a class="oai" href="{$jumpoffURL}{$sid}" target="_blank" title="Jumpoff page for {$oaiId}">JOP</a>
                    </xsl:if>
                </xsl:if>
            </div>
            <xsl:choose>
                <xsl:when test="@status = 'deleted'">
                    <div class="deleted">DELETED</div>
                </xsl:when>
                <xsl:otherwise>
                    <div class="setSpecs">
                        <span class="key">member of sets</span>
                        <table>
                        <xsl:for-each select="oai:setSpec">
                            <xsl:sort select="."/>
                            <tr>
                                <td  class="setSpec">
                                    <span class="set"><xsl:value-of select="."/></span>
                                </td>
                                <td class="links">
                                    <a class="oai identifiers" href="?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set={.}" title="ListIdentifiers for set {.}">identifiers</a>
                                    <a class="oai records" href="?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set={.}" title="ListRecords for set {.}">records</a>
                                </td>
                            </tr>
                        </xsl:for-each>
                        </table>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
            <div class="dateStamp">
                <span class="key">datestamp</span>
                <span class="value"><xsl:value-of select="oai:datestamp"/></span>
            </div>
            
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:ListSets">
        <div id="list-sets">
            <p>Sets available from this repository:</p>
            <xsl:for-each select="oai:set">
                <xsl:sort select="oai:setSpec"/>
                <xsl:variable name="hierarchy-depth">
                    <xsl:choose>
                        <xsl:when test="contains(substring-after(oai:setSpec, ':'), ':')">
                            <xsl:value-of select="2"/>
                        </xsl:when>
                        <xsl:when test="contains(oai:setSpec, ':')">
                            <xsl:value-of select="1"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="0"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <div class="oai-set depth{$hierarchy-depth}">
                    <h2><xsl:value-of select="oai:setName"/></h2>
                    <div class="container">
                        <div class="links">
                            <a class="oai identifiers" href="?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set={oai:setSpec}" title="ListIdentifiers for set {oai:setSpec}">identifiers</a>
                            <a class="oai records" href="?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set={oai:setSpec}" title="ListRecords for set {oai:setSpec}">records</a>
                        </div>
                        <div class="setSpec"><xsl:value-of select="oai:setSpec"/></div>
                    </div>
                    <xsl:apply-templates select="oai:setDescription/oai_dc:dc/dc:description"/>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>
    
    <xsl:template match="oai:setDescription/oai_dc:dc/dc:description">
        <p class="description"><xsl:value-of select="."/></p>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:ListMetadataFormats">
        <div id="list-mdfs">
            <xsl:choose>
                <xsl:when test="$identifier">
                    <p>Metadata formats available for item <span class="identifier"><xsl:value-of select="$identifier"/></span></p>
                </xsl:when>
                <xsl:otherwise>
                    <p>Metadata formats available from this repository:</p>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="oai:metadataFormat"/>
        </div>
    </xsl:template>

    <xsl:template match="oai:metadataFormat">
        <table>
            <tr>
                <td class="key">metadataPrefix</td>
                <td class="value">
                    <span class="prefix"><xsl:value-of select="oai:metadataPrefix"/></span>
                    <xsl:choose>
                        <xsl:when test="$identifier">
                            <xsl:call-template name="mdf-identifier"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="mdf"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td class="key">metadataNamespace</td>
                <td class="value"><xsl:value-of select="oai:metadataNamespace"/></td>
            </tr>
            <tr>
                <td class="key">schema</td>
                <td class="value"><a class="url" href="{oai:schema}" target="_blank"><xsl:value-of select="oai:schema"/></a></td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template name="mdf-identifier">
        <a class="oai record" href="?verb=GetRecord&amp;metadataPrefix={oai:metadataPrefix}&amp;identifier={$identifier}">GetRecord</a>
    </xsl:template>
    <xsl:template name="mdf">
        <a class="oai identifiers {oai:metadataPrefix}" href="?verb=ListIdentifiers&amp;metadataPrefix={oai:metadataPrefix}">ListIdentifiers</a>
        <a class="oai records {oai:metadataPrefix}" href="?verb=ListRecords&amp;metadataPrefix={oai:metadataPrefix}">ListRecords</a>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:Identify">
        <div id="identify">
            <h3>Identity</h3>
            <table>
                <xsl:for-each select="*[name(.) != 'description']">
                    <tr>
                    <td class="key"><xsl:value-of select="name(.)"/></td>
                    <td class="value"><xsl:value-of select="."/></td>
                    </tr>
                </xsl:for-each>
            </table>
            <xsl:apply-templates select="oai:description"/>
        </div>
    </xsl:template>

    <xsl:template match="oai-id:oai-identifier">
        <h3>OAI Identifier</h3>
        <table>
            <xsl:for-each select="*">
                <tr>
                    <td class="key"><xsl:value-of select="name(.)"/></td>
                    <td class="value"><xsl:value-of select="."/></td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
    
    <xsl:template match="oai_dc:dc">
        <h3>Repository Description</h3>
        <xsl:call-template name="dc-table"/>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template match="oai:error">
        <div id="responseError">
            <table><tr>
                <td class="key">Error code</td>
                <td class="value"><xsl:value-of select="@code"/></td>
            </tr><tr>
                <td class="key"> </td>
                <td class="value"><xsl:value-of select="."/></td>
            </tr></table>
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    -->  
    <xsl:template name="vendor-message">
        <p class="message">
            xslt transformation:
            version: <xsl:value-of select="system-property('xsl:version')"/>,
            vendor: <xsl:value-of select="system-property('xsl:vendor')"/>,
            vendor URL: <xsl:value-of select="system-property('xsl:vendor-url')"/>
        </p>
    </xsl:template>
    <!-- 
        ======================================== 
    -->  
    <xsl:template name="render-message">
        <p class="message">OAI-PMH response rendered by a stylesheet. Click 'page source' in your browser to view the xml-output.</p>
    </xsl:template>
    <!-- 
        ======================================== 
    -->  
    <xsl:template name="navigation">
        <xsl:call-template name="topBar"/>
        <xsl:call-template name="floatingBar"/>
    </xsl:template>
    <!-- 
        ======================================== 
    -->
    <xsl:template name="topBar">
        <div class="navbar top-bar">
            <xsl:element name="a">
                <xsl:attribute name="href">?verb=Identify</xsl:attribute>
                <xsl:if test="$verb = 'Identify'">
                    <xsl:attribute name="class">requested</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="'Identify'"/>
            </xsl:element>
            <xsl:element name="a">
                <xsl:attribute name="href">?verb=ListMetadataFormats</xsl:attribute>
                <xsl:if test="$verb = 'ListMetadataFormats'">
                    <xsl:attribute name="class">requested</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="'ListMetadataFormats'"/>
            </xsl:element>
            <xsl:element name="a">
                <xsl:attribute name="href">?verb=ListSets</xsl:attribute>
                <xsl:if test="$verb = 'ListSets'">
                    <xsl:attribute name="class">requested</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="'ListSets'"/>
            </xsl:element>
            <xsl:element name="div">
                <xsl:choose>
                    <xsl:when test="$verb = 'ListIdentifiers'">
                        <xsl:attribute name="class">menu requested</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class">menu</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="'ListIdentifiers'"/>
                <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
                    <a href="?verb=ListIdentifiers&amp;metadataPrefix={.}">[<xsl:value-of select="."/>]</a>
                </xsl:for-each>
            </xsl:element>
            <xsl:element name="div">
                <xsl:choose>
                    <xsl:when test="$verb = 'ListRecords'">
                        <xsl:attribute name="class">menu requested</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class">menu</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="'ListRecords'"/>
                <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
                    <a href="?verb=ListRecords&amp;metadataPrefix={.}">format [<xsl:value-of select="."/>]</a>
                </xsl:for-each>
            </xsl:element>
            <xsl:if test="$verb = 'GetRecord'">
                <span>GetRecord</span>
            </xsl:if>
            <xsl:if test="oai:OAI-PMH/oai:error">
                <span class="error">Error</span>
            </xsl:if>
        </div> 
    </xsl:template>
    <!--
    ======================================== 
    -->
    <xsl:template name="floatingBar">
        <xsl:variable name="cursor">
            <xsl:choose>
                <xsl:when test="//oai:resumptionToken/@cursor">
                    <xsl:value-of select="number(//oai:resumptionToken/@cursor)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="0"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <div class="navbar floating-bar">
            <xsl:if test="$verb">
                <span class="verb"><xsl:value-of select="$verb"/></span>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$verb = 'ListMetadataFormats'">
                    <span>count</span>
                    <span title="formats passed"><xsl:value-of select="count(//oai:metadataFormat) + $cursor"/></span>
                </xsl:when>
                <xsl:when test="$verb = 'ListSets'">
                    <span>count</span>
                    <span title="sets passed"><xsl:value-of select="count(//oai:set) + $cursor"/></span>
                </xsl:when>
                <xsl:when test="$verb = 'ListIdentifiers'">
                    <span>count</span>
                    <span title="identifiers passed"><xsl:value-of select="count(//oai:identifier) + $cursor"/></span>
                </xsl:when>
                <xsl:when test="$verb = 'ListRecords'">
                    <span>count</span>
                    <span title="records passed"><xsl:value-of select="count(//oai:record) + $cursor"/></span>
                </xsl:when>
            </xsl:choose>
            <xsl:variable name="resumtionToken" select="//oai:resumptionToken"/>
            <xsl:if test="$resumtionToken != ''">
                <a href="?verb={$verb}&amp;resumptionToken={$resumtionToken}" title="resume {$verb}">Resume</a>
            </xsl:if>
            <a href="#top" title="Top">&#x25B2;</a>
            <a href="#bottom" title="Bottom">&#x25BC;</a>  
        </div>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
    <xsl:template name="cssStyle">
        <link rel="stylesheet" type="text/css" href="static/reset.css"/>
        <link rel="stylesheet" type="text/css" href="static/navbar.css"/>
        <link rel="stylesheet" type="text/css" href="static/content.css"/>
        <link rel="stylesheet" type="text/css" href="static/xml-html.css"/>
    </xsl:template>
    <!-- 
        ======================================== 
    --> 
</xsl:stylesheet>
