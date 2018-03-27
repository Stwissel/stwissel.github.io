<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:d="http://www.lotus.com/dxl" exclude-result-prefixes="xd d" version="1.0">

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master page-height="297mm" page-width="210mm" margin-top="25mm"
                    margin-bottom="25mm" margin-left="25mm" margin-right="25mm"
                    master-name="A4portrait">
                    <fo:region-body  margin-top="1.5cm" margin-bottom="2.5cm"/>
                    <fo:region-before extent="3.5cm"/>
                    <fo:region-after  extent="2.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4portrait">
                <fo:static-content flow-name="xsl-region-before" border-color="gray"
                    border-style="solid" border-width="1px">
                    <fo:block font-size="24px" font-weight="bold" border-bottom-width="2px">
                        <xsl:apply-templates select="d:database" mode="header"/>
                    </fo:block>
                    
                </fo:static-content>

                <fo:static-content flow-name="xsl-region-after" border-color="gray"
                    border-style="solid" border-width="1px">
                    <fo:block font-size="12px"> Default Notes2Fo Stylesheet for DXL Page:
                        <fo:page-number/> / <fo:page-number-citation ref-id="last-page"/>
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <xsl:apply-templates/>
                    <fo:block id="last-page"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="d:database" mode="header">       
            Database <xsl:value-of select="@title"/>
    </xsl:template>

    <xsl:template match="d:database">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="d:document">
        <fo:block margin-bottom="10px"/>
        <fo:block font-size="12px" margin-bottom="10px">Form <xsl:value-of select="@form"/> (UNID <xsl:value-of
                select="d:noteinfo/@unid"/>) </fo:block>

        <fo:table width="15cm">
             <fo:table-header>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-weight="bold">Item</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">Value</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-header>

            <fo:table-body>
                <xsl:apply-templates select="d:item"/>
            </fo:table-body>
        </fo:table>

        <fo:block border-bottom="2px solid red" margin-top="10px" margin-bottom="10px"/>

    </xsl:template>

    <xsl:template match="d:item">
        <fo:table-row>
            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="@name"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block>
                    <xsl:apply-templates/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>
    
    <xsl:template match="*">
        <fo:block font-size="12px">
            <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>

</xsl:stylesheet>
