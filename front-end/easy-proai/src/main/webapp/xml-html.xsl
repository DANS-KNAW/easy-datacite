<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      exclude-result-prefixes="xs xsi"
      version="2.0">
    
    <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
    
    
    <xsl:template name="xmlToHtml">
        <xsl:param name="print-xml-declaration" select="true()"/>
        <div class="xml-doc">
            <xsl:if test="$print-xml-declaration">
                <div class="xml-declaration">&lt;?xml version="1.0" encoding="UTF-8"?&gt;</div>
            </xsl:if>
            <xsl:apply-templates select="." mode="xml"/>
        </div>
    </xsl:template>
    
    <xsl:template match="node()" mode="xml">
        <xsl:param name="repeat-xsi-namespace"/>
        <div class="xml"><span class="element-name">&lt;<xsl:value-of select="name()"/>
            <xsl:call-template name="namespaces"/>
            <xsl:apply-templates select="attribute::node()" mode="xml"/>&gt;</span>
            <span class="element-text"><xsl:value-of select="text()[1]"/></span>
            <xsl:apply-templates select="child::*" mode="xml"/>
            <span class="element-name">&lt;/<xsl:value-of select="name()"/>&gt;</span>
        </div>
    </xsl:template>
    
    <xsl:template match="@schemaLocation" mode="xml">
        <span class="schemaLocation"><xsl:value-of select="name()"/>=</span><span class="sl-text">"<xsl:value-of select="."/>"</span>
    </xsl:template>
    
    <xsl:template match="@*" mode="xml" priority="-100">
        <xsl:text>&#160;</xsl:text><span class="attr-name"><xsl:value-of select="name()"/>=</span><span class="attr-text">"<xsl:value-of select="."/>"</span>
    </xsl:template>
    
    <xsl:template name="namespaces">
        <!-- Unfortunately Firefox will not follow the namespace::axis.
            See https://developer.mozilla.org/en-US/docs/Common_XSLT_Errors -->
        <xsl:for-each select="namespace::*[name()!='xml'][not(.=ancestor::*[position()>1]/namespace::*)]">
            <p>
                <span class="ns-name">
                    <xsl:choose>
                        <xsl:when test="name() = ''">
                            <xsl:text>xmlns</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>xmlns:</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="name()"/>=</span><span class="ns-text">"<xsl:value-of select="."/>"</span>
            </p>
        </xsl:for-each>
        <!-- repeat xsi namespace on every element that has a @xsi:schemaLocation -->
        <xsl:if test="@xsi:schemaLocation">
            <p>
                <span class="ns-name">xmlns:<xsl:value-of select="substring-before(name(@xsi:schemaLocation), ':')"/>=</span>
                <span class="ns-text">"<xsl:value-of select="namespace-uri(@xsi:schemaLocation)"/>"</span>
            </p>
        </xsl:if>
    </xsl:template>
    
    <!-- Can be called with
        <xsl:apply-templates select="preceding-sibling::comment()[1]" mode="xml"/>
        but not working as expected. -->
    <!--xsl:template match="comment()" mode="xml">
        <p class="comment">
            <span><xsl:value-of select="."/></span>
        </p>
    </xsl:template-->

</xsl:stylesheet>
