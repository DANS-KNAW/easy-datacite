<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">

	<xsl:output method="xml" indent="yes" encoding="UTF-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
		omit-xml-declaration="no" />

	<xsl:template match="entry">
		<xsl:variable name="current" select="."/>
		<tr>
			<td>
				<xsl:value-of select="@key"/>
			</td>
			<td>
				<xsl:value-of select="$current"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>Easy II Meta Data Tranlastion Table</title>
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
				<p><xsl:value-of select="/properties/comment"/></p>
				<table>
					<thead>
						<tr>
							<th><strong>XML content</strong></th>
							<th><strong>displayed value</strong></th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="/properties/entry"/>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
