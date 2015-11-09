<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:uibk="http://igwee.uibk.ac.at/custom/ns"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="persNamePattern">^P:\s+(.*)$</xsl:param>
    <xsl:param name="placeNamePattern">^O:\s+(.*)$</xsl:param>
    <xsl:param name="indexPattern">^S:\s+(.*?)$</xsl:param>
    
    <xsl:output indent="no" method="xml"/>
    
    <xsl:template match="/">
        <xsl:variable name="toRef">
            <xsl:apply-templates mode="rsToRef"/>
        </xsl:variable>
        
        <xsl:variable name="refCleaned">
            <xsl:apply-templates select="$toRef" mode="cleanNote"/>
        </xsl:variable>
        
        <xsl:apply-templates select="$refCleaned" mode="intelligentCopy"/>
        
    </xsl:template>
    
    <xsl:template match="@*|*|text()|comment()" mode="intelligentCopy">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="intelligentCopy"/>
            <xsl:apply-templates select="node()|comment()" mode="intelligentCopy"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:titleStmt" mode="intelligentCopy">
        <xsl:copy>
            <xsl:apply-templates mode="intelligentCopy" select="@*"/>
            <xsl:apply-templates mode="intelligentCopy"/>
            <xsl:if test="not(tei:title)">
                <title>
                    <xsl:choose>
                        <xsl:when test="//tei:head">
                            <xsl:apply-templates select="//tei:head[1]" mode="intelligentCopy"/>
                        </xsl:when>
                        <xsl:when test="//tei:text//tei:p">
                            <xsl:apply-templates select="//tei:text//tei:p[1]" mode="intelligentCopy"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>UNKNOWN</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </title>
            </xsl:if>
            <xsl:if test="not(tei:author)">
                <author>UNKNOWN</author>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:titleStmt/tei:author" mode="intelligentCopy">
        <xsl:copy>
            <xsl:apply-templates mode="intelligentCopy" select="@*"/>
            <xsl:apply-templates mode="intelligentCopy"/>
            <xsl:if test="string()=''">
                <xsl:text>UNKNOWN</xsl:text>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:titleStmt/tei:title" mode="intelligentCopy">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="intelligentCopy"/>
            <xsl:apply-templates mode="intelligentCopy"/>
            <xsl:if test="string()=''">
                <xsl:text>UNKNOWN</xsl:text>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    
    <xsl:template mode="cleanNote" match="@*|*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="cleanNote"/>
            <xsl:apply-templates mode="cleanNote"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template mode="cleanNote" match="tei:note[@place='comment']">
        <!-- DELETE THIS -->
    </xsl:template>
    
    <xsl:template mode="cleanNote" match="tei:note[@place='commentAdded']">
        <xsl:copy>
            <xsl:apply-templates select="@*[not(name()='place')]" mode="cleanNote"/>
            <xsl:attribute name="place">comment</xsl:attribute>
            <xsl:apply-templates mode="cleanNote"></xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    
    <xsl:template match="@*|text()|comment()" mode="rsToRef">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="*" mode="rsToRef">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="rsToRef"/>
            <xsl:apply-templates mode="rsToRef"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:rs" mode="rsToRef">
        <xsl:variable name="refedId" select="@ref"/>
        <xsl:variable name="refedNote">
            <xsl:call-template name="searchForNote">
                <xsl:with-param name="ref" select="$refedId"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$refedNote!=''">
                <xsl:choose>
                    <xsl:when test="count($refedNote/tei:note/tei:p)=0">
                        <xsl:call-template name="createRefNote">
                            <xsl:with-param name="noteNode" select="$refedNote"/>
                            <xsl:with-param name="rsNode" select="."/>
                            <xsl:with-param name="hintString" select="string($refedNote)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="createRefNote">
                            <xsl:with-param name="noteNode" select="$refedNote"/>
                            <xsl:with-param name="rsNode" select="."/>
                            <xsl:with-param name="hintString" select="string($refedNote/tei:note[1]/tei:p[1])"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="createRefNote">
        <xsl:param name="rsNode"/>
        <xsl:param name="hintString"/>
        <xsl:param name="noteNode"/>
        <xsl:variable name="nameAndKey">
            <xsl:call-template name="getElementNameAndKeyValue">
                <xsl:with-param name="hintString" select="$hintString"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="elementName" select="$nameAndKey/uibk:nameAndKey/uibk:name"/>
        <xsl:variable name="keyValue" select="$nameAndKey/uibk:nameAndKey/uibk:key"/>
        <xsl:element name="rs">
            <xsl:if test="$elementName">
                <xsl:attribute name="type">
                    <xsl:value-of select="$elementName"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$noteNode/tei:note/@resp">
                <xsl:attribute name="resp" select="$noteNode/tei:note/@resp"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$keyValue!=''">
                    <xsl:attribute name="key">
                        <xsl:value-of select="$keyValue"/>
                    </xsl:attribute>
                    <xsl:if test="count($noteNode/tei:note/tei:p) &gt; 1">
                        <xsl:attribute name="target">
                            <xsl:value-of select="$rsNode/@ref"/>
                        </xsl:attribute>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="target">
                        <xsl:value-of select="$rsNode/@ref"/>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="$rsNode/node()" mode="rsToRef"/>
        </xsl:element>
        <xsl:choose>
            <xsl:when test="string($keyValue)=''">
                <note>
                    <xsl:apply-templates select="$noteNode/tei:note/@*[not(name()='place')]" mode="rsToRef"/>
                    <xsl:attribute name="place">commentAdded</xsl:attribute>
                    <xsl:apply-templates select="$noteNode/tei:note/tei:*"/>
                </note>
            </xsl:when>
            <xsl:when test="count($noteNode/tei:note/tei:p) &gt; 1">
                <note>
                    <xsl:attribute name="place">commentAdded</xsl:attribute>
                    <xsl:apply-templates select="$noteNode/tei:note/@*[not(name()='place')]" mode="rsToRef"/>
                    <xsl:for-each select="$noteNode/tei:note/tei:p">
                        <xsl:if test="position()!=1">
                            <xsl:apply-templates select="." mode="rsToRef"/>
                        </xsl:if>
                    </xsl:for-each>
                </note>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template name="getElementNameAndKeyValue">
        <xsl:param name="hintString"/>
        <xsl:choose>
            <xsl:when test="matches($hintString, $persNamePattern)">
                <xsl:analyze-string select="$hintString" regex="{$persNamePattern}">
                    <xsl:matching-substring>
                        <uibk:nameAndKey>
                            <uibk:name>person</uibk:name>
                            <uibk:key><xsl:value-of select="regex-group(1)"/></uibk:key>
                        </uibk:nameAndKey>
                    </xsl:matching-substring>
                </xsl:analyze-string>
            </xsl:when>
            <xsl:when test="matches($hintString, $placeNamePattern)">
                <xsl:analyze-string select="$hintString" regex="{$placeNamePattern}">
                    <xsl:matching-substring>
                        <uibk:nameAndKey>
                            <uibk:name>place</uibk:name>
                            <uibk:key><xsl:value-of select="regex-group(1)"/></uibk:key>
                        </uibk:nameAndKey>
                    </xsl:matching-substring>
                </xsl:analyze-string>
            </xsl:when>
            <xsl:when test="matches($hintString, $indexPattern)">
                <xsl:analyze-string select="$hintString" regex="{$indexPattern}">
                    <xsl:matching-substring>
                        <uibk:nameAndKey>
                            <uibk:name>keyword</uibk:name>
                            <uibk:key><xsl:value-of select="regex-group(1)"/></uibk:key>
                        </uibk:nameAndKey>
                    </xsl:matching-substring>
                </xsl:analyze-string>
            </xsl:when>
            <xsl:otherwise>
                <uibk:nameAndKey>
                    <uibk:name>remark</uibk:name>
                </uibk:nameAndKey>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template name="searchForNote">
        <xsl:param name="ref"/>
        <xsl:variable name="id">
            <xsl:choose>
                <xsl:when test="starts-with($ref,'#')">
                    <xsl:value-of select="substring-after($ref,'#')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$ref"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:copy-of select="//tei:note[@xml:id=$id]"></xsl:copy-of>
    </xsl:template>
    
    
    
    
</xsl:stylesheet>