<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"></xsl:output>
    <xsl:template match="/">
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <xsl:text>
            </xsl:text>
            <xsl:for-each select="*">
                <xsl:for-each select="*">
                    <xsl:copy-of select="."></xsl:copy-of>
                </xsl:for-each>
            </xsl:for-each>
        </oai_dc:dc>
    </xsl:template>
</xsl:stylesheet>
