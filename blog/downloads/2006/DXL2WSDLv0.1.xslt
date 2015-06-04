<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:a="http://www.lotus.com/dxl" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/">
	<xsl:output indent="yes" method="xml"/>
	<!-- Variable definitions, can be uses with {$varname} inside a string -->
	<xsl:variable name="formName" select="a:form/@name"/>
	<xsl:variable name="statusName"><xsl:value-of select="$formName"/>Status</xsl:variable>
	<xsl:variable name="typeName"><xsl:value-of select="$formName"/>Type</xsl:variable>
	<xsl:variable name="resulttypeName"><xsl:value-of select="$formName"/>ResultType</xsl:variable>
	
	<xsl:template match="/">
		<wsdl:definitions targetNamespace="urn:DefaultNamespace"
			xmlns="http://schemas.xmlsoap.org/wsdl/"
			xmlns:apachesoap="http://xml.apache.org/xml-soap" xmlns:impl="urn:DefaultNamespace"
			xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
			xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
			xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="{$formName}">
			<wsdl:types>
				<schema targetNamespace="urn:DefaultNamespace"
					xmlns="http://www.w3.org/2001/XMLSchema">
					<!-- Here we define the fields as their own datatype -->
					<xsl:apply-templates select="//a:field" mode="fieldDef"/>
					<!-- Here we insert all non computed fields from the DXL Form, every type has it's own template -->
					<xsd:complexType name="{$typeName}">
						<xsd:sequence>
							<xsl:apply-templates select="//a:field" mode="inputform"/>
						</xsd:sequence>
					</xsd:complexType>
					<xsd:complexType name="{$resulttypeName}">
						<xsd:sequence>
							<xsl:apply-templates select="//a:field" mode="resultform"/>
						</xsd:sequence>
					</xsd:complexType>
					<xsd:element name="{$formName}" type="impl:{$typeName}" />

					<xsd:complexType name="{$statusName}">
						<xsd:sequence>
							<xsd:element name="{$formName}" nillable="true" type="impl:{$resulttypeName}" />
							<xsd:element name="Status" type="xsd:string"/>
						</xsd:sequence>
					</xsd:complexType>
					<element name="Create{$formName}Return" type="impl:{$statusName}"/>
					<element name="Save{$formName}Return" type="impl:{$statusName}"/>
					<element name="Get{$formName}Return" type="impl:{$statusName}"/>
					<element name="Delete{$formName}Return" type="impl:{$statusName}"/>
					<element name="DocumentID" type="xsd:string"/>
				</schema>
			</wsdl:types>

			<wsdl:message name="Get{$formName}Response">
				<wsdl:part element="impl:Get{$formName}Return" name="Get{$formName}Return"/>
			</wsdl:message>
			<wsdl:message name="Get{$formName}Request">
				<wsdl:part element="impl:DocumentID" name="DocumentID"/>
			</wsdl:message>
			<wsdl:message name="Create{$formName}Response">
				<wsdl:part element="impl:Create{$formName}Return" name="Create{$formName}Return"/>
			</wsdl:message>
			<wsdl:message name="Create{$formName}Request">
				<wsdl:part element="impl:{$formName}" name="Doc"/>
			</wsdl:message>
			<wsdl:message name="Delete{$formName}Response">
				<wsdl:part element="impl:Delete{$formName}Return" name="Delete{$formName}Return"/>
			</wsdl:message>
			<wsdl:message name="Delete{$formName}Request">
				<wsdl:part element="impl:DocumentID" name="DocumentID"/>
			</wsdl:message>
			<wsdl:message name="Save{$formName}Response">
				<wsdl:part element="impl:Save{$formName}Return" name="Save{$formName}Return"/>
			</wsdl:message>
			<wsdl:message name="Save{$formName}Request">
				<wsdl:part element="impl:{$formName}" name="Doc"/>
			</wsdl:message>

			<wsdl:portType name="{$formName}Actions">
				<wsdl:operation name="Create{$formName}">
					<wsdl:input message="impl:Create{$formName}Request" name="Create{$formName}Request"/>
					<wsdl:output message="impl:Create{$formName}Response" name="Create{$formName}Response"
					/>
				</wsdl:operation>
				<wsdl:operation name="Save{$formName}">
					<wsdl:input message="impl:Save{$formName}Request" name="Save{$formName}Request"/>
					<wsdl:output message="impl:Save{$formName}Response" name="Save{$formName}Response"/>
				</wsdl:operation>
				<wsdl:operation name="Get{$formName}">
					<wsdl:input message="impl:Get{$formName}Request" name="Get{$formName}Request"/>
					<wsdl:output message="impl:Get{$formName}Response" name="Get{$formName}Response"/>
				</wsdl:operation>
				<wsdl:operation name="Delete{$formName}">
					<wsdl:input message="impl:Delete{$formName}Request" name="Delete{$formName}Request"/>
					<wsdl:output message="impl:Delete{$formName}Response" name="Delete{$formName}Response"
					/>
				</wsdl:operation>
			</wsdl:portType>
			<wsdl:binding name="DominoSoapBinding" type="impl:{$formName}Actions">
				<wsdlsoap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
				<wsdl:operation name="Create{$formName}">
					<wsdlsoap:operation soapAction="Create{$formName}"/>
					<wsdl:input name="Create{$formName}Request">
						<wsdlsoap:body use="literal"/>
					</wsdl:input>
					<wsdl:output name="Create{$formName}Response">
						<wsdlsoap:body use="literal"/>
					</wsdl:output>
				</wsdl:operation>
				<wsdl:operation name="Save{$formName}">
					<wsdlsoap:operation soapAction="Save{$formName}"/>
					<wsdl:input name="Save{$formName}Request">
						<wsdlsoap:body use="literal"/>
					</wsdl:input>
					<wsdl:output name="Save{$formName}Response">
						<wsdlsoap:body use="literal"/>
					</wsdl:output>
				</wsdl:operation>
				<wsdl:operation name="Get{$formName}">
					<wsdlsoap:operation soapAction="Get{$formName}"/>
					<wsdl:input name="Get{$formName}Request">
						<wsdlsoap:body use="literal"/>
					</wsdl:input>
					<wsdl:output name="Get{$formName}Response">
						<wsdlsoap:body use="literal"/>
					</wsdl:output>
				</wsdl:operation>
				<wsdl:operation name="Delete{$formName}">
					<wsdlsoap:operation soapAction="Delete{$formName}"/>
					<wsdl:input name="Delete{$formName}Request">
						<wsdlsoap:body use="literal"/>
					</wsdl:input>
					<wsdl:output name="Delete{$formName}Response">
						<wsdlsoap:body use="literal"/>
					</wsdl:output>
				</wsdl:operation>
			</wsdl:binding>
			<wsdl:service name="{$formName}ActionsService">
				<wsdl:port binding="impl:DominoSoapBinding" name="Domino">
					<wsdlsoap:address location="http://localhost"/>
				</wsdl:port>
			</wsdl:service>
		</wsdl:definitions>

	</xsl:template>

	<!-- Here are the field templates -->
	<!-- No datatype for computed fields in inputform -->
	<xsl:template match="a:field[@kind='computed']" mode="inputform"/>
	<xsl:template match="a:field[@kind='computedfordisplay']" mode="inputform"/>
	<!-- no computed for display fields at all -->
	<xsl:template match="a:field[@kind='computedfordisplay']" mode="resultform" />

	<!-- Definition for all fields -->
	<xsl:template match="a:field" mode="inputform">
		<xsd:element>
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="type">impl:<xsl:value-of select="@name"/>Type</xsl:attribute>
		</xsd:element>
	</xsl:template>

	<xsl:template match="a:field" mode="resultform">
		<xsd:element>
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="type">impl:<xsl:value-of select="@name"/>Type</xsl:attribute>
		</xsd:element>
	</xsl:template>
	
	<!-- Datatype definition for all fields, could be tweaked to cater to special needs -->
	<!-- First the text fields -->
	<xsl:template match="a:field[@type='text' or @type='keyword']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:string"/>
		</xsd:simpleType>
	</xsl:template>
	
	<xsl:template match="a:field[@type='readers']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:string"/>
		</xsd:simpleType>
	</xsl:template>
	
	<xsl:template match="a:field[@type='authors']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:string"/>
		</xsd:simpleType>
	</xsl:template>
	
	<xsl:template match="a:field[@type='names']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:string"/>
		</xsd:simpleType>
	</xsl:template>
	<!-- RichText fields -->
	<xsl:template match="a:field[@type='richtext']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:string"/>
		</xsd:simpleType>
	</xsl:template>
	
	<!-- Number fields -->
	<xsl:template match="a:field[@type='number']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:double"/>
		</xsd:simpleType>
	</xsl:template>
	
	<!-- Date fields -->
	<xsl:template match="a:field[@type='datetime']" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsd:restriction base="xsd:date"/>
		</xsd:simpleType>
	</xsl:template>
	
	<!-- The rest of the field definitions if not specified above -->
	<xsl:template match="a:field" mode="fieldDef">
		<xsl:variable name="fieldName" select="@name"/>
		<xsd:simpleType>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldName"/>Type</xsl:attribute>
			<xsl:comment>Type: <xsl:value-of select="@type"/></xsl:comment>
			<xsd:restriction base="xsd:anyType" />
		</xsd:simpleType>
	</xsl:template>
</xsl:stylesheet>