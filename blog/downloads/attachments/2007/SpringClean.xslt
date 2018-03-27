<?xml version="1.0" encoding="UTF-8"?>
<!-- 

    This stylesheet is provided as is without warranty to be fit for any purpose.
    (CC) Stephan Wissel - Creative Commons 3.0
    see: http://creativecommons.org/licenses/by-nc-sa/3.0/
    
    Stylesheet is supposed to run against a single exported form from a Domino database
    The doctype-system needs adjustment to your environment

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:d="http://www.lotus.com/dxl" exclude-result-prefixes="d">
    <xsl:output indent="yes" method="xml"
        doctype-system="C:\Program Files\IBM\Lotus\Notes\xmlschemas/domino_8_0_M4.dtd"
        encoding="UTF-8"/>

    <!-- Some variables we might need in the due course of processing -->
    <xsl:variable name="pardefcount" select="count(//d:pardef)+1"/>
    <xsl:variable name="formname" select="//d:form[position()=1]/@name"/>

    <!-- Section 1 : Provide the entry point for all elements -->
    <xsl:template match="/">
        <database>
            <!-- We get the attribute from the first element which might be a database or a form -->
            <xsl:attribute name="replicaid">
                <!-- Get the replicaid -->
                <xsl:value-of select="/child::node()[1]/@replicaid"/>
            </xsl:attribute>
            <xsl:attribute name="version">
                <!-- Get the designer version -->
                <xsl:value-of select="/child::node()[1]/@version"/>
            </xsl:attribute>
            <!-- get all other attributes -->
            <xsl:apply-templates select="@*"/>
            <!-- apply the templates for regular forms -->
            <xsl:apply-templates/>
            <!-- apply the templates for the additional generation like LotusScript and Suforms -->
            <xsl:apply-templates mode="genLS" select="/d:form/d:globals"/>
            <xsl:apply-templates mode="genSub" select="//d:section[@accessfieldkind]"/>
            <xsl:apply-templates mode="genRead" select="//d:section[@accessfieldkind]"/>
        </database>

    </xsl:template>

    <!-- We might have a database tag, wich we have generated before, so we just apply the content of it -->
    <xsl:template match="d:database">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Section 2: Filter out elements we do not want or need -->
    <!--  Filter out  change of font if there is not content inside-->
    <xsl:template match="d:run[node()[last()=1][self::d:font]]"/>

    <!-- Filter out the note infoand update info -->
    <xsl:template match="d:noteinfo"/>
    <xsl:template match="d:updatedby"/>
    <xsl:template match="d:wassignedby"/>
    
    <!-- remove a form background color -->
    <xsl:template match="d:form/@bgcolor" />


    <!-- Filter out items after the body tag -->
    <xsl:template match="d:form/d:item"/>

    <!-- Filter out attributes -->
    <xsl:template match="@replicaid"/>
    <xsl:template match="@version"/>
    <xsl:template match="@milestonebuild"/>

    <!-- remove left margin values from pardefs, so they use the default dangerous!!!! -->
    <xsl:template match="@leftmargin"/>
    <xsl:template match="@firstlineleftmargin"/>

    <!-- Filter font information we don't want -->
    <xsl:template match="d:font/@truetype"/>
    <xsl:template match="d:font/@name"/>
    <xsl:template match="d:font/@pitch"/>
    <xsl:template match="d:font/@familyid"/>

    <!-- Section 3 : Modify elements to match our specific needs -->

    <xsl:template match="d:form">
        <form>
            <!-- Copy all attributes -->
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </form>
    </xsl:template>

    <xsl:template match="d:richtext">
        <richtext>
            <!-- Add a hidden field for Debug information and update all hidden fields to use that formula instead of hide='read edit copy preview previewedit' -->
            <xsl:if test="count(//d:field[@name='isNotDebug'])=0">
                <pardef hide="read edit print copy preview previewedit">
                    <xsl:attribute name="id">
                        <xsl:value-of select="$pardefcount"/>
                    </xsl:attribute>
                </pardef>
                <par>
                    <xsl:attribute name="def">
                        <xsl:value-of select="$pardefcount"/>
                    </xsl:attribute>
                    <run>
                        <font color="red"/>
                        <field type="number" kind="computedfordisplay" name="isNotDebug">
                            <code event="defaultvalue">
                                <formula>@isNotMember("[Debug]";@UserRoles)</formula>
                            </code>
                        </field>
                    </run>
                </par>
            </xsl:if>
            <xsl:apply-templates/>
        </richtext>
    </xsl:template>

    <!-- Replace all paragraph definition where hide='read edit copy preview previewedit' with hidewhen isNotDebug -->
    <xsl:template
        match="d:pardef[@hide='read edit copy preview previewedit' or @hide='read edit print copy preview previewedit']">
        <pardef>
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <code event="hidewhen">
                <formula>isNotDebug</formula>
            </code>
        </pardef>
    </xsl:template>

    <!-- change the ugly fucsia font to light gray -->
    <xsl:template match="d:font/@color[node()='fucisa']">
        <xsl:attribute name="color">gray</xsl:attribute>
    </xsl:template>

    <!-- standardize on 9pt for 8 and 9 pt font sizes -->
    <xsl:template match="d:font/@size['8pt' or '9pt']">
        <xsl:attribute name="size">9pt</xsl:attribute>
    </xsl:template>

    <!-- remove font sizes for the rest -->
    <xsl:template match="d:font/@size[not ('8pt' or '9pt')]"/>

    <!-- more radical approach : remove all font information -->
    <!-- xsl:template match="d:font" / -->

    <!-- Remove all LotusScript that is in the globals section (it will be in a script library) -->
    <xsl:template match="d:form/d:globals">
        <globals>
            <code event="options">
                <lotusscript>Option Public
                    Option Declare
                    Use "formlib_<xsl:value-of select="$formname"/>"
                </lotusscript>
            </code>
        </globals>
    </xsl:template>
    
    <!-- Create the libary -->
    <xsl:template match="d:form/d:globals" mode="genLS">
        <scriptlibrary>
            <xsl:attribute name="name">formlib_<xsl:value-of select="$formname"/></xsl:attribute>
            <globals>
                <code event="options">
                    <lotusscript>Option Public
                        Option Declare
                        <xsl:value-of select="d:code[@event='options']/d:lotusscript"/>                    
                    </lotusscript>
                </code>
                <xsl:apply-templates mode="genLS" />
            </globals>
        </scriptlibrary>
    </xsl:template>
    
    <xsl:template mode="genLS" match="d:globals/d:code[@event!='options']">
        <code>
            <xsl:attribute name="event"><xsl:value-of select="@event"/></xsl:attribute>
            <xsl:value-of select="."/>
        </code>
        
    </xsl:template>
    
    <!-- turn an access controlled section into a computed subform -->
    <xsl:template match="d:section[@accessfieldkind]">
        <xsl:variable name="en"><xsl:value-of select="$formname"/>_<xsl:value-of select="d:sectiontitle/d:text"/>_Edit</xsl:variable>
        <xsl:variable name="rn"><xsl:value-of select="$formname"/>_<xsl:value-of select="d:sectiontitle/d:text"/>_Read</xsl:variable>
        <subformref>
            <code event="value">
                <formula>@if(@isMember(@UserName;@trim(<xsl:value-of select="d:code[@event='defaultvalue']/d:formula"/>:<xsl:value-of select="@accessfieldname"/>));"<xsl:value-of select="$en"/>";"<xsl:value-of select="$rn"/>")</formula> 
            </code>
        </subformref>        
    </xsl:template>
    
    <xsl:template match="d:section[@accessfieldkind]" mode="genSub">
        <subform>
            <xsl:attribute name="name"><xsl:value-of select="$formname"/>_<xsl:value-of select="d:sectiontitle/d:text"/>_Edit</xsl:attribute>
            <xsl:apply-templates mode="genSub" />
        </subform> 
    </xsl:template>
    <xsl:template match="d:section[@accessfieldkind]" mode="genRead">
        <subform>
            <xsl:attribute name="name"><xsl:value-of select="$formname"/>_<xsl:value-of select="d:sectiontitle/d:text"/>_Read</xsl:attribute>
            <xsl:apply-templates mode="genRead" />
        </subform>
    </xsl:template>
    <!-- Fix the fields for genRead -->
    <xsl:template mode="genRead" match="d:field">
        <xsl:variable name="curField"><xsl:value-of select="@name"/></xsl:variable>
        <field kind="computedfordisplay">
            <xsl:apply-templates mode="genRead" select="@*" />
            <code
                event='defaultvalue'><formula><xsl:value-of select="$curField"/></formula></code>
        </field>
    </xsl:template>
    <xsl:template mode="genSub" match="d:sectiontitle" />
    <xsl:template mode="genSub" match="d:section/d:code[@event='defaultvalue']" />
    <xsl:template mode="genRead" match="d:sectiontitle" />
    <xsl:template mode="genRead" match="d:section/d:code[@event='defaultvalue']" />
    <xsl:template mode="genRead" match="d:field/@kind" />
    
    <!-- Section 4 : Copy any element 1:1 from source to target -->
    <xsl:template match="*">
        <xsl:variable name="curTagname" select="name()"/>
        <xsl:element name="{$curTagname}">
            <!-- Walk through the attributes -->
            <xsl:apply-templates select="@*"/>
            <!-- process the children -->
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:variable name="curAttName" select="name()"/>
        <xsl:attribute name="{$curAttName}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="*" mode="genSub">
        <xsl:variable name="curTagname" select="name()"/>
        <xsl:element name="{$curTagname}">
            <!-- Walk through the attributes -->
            <xsl:apply-templates select="@*" mode="genSub"/>
            <!-- process the children -->
            <xsl:apply-templates mode="genSub"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="@*" mode="genSub">
        <xsl:variable name="curAttName" select="name()"/>
        <xsl:attribute name="{$curAttName}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="*" mode="genRead">
        <xsl:variable name="curTagname" select="name()"/>
        <xsl:element name="{$curTagname}">
            <!-- Walk through the attributes -->
            <xsl:apply-templates select="@*" mode="genRead"/>
            <!-- process the children -->
            <xsl:apply-templates mode="genRead"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="@*" mode="genRead">
        <xsl:variable name="curAttName" select="name()"/>
        <xsl:attribute name="{$curAttName}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>
    

    <!-- make sure nothing is copied in the additional modes if not defined by a template-->
    <xsl:template mode="genLS" match="*"/>
    
</xsl:stylesheet>
