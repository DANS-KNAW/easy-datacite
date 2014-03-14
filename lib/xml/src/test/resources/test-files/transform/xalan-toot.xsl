<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:test="xalan://nl.knaw.dans.pf.language.xml.transform.XMLTransformerTest" 
version="1.0">
    <xsl:output encoding="UTF-8" method="text"/>
    <xsl:template match="/">
    	<xsl:variable name="honk">
    		<xsl:value-of select="test:getHonk()"/>
    	</xsl:variable>
        <xsl:for-each select="/root/toot[@honk= $honk]">
            <xsl:value-of select="concat(., ' ')"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
