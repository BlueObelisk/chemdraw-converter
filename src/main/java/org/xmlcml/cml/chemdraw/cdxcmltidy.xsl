<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:param name="outdir">file:/D:/wwmm/marker2003/examples/ley</xsl:param>
  <xsl:template match="/">
    <xsl:for-each select="list/cml/molecule">
      <xsl:result-document href="{$outdir}/mol{position()}.cml">
        <xsl:message>MOL <xsl:value-of select="position()"/></xsl:message>
        <xsl:copy-of select="."/>
      </xsl:result-document>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
