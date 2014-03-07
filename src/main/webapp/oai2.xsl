<?xml version="1.0" encoding="utf-8"?>
<!-- 
  A stylesheet for rendering OAI-PMH 2.0 output.
  Based on a stylesheet by Christopher Gutteridge, of the University of Southampton.
	DANS adaptations. version 2012-12.
	
	This stylesheet is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
-->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:oai="http://www.openarchives.org/OAI/2.0/"
  xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:format="metadata:format:prefixes"
  exclude-result-prefixes="xsl oai oai_dc dc format">

<!-- 
  A list of metadataPrefixes of formats available from this repository. 
  Attribute 'integral' specifies whether the metadata format is available
  for all records. 
-->
  <format:prefixes>
    <prefix integral="true">oai_dc</prefix>
    <prefix integral="true">nl_didl</prefix>
    <prefix integral="true">didl</prefix>
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
  <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>


  <xsl:template name="style">
body { 
  color: #102020;
  margin: 1em 2em 1em 2em;
  font-family: Verdana, Geneva, sans-serif;
  font-size: 90%;
}
  h1, h2, h3 {
  font-family: sans-serif;
  clear: left;
  color: #0090B3;
}
h1 {
  padding-bottom: 4px;
  margin-bottom: 0px;
}
h2 {
  margin-bottom: 0.5em;
}
h3 {
  margin-bottom: 0.3em;
  font-size: medium;
}
table.error {
  color: red;
  font-weight: bold;
}
table.set {
  width: 40em;
}
table.dcdata {
  border-collapse: collapse;
}
td.value {
	vertical-align: top;
	text-align: left;
	padding: 3px 5px 3px 5px;
}
td.hier1 {
	color: #FFFFFF;
	background-color: #0090B3;
	padding: 3px 7px 3px 7px;
	text-align: left;
	font-weight: bold;
	vertical-align: top;
}
td.hier2 {
  color: #0090B3;
  background-color: #EFF5FB;
  font-weight: bold;
  padding: 3px 7px 3px 7px;
  border:1px solid #0090B3;
  text-align: left;
  vertical-align: top;
}
td.hier3 {
  color: #747474;
  font-weight: bold;
  padding: 3px 7px 3px 7px;
  border:1px solid #BBBBBB;
  text-align: left;
  vertical-align: top;
}
td.hierred {
  color: #FFFFFF;
  background-color: red;
  font-weight: bold;
  padding: 3px 7px 3px 7px;
  text-align: left;
  vertical-align: top;
}

td.maxw {
  min-width: 30em;
}
td.al_right {
  text-align: right;
}
td.setSpec {
  padding: 3px 7px 3px 7px;
  font-weight: bold;
}

.buttons {
  padding: 3px 7px 7px 0px;
}

.link {
  color: #FFFFFF;
  background-color: #BDBDBD;
  font-weight: bold;
  text-decoration: none !important;
  padding: 2px 9px 2px 9px;
  min-width: 10em;
  border-left: 3px solid white;
  height: 1em; 
  font-size: 90%;
}
.link:hover {
  color: #102020;
  background-color: #EDEDED;
  text-decoration: none !important;
}

a {
  color: #00A7D4;
  text-decoration: none !important;
  font-family: Verdana, Geneva, sans-serif !important;
  font-size: 100%;
}
a:hover {
  text-decoration: underline !important;
  color: #156083;
}
a.action {
  color:#00A7D4 !important;
}
a.action:hover{
  color: #156083;
}

h2.oaiRecordTitle {
	color: #FFFFFF;
	background-color: #0090B3;
	font-size: medium;
	font-weight: bold;
	padding: 7px 9px 7px 9px;
	margin: 9px 0px 7px 0px;
	width: 47em;
}
.oaiRecord {
	margin-bottom: 3em;
}

.oaiHeader {
  margin-bottom: 2em;
}

.results {
	margin-bottom: 1.5em;
}

