<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:properties="java:nl.knaw.dans.easy.tools.task.adhoc.Properties"
	xmlns:converter="java:nl.knaw.dans.easy.tools.task.adhoc.Converter"
	xmlns:fun="http://easy/migration/function">

	<xsl:template name="currentTime" xmlns:date="java:java.util.Date">
		<xsl:value-of select="date:new()" />
	</xsl:template>

	<xsl:template name="getComment">
		<xsl:value-of select="properties:getComment()" />
	</xsl:template>

	<xsl:template name="getCollectionId">
		<xsl:value-of select="properties:getCollectionId()" />
	</xsl:template>

	<xsl:template name="normalizeDateTime">
		<xsl:param name="dateString" />
		<xsl:value-of select="converter:normalizeDateTime($dateString)" />
	</xsl:template>

	<xsl:template name="getDatasetAccessCategory">
		<xsl:param name="oldValue" />
		<xsl:value-of select="converter:getDatasetAccessCategory($oldValue)" />
	</xsl:template>

	<xsl:template name="getConversionProperties">
		<xsl:element name="property-list">

			<xsl:element name="comment">
				<xsl:call-template name="getComment" />
			</xsl:element>

			<xsl:element name="entry">
				<xsl:attribute name="key">
                    <xsl:value-of select="'conversion.date'" />
                </xsl:attribute>
				<xsl:call-template name="currentTime" />
			</xsl:element>

			<xsl:element name="entry">
				<xsl:attribute name="key">
                    <xsl:value-of select="'previous.collection-id'" />
                </xsl:attribute>
				<xsl:call-template name="getCollectionId" />
			</xsl:element>

		</xsl:element>
	</xsl:template>
	
	<xsl:function name="fun:convertDateFormat">
		<xsl:param name="oldFormat"/>
		<xsl:choose>
			<xsl:when test="$oldFormat='yyyy'">
				<xsl:value-of select="'YEAR'"/>
			</xsl:when>
			<xsl:when test="$oldFormat='yyyy-MM'">
				<xsl:value-of select="'MONTH'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'DAY'"/>
			</xsl:otherwise>
		</xsl:choose>  
	</xsl:function>

</xsl:stylesheet>
