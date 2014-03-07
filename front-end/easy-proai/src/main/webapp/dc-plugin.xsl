<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:dc="http://purl.org/dc/elements/1.1/" 
      exclude-result-prefixes="xs dc"
      version="2.0">
    
    <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
    
    <xsl:variable name="pid-resolver" select="'http://www.persistent-identifier.nl/?identifier='"/>
    
    <xsl:template match="dc:*">
        <!-- catch all to prevent double values -->
    </xsl:template>
    
    <xsl:template match="dc:identifier">
        <xsl:if test="starts-with(., 'urn:nbn:nl:')">
            <a class="url" href="{$pid-resolver}{.}" target="_blank">&#8682; pid</a>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="dc:relation">
        <xsl:if test="contains(., 'URI=http')">
            <a class="url" href="{substring-after(., 'URI=')}" target="_blank">&#8682; link</a>
        </xsl:if>
	<xsl:if test="contains(., 'URI=www')">
            <a class="url" href="{concat('http://', substring-after(., 'URI='))}" target="_blank">&#8682; link</a>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="dc:coverage">
        <xsl:if test="contains(., 'projection=http://www.opengis.net/def/crs/EPSG/0/4326;')">
            <xsl:variable name="latlon" select="substring-before(substring-after(., 'φλ='), ';')"/>
            <xsl:variable name="phi" select="substring-before($latlon, ' ')"/>
            <xsl:variable name="lambda" select="substring-after($latlon, ' ')"/>
            <xsl:variable name="source" select="substring-before(substring-after(., 'source='), ';')"/>
            <xsl:variable name="position" select="substring-before(substring-after(., 'position='), ';')"/>
            <xsl:variable name="label">
                <xsl:choose>
                    <xsl:when test="$position">
                        <xsl:value-of select="concat($position, '.%20', $source)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('point.%20', $source)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <a class="url" href="http://maps.google.com/maps?q={$phi}+{$lambda}+({$label})&amp;ll={$latlon}&amp;z=16&amp;iwloc=near" target="_blank">&#8682; map</a>
        </xsl:if>
    </xsl:template>
    

</xsl:stylesheet>