p.intro {
  margin-top: 1.5em;
  margin-bottom: 1.5em;
	font-size: 80%;
}
#navbar {
  margin: 0;
  padding: 0;
  height: 1em; 
  font-size: 90%;
}
#navbar li {
  list-style: none;
  float: left; 
}
#navbar li a {
  display: block;
  color: #FFFFFF;
  background-color: #BDBDBD;
  font-weight: bold;
  text-decoration: none !important;
  padding: 2px 9px 2px 9px;
  min-width: 10em;
  border-left: 1px solid white;
  border-bottom: 1px solid white;
}
#navbar li a:hover {
  color: #102020;
  background-color: #EDEDED;
}
#navbar li ul {
  display: none; 
  min-width: 10em;
}
#navbar li:hover ul, #navbar li.hover ul {
  display: block;
  position: absolute;
  margin: 0;
  padding: 0; 
}
#navbar li:hover li, #navbar li.hover li {
  float: none; 
}
#navbar li:hover li a, #navbar li.hover li a {
  background-color: #BDBDBD;
  color: #FFFFFF;
  border-bottom: 1px solid white;
}
#navbar li li a:hover {
  color: #102020;
  background-color: #EDEDED;
  border-bottom: 1px solid white;
}

  <xsl:call-template name="xmlstyle"/>
</xsl:template>

  <xsl:variable name="identifier" select="//oai:request/@identifier"/>
  <xsl:variable name="verb" select="//oai:request/@verb"/>
  <xsl:variable name="mdFormat" select="//oai:request/@metadataPrefix"/>
  <!-- 
    ==================================================================
  -->
  <xsl:template match="/">
    <xsl:variable name="titlePrefix">
      <xsl:choose>
        <xsl:when test="//oai:error">
          <xsl:value-of select="'Error'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$verb"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <html>
      <head>
        <title><xsl:value-of select="$titlePrefix"/> - OAI-PMH 2.0 Request Results</title>
        <style type="text/css"><xsl:call-template name="style"/></style>
      </head>
      <body>
        
        <a id="top"></a>
        <h1>OAI-PMH 2.0 Request Results - <xsl:value-of select="$titlePrefix"/></h1>
        
        <xsl:call-template name="navigation"/>
        <xsl:apply-templates select="/oai:OAI-PMH"/>
        <xsl:call-template name="floating-bar"/>
        <hr/>
        <xsl:call-template name="render-message"/>
        <a id="bottom"></a>
      </body>
    </html>
  </xsl:template>
  <!-- 
    ==================================================================
  -->
  <xsl:template name="floating-bar">
    <style>
div.bar {
bottom: 10px;
right: 20px;
position: fixed;
font-size: 125%;
z-index: 100;
}
div.bar a {
color: #FFFFFF;
background-color: #848484;
padding: 2px 9px 2px 9px;
text-decoration: none !important;
border-left: 1px solid white;
}
div.bar a:active {
background-color: red;
}
div.bar span {
color: #FFFFFF;
background-color: #0090B3;
padding: 2px 9px 2px 9px;
border-left: 1px solid white;
}
      
    </style>
    <div class="bar">
        <xsl:variable name="cursor">
          <xsl:choose>
            <xsl:when test="string(number(//oai:resumptionToken/@cursor)) = 'NaN'">
              <xsl:value-of select="0"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//oai:resumptionToken/@cursor"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
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
          <a href="?verb={$verb}&amp;resumptionToken={$resumtionToken}" title="resume {$resumtionToken}">Resume</a>
        </xsl:if>
        <a href="#top" title="Top">&#x25B2;</a>
        <a href="#bottom" title="Bottom">&#x25BC;</a>
      </div>
  </xsl:template>
  
  <xsl:template name="navigation">
    <!-- IE will not show the drop-down menu -->
    <p/>
    <ul id="navbar">
      <li><a href="?verb=Identify">Identify</a></li>
      <li><a href="?verb=ListMetadataFormats">ListMetadataFormats</a></li>
      <li><a href="?verb=ListSets">ListSets</a></li>
      <li><a href="?verb=ListIdentifiers&amp;metadataPrefix=oai_dc">ListIdentifiers</a><ul>
        <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
          <li><a href="?verb=ListIdentifiers&amp;metadataPrefix={.}">[<xsl:value-of select="."/>]</a></li>
        </xsl:for-each>
      </ul>
      </li>
      <li><a href="?verb=ListRecords&amp;metadataPrefix=oai_dc">ListRecords</a><ul>
        <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
          <li><a href="?verb=ListRecords&amp;metadataPrefix={.}">format [<xsl:value-of select="."/>]</a></li>
        </xsl:for-each>
      </ul>
      </li>
    </ul>
    <p/>
    <xsl:call-template name="render-message"/>
  </xsl:template>

  <xsl:template name="render-message">
    <p class="intro">OAI-PMH response rendered by a stylesheet. Click 'page source' in your browser to view the xml-output.</p>
  </xsl:template>
  <!-- 
    ==================================================================
  -->
  <xsl:template match="/oai:OAI-PMH">
    <table class="values">
      <tr>
        <td class="hier1">Datestamp of response</td>
        <td class="value">
          <xsl:value-of select="oai:responseDate"/>
        </td>
      </tr>
      <tr>
        <td class="hier1">Request URL</td>
        <td class="value">
          <xsl:value-of select="oai:request"/>
        </td>
      </tr>
    </table>
    <hr/>
    <p/>
    <xsl:choose>
      <xsl:when test="oai:error">
        <div class="results">
          <xsl:apply-templates select="oai:error"/>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <div class="results">
          <xsl:apply-templates select="oai:Identify"/>
          <xsl:apply-templates select="oai:GetRecord"/>
          <xsl:apply-templates select="oai:ListRecords"/>
          <xsl:apply-templates select="oai:ListSets"/>
          <xsl:apply-templates select="oai:ListMetadataFormats"/>
          <xsl:apply-templates select="oai:ListIdentifiers"/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- ERROR -->

  <xsl:template match="/oai:OAI-PMH/oai:error">
    <table class="error">
      <tr>
        <td class="hierred">Error Code</td>
        <td class="value">
          <xsl:value-of select="@code"/>
        </td>
      </tr>
      <tr>
        <td class="hierred"> </td>
        <td class="value">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </table>
  </xsl:template>

  <!-- IDENTIFY -->

  <xsl:template match="/oai:OAI-PMH/oai:Identify">
    <h3>Identity</h3>
    <table class="values">
      <tr>
        <td class="hier3">Repository Name</td>
        <td class="value">
          <xsl:value-of select="oai:repositoryName"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Base URL</td>
        <td class="value">
          <xsl:value-of select="oai:baseURL"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Protocol Version</td>
        <td class="value">
          <xsl:value-of select="oai:protocolVersion"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Earliest Datestamp</td>
        <td class="value">
          <xsl:value-of select="oai:earliestDatestamp"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Deleted Record Policy</td>
        <td class="value">
          <xsl:value-of select="oai:deletedRecord"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Granularity</td>
        <td class="value">
          <xsl:value-of select="oai:granularity"/>
        </td>
      </tr>
      <xsl:apply-templates select="oai:adminEmail"/>
    </table>
    <xsl:apply-templates select="oai:description"/>
  </xsl:template>

  <xsl:template match="/oai:OAI-PMH/oai:Identify/oai:adminEmail">
    <tr>
      <td class="hier3">Admin Email</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <!--
   Identify / Unsupported Description
-->
  <xsl:template match="oai:description/*" priority="-100">
    <h2>Unsupported Description Type</h2>
    <p>The XSL currently does not support this type of description.</p>
    <div class="xmlSource">
      <xsl:apply-templates select="." mode="xmlMarkup"/>
    </div>
  </xsl:template>


  <!--
   Identify / OAI-Identifier
-->
  <xsl:template match="id:oai-identifier" xmlns:id="http://www.openarchives.org/OAI/2.0/oai-identifier">
    <h3>OAI-Identifier</h3>
    <table class="values">
      <tr>
        <td class="hier3">Scheme</td>
        <td class="value">
          <xsl:value-of select="id:scheme"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Repository Identifier</td>
        <td class="value">
          <xsl:value-of select="id:repositoryIdentifier"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Delimiter</td>
        <td class="value">
          <xsl:value-of select="id:delimiter"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">Sample OAI Identifier</td>
        <td class="value">
          <xsl:value-of select="id:sampleIdentifier"/>
        </td>
      </tr>
    </table>
  </xsl:template>


  <!-- GetRecord -->

  <xsl:template match="oai:GetRecord">
    <xsl:apply-templates select="oai:record"/>
  </xsl:template>

  <!-- ListRecords -->

  <xsl:template match="oai:ListRecords">
    <h3>Records</h3>
    <xsl:apply-templates select="oai:record"/>
  </xsl:template>

  <!-- ListIdentifiers -->

  <xsl:template match="oai:ListIdentifiers">
    <h3>Identifiers</h3>
    <xsl:apply-templates select="oai:header"/>
  </xsl:template>

  <!-- ListSets -->

  <xsl:template match="oai:ListSets">
    <h3>Sets</h3>
    <table class="set">
      <xsl:for-each select="oai:set">
        <xsl:sort select="oai:setSpec"/>
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="oai:set">
    <xsl:choose>
      <xsl:when test="contains(substring-after(oai:setSpec, ':'), ':')">
        <tr>
          <td class="hier3" colspan="2">
            <xsl:value-of select="oai:setName"/>
          </td>
        </tr>
      </xsl:when>
      <xsl:when test="contains(oai:setSpec, ':')">
        <tr>
          <td class="hier2" colspan="2">
            <xsl:value-of select="oai:setName"/>
          </td>
        </tr>
      </xsl:when>
      <xsl:otherwise>
        <tr>
          <td class="hier1" colspan="2">
            <xsl:value-of select="oai:setName"/>
          </td>
        </tr>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="oai:setDescription/oai_dc:dc/dc:description"/>
    <xsl:call-template name="oai:setSpec2"/>
    <tr>
      <td colspan="2">
        <p/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="oai:setSpec2">
    <tr>
      <td class="setSpec"><xsl:value-of select="oai:setSpec"/></td>
      <td class="al_right">
        <a class="link" href="?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set={oai:setSpec}">identifiers</a>
        <a class="link" href="?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set={oai:setSpec}">&#160;&#160;records&#160;&#160;</a>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:setDescription/oai_dc:dc/dc:description">
    <tr>
      <td colspan="2">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <!-- ListMetadataFormats -->

  <xsl:template match="oai:ListMetadataFormats">
    <xsl:choose>
      <xsl:when test="$identifier">
        <p>This is a list of metadata formats available for the record <b><xsl:value-of select="$identifier"/></b></p>
        <p>Use these links to view the metadata: 
          <xsl:apply-templates select="oai:metadataFormat/oai:metadataPrefix"/></p>
      </xsl:when>
      <xsl:otherwise>
        <p>This is a list of metadata formats available from this archive.</p>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="oai:metadataFormat"/>
  </xsl:template>

  <xsl:template match="oai:metadataFormat">
    <p/>
    <table class="values">
      <tr>
        <td class="hier3">metadataPrefix</td>
        <td class="value">
          <xsl:value-of select="oai:metadataPrefix"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">metadataNamespace</td>
        <td class="value">
          <xsl:value-of select="oai:metadataNamespace"/>
        </td>
      </tr>
      <tr>
        <td class="hier3">schema</td>
        <td class="value">
          <a href="{oai:schema}">
            <xsl:value-of select="oai:schema"/>
          </a>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="oai:metadataPrefix">
    <xsl:text> </xsl:text>
    <a class="link" href="?verb=GetRecord&amp;metadataPrefix={.}&amp;identifier={$identifier}">
      <xsl:value-of select="."/>
    </a>
  </xsl:template>

  <!-- record object -->

  <xsl:template match="oai:record">
    <div class="oaiRecord">
      <xsl:apply-templates select="oai:header"/>
      <xsl:apply-templates select="oai:metadata"/>
      <xsl:apply-templates select="oai:about"/>
    </div>
  </xsl:template>

  <xsl:template match="oai:header">
    <div class="oaiHeader">
    <xsl:variable name="oaiId" select="oai:identifier"/>
    <h2 class="oaiRecordTitle"><xsl:value-of select="$oaiId"/></h2>
    <div class="buttons">
      <xsl:for-each select="document('')/xsl:stylesheet/format:prefixes/prefix">
        <xsl:if test="./@integral='true'">
          <a class="link" href="?verb=GetRecord&amp;metadataPrefix={.}&amp;identifier={$oaiId}"><xsl:value-of select="."/></a>
        </xsl:if>
      </xsl:for-each>
      <xsl:if test="document('')/xsl:stylesheet/format:prefixes/prefix[@integral = 'false']">
        <a class="link" href="?verb=ListMetadataFormats&amp;identifier={oai:identifier}">all&#160;formats</a>
      </xsl:if>
      <xsl:if test="$jumpoffURL">
        <xsl:variable name="sid" select="substring-after(oai:identifier, $oaiPrefix)"/>
        <xsl:if test="starts-with($sid, $sidPrefix)">
          <a class="link" href="{$jumpoffURL}{$sid}" target="_blank">JOP</a>
        </xsl:if>
      </xsl:if>
    </div>
    <table class="values">
      <tr>
        <td class="hier3">OAI Identifier</td>
        <td class="value" colspan="2">
          <xsl:value-of select="oai:identifier"/>
          
        </td>
      </tr>
      <tr>
        <td class="hier3">Datestamp</td>
        <td class="value">
          <xsl:value-of select="oai:datestamp"/>
        </td>
        <td/>
      </tr>
      <xsl:apply-templates select="oai:setSpec"/>
    </table>
    <xsl:if test="@status='deleted'">
      <p>This record has been deleted.</p>
    </xsl:if>
    </div>
  </xsl:template>


  <xsl:template match="oai:about">
    <p>"about" part of record container not supported by the XSL</p>
  </xsl:template>

  <xsl:template match="oai:metadata">
  <div class="metadata">
      <xsl:apply-templates select="*"/>
  </div>
  </xsl:template>

  <!-- oai setSpec object -->

  <xsl:template match="oai:setSpec">
    <tr>
      <td class="hier3">setSpec</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
      <td class="maxw">
        <a class="link identifiers" href="?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set={.}">identifiers</a>
        <a class="link spRecords" href="?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set={.}">&#160;&#160;records&#160;&#160;</a>
      </td>
    </tr>
  </xsl:template>



  <!-- unknown metadata format -->

  <xsl:template match="oai:metadata/*" priority="-100">
    <h3>Metadata</h3>
    <div class="xmlSource">
      <xsl:apply-templates select="." mode="xmlMarkup"/>
    </div>
  </xsl:template>

  <!-- oai_dc record -->

  <xsl:template match="oai_dc:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">
    <div>
      <h3>Dublin Core Metadata (oai_dc)</h3>
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

  <xsl:template match="dc:title" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Title</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:creator" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Creator</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:subject" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Subject</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:description" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Description</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:publisher" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Publisher</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:contributor" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Contributor</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:date" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Date</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:type" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Type</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:format" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Format</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:identifier" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Identifier</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:source" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Source</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:language" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Language</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:relation" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Relation</td>
      <td class="value">
        <xsl:choose>
          <xsl:when test="starts-with(.,&quot;http&quot; )">
            <xsl:choose>
              <xsl:when test="string-length(.) &gt; 50">
                <a class="link" href="{.}">URL</a>
                <i> URL not shown as it is very long.</i>
              </xsl:when>
              <xsl:otherwise>
                <a href="{.}">
                  <xsl:value-of select="."/>
                </a>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:coverage" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Coverage</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:rights" xmlns:dc="http://purl.org/dc/elements/1.1/">
    <tr>
      <td class="hier2">Rights</td>
      <td class="value">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <!-- XML Pretty Maker -->

  <xsl:template match="node()" mode="xmlMarkup">
    <div class="xmlBlock">
      <span class="xmlTagName">&lt;<xsl:value-of select="name(.)"/></span>
      <xsl:apply-templates select="@*" mode="xmlMarkup"/>
      <span class="xmlTagName">&gt;</span>
      <xsl:apply-templates select="node()" mode="xmlMarkup"/>
      <span class="xmlTagName">&lt;/<xsl:value-of select="name(.)"/>&gt;</span>
    </div>
  </xsl:template>

  <xsl:template match="text()" mode="xmlMarkup">
    <span class="xmlText">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="@*" mode="xmlMarkup">
    <xsl:text> </xsl:text>
    <span class="xmlAttrName"><xsl:value-of select="name()"/>=</span>
    <span class="xmlAttrValue">"<xsl:value-of select="."/>"</span>
  </xsl:template>

  <xsl:template name="xmlstyle"
    >
.xmlSource {
	font-size: 80%;
	/* border: solid #c0c0a0 1px; */
	background-color: #FAFBFC;
	padding: 1em 1em 1em 0em;
}
.xmlBlock {
	padding-left: 1em;
}
.xmlTagName {
	color: #3B64EF;
	/* font-weight: bold; */
}
.xmlAttrName {
	color: red; /* #F70EE1; */
	/* font-weight: bold; */
}
.xmlAttrValue {
	color: #506080;
}
</xsl:template>

</xsl:stylesheet>
