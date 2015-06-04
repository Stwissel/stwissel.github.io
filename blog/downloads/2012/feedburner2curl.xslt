<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:content="http://purl.org/rss/1.0/modules/content/"
    xmlns:wfw="http://wellformedweb.org/CommentAPI/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
    xmlns:slash="http://purl.org/rss/1.0/modules/slash/"
    xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd"
    xmlns:rawvoice="http://www.rawvoice.com/rawvoiceRssModule/"
    xmlns:media="http://search.yahoo.com/mrss/"
    xmlns:feedburner="http://rssnamespace.org/feedburner/ext/1.0"
    exclude-result-prefixes="xd"
    version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 2, 2012</xd:p>
            <xd:p><xd:b>Author:</xd:b> stw</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output indent="no" method="text"/>
    
    <xsl:template match="/">#!/bin/bash<xsl:apply-templates select="//media:content" /></xsl:template>
    
    <xsl:template match="media:content">
        curl -C - -G <xsl:value-of select="@url"/> -L -o <xsl:value-of select="reverse(tokenize(@url,'/'))[1]"/>
    </xsl:template>
    
</xsl:stylesheet>