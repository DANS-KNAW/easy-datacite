<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">

	<xsl:output method="xml" indent="yes" encoding="UTF-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
		omit-xml-declaration="no" />

	<xsl:template name="choices">
		<xsl:variable name="current" select="."/>
		<em>
			<xsl:choose>
				<xsl:when test="$current='custom.disciplines'">
					currently:
					<a href="common.dcterms.audience.html">common.dcterms.audience.html</a>
					<br/>other configurations might be implemented
				</xsl:when>
				<xsl:otherwise>
					<a href="{$current}.html">
						<xsl:value-of select="$current"/><xsl:text>.html</xsl:text>
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</em>
	</xsl:template>

	<xsl:template name="propertyID">
		<xsl:variable name="current" select="."/>
		<xsl:choose>
			<xsl:when test="substring($current,1,3 )='dc.'">
				<a href="http://dublincore.org/documents/dcmi-terms/#elements-{substring($current,4)}"><xsl:value-of select="$current"/></a>
			</xsl:when>
			<xsl:when test="substring($current,1,8)='dcterms.'">
				<a href="http://dublincore.org/documents/dcmi-terms/#terms-{substring($current,9)}"><xsl:value-of select="$current"/></a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$current"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

	<xsl:template match="helpItem">
		<xsl:variable name="current" select="."/>
		<em>
			<a href="../../../easy-home/editable/help/{$current}.template">
				<xsl:value-of select="$current"/><xsl:text></xsl:text>
			</a>
		</em>
	</xsl:template>
	
	<xsl:template match="termPanel">
		<xsl:variable name="current" select="."/>
		<tr>
			<td>
				<xsl:value-of select="termName"/>
			</td>
			<td>
				<xsl:for-each select="properties/id">
					<xsl:call-template name="propertyID" />
				</xsl:for-each>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="$current[required='true']">
						<xsl:text>mandatory</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>optional</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="$current[repeating='true']">
						<xsl:text>, repeating</xsl:text>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:for-each select="$current/choiceListDefinition/properties/id">
					<xsl:call-template name="choices" />
				</xsl:for-each>
			</td>
			<td>
				<xsl:apply-templates select="properties/helpItem"/>
				<xsl:value-of select="properties/shortHelpResourceKey"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="*">
		<li>
			<ul>
				<xsl:apply-templates select="*"/>
			</ul>
		</li>
	</xsl:template>
	
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>Easy II Meta Data Format: <xsl:value-of select="//properties/id"/></title>
				<style>
					<![CDATA[
					h1 { font-size: 150% ; }
					h2 { font-size: 125% ; }
					table { border-collapse: collapse ; border: 1px solid black ; }
					td, th
					{ border: 1px solid black ;
						padding-left:0.5em; padding-right: 0.5em; 
						padding-top:0.2ex ; padding-bottom:0.2ex 
					}
					]]>
				</style>
			</head>
			<body>
				<h1>Easy II Meta Data Format: <xsl:value-of select="//properties/id"/></h1>
				<table>
					<tr>
						<td>term  name</td>
						<td>properties id</td>
						<td></td>
						<td>translation table</td>
						<td colspan="2">help</td>
					</tr>
					<xsl:apply-templates select="//panelDefinitions/termPanel"/>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
