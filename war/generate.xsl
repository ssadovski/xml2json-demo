<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:json="http://json.org/">
    <xsl:import href="xml-to-json.xsl"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="node()|@*" name="identity">
		<xsl:value-of select="json:generate(.)"/>
	</xsl:template>

	<xsl:template match="comment()"/>

</xsl:stylesheet>