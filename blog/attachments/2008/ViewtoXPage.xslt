<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xp="http://www.ibm.com/xsp/core" 
    xmlns:d='http://www.lotus.com/dxl'>
    
    <xsl:output method="xml" indent="yes" />
    <xsl:template match="/">
        <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
        <xsl:apply-templates select="//d:view" />       
       </xp:view>
        
    </xsl:template>
    
    <xsl:template match="d:view">
        <xsl:variable name="panelid"><xsl:value-of select="@name" /></xsl:variable>
        
        <xp:viewPanel rows="30" viewStyle="width:100%">
            <xsl:attribute name="id">viewPanel<xsl:value-of select="$panelid" /></xsl:attribute>
           
            <xp:this.facets>
                <xp:pager layout="Previous Group Next" xp:key="headerPager"
                    id="pager1">
                </xp:pager>
            </xp:this.facets>
            <xp:this.data>
                <xp:dominoView>
                   <xsl:attribute name="var"><xsl:value-of select="$panelid" /></xsl:attribute>
                    <xsl:attribute name="viewName"><xsl:value-of select="$panelid" /></xsl:attribute>
                    </xp:dominoView>
            </xp:this.data>
            <xsl:apply-templates select="d:column" />
        </xp:viewPanel>        
    </xsl:template>
    
    <xsl:template match="d:column">
        <xsl:variable name="colid">viewColumn<xsl:value-of select="position()" /></xsl:variable>
        <xsl:variable name="headid">viewColumnHeader<xsl:value-of select="position()" /></xsl:variable>
        
        <xp:viewColumn>
            <xsl:attribute name="columnName"><xsl:value-of select="d:columnheader/@title" /></xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="$colid" /></xsl:attribute>
            <xp:viewColumnHeader>
                <xsl:attribute name="value"><xsl:value-of select="d:columnheader/@title" /></xsl:attribute>
                <xsl:attribute name="id"><xsl:value-of select="$headid" /></xsl:attribute>
            </xp:viewColumnHeader>
        </xp:viewColumn>
        
    </xsl:template>

</xsl:stylesheet>
