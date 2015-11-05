<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    xmlns:uibk="http://igwee.uibk.ac.at/custom/ns"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output indent="no" method="xml"/>
    
    
    <xsl:template match="/">
        <xsl:variable name="hiToMilestone">
            <xsl:apply-templates mode="hiToMilestones"/>
        </xsl:variable>
        
        <xsl:variable name="useRS">
            <xsl:call-template name="elimComment">
                <xsl:with-param name="node" select="$hiToMilestone"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:variable name="backToHi">
            <xsl:call-template name="backToHi">
                <xsl:with-param name="node" select="$useRS"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:copy-of select="$backToHi"/>
    </xsl:template>
    
    <xsl:template name="elimComment">
        <xsl:param name="node"/>
        <xsl:variable name="result">
            <xsl:apply-templates select="$node" mode="elimateCommentStartEnd"/>
        </xsl:variable>
        <xsl:variable name="result2">
            <xsl:choose>
                <xsl:when test="$result//uibk:comment">
                    <xsl:variable name="testRun">
                        <xsl:apply-templates select="$result" mode="elimateCommentStartEnd"/>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$testRun!=$result">
                            <xsl:call-template name="elimComment">
                                <xsl:with-param name="node" select="$testRun"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$testRun"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$result"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:copy-of select="$result2"></xsl:copy-of>
    </xsl:template>
    
    <xsl:template mode="elimateCommentStartEnd" match="@*|comment()">
        <xsl:copy>
            <xsl:apply-templates mode="elimateCommentStartEnd" select="@*"/>
            <xsl:apply-templates select="text()|comment()|*" mode="elimateCommentStartEnd"/>
        </xsl:copy>
    </xsl:template>
    
    
    
    <xsl:template match="text()|*" mode="elimateCommentStartEnd">
        <xsl:variable name="prevUibkComment" select="preceding-sibling::uibk:comment[1]"/>
        <xsl:variable name="nextUibkComment" select="following-sibling::uibk:comment[1]"/>
        <xsl:choose>
            <xsl:when test="$prevUibkComment/@commentId = $nextUibkComment/@commentId">
                <!-- DELETED -->
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*" mode="elimateCommentStartEnd"/>
                    <xsl:apply-templates select="text()|*" mode="elimateCommentStartEnd"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    
    <xsl:template mode="elimateCommentStartEnd" match="uibk:comment[@type='start']">
        <xsl:variable name="commentId" select="@commentId"/>
        <xsl:variable name="nextUibkComment" select="following-sibling::uibk:comment[1]"/>
        <xsl:variable name="canConvert">
            <xsl:if test="$nextUibkComment/@type='end' and $nextUibkComment/@commentId=$commentId">yes</xsl:if>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$canConvert='yes'">
                <xsl:element name="rs">
                    <xsl:attribute name="ref">
                        <xsl:text>#note_</xsl:text>
                        <xsl:value-of select="$commentId"/>
                    </xsl:attribute>
                    <xsl:call-template name="copyWithIn">
                        <xsl:with-param name="parent" select="parent::*"/>
                        <xsl:with-param name="from" select="count(preceding-sibling::node())"/>
                        <xsl:with-param name="to" select="count($nextUibkComment/preceding-sibling::node())"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template mode="elimateCommentStartEnd" match="uibk:comment[@type='end']">
        <xsl:variable name="commentId" select="@commentId"/>
        <xsl:variable name="prevUibkComment" select="preceding-sibling::uibk:comment[1]"/>
        <xsl:variable name="canConvert">
            <xsl:if test="$prevUibkComment/@type='start' and $prevUibkComment/@commentId=$commentId">yes</xsl:if>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$canConvert='yes'">
                <!-- DO nothing -->
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    
    <xsl:template match="@*|*|text()|comment()" mode="hiToMilestones">
        <xsl:copy>
            <xsl:apply-templates mode="hiToMilestones" select="@*"/>
            <xsl:apply-templates mode="hiToMilestones"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:hi" mode="hiToMilestones">
        <xsl:variable name="generateId" select="generate-id()"/>
        <xsl:element name="uibk:Hi">
            <xsl:attribute name="generateId" select="$generateId"/>
            <xsl:attribute name="type">start</xsl:attribute>
            <xsl:apply-templates select="@*" mode="hiToMilestones"/>
        </xsl:element>
        <xsl:apply-templates select="*|text()|comment()" mode="hiToMilestones"/>
        <xsl:element name="uibk:Hi">
            <xsl:attribute name="generateId" select="$generateId"/>
            <xsl:attribute name="type">end</xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="backToHi">
        <xsl:param name="node"/>
        <xsl:variable name="result">
            <xsl:apply-templates select="$node" mode="backToHi"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$result//uibk:Hi">
                <xsl:apply-templates select="$result" mode="backToHi"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$result"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template mode="backToHi" match="@*|comment()">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template mode="backToHi" match="uibk:Hi[@type='start']">
        <xsl:variable name="hiAfter" select="following-sibling::uibk:Hi[1]"/>
        <xsl:choose>
            <xsl:when test="@generateId = $hiAfter/@generateId">
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template mode="backToHi" match="uibk:Hi[@type='end']">
        <xsl:variable name="hiBefore" select="preceding-sibling::uibk:Hi[1]"/>
        <xsl:choose>
            <xsl:when test="@generateId = $hiBefore/@generateId">
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="text()" mode="backToHi">
        <xsl:variable name="hiBefore" select="preceding-sibling::uibk:Hi[1]"/>
        <xsl:variable name="hiAfter" select="following-sibling::uibk:Hi[1]"/>
        <xsl:choose>
            <xsl:when test="$hiBefore/@generateId = $hiAfter/@generateId">
                <xsl:element name="hi">
                    <xsl:attribute name="rend" select="$hiBefore/@rend"/>
                    <xsl:copy/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template mode="backToHi" match="*">
        <xsl:copy>
            <xsl:apply-templates mode="backToHi" select="@*"/>
            <xsl:apply-templates mode="backToHi"/>
        </xsl:copy>
    </xsl:template>
    
    
    
    <xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>
    
    <xsl:template match="*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    
    
    <xsl:template match="tei:hi[@rend='annotation_reference']">
        <xsl:apply-templates/>
    </xsl:template>
    
    
    <xsl:template name="canConvert">
        <xsl:param name="startNode"/>
        <xsl:param name="endNode"/>
        <xsl:value-of select="$startNode/ancestor::tei:p[1]=$endNode/ancestor::tei:p[1]"/>
    </xsl:template>
    
    <xsl:template name="getNote">
        <xsl:param name="commentId"/>
        <xsl:value-of select="//tei:note[@commentId=$commentId]"/>
    </xsl:template>
    
    <xsl:template name="copyWithIn">
        <xsl:param name="parent"/>
        <xsl:param name="from"/>
        <xsl:param name="to"/>
        <xsl:variable name="fromPos" select="$from+1"/>
        <xsl:variable name="toPos" select="$to"/>
        <xsl:for-each select="$parent/(text() | *)">
            <xsl:variable name="pos" select="position()"/>
            <xsl:if test="$pos &gt; $fromPos and $pos &lt;= $toPos">
                <xsl:apply-templates select="." mode="copy"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template mode="copy" match="text()|@*|*|comment()">
        <xsl:copy>
            <xsl:apply-templates mode="copy" select="@*"/>
            <xsl:apply-templates mode="copy"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>