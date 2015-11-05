<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:uibk="http://igwee.uibk.ac.at/custom/ns"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs uibk"
    version="2.0">
    
    <xsl:output method="xml" indent="no"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@*">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:*">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="uibk:comment">
        <xsl:element name="anchor">
            <xsl:if test="@commentId">
                <xsl:attribute name="ref">
                    <xsl:text>#</xsl:text>
                    <xsl:value-of select="@commentId"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@type">
                <xsl:attribute name="n">
                    <xsl:value-of select="@commentId"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>