<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="no"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@*|*|text()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="text()[ancestor::tei:text and not(ancestor::tei:date)]">
        <xsl:call-template name="date1_TTMMJJJJ">
            <xsl:with-param name="string" select="."/>
        </xsl:call-template>        
    </xsl:template>
    
    <xsl:template name="date5_JJJJ">
        <xsl:param name="string"/>
        <xsl:analyze-string select="$string" regex="(\d{{4}})">
            <xsl:matching-substring>
                <xsl:element name="date">
                    <xsl:attribute name="when">
                        <xsl:value-of select="regex-group(1)"/>
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:value-of select="."/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="date4_mmmmJJJJ">
        <xsl:param name="string"/>
        <xsl:analyze-string select="$string" regex="(\w+)\.*\s*(\d{{4}})">
            <xsl:matching-substring>
                <xsl:variable name="month">
                    <xsl:call-template name="convertStringToMonth">
                        <xsl:with-param name="word" select="regex-group(1)"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$month!='0'">
                        <xsl:element name="date">
                            <xsl:attribute name="when">
                                <xsl:value-of select="regex-group(2)"/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="$month"/>
                            </xsl:attribute>
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="date5_JJJJ">
                            <xsl:with-param name="string" select="."/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:call-template name="date5_JJJJ">
                    <xsl:with-param name="string" select="."/>
                </xsl:call-template>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
    <xsl:template name="date3_TTmmmmJJJJ">
        <xsl:param name="string"/>
        <xsl:analyze-string select="$string" regex="(\d+)\.*\s*(\w+)\.*\s*(\d{{4}})">
            <xsl:matching-substring>
                <xsl:variable name="month">
                    <xsl:call-template name="convertStringToMonth">
                        <xsl:with-param name="word" select="regex-group(2)"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$month!='0'">
                        <xsl:element name="date">
                            <xsl:attribute name="when">
                                <xsl:value-of select="regex-group(3)"/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="$month"/>
                                <xsl:text>-</xsl:text>
                                <xsl:if test="string-length(regex-group(1))=1">
                                    <xsl:text>0</xsl:text>
                                </xsl:if>
                                <xsl:value-of select="regex-group(1)"/>
                            </xsl:attribute>
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="date4_mmmmJJJJ">
                            <xsl:with-param name="string" select="."/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:call-template name="date4_mmmmJJJJ">
                    <xsl:with-param name="string" select="."/>
                </xsl:call-template>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
    <xsl:template name="date2_MMJJJJ">
        <xsl:param name="string"/>
        <xsl:analyze-string select="$string" regex="(\d+)\.\s*(\d{{4}})">
            <xsl:matching-substring>
                <xsl:element name="date">
                    <xsl:attribute name="when">
                        <xsl:value-of select="regex-group(2)"/>
                        <xsl:text>-</xsl:text>
                        <xsl:if test="string-length(regex-group(1))=1">0</xsl:if>
                        <xsl:value-of select="regex-group(1)"/>
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:call-template name="date3_TTmmmmJJJJ">
                    <xsl:with-param name="string" select="."/>
                </xsl:call-template>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>    
    
    <xsl:template name="date1_TTMMJJJJ">
        <xsl:param name="string"/>
        <xsl:analyze-string select="$string" regex="(\d+)\.\s*(\d+)\.\s*(\d{{4}})">
            <xsl:matching-substring>
                <xsl:element name="date">
                    <xsl:attribute name="when">
                        <xsl:value-of select="regex-group(3)"/>
                        <xsl:text>-</xsl:text>
                        <xsl:if test="string-length(regex-group(2))=1">0</xsl:if>
                        <xsl:value-of select="regex-group(2)"/>
                        <xsl:text>-</xsl:text>
                        <xsl:if test="string-length(regex-group(1))=1">0</xsl:if>
                        <xsl:value-of select="regex-group(1)"/>
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:call-template name="date2_MMJJJJ">
                    <xsl:with-param name="string" select="."/>
                </xsl:call-template>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
    <xsl:template name="convertStringToMonth">
        <xsl:param name="word"/>
        <xsl:variable name="w" select="lower-case($word)"/>
        <xsl:choose>
            <xsl:when test="starts-with($w, 'jan') or starts-with($w, 'jän')">
                <xsl:text>01</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'feb')">
                <xsl:text>02</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'mar') or starts-with($w, 'mär')">
                <xsl:text>03</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'apr')">
                <xsl:text>04</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'mai')">
                <xsl:text>05</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'jun')">
                <xsl:text>06</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'jul')">
                <xsl:text>07</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'aug')">
                <xsl:text>08</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'sep')">
                <xsl:text>09</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'okt') or starts-with($w, 'oct')">
                <xsl:text>10</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'nov')">
                <xsl:text>11</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($w, 'dez') or starts-with($w, 'dec')">
                <xsl:text>12</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>0</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>