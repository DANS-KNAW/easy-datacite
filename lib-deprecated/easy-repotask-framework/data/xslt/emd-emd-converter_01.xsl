<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
    xmlns:emd-old="http://easy.dans.knaw.nl/dms/easymetadata"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:include href="emd-simple-emd-converter_01.xsl"/>
    <xsl:include href="emd-arch-emd-converter_01.xsl"/>
    <xsl:include href="common/utils_01.xsl"/>
    
    <!-- 
        This xslt stylesheet generates easymetadata ("http://easy.dans.knaw.nl/easy/easymetadata/")
        from EasyMetadata ("http://easy.dans.knaw.nl/dms/easymetadata").
        It delegates to separate stylesheets for meta/breed 'simple' and meta/breed 'arch'.
    -->
      
    <xsl:template match="EasyMetadata">
        <xsl:result-document exclude-result-prefixes="emd-old">            
            <emd:easymetadata>              
                <xsl:attribute name="emd:version" select="0.1"/>
<xsl:text>&#xA;</xsl:text>            
<xsl:comment>
    converted from : http://easy.dans.knaw.nl/dms/easymetadata:EasyMetadata
    converted by   : emd-emd-converter_01.xsl
    conversion time: <xsl:call-template name="currentTime"/>
<xsl:text>&#xA;</xsl:text>
</xsl:comment>
<xsl:text>&#xA;</xsl:text>
                <xsl:choose>
                    <xsl:when test="emd-old:meta/breed/text()='simple'">
                        <xsl:call-template name="simpleConverter"/>
                    </xsl:when>
                    <xsl:when test="emd-old:meta/breed/text()='arch'">
                        <xsl:call-template name="archConverter"/>
                    </xsl:when>
                </xsl:choose>
            </emd:easymetadata>
        </xsl:result-document>
    </xsl:template>
    
</xsl:stylesheet>
