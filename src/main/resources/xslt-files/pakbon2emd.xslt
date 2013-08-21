<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/" xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/" xmlns:gml="http://www.opengis.net/gml/3.2"
  xmlns:sikb="http://www.sikb.nl/sikb0102/2.1.0" exclude-result-prefixes="sikb gml">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="sikb:sikb0102">

    <emd:easymetadata xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
      xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://easy.dans.knaw.nl/easy/easymetadata/ http://easy.dans.knaw.nl/schemas/md/emd/2012/11/emd.xsd" emd:version="0.1">
      <!-- emd:title -->
      <emd:title>
        <xsl:for-each select="sikb:project/sikb:projectnaam">
          <dc:title>
            <xsl:value-of select="."/>
          </dc:title>
        </xsl:for-each>
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:titel">
          <dcterms:alternative>
            <xsl:value-of select="."/>
          </dcterms:alternative>
        </xsl:for-each>
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../@bronId">
          <dcterms:alternative>
            <xsl:value-of select="."/>
          </dcterms:alternative>
        </xsl:for-each>
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:serie">
          <dcterms:alternative>
            <xsl:value-of select="."/>
          </dcterms:alternative>
        </xsl:for-each>
      </emd:title>

      <!-- emd:creator  -->
      <emd:creator>

        <xsl:for-each select="sikb:project/sikb:uitvoerder">
          <xsl:variable name="organisatieId" select="sikb:organisatieId"/>
          <xsl:variable name="opgravingsleiderId" select="sikb:opgravingsleiderPersoonId"/>
          <xsl:variable name="achternaam" select="/sikb:sikb0102/sikb:persoon[@sikb:id = $opgravingsleiderId]/sikb:naam/sikb:achternaam"/>
          <xsl:variable name="initialen" select="/sikb:sikb0102/sikb:persoon[@sikb:id = $opgravingsleiderId]/sikb:naam/sikb:initialen"/>
          <xsl:variable name="tussenvoegsel" select="/sikb:sikb0102/sikb:persoon[@sikb:id = $opgravingsleiderId]/sikb:naam/sikb:tussenvoegsel"/>
          <xsl:variable name="titel" select="/sikb:sikb0102/sikb:persoon[@sikb:id = $opgravingsleiderId]/sikb:naam/sikb:titel"/>
          <xsl:variable name="voornaam" select="/sikb:sikb0102/sikb:persoon[@sikb:id = $opgravingsleiderId]/sikb:naam/sikb:voornaam"/>
          <xsl:variable name="organisatie" select="/sikb:sikb0102/sikb:organisatie[@sikb:id = $organisatieId]/sikb:naam"/>
          <eas:creator>
            <xsl:if test="$titel">
              <eas:title>
                <xsl:value-of select="$titel"/>
              </eas:title>
            </xsl:if>
            <xsl:choose>
              <xsl:when test="$initialen">
                <eas:initials>
                  <xsl:value-of select="$initialen"/>
                </eas:initials>
              </xsl:when>
              <xsl:when test="$voornaam and not($initialen)">
                <eas:initials>
                  <xsl:value-of select="$voornaam"/>
                </eas:initials>
              </xsl:when>
            </xsl:choose>
            <xsl:if test="$tussenvoegsel">
              <eas:prefix>
                <xsl:value-of select="$tussenvoegsel"/>
              </eas:prefix>
            </xsl:if>
            <xsl:if test="$achternaam">
              <eas:surname>
                <xsl:value-of select="$achternaam"/>
              </eas:surname>
            </xsl:if>
            <xsl:if test="$organisatie">
              <eas:organization>
                <xsl:value-of select="$organisatie"/>
              </eas:organization>
            </xsl:if>
          </eas:creator>

        </xsl:for-each>

      </emd:creator>

      <!-- emd:subject -->
      <emd:subject>

        <xsl:for-each select="sikb:project/sikb:onderzoektype">
          <xsl:variable name="waarde" select="."/>
          <xsl:choose>
            <xsl:when test="$waarde='AXX'">
              <dc:subject>archeologisch: onbepaald</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ABE'">
              <dc:subject>archeologisch: non-destructief: begeleiding</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ABO'">
              <dc:subject>archeologisch: non-destructief: boring</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ABU'">
              <dc:subject>archeologisch: non-destructief: bureauonderzoek</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ADU'">
              <dc:subject>archeologisch: non-destructief: duikactiviteiten</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AGO'">
              <dc:subject>archeologisch: non-destructief: geofysisch onderzoek</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AIN'">
              <dc:subject>archeologisch: non-destructief: inspectie</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AKA'">
              <dc:subject>archeologisch: non-destructief: verwachtingskaart</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AVE'">
              <dc:subject>archeologisch: non-destructief: (veld)kartering</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AOP'">
              <dc:subject>archeologisch: destructief: opgraving</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AOW'">
              <dc:subject>archeologisch: destructief: onderwaterarcheologie</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='APP'">
              <dc:subject>archeologisch: destructief: proefputten/proefsleuven</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IAR'">
              <dc:subject>indirect: archief</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ICO'">
              <dc:subject>indirect: collectiebeschrijving</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ILI'">
              <dc:subject>indirect: literatuur</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IXX'">
              <dc:subject>indirect: onbepaald</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='LUC'">
              <dc:subject>luchtfoto/remote sensing</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='AHN'">
              <dc:subject>aktueel hoogtemodel</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NXX'">
              <dc:subject>niet-archeologisch: onbepaald</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NBA'">
              <dc:subject>niet-archeologisch: baggerwerk</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NBO'">
              <dc:subject>niet-archeologisch: boring</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NDE'">
              <dc:subject>niet-archeologisch: metaaldetector</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NDU'">
              <dc:subject>niet-archeologisch: duikactiviteiten</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NGR'">
              <dc:subject>niet-archeologisch: graafwerk</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NKA'">
              <dc:subject>niet-archeologisch: kartering</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NWA'">
              <dc:subject>niet-archeologisch: waterwerk en exploitatie</dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='XXX'">
              <dc:subject>onbekend</dc:subject>
            </xsl:when>
            <xsl:otherwise>
              <dc:subject>
                <xsl:value-of select="."/>
              </dc:subject>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

        <!-- ABR - complextype -->
        <xsl:for-each select="sikb:vindplaats/sikb:vindplaatstype">
          <xsl:variable name="waarde" select="."/>
          <xsl:choose>
            <xsl:when test="$waarde='DEPO'">
              <!-- Depot -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EX'">
              <!-- Economie, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GX'">
              <!-- Begraving, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IX'">
              <!-- Infrastructuur, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NX'">
              <!-- Nederzetting, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='RX'">
              <!-- Religie, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VX'">
              <!-- Versterking, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='XXX'">
              <!-- Onbekend -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELA'">
              <!--   Economie - Akker/tuin -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIBB'">
              <!--  Economie - Beenbewerking -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIB'">
              <!--  Economie - Brouwerij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELCF'">
              <!--  Economie - Celtic field/raatakker -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELDP'">
              <!--  Economie - Drenkplaats/dobbe -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELEK'">
              <!-- Economie - Eendekooi -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIGB'">
              <!--  Economie - Glasblazerij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGX'">
              <!-- Economie - Grondstofwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIHB'">
              <!-- Economie - Houtbewerking -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIHK'">
              <!-- Economie - Houtskool-/kolenbranderij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGYW'">
              <!-- Economie - IJzerwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIX'">
              <!-- Economie - Industrie/nijverheid -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIKB'">
              <!-- Economie - Kalkbranderij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGKW'">
              <!-- Economie - Kleiwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELX'">
              <!-- Economie - Landbouw -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EILL'">
              <!-- Economie - Leerlooierij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGMW'">
              <!-- Economie - Mergel-/kalkwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIMB'">
              <!-- Economie - Metaalbewerking/smederij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIM'">
              <!-- Economie - Molen -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIPB'">
              <!-- Economie - Pottenbakkerij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ESCH'">
              <!-- Economie - Scheepvaart -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EISM'">
              <!-- Economie - Smelterij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EISB'">
              <!-- Economie - Steen-/pannenbakkerij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EITN'">
              <!-- Economie - Textielnijverheid -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ELVK'">
              <!-- Economie - Veekraal/schaapskooi -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGVW'">
              <!-- Economie - Veenwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EVX'">
              <!-- Economie - Visserij -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EIVB'">
              <!-- Economie - Vuursteenbewerking -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGVW'">
              <!-- Economie - Vuursteenwinning -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='EGZW'">
              <!-- Economie - Zoutwinning/moernering -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GC'">
              <!-- Begraving - Crematiegraf -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GD'">
              <!-- Begraving - Dieregraf -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GHC'">
              <!-- Begraving - Grafheuvel, crematie -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GHIC'">
              <!-- Begraving - Grafheuvel, gemengd -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GHI'">
              <!-- Begraving - Grafheuvel, inhumatie -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GHX'">
              <!-- Begraving - Grafheuvel, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVC'">
              <!-- Begraving - Grafveld, crematies -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVIC'">
              <!-- Begraving - Grafveld, gemengd -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVI'">
              <!-- Begraving - Grafveld, inhumaties -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVX'">
              <!-- Begraving - Grafveld, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GX'">
              <!-- Begraving - Graf, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GI'">
              <!-- Begraving - Inhumatiegraf -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVIK'">
              <!-- Begraving - Kerkhof -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GMEG'">
              <!-- Begraving - Megalietgraf -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVIR'">
              <!-- Begraving - Rijengrafveld -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GVCU'">
              <!-- Begraving - Urnenveld -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GCV'">
              <!-- Begraving - Vlakgraf, crematie -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GIV'">
              <!-- Begraving - Vlakgraf, inhumatie -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='GXV'">
              <!-- Begraving - Vlakgraf, onbepaald -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IBRU'">
              <!-- Infrastructuur - Brug -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IDAM'">
              <!-- Infrastructuur - Dam -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IDIJ'">
              <!-- Infrastructuur - Dijk -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IDUI'">
              <!-- Infrastructuur - Duiker -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IGEM'">
              <!-- Infrastructuur - Gemaal -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IHAV'">
              <!-- Infrastructuur - Haven -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IKAN'">
              <!-- Infrastructuur - Kanaal/vaarweg -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IPER'">
              <!-- Infrastructuur - Percelering/verkaveling -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ISLU'">
              <!-- Infrastructuur - Sluis -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='ISTE'">
              <!-- Infrastructuur - Steiger -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IVW'">
              <!-- Infrastructuur - Veenweg/veenbrug -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IWAT'">
              <!-- Infrastructuur - Waterweg (natuurlijk) -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='IWEG'">
              <!-- Infrastructuur - Weg -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NBAS'">
              <!-- Nederzetting - Basiskamp/basisnederzetting -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NVB'">
              <!-- Nederzetting - Borg/stins/versterkt huis -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NEXT'">
              <!-- Nederzetting - Extractiekamp/-nederzetting -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NVH'">
              <!-- Nederzetting - Havezathe/ridderhofstad -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NHP'">
              <!-- Nederzetting - Huisplaats, onverhoogd -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NHT'">
              <!-- Nederzetting - Huisterp -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NKD'">
              <!-- Nederzetting - Kampdorp -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NMS'">
              <!-- Nederzetting - Moated site -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NRV'">
              <!-- Nederzetting - Romeins villa(complex) -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NS'">
              <!-- Nederzetting - Stad -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NT'">
              <!-- Nederzetting - Terp/wierde -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='NWD'">
              <!-- Nederzetting - Wegdorp -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='RCP'">
              <!-- Religie - Cultusplaats/heiligdom/tempel -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='RKAP'">
              <!-- Religie - Kapel -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='RKER'">
              <!-- Religie - Kerk -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='RKLO'">
              <!-- Religie - Klooster(complex) -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VK'">
              <!-- Versterking - Kasteel -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VLW'">
              <!-- Versterking - Landweer -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VLP'">
              <!-- Versterking - Legerplaats -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VKM'">
              <!-- Versterking - Motte/kasteelheuvel/vliedberg -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VSCH'">
              <!-- Versterking - Schans -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VWP'">
              <!-- Versterking - Wachtpost -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VWB'">
              <!-- Versterking - Wal-/vluchtburcht -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VWAL'">
              <!-- Versterking - Wal/omwalling -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:when test="$waarde='VKW'">
              <!-- Versterking - Waterburcht -->
              <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                <xsl:value-of select="$waarde"/>
              </dc:subject>
            </xsl:when>
            <xsl:otherwise>
              <!-- niet voorkomend in de Archis-lijst van ABR codes voor complextype -->
              <dc_subject>
                <xsl:value-of select="$waarde"/>
                <xsl:text> (ABR_Complex)</xsl:text>
              </dc_subject>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

      </emd:subject>

      <!-- emd:description -->
      <emd:description>
        <xsl:for-each select="sikb:project/sikb:omschrijving">
          <dc:description>
            <xsl:value-of select="."/>
          </dc:description>
        </xsl:for-each>
      </emd:description>

      <!-- emd:publisher -->
      <emd:publisher>
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:uitgever">
          <dc:publisher>
            <xsl:value-of select="."/>
          </dc:publisher>
        </xsl:for-each>
      </emd:publisher>

      <!-- emd: contributor -->
      <emd:contributor>
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:auteur">
          <dc:contributor>
            <xsl:value-of select="."/>
          </dc:contributor>
        </xsl:for-each>
      </emd:contributor>

      <!-- emd:date  -->
      <emd:date>

        <!-- dc:date(s) -->

        <xsl:for-each select="sikb:project/sikb:startdatum">
          <dc:date>
            <xsl:value-of select="."/>
            <xsl:text> (startdatum)</xsl:text>
          </dc:date>
        </xsl:for-each>
        <xsl:for-each select="sikb:project/sikb:einddatum">
          <dc:date>
            <xsl:value-of select="."/>
            <xsl:text> (einddatum)</xsl:text>
          </dc:date>
        </xsl:for-each>

        <!-- eas:date(s)  -->
        <!-- dcterms:date_created is altijd in W3CDTF format, meest recente jaar van de rapport(en) -->
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:jaar">
          <xsl:sort order="descending" select="."/>
          <xsl:if test="position()=1">
            <xsl:choose>
              <xsl:when test="string-length(.)=4">
                <eas:created eas:scheme="W3CDTF" eas:format="YEAR">
                  <xsl:value-of select="."/>
                  <xsl:text>-01-01T00:00:00+01:00</xsl:text>
                </eas:created>
              </xsl:when>
              <xsl:when test="string-length(.)=7">
                <eas:created eas:scheme="W3CDTF" eas:format="MONTH">
                  <xsl:value-of select="."/>
                  <xsl:text>-01T00:00:00+01:00</xsl:text>
                </eas:created>
              </xsl:when>
              <xsl:when test="string-length(.)=10">
                <eas:created eas:scheme="W3CDTF" eas:format="DAY">
                  <xsl:value-of select="."/>
                  <xsl:text>T00:00:00+01:00</xsl:text>
                </eas:created>
              </xsl:when>
            </xsl:choose>
          </xsl:if>
        </xsl:for-each>
        <!-- date_submitted wordt door EASY toegevoegd -->
        <!-- <eas:dateSubmitted eas:scheme="W3CDTF" eas:format="DAY">2007-02-22T00:00:00.000+01:00</eas:dateSubmitted> -->

        <!-- date_available is standaard datum, te veranderen om moment van conversie-->
        <eas:available eas:scheme="W3CDTF" eas:format="DAY">
          <xsl:text>2012-01-01T00:00:00+01:00</xsl:text>
        </eas:available>

        <!-- dcterms:issued voor het publicatie jaar van de rapport(en) -->
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:jaar">
          <xsl:choose>
            <xsl:when test="string-length(.)=4">
              <eas:issued eas:scheme="W3CDTF" eas:format="YEAR">
                <xsl:value-of select="."/>
                <xsl:text>-01-01T00:00:00+01:00</xsl:text>
              </eas:issued>
            </xsl:when>
            <xsl:when test="string-length(.)=7">
              <eas:issued eas:scheme="W3CDTF" eas:format="MONTH">
                <xsl:value-of select="."/>
                <xsl:text>-01T00:00:00+01:00</xsl:text>
              </eas:issued>
            </xsl:when>
            <xsl:when test="string-length(.)=10">
              <eas:issued eas:scheme="W3CDTF" eas:format="DAY">
                <xsl:value-of select="."/>
                <xsl:text>T00:00:00+01:00</xsl:text>
              </eas:issued>
            </xsl:when>
          </xsl:choose>
        </xsl:for-each>

      </emd:date>

      <!-- emd:type -->
      <emd:type>
        <xsl:variable name="bestanden" select="sikb:bestand"/>
        <xsl:variable name="documenten" select="sikb:document/sikb:bestandId"/>
        <xsl:variable name="fotos" select="sikb:foto/sikb:bestandId"/>
        <xsl:variable name="tekeningen" select="sikb:tekening/sikb:bestandId"/>
        <xsl:if test="$bestanden">
          <dc:type eas:scheme="DCMI" eas:schemeId="common.dc.type">
            <xsl:text>Dataset</xsl:text>
          </dc:type>
        </xsl:if>
        <xsl:if test="$documenten">
          <dc:type eas:scheme="DCMI" eas:schemeId="common.dc.type">
            <xsl:text>Text</xsl:text>
          </dc:type>
        </xsl:if>
        <xsl:if test="($fotos) or ($tekeningen)">
          <dc:type eas:scheme="DCMI" eas:schemeId="common.dc.type">
            <xsl:text>Image</xsl:text>
          </dc:type>
        </xsl:if>

      </emd:type>

      <!--  emd:format -->
      <emd:format>

        <!-- unieke waaarden van het bestandstype  -->
        <xsl:for-each select="sikb:bestand">
          <xsl:variable name="type_ingevuld" select="sikb:bestandtype"/>
          <xsl:if test="$type_ingevuld">
            <xsl:for-each select="sikb:bestandtype[(not(text()=../preceding-sibling::sikb:bestand/sikb:bestandtype))]">
              <dc:format>
                <xsl:value-of select="."/>
              </dc:format>
            </xsl:for-each>
          </xsl:if>
        </xsl:for-each>
      </emd:format>

      <!--  identifier  -->
      <emd:identifier>
        <!-- Archisnr (=OMG) is in EASY II gewoon deel van de identifiers -->
        <xsl:for-each select="sikb:project/sikb:onderzoeksmeldingsnummer">
          <dc:identifier eas:scheme="Archis_onderzoek_m_nr" eas:schemeId="archaeology.dc.identifier" eas:identification-system="http://archis2.archis.nl">
            <xsl:value-of select="."/>
          </dc:identifier>
        </xsl:for-each>
        <xsl:for-each select="sikb:project/@bronId">
          <dc:identifier>
            <xsl:value-of select="."/>
            <xsl:text> (project identificatie)</xsl:text>
          </dc:identifier>
        </xsl:for-each>

        <xsl:for-each select="sikb:vindplaats/sikb:vondstmeldingsnummer">
          <dc:identifier eas:scheme="Archis_vondstmelding">
            <xsl:value-of select="."/>
          </dc:identifier>
        </xsl:for-each>
        <xsl:for-each select="sikb:vindplaats/sikb:waarnemingsnummer">
          <dc:identifier eas:scheme="Archis_waarneming">
            <xsl:value-of select="."/>
          </dc:identifier>
        </xsl:for-each>

        <xsl:for-each select="sikb:document/sikb:isbn">
          <dc:identifier eas:scheme="ISBN">
            <xsl:value-of select="."/>
          </dc:identifier>
        </xsl:for-each>
        <xsl:for-each select="sikb:document/sikb:issn">
          <dc:identifier eas:scheme="ISSN">
            <xsl:value-of select="."/>
          </dc:identifier>
        </xsl:for-each>

        <!-- archeologie datasets a-nummer:  lege identifier e-depot  -->
        <dc:identifier eas:scheme="eDNA-project"/>

      </emd:identifier>

      <!-- emd:source -->
      <!-- <emd:source>
  <xsl:for-each select="source">
  <dc:source><xsl:value-of select="."/></dc:source>
  </xsl:for-each>	
</emd:source> -->

      <!-- emd:language -->
      <emd:language>
        <!-- Nederlands is als default waarde gekozen-->
        <dc:language eas:scheme="ISO 639" eas:schemeId="common.dc.language">dut/nld</dc:language>

      </emd:language>

      <!-- emd: relation -->
      <emd:relation>
      </emd:relation>

      <!-- emd:coverage -->
      <emd:coverage>

        <!-- spatial -->

        <xsl:for-each select="sikb:project/sikb:projectlocatieId">
          <xsl:variable name="locatieId" select="."/>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:provinciecode">
            <xsl:variable name="waarde" select="."/>
            <xsl:choose>
              <xsl:when test="$waarde='20'">
                <dcterms:spatial>Groningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='21'">
                <dcterms:spatial>Friesland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='22'">
                <dcterms:spatial>Drenthe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='23'">
                <dcterms:spatial>Overijssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='24'">
                <dcterms:spatial>Flevoland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='25'">
                <dcterms:spatial>Gelderland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='26'">
                <dcterms:spatial>Utrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='27'">
                <dcterms:spatial>Noord-Holland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='28'">
                <dcterms:spatial>Zuid-Holland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='29'">
                <dcterms:spatial>Zeeland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='30'">
                <dcterms:spatial>Noord-Brabant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='31'">
                <dcterms:spatial>Limburg</dcterms:spatial>
              </xsl:when>
              <xsl:otherwise>
                <dcterms:spatial>
                  <xsl:value-of select="."/>
                </dcterms:spatial>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:gemeentecode">
            <xsl:variable name="waarde" select="."/>
            <xsl:choose>
              <xsl:when test="$waarde='1680'">
                <dcterms:spatial>Aa en Hunze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='738'">
                <dcterms:spatial>Aalburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='358'">
                <dcterms:spatial>Aalsmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='197'">
                <dcterms:spatial>Aalten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='59'">
                <dcterms:spatial>Achtkarspelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='482'">
                <dcterms:spatial>Alblasserdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='613'">
                <dcterms:spatial>Albrandswaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='361'">
                <dcterms:spatial>Alkmaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='141'">
                <dcterms:spatial>Almelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='34'">
                <dcterms:spatial>Almere</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='484'">
                <dcterms:spatial>Alphen aan den Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1723'">
                <dcterms:spatial>Alphen-Chaam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='60'">
                <dcterms:spatial>Ameland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='307'">
                <dcterms:spatial>Amersfoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='362'">
                <dcterms:spatial>Amstelveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='363'">
                <dcterms:spatial>Amsterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='366'">
                <dcterms:spatial>Anna Paulowna</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='200'">
                <dcterms:spatial>Apeldoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3'">
                <dcterms:spatial>Appingedam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='202'">
                <dcterms:spatial>Arnhem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='106'">
                <dcterms:spatial>Assen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='743'">
                <dcterms:spatial>Asten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='744'">
                <dcterms:spatial>Baarle-Nassau</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='308'">
                <dcterms:spatial>Baarn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='489'">
                <dcterms:spatial>Barendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='203'">
                <dcterms:spatial>Barneveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='5'">
                <dcterms:spatial>Bedum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='888'">
                <dcterms:spatial>Beek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='370'">
                <dcterms:spatial>Beemster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='889'">
                <dcterms:spatial>Beesel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='7'">
                <dcterms:spatial>Bellingwedde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='491'">
                <dcterms:spatial>Bergambacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1724'">
                <dcterms:spatial>Bergeijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='893'">
                <dcterms:spatial>Bergen (L.)</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='373'">
                <dcterms:spatial>Bergen (NH.)</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='748'">
                <dcterms:spatial>Bergen op Zoom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1859'">
                <dcterms:spatial>Berkelland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1721'">
                <dcterms:spatial>Bernheze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='568'">
                <dcterms:spatial>Bernisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='753'">
                <dcterms:spatial>Best</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='209'">
                <dcterms:spatial>Beuningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='375'">
                <dcterms:spatial>Beverwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='63'">
                <dcterms:spatial>het Bildt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='310'">
                <dcterms:spatial>De Bilt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='585'">
                <dcterms:spatial>Binnenmaas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1728'">
                <dcterms:spatial>Bladel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='376'">
                <dcterms:spatial>Blaricum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='377'">
                <dcterms:spatial>Bloemendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='55'">
                <dcterms:spatial>Boarnsterhim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1901'">
                <dcterms:spatial>Bodegraven-Reeuwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='755'">
                <dcterms:spatial>Boekel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='9'">
                <dcterms:spatial>Ten Boer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1681'">
                <dcterms:spatial>Borger-Odoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='147'">
                <dcterms:spatial>Borne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='654'">
                <dcterms:spatial>Borsele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='499'">
                <dcterms:spatial>Boskoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='756'">
                <dcterms:spatial>Boxmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='757'">
                <dcterms:spatial>Boxtel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='758'">
                <dcterms:spatial>Breda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='501'">
                <dcterms:spatial>Brielle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1876'">
                <dcterms:spatial>Bronckhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='213'">
                <dcterms:spatial>Brummen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='899'">
                <dcterms:spatial>Brunssum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='312'">
                <dcterms:spatial>Bunnik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='313'">
                <dcterms:spatial>Bunschoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='214'">
                <dcterms:spatial>Buren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='381'">
                <dcterms:spatial>Bussum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='502'">
                <dcterms:spatial>Capelle aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='383'">
                <dcterms:spatial>Castricum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='109'">
                <dcterms:spatial>Coevorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1706'">
                <dcterms:spatial>Cranendonck</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='611'">
                <dcterms:spatial>Cromstrijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1684'">
                <dcterms:spatial>Cuijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='216'">
                <dcterms:spatial>Culemborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='148'">
                <dcterms:spatial>Dalfsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1891'">
                <dcterms:spatial>Dantumadiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='503'">
                <dcterms:spatial>Delft</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='10'">
                <dcterms:spatial>Delfzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='762'">
                <dcterms:spatial>Deurne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='150'">
                <dcterms:spatial>Deventer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='384'">
                <dcterms:spatial>Diemen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1774'">
                <dcterms:spatial>Dinkelland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='504'">
                <dcterms:spatial>Dirksland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='221'">
                <dcterms:spatial>Doesburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='222'">
                <dcterms:spatial>Doetinchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='766'">
                <dcterms:spatial>Dongen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='58'">
                <dcterms:spatial>Dongeradeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='505'">
                <dcterms:spatial>Dordrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='498'">
                <dcterms:spatial>Drechterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1719'">
                <dcterms:spatial>Drimmelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='303'">
                <dcterms:spatial>Dronten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='225'">
                <dcterms:spatial>Druten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='226'">
                <dcterms:spatial>Duiven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1711'">
                <dcterms:spatial>Echt-Susteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='385'">
                <dcterms:spatial>Edam-Volendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='228'">
                <dcterms:spatial>Ede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='317'">
                <dcterms:spatial>Eemnes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1651'">
                <dcterms:spatial>Eemsmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='770'">
                <dcterms:spatial>Eersel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1903'">
                <dcterms:spatial>Eijsden-Margraten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='772'">
                <dcterms:spatial>Eindhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='230'">
                <dcterms:spatial>Elburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='114'">
                <dcterms:spatial>Emmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='388'">
                <dcterms:spatial>Enkhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='153'">
                <dcterms:spatial>Enschede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='232'">
                <dcterms:spatial>Epe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='233'">
                <dcterms:spatial>Ermelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='777'">
                <dcterms:spatial>Etten-Leur</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1722'">
                <dcterms:spatial>Ferwerderadiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='70'">
                <dcterms:spatial>Franekeradeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='653'">
                <dcterms:spatial>Gaasterlân-Sleat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='779'">
                <dcterms:spatial>Geertruidenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='236'">
                <dcterms:spatial>Geldermalsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1771'">
                <dcterms:spatial>Geldrop-Mierlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1652'">
                <dcterms:spatial>Gemert-Bakel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='907'">
                <dcterms:spatial>Gennep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='689'">
                <dcterms:spatial>Giessenlanden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='784'">
                <dcterms:spatial>Gilze en Rijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='511'">
                <dcterms:spatial>Goedereede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='664'">
                <dcterms:spatial>Goes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='785'">
                <dcterms:spatial>Goirle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='512'">
                <dcterms:spatial>Gorinchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='513'">
                <dcterms:spatial>Gouda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='693'">
                <dcterms:spatial>Graafstroom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='365'">
                <dcterms:spatial>Graft-De Rijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='786'">
                <dcterms:spatial>Grave</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='518'">
                <dcterms:spatial>'s-Gravenhage</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='241'">
                <dcterms:spatial>Groesbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='14'">
                <dcterms:spatial>Groningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='15'">
                <dcterms:spatial>Grootegast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1729'">
                <dcterms:spatial>Gulpen-Wittem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='158'">
                <dcterms:spatial>Haaksbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='788'">
                <dcterms:spatial>Haaren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='392'">
                <dcterms:spatial>Haarlem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='393'">
                <dcterms:spatial>Haarlemmerliede en Spaarnwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='394'">
                <dcterms:spatial>Haarlemmermeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1655'">
                <dcterms:spatial>Halderberge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='160'">
                <dcterms:spatial>Hardenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='243'">
                <dcterms:spatial>Harderwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='523'">
                <dcterms:spatial>Hardinxveld-Giessendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='17'">
                <dcterms:spatial>Haren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='395'">
                <dcterms:spatial>Harenkarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='72'">
                <dcterms:spatial>Harlingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='244'">
                <dcterms:spatial>Hattem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='396'">
                <dcterms:spatial>Heemskerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='397'">
                <dcterms:spatial>Heemstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='246'">
                <dcterms:spatial>Heerde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='74'">
                <dcterms:spatial>Heerenveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='398'">
                <dcterms:spatial>Heerhugowaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='917'">
                <dcterms:spatial>Heerlen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1658'">
                <dcterms:spatial>Heeze-Leende</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='399'">
                <dcterms:spatial>Heiloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='400'">
                <dcterms:spatial>Den Helder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='163'">
                <dcterms:spatial>Hellendoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='530'">
                <dcterms:spatial>Hellevoetsluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='794'">
                <dcterms:spatial>Helmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='531'">
                <dcterms:spatial>Hendrik-Ido-Ambacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='164'">
                <dcterms:spatial>Hengelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='796'">
                <dcterms:spatial>'s-Hertogenbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='252'">
                <dcterms:spatial>Heumen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='797'">
                <dcterms:spatial>Heusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='534'">
                <dcterms:spatial>Hillegom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='798'">
                <dcterms:spatial>Hilvarenbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='402'">
                <dcterms:spatial>Hilversum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1735'">
                <dcterms:spatial>Hof van Twente</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='118'">
                <dcterms:spatial>Hoogeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='18'">
                <dcterms:spatial>Hoogezand-Sappemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='405'">
                <dcterms:spatial>Hoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1507'">
                <dcterms:spatial>Horst aan de Maas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='321'">
                <dcterms:spatial>Houten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='406'">
                <dcterms:spatial>Huizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='677'">
                <dcterms:spatial>Hulst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='353'">
                <dcterms:spatial>IJsselstein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1884'">
                <dcterms:spatial>Kaag en Braassem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='166'">
                <dcterms:spatial>Kampen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='678'">
                <dcterms:spatial>Kapelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='537'">
                <dcterms:spatial>Katwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='928'">
                <dcterms:spatial>Kerkrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1598'">
                <dcterms:spatial>Koggenland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='79'">
                <dcterms:spatial>Kollumerland en Nieuwkruisland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='588'">
                <dcterms:spatial>Korendijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='542'">
                <dcterms:spatial>Krimpen aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1659'">
                <dcterms:spatial>Laarbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1685'">
                <dcterms:spatial>Landerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='882'">
                <dcterms:spatial>Landgraaf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='415'">
                <dcterms:spatial>Landsmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='416'">
                <dcterms:spatial>Langedijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1621'">
                <dcterms:spatial>Lansingerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='417'">
                <dcterms:spatial>Laren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='22'">
                <dcterms:spatial>Leek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='545'">
                <dcterms:spatial>Leerdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='80'">
                <dcterms:spatial>Leeuwarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='81'">
                <dcterms:spatial>Leeuwarderadeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='546'">
                <dcterms:spatial>Leiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='547'">
                <dcterms:spatial>Leiderdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1916'">
                <dcterms:spatial>Leidschendam-Voorburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='995'">
                <dcterms:spatial>Lelystad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='82'">
                <dcterms:spatial>Lemsterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1640'">
                <dcterms:spatial>Leudal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='327'">
                <dcterms:spatial>Leusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='694'">
                <dcterms:spatial>Liesveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='733'">
                <dcterms:spatial>Lingewaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1705'">
                <dcterms:spatial>Lingewaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='553'">
                <dcterms:spatial>Lisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='140'">
                <dcterms:spatial>Littenseradiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='262'">
                <dcterms:spatial>Lochem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='809'">
                <dcterms:spatial>Loon op Zand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='331'">
                <dcterms:spatial>Lopik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='24'">
                <dcterms:spatial>Loppersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='168'">
                <dcterms:spatial>Losser</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1671'">
                <dcterms:spatial>Maasdonk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='263'">
                <dcterms:spatial>Maasdriel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1641'">
                <dcterms:spatial>Maasgouw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='556'">
                <dcterms:spatial>Maassluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='935'">
                <dcterms:spatial>Maastricht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1663'">
                <dcterms:spatial>De Marne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='25'">
                <dcterms:spatial>Marum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='420'">
                <dcterms:spatial>Medemblik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='938'">
                <dcterms:spatial>Meerssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1908'">
                <dcterms:spatial>Menameradiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1987'">
                <dcterms:spatial>Menterwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='119'">
                <dcterms:spatial>Meppel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='687'">
                <dcterms:spatial>Middelburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='559'">
                <dcterms:spatial>Middelharnis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1842'">
                <dcterms:spatial>Midden-Delfland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1731'">
                <dcterms:spatial>Midden-Drenthe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='815'">
                <dcterms:spatial>Mill en Sint Hubert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='265'">
                <dcterms:spatial>Millingen aan de Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1709'">
                <dcterms:spatial>Moerdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1955'">
                <dcterms:spatial>Montferland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='335'">
                <dcterms:spatial>Montfoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='944'">
                <dcterms:spatial>Mook en Middelaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='424'">
                <dcterms:spatial>Muiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='425'">
                <dcterms:spatial>Naarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1740'">
                <dcterms:spatial>Neder-Betuwe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='643'">
                <dcterms:spatial>Nederlek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='946'">
                <dcterms:spatial>Nederweert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='304'">
                <dcterms:spatial>Neerijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='412'">
                <dcterms:spatial>Niedorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='571'">
                <dcterms:spatial>Nieuw-Lekkerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='356'">
                <dcterms:spatial>Nieuwegein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='569'">
                <dcterms:spatial>Nieuwkoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='267'">
                <dcterms:spatial>Nijkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='268'">
                <dcterms:spatial>Nijmegen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1695'">
                <dcterms:spatial>Noord-Beveland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1699'">
                <dcterms:spatial>Noordenveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='171'">
                <dcterms:spatial>Noordoostpolder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='575'">
                <dcterms:spatial>Noordwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='576'">
                <dcterms:spatial>Noordwijkerhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='820'">
                <dcterms:spatial>Nuenen, Gerwen en Nederwetten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='302'">
                <dcterms:spatial>Nunspeet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='951'">
                <dcterms:spatial>Nuth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='579'">
                <dcterms:spatial>Oegstgeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='823'">
                <dcterms:spatial>Oirschot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='824'">
                <dcterms:spatial>Oisterwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1895'">
                <dcterms:spatial>Oldambt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='269'">
                <dcterms:spatial>Oldebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='173'">
                <dcterms:spatial>Oldenzaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1773'">
                <dcterms:spatial>Olst-Wijhe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='175'">
                <dcterms:spatial>Ommen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='881'">
                <dcterms:spatial>Onderbanken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1586'">
                <dcterms:spatial>Oost Gelre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='826'">
                <dcterms:spatial>Oosterhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='580'">
                <dcterms:spatial>Oostflakkee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='85'">
                <dcterms:spatial>Ooststellingwerf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='431'">
                <dcterms:spatial>Oostzaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='432'">
                <dcterms:spatial>Opmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='86'">
                <dcterms:spatial>Opsterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='828'">
                <dcterms:spatial>Oss</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='584'">
                <dcterms:spatial>Oud-Beijerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1509'">
                <dcterms:spatial>Oude IJsselstreek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='437'">
                <dcterms:spatial>Ouder-Amstel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='644'">
                <dcterms:spatial>Ouderkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='589'">
                <dcterms:spatial>Oudewater</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1734'">
                <dcterms:spatial>Overbetuwe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='590'">
                <dcterms:spatial>Papendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1894'">
                <dcterms:spatial>Peel en Maas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='765'">
                <dcterms:spatial>Pekela</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1926'">
                <dcterms:spatial>Pijnacker-Nootdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='439'">
                <dcterms:spatial>Purmerend</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='273'">
                <dcterms:spatial>Putten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='177'">
                <dcterms:spatial>Raalte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='703'">
                <dcterms:spatial>Reimerswaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='274'">
                <dcterms:spatial>Renkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='339'">
                <dcterms:spatial>Renswoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1667'">
                <dcterms:spatial>Reusel-De Mierden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='275'">
                <dcterms:spatial>Rheden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='340'">
                <dcterms:spatial>Rhenen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='597'">
                <dcterms:spatial>Ridderkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='196'">
                <dcterms:spatial>Rijnwaarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1672'">
                <dcterms:spatial>Rijnwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1742'">
                <dcterms:spatial>Rijssen-Holten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='603'">
                <dcterms:spatial>Rijswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1669'">
                <dcterms:spatial>Roerdalen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='957'">
                <dcterms:spatial>Roermond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='736'">
                <dcterms:spatial>De Ronde Venen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1674'">
                <dcterms:spatial>Roosendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='599'">
                <dcterms:spatial>Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='277'">
                <dcterms:spatial>Rozendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='840'">
                <dcterms:spatial>Rucphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='441'">
                <dcterms:spatial>Schagen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='458'">
                <dcterms:spatial>Schermer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='279'">
                <dcterms:spatial>Scherpenzeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='606'">
                <dcterms:spatial>Schiedam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='88'">
                <dcterms:spatial>Schiermonnikoog</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='844'">
                <dcterms:spatial>Schijndel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='962'">
                <dcterms:spatial>Schinnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='608'">
                <dcterms:spatial>Schoonhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1676'">
                <dcterms:spatial>Schouwen-Duiveland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='965'">
                <dcterms:spatial>Simpelveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1702'">
                <dcterms:spatial>Sint Anthonis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='845'">
                <dcterms:spatial>Sint-Michielsgestel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='846'">
                <dcterms:spatial>Sint-Oedenrode</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1883'">
                <dcterms:spatial>Sittard-Geleen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='51'">
                <dcterms:spatial>Skarsterlân</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='610'">
                <dcterms:spatial>Sliedrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='40'">
                <dcterms:spatial>Slochteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1714'">
                <dcterms:spatial>Sluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='90'">
                <dcterms:spatial>Smallingerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='342'">
                <dcterms:spatial>Soest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='847'">
                <dcterms:spatial>Someren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='848'">
                <dcterms:spatial>Son en Breugel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='612'">
                <dcterms:spatial>Spijkenisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='37'">
                <dcterms:spatial>Stadskanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='180'">
                <dcterms:spatial>Staphorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='532'">
                <dcterms:spatial>Stede Broec</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='851'">
                <dcterms:spatial>Steenbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1708'">
                <dcterms:spatial>Steenwijkerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='971'">
                <dcterms:spatial>Stein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1904'">
                <dcterms:spatial>Stichtse Vecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='617'">
                <dcterms:spatial>Strijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1900'">
                <dcterms:spatial>Súdwest Fryslân</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='715'">
                <dcterms:spatial>Terneuzen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='93'">
                <dcterms:spatial>Terschelling</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='448'">
                <dcterms:spatial>Texel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1525'">
                <dcterms:spatial>Teylingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='716'">
                <dcterms:spatial>Tholen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='281'">
                <dcterms:spatial>Tiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='855'">
                <dcterms:spatial>Tilburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='183'">
                <dcterms:spatial>Tubbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1700'">
                <dcterms:spatial>Twenterand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1730'">
                <dcterms:spatial>Tynaarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='737'">
                <dcterms:spatial>Tytsjerksteradiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='282'">
                <dcterms:spatial>Ubbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='856'">
                <dcterms:spatial>Uden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='450'">
                <dcterms:spatial>Uitgeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='451'">
                <dcterms:spatial>Uithoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='184'">
                <dcterms:spatial>Urk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='344'">
                <dcterms:spatial>Utrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1581'">
                <dcterms:spatial>Utrechtse Heuvelrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='981'">
                <dcterms:spatial>Vaals</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='994'">
                <dcterms:spatial>Valkenburg aan de Geul</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='858'">
                <dcterms:spatial>Valkenswaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='47'">
                <dcterms:spatial>Veendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='345'">
                <dcterms:spatial>Veenendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='717'">
                <dcterms:spatial>Veere</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='860'">
                <dcterms:spatial>Veghel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='861'">
                <dcterms:spatial>Veldhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='453'">
                <dcterms:spatial>Velsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='983'">
                <dcterms:spatial>Venlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='984'">
                <dcterms:spatial>Venray</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='620'">
                <dcterms:spatial>Vianen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='622'">
                <dcterms:spatial>Vlaardingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='48'">
                <dcterms:spatial>Vlagtwedde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='96'">
                <dcterms:spatial>Vlieland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='718'">
                <dcterms:spatial>Vlissingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='623'">
                <dcterms:spatial>Vlist</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='986'">
                <dcterms:spatial>Voerendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='626'">
                <dcterms:spatial>Voorschoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='285'">
                <dcterms:spatial>Voorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='865'">
                <dcterms:spatial>Vught</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='866'">
                <dcterms:spatial>Waalre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='867'">
                <dcterms:spatial>Waalwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='627'">
                <dcterms:spatial>Waddinxveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='289'">
                <dcterms:spatial>Wageningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='629'">
                <dcterms:spatial>Wassenaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='852'">
                <dcterms:spatial>Waterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='988'">
                <dcterms:spatial>Weert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='457'">
                <dcterms:spatial>Weesp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='870'">
                <dcterms:spatial>Werkendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='668'">
                <dcterms:spatial>West Maas en Waal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1701'">
                <dcterms:spatial>Westerveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='293'">
                <dcterms:spatial>Westervoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1783'">
                <dcterms:spatial>Westland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='98'">
                <dcterms:spatial>Weststellingwerf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='614'">
                <dcterms:spatial>Westvoorne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='189'">
                <dcterms:spatial>Wierden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='462'">
                <dcterms:spatial>Wieringen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='463'">
                <dcterms:spatial>Wieringermeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='296'">
                <dcterms:spatial>Wijchen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1696'">
                <dcterms:spatial>Wijdemeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='352'">
                <dcterms:spatial>Wijk bij Duurstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='53'">
                <dcterms:spatial>Winsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='294'">
                <dcterms:spatial>Winterswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='873'">
                <dcterms:spatial>Woensdrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='632'">
                <dcterms:spatial>Woerden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1690'">
                <dcterms:spatial>De Wolden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='880'">
                <dcterms:spatial>Wormerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='351'">
                <dcterms:spatial>Woudenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='874'">
                <dcterms:spatial>Woudrichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='479'">
                <dcterms:spatial>Zaanstad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='297'">
                <dcterms:spatial>Zaltbommel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='473'">
                <dcterms:spatial>Zandvoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='707'">
                <dcterms:spatial>Zederik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='478'">
                <dcterms:spatial>Zeevang</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='50'">
                <dcterms:spatial>Zeewolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='355'">
                <dcterms:spatial>Zeist</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='299'">
                <dcterms:spatial>Zevenaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='476'">
                <dcterms:spatial>Zijpe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='637'">
                <dcterms:spatial>Zoetermeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='638'">
                <dcterms:spatial>Zoeterwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='56'">
                <dcterms:spatial>Zuidhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1892'">
                <dcterms:spatial>Zuidplas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='879'">
                <dcterms:spatial>Zundert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='301'">
                <dcterms:spatial>Zutphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1896'">
                <dcterms:spatial>Zwartewaterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='642'">
                <dcterms:spatial>Zwijndrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='193'">
                <dcterms:spatial>Zwolle</dcterms:spatial>
              </xsl:when>
              <xsl:otherwise>
                <dcterms:spatial>
                  <xsl:value-of select="."/>
                </dcterms:spatial>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:woonplaatscode">
            <xsl:variable name="waarde" select="."/>
            <xsl:choose>
              <xsl:when test="$waarde='1000'">
                <dcterms:spatial>Hoogerheide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1001'">
                <dcterms:spatial>Huijbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1002'">
                <dcterms:spatial>Ossendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1003'">
                <dcterms:spatial>Putte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1004'">
                <dcterms:spatial>Woensdrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1005'">
                <dcterms:spatial>Gouda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1006'">
                <dcterms:spatial>Waalre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1007'">
                <dcterms:spatial>Middelburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1008'">
                <dcterms:spatial>Arnemuiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1009'">
                <dcterms:spatial>Nieuw- en Sint Joosland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1010'">
                <dcterms:spatial>Etten-Leur</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1011'">
                <dcterms:spatial>Huizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1012'">
                <dcterms:spatial>Weesp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1013'">
                <dcterms:spatial>Soest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1014'">
                <dcterms:spatial>Soesterberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1015'">
                <dcterms:spatial>Vlaardingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1016'">
                <dcterms:spatial>Nieuwerkerk aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1017'">
                <dcterms:spatial>Foxhol</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1018'">
                <dcterms:spatial>Hoogezand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1019'">
                <dcterms:spatial>Kiel-Windeweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1020'">
                <dcterms:spatial>Kropswolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1021'">
                <dcterms:spatial>Sappemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1022'">
                <dcterms:spatial>Waterhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1023'">
                <dcterms:spatial>Westerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1024'">
                <dcterms:spatial>Amsterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1025'">
                <dcterms:spatial>Amsterdam Zuidoost</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1026'">
                <dcterms:spatial>America</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1027'">
                <dcterms:spatial>Broekhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1028'">
                <dcterms:spatial>Broekhuizenvorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1029'">
                <dcterms:spatial>Griendtsveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1030'">
                <dcterms:spatial>Grubbenvorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1031'">
                <dcterms:spatial>Hegelsom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1032'">
                <dcterms:spatial>Horst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1033'">
                <dcterms:spatial>Lottum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1034'">
                <dcterms:spatial>Melderslo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1035'">
                <dcterms:spatial>Meterik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1036'">
                <dcterms:spatial>Hilversum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1037'">
                <dcterms:spatial>Eemnes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1038'">
                <dcterms:spatial>Daarle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1039'">
                <dcterms:spatial>Daarlerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1040'">
                <dcterms:spatial>Haarle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1041'">
                <dcterms:spatial>Hellendoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1042'">
                <dcterms:spatial>Nijverdal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1043'">
                <dcterms:spatial>Tilburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1044'">
                <dcterms:spatial>Berkel-Enschot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1045'">
                <dcterms:spatial>Udenhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1046'">
                <dcterms:spatial>Staphorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1047'">
                <dcterms:spatial>Rouveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1048'">
                <dcterms:spatial>IJhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1049'">
                <dcterms:spatial>Punthorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1050'">
                <dcterms:spatial>Amstelveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1051'">
                <dcterms:spatial>Lelystad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1052'">
                <dcterms:spatial>Naarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1053'">
                <dcterms:spatial>Andijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1054'">
                <dcterms:spatial>Barneveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1055'">
                <dcterms:spatial>Voorthuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1056'">
                <dcterms:spatial>Kootwijkerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1057'">
                <dcterms:spatial>Garderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1058'">
                <dcterms:spatial>Terschuur</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1059'">
                <dcterms:spatial>Stroe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1060'">
                <dcterms:spatial>Zwartebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1061'">
                <dcterms:spatial>De Glind</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1062'">
                <dcterms:spatial>Kootwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1063'">
                <dcterms:spatial>Achterveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1064'">
                <dcterms:spatial>Bladel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1065'">
                <dcterms:spatial>Hapert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1066'">
                <dcterms:spatial>Hoogeloon</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1067'">
                <dcterms:spatial>Casteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1068'">
                <dcterms:spatial>Netersel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1069'">
                <dcterms:spatial>Maassluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1070'">
                <dcterms:spatial>Groningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1071'">
                <dcterms:spatial>Woerden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1072'">
                <dcterms:spatial>Harmelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1073'">
                <dcterms:spatial>Kamerik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1074'">
                <dcterms:spatial>Zegveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1075'">
                <dcterms:spatial>Zeewolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1076'">
                <dcterms:spatial>Oost-Souburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1077'">
                <dcterms:spatial>Ritthem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1078'">
                <dcterms:spatial>Vlissingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1079'">
                <dcterms:spatial>Aarlanderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1080'">
                <dcterms:spatial>Alphen aan den Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1081'">
                <dcterms:spatial>Zwammerdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1082'">
                <dcterms:spatial>Meppel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1083'">
                <dcterms:spatial>Nijeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1084'">
                <dcterms:spatial>Rogat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1085'">
                <dcterms:spatial>De Schiphorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1086'">
                <dcterms:spatial>Broekhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1087'">
                <dcterms:spatial>Wierden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1088'">
                <dcterms:spatial>Enter</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1089'">
                <dcterms:spatial>Hoge Hexel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1090'">
                <dcterms:spatial>Notter</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1091'">
                <dcterms:spatial>Zuna</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1092'">
                <dcterms:spatial>Baarn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1093'">
                <dcterms:spatial>Lage Vuursche</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1094'">
                <dcterms:spatial>Dodewaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1095'">
                <dcterms:spatial>Echteld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1096'">
                <dcterms:spatial>Kesteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1097'">
                <dcterms:spatial>Ochten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1098'">
                <dcterms:spatial>Opheusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1099'">
                <dcterms:spatial>IJzendoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1100'">
                <dcterms:spatial>Valkenswaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1101'">
                <dcterms:spatial>Eindhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1102'">
                <dcterms:spatial>Bovenkarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1103'">
                <dcterms:spatial>Grootebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1104'">
                <dcterms:spatial>Lutjebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1105'">
                <dcterms:spatial>Capelle aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1106'">
                <dcterms:spatial>Wervershoof</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1107'">
                <dcterms:spatial>Zwaagdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1108'">
                <dcterms:spatial>Nieuwegein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1109'">
                <dcterms:spatial>Empe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1110'">
                <dcterms:spatial>Tonden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1111'">
                <dcterms:spatial>Brummen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1112'">
                <dcterms:spatial>Leuvenheim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1113'">
                <dcterms:spatial>Hall</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1114'">
                <dcterms:spatial>Eerbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1115'">
                <dcterms:spatial>Sint Pancras</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1116'">
                <dcterms:spatial>Broek op Langedijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1117'">
                <dcterms:spatial>Zuid-Scharwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1118'">
                <dcterms:spatial>Noord-Scharwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1119'">
                <dcterms:spatial>Oudkarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1120'">
                <dcterms:spatial>Koedijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1121'">
                <dcterms:spatial>Bergen L</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1122'">
                <dcterms:spatial>Afferden L</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1123'">
                <dcterms:spatial>Siebengewald</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1124'">
                <dcterms:spatial>Well L</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1125'">
                <dcterms:spatial>Wellerlooi</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1126'">
                <dcterms:spatial>Geldrop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1127'">
                <dcterms:spatial>Mierlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1128'">
                <dcterms:spatial>Axel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1129'">
                <dcterms:spatial>Biervliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1130'">
                <dcterms:spatial>Hoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1131'">
                <dcterms:spatial>Koewacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1132'">
                <dcterms:spatial>Overslag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1133'">
                <dcterms:spatial>Philippine</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1134'">
                <dcterms:spatial>Sas van Gent</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1135'">
                <dcterms:spatial>Sluiskil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1136'">
                <dcterms:spatial>Spui</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1137'">
                <dcterms:spatial>Terneuzen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1138'">
                <dcterms:spatial>Westdorpe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1139'">
                <dcterms:spatial>Zaamslag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1140'">
                <dcterms:spatial>Zuiddorpe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1141'">
                <dcterms:spatial>Pijnacker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1142'">
                <dcterms:spatial>Nootdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1143'">
                <dcterms:spatial>Delfgauw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1144'">
                <dcterms:spatial>Krimpen aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1145'">
                <dcterms:spatial>Enschede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1146'">
                <dcterms:spatial>Helmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1147'">
                <dcterms:spatial>Amen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1148'">
                <dcterms:spatial>Anderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1149'">
                <dcterms:spatial>Anloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1150'">
                <dcterms:spatial>Annen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1151'">
                <dcterms:spatial>Annerveenschekanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1152'">
                <dcterms:spatial>Balloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1153'">
                <dcterms:spatial>Balloërveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1154'">
                <dcterms:spatial>Deurze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1155'">
                <dcterms:spatial>Eext</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1156'">
                <dcterms:spatial>Eexterveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1157'">
                <dcterms:spatial>Eexterveenschekanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1158'">
                <dcterms:spatial>Eexterzandvoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1159'">
                <dcterms:spatial>Ekehaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1160'">
                <dcterms:spatial>Eldersloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1161'">
                <dcterms:spatial>Eleveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1162'">
                <dcterms:spatial>Gasselte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1163'">
                <dcterms:spatial>Gasselternijveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1164'">
                <dcterms:spatial>Gasselternijveenschemond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1165'">
                <dcterms:spatial>Gasteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1166'">
                <dcterms:spatial>Geelbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1167'">
                <dcterms:spatial>Gieten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1168'">
                <dcterms:spatial>Gieterveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1169'">
                <dcterms:spatial>Grolloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1170'">
                <dcterms:spatial>Marwijksoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1171'">
                <dcterms:spatial>Nieuw Annerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1172'">
                <dcterms:spatial>Nieuwediep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1173'">
                <dcterms:spatial>Nijlande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1174'">
                <dcterms:spatial>Nooitgedacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1175'">
                <dcterms:spatial>Oud Annerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1176'">
                <dcterms:spatial>Papenvoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1177'">
                <dcterms:spatial>Rolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1178'">
                <dcterms:spatial>Schipborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1179'">
                <dcterms:spatial>Schoonloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1180'">
                <dcterms:spatial>Spijkerboor</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1181'">
                <dcterms:spatial>Vredenheim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1182'">
                <dcterms:spatial>Zwolle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1183'">
                <dcterms:spatial>Avenhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1184'">
                <dcterms:spatial>Berkhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1185'">
                <dcterms:spatial>De Goorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1186'">
                <dcterms:spatial>Hensbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1187'">
                <dcterms:spatial>Obdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1188'">
                <dcterms:spatial>Oudendijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1189'">
                <dcterms:spatial>Scharwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1190'">
                <dcterms:spatial>Spierdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1191'">
                <dcterms:spatial>Ursem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1192'">
                <dcterms:spatial>Zuidermeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1193'">
                <dcterms:spatial>Aalten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1194'">
                <dcterms:spatial>Bredevoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1195'">
                <dcterms:spatial>De Heurne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1196'">
                <dcterms:spatial>Dinxperlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1197'">
                <dcterms:spatial>Leeuwarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1198'">
                <dcterms:spatial>Lekkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1199'">
                <dcterms:spatial>Miedum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1200'">
                <dcterms:spatial>Snakkerburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1201'">
                <dcterms:spatial>Goutum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1202'">
                <dcterms:spatial>Hempens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1203'">
                <dcterms:spatial>Teerns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1204'">
                <dcterms:spatial>Swichum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1205'">
                <dcterms:spatial>Wirdum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1206'">
                <dcterms:spatial>Wytgaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1207'">
                <dcterms:spatial>Oirschot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1208'">
                <dcterms:spatial>Oost West en Middelbeers</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1209'">
                <dcterms:spatial>Oostvoorne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1210'">
                <dcterms:spatial>Rockanje</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1211'">
                <dcterms:spatial>Tinte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1212'">
                <dcterms:spatial>Moerkapelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1213'">
                <dcterms:spatial>Zevenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1214'">
                <dcterms:spatial>Beugen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1215'">
                <dcterms:spatial>Boxmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1216'">
                <dcterms:spatial>Groeningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1217'">
                <dcterms:spatial>Holthees</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1218'">
                <dcterms:spatial>Maashees</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1219'">
                <dcterms:spatial>Oeffelt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1220'">
                <dcterms:spatial>Overloon</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1221'">
                <dcterms:spatial>Rijkevoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1222'">
                <dcterms:spatial>Sambeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1223'">
                <dcterms:spatial>Vierlingsbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1224'">
                <dcterms:spatial>Vortum-Mullem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1225'">
                <dcterms:spatial>Gulpen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1226'">
                <dcterms:spatial>Ingber</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1227'">
                <dcterms:spatial>Reijmerstok</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1228'">
                <dcterms:spatial>Heijenrath</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1229'">
                <dcterms:spatial>Slenaken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1230'">
                <dcterms:spatial>Beutenaken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1231'">
                <dcterms:spatial>Mechelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1232'">
                <dcterms:spatial>Epen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1233'">
                <dcterms:spatial>Wittem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1234'">
                <dcterms:spatial>Eys</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1235'">
                <dcterms:spatial>Elkenrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1236'">
                <dcterms:spatial>Wijlre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1237'">
                <dcterms:spatial>Dalfsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1238'">
                <dcterms:spatial>Lemelerveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1239'">
                <dcterms:spatial>Nieuwleusen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1240'">
                <dcterms:spatial>Veendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1241'">
                <dcterms:spatial>Wildervank</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1242'">
                <dcterms:spatial>Borgercompagnie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1243'">
                <dcterms:spatial>Heiloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1244'">
                <dcterms:spatial>Moordrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1245'">
                <dcterms:spatial>'s-Gravenhage</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1246'">
                <dcterms:spatial>Oudenbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1247'">
                <dcterms:spatial>Stampersgat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1248'">
                <dcterms:spatial>Oud Gastel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1249'">
                <dcterms:spatial>Bosschenhoofd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1250'">
                <dcterms:spatial>Hoeven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1251'">
                <dcterms:spatial>Baarland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1252'">
                <dcterms:spatial>Borssele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1253'">
                <dcterms:spatial>Driewegen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1254'">
                <dcterms:spatial>Ellewoutsdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1255'">
                <dcterms:spatial>Heinkenszand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1256'">
                <dcterms:spatial>Hoedekenskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1257'">
                <dcterms:spatial>Kwadendamme</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1258'">
                <dcterms:spatial>Lewedorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1259'">
                <dcterms:spatial>Nieuwdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1260'">
                <dcterms:spatial>Nisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1261'">
                <dcterms:spatial>Oudelande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1262'">
                <dcterms:spatial>Ovezande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1263'">
                <dcterms:spatial>'s-Gravenpolder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1264'">
                <dcterms:spatial>'s-Heer Abtskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1265'">
                <dcterms:spatial>'s-Heerenhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1266'">
                <dcterms:spatial>Haren Gn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1267'">
                <dcterms:spatial>Glimmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1268'">
                <dcterms:spatial>Noordlaren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1269'">
                <dcterms:spatial>Onnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1270'">
                <dcterms:spatial>Almere</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1271'">
                <dcterms:spatial>Emmeloord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1272'">
                <dcterms:spatial>Bant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1273'">
                <dcterms:spatial>Luttelgeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1274'">
                <dcterms:spatial>Marknesse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1275'">
                <dcterms:spatial>Kraggenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1276'">
                <dcterms:spatial>Ens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1277'">
                <dcterms:spatial>Schokland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1278'">
                <dcterms:spatial>Nagele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1279'">
                <dcterms:spatial>Tollebeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1280'">
                <dcterms:spatial>Espel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1281'">
                <dcterms:spatial>Creil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1282'">
                <dcterms:spatial>Rutten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1283'">
                <dcterms:spatial>Albergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1284'">
                <dcterms:spatial>Fleringen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1285'">
                <dcterms:spatial>Geesteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1286'">
                <dcterms:spatial>Haarle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1287'">
                <dcterms:spatial>Harbrinkhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1288'">
                <dcterms:spatial>Hezingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1289'">
                <dcterms:spatial>Langeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1290'">
                <dcterms:spatial>Mander</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1291'">
                <dcterms:spatial>Manderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1292'">
                <dcterms:spatial>Mariaparochie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1293'">
                <dcterms:spatial>Reutum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1294'">
                <dcterms:spatial>Tubbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1295'">
                <dcterms:spatial>Vasse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1296'">
                <dcterms:spatial>Arnhem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1297'">
                <dcterms:spatial>Zevenaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1298'">
                <dcterms:spatial>Babberich</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1299'">
                <dcterms:spatial>Angerlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1300'">
                <dcterms:spatial>Giesbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1301'">
                <dcterms:spatial>Lathum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1302'">
                <dcterms:spatial>Wijk bij Duurstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1303'">
                <dcterms:spatial>Langbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1304'">
                <dcterms:spatial>Cothen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1305'">
                <dcterms:spatial>Zandvoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1306'">
                <dcterms:spatial>Bentveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1307'">
                <dcterms:spatial>Hilvarenbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1308'">
                <dcterms:spatial>Diessen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1309'">
                <dcterms:spatial>Biest-Houtakker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1310'">
                <dcterms:spatial>Haghorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1311'">
                <dcterms:spatial>Esbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1312'">
                <dcterms:spatial>Heerlen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1313'">
                <dcterms:spatial>Hoensbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1314'">
                <dcterms:spatial>Heeze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1315'">
                <dcterms:spatial>Leende</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1316'">
                <dcterms:spatial>Sterksel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1317'">
                <dcterms:spatial>Willemstad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1318'">
                <dcterms:spatial>Heijningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1319'">
                <dcterms:spatial>Klundert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1320'">
                <dcterms:spatial>Oudemolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1321'">
                <dcterms:spatial>Fijnaart</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1322'">
                <dcterms:spatial>Moerdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1323'">
                <dcterms:spatial>Zevenbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1324'">
                <dcterms:spatial>Standdaarbuiten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1325'">
                <dcterms:spatial>Noordhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1326'">
                <dcterms:spatial>Langeweg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1327'">
                <dcterms:spatial>Zevenbergschen Hoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1328'">
                <dcterms:spatial>Landsmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1329'">
                <dcterms:spatial>Den Ilp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1330'">
                <dcterms:spatial>Purmerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1331'">
                <dcterms:spatial>Bussum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1332'">
                <dcterms:spatial>Abcoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1333'">
                <dcterms:spatial>Baambrugge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1334'">
                <dcterms:spatial>Almen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1335'">
                <dcterms:spatial>Barchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1336'">
                <dcterms:spatial>Eefde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1337'">
                <dcterms:spatial>Epse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1338'">
                <dcterms:spatial>Gorssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1339'">
                <dcterms:spatial>Harfsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1340'">
                <dcterms:spatial>Joppe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1341'">
                <dcterms:spatial>Kring van Dorth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1342'">
                <dcterms:spatial>Laren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1343'">
                <dcterms:spatial>Lochem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1344'">
                <dcterms:spatial>Epe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1345'">
                <dcterms:spatial>Vaassen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1346'">
                <dcterms:spatial>Emst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1347'">
                <dcterms:spatial>Oene</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1348'">
                <dcterms:spatial>Froombosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1349'">
                <dcterms:spatial>Harkstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1350'">
                <dcterms:spatial>Hellum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1351'">
                <dcterms:spatial>Kolham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1352'">
                <dcterms:spatial>Lageland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1353'">
                <dcterms:spatial>Luddeweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1354'">
                <dcterms:spatial>Overschild</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1355'">
                <dcterms:spatial>Scharmer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1356'">
                <dcterms:spatial>Schildwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1357'">
                <dcterms:spatial>Siddeburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1358'">
                <dcterms:spatial>Slochteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1359'">
                <dcterms:spatial>Steendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1360'">
                <dcterms:spatial>Tjuchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1361'">
                <dcterms:spatial>Woudbloem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1362'">
                <dcterms:spatial>Tzum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1363'">
                <dcterms:spatial>Hitzum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1364'">
                <dcterms:spatial>Achlum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1365'">
                <dcterms:spatial>Herbaijum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1366'">
                <dcterms:spatial>Franeker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1367'">
                <dcterms:spatial>Zweins</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1368'">
                <dcterms:spatial>Peins</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1369'">
                <dcterms:spatial>Schalsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1370'">
                <dcterms:spatial>Ried</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1371'">
                <dcterms:spatial>Boer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1372'">
                <dcterms:spatial>Dongjum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1373'">
                <dcterms:spatial>Sexbierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1374'">
                <dcterms:spatial>Pietersbierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1375'">
                <dcterms:spatial>Oosterbierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1376'">
                <dcterms:spatial>Klooster Lidlum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1377'">
                <dcterms:spatial>Tzummarum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1378'">
                <dcterms:spatial>Firdgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1379'">
                <dcterms:spatial>Olst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1380'">
                <dcterms:spatial>Wijhe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1381'">
                <dcterms:spatial>Wesepe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1382'">
                <dcterms:spatial>Welsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1383'">
                <dcterms:spatial>Marle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1384'">
                <dcterms:spatial>Veldhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1385'">
                <dcterms:spatial>Abbenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1386'">
                <dcterms:spatial>Geervliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1387'">
                <dcterms:spatial>Heenvliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1388'">
                <dcterms:spatial>Oudenhoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1389'">
                <dcterms:spatial>Simonshaven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1390'">
                <dcterms:spatial>Zuidland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1391'">
                <dcterms:spatial>Bennebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1392'">
                <dcterms:spatial>Breukelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1393'">
                <dcterms:spatial>Kockengen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1394'">
                <dcterms:spatial>Nieuwer Ter Aa</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1395'">
                <dcterms:spatial>Zutphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1396'">
                <dcterms:spatial>Warnsveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1397'">
                <dcterms:spatial>Oude Pekela</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1398'">
                <dcterms:spatial>Nieuwe Pekela</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1399'">
                <dcterms:spatial>Rhenen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1400'">
                <dcterms:spatial>Elst Ut</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1401'">
                <dcterms:spatial>De Heen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1402'">
                <dcterms:spatial>Dinteloord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1403'">
                <dcterms:spatial>Kruisland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1404'">
                <dcterms:spatial>Nieuw-Vossemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1405'">
                <dcterms:spatial>Steenbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1406'">
                <dcterms:spatial>Maastricht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1407'">
                <dcterms:spatial>Agelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1408'">
                <dcterms:spatial>Denekamp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1409'">
                <dcterms:spatial>Deurningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1410'">
                <dcterms:spatial>Lattrop-Breklenkamp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1411'">
                <dcterms:spatial>Nutter</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1412'">
                <dcterms:spatial>Ootmarsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1413'">
                <dcterms:spatial>Oud Ootmarsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1414'">
                <dcterms:spatial>Rossum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1415'">
                <dcterms:spatial>Saasveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1416'">
                <dcterms:spatial>Tilligte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1417'">
                <dcterms:spatial>Weerselo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1418'">
                <dcterms:spatial>Beerta</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1419'">
                <dcterms:spatial>Finsterwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1420'">
                <dcterms:spatial>Bad Nieuweschans</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1421'">
                <dcterms:spatial>Nieuw Beerta</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1422'">
                <dcterms:spatial>Drieborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1423'">
                <dcterms:spatial>Oudezijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1424'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1425'">
                <dcterms:spatial>Biezenmortel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1426'">
                <dcterms:spatial>Esch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1427'">
                <dcterms:spatial>Haaren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1428'">
                <dcterms:spatial>Helvoirt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1429'">
                <dcterms:spatial>Vaals</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1430'">
                <dcterms:spatial>Lemiers</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1431'">
                <dcterms:spatial>Vijlen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1432'">
                <dcterms:spatial>Arkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1433'">
                <dcterms:spatial>Giessenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1434'">
                <dcterms:spatial>Hoogblokland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1435'">
                <dcterms:spatial>Hoornaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1436'">
                <dcterms:spatial>Noordeloos</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1437'">
                <dcterms:spatial>Schelluinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1438'">
                <dcterms:spatial>Ambt Delden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1439'">
                <dcterms:spatial>Bentelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1440'">
                <dcterms:spatial>Delden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1441'">
                <dcterms:spatial>Diepenheim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1442'">
                <dcterms:spatial>Goor</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1443'">
                <dcterms:spatial>Hengevelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1444'">
                <dcterms:spatial>Markelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1445'">
                <dcterms:spatial>Aarle-Rixtel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1446'">
                <dcterms:spatial>Beek en Donk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1447'">
                <dcterms:spatial>Lieshout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1448'">
                <dcterms:spatial>Mariahout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1449'">
                <dcterms:spatial>Beers NB</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1450'">
                <dcterms:spatial>Cuijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1451'">
                <dcterms:spatial>Haps</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1452'">
                <dcterms:spatial>Katwijk NB</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1453'">
                <dcterms:spatial>Linden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1454'">
                <dcterms:spatial>Sint Agatha</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1455'">
                <dcterms:spatial>Vianen NB</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1456'">
                <dcterms:spatial>Best</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1457'">
                <dcterms:spatial>Bergeijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1458'">
                <dcterms:spatial>Luyksgestel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1459'">
                <dcterms:spatial>Riethoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1460'">
                <dcterms:spatial>Westerhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1461'">
                <dcterms:spatial>Bunde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1462'">
                <dcterms:spatial>Geulle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1463'">
                <dcterms:spatial>Meerssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1464'">
                <dcterms:spatial>Moorveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1465'">
                <dcterms:spatial>Ulestraten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1466'">
                <dcterms:spatial>Lisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1467'">
                <dcterms:spatial>Kapelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1468'">
                <dcterms:spatial>Kloetinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1469'">
                <dcterms:spatial>Schore</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1470'">
                <dcterms:spatial>Wemeldinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1471'">
                <dcterms:spatial>Winschoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1472'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1473'">
                <dcterms:spatial>Venlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1474'">
                <dcterms:spatial>Tegelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1475'">
                <dcterms:spatial>Steyl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1476'">
                <dcterms:spatial>Belfeld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1477'">
                <dcterms:spatial>Papendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1478'">
                <dcterms:spatial>Winterswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1479'">
                <dcterms:spatial>Winterswijk Meddo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1480'">
                <dcterms:spatial>Winterswijk Huppel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1481'">
                <dcterms:spatial>Winterswijk Ratum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1482'">
                <dcterms:spatial>Winterswijk Kotten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1483'">
                <dcterms:spatial>Winterswijk Woold</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1484'">
                <dcterms:spatial>Winterswijk Miste</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1485'">
                <dcterms:spatial>Winterswijk Henxel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1486'">
                <dcterms:spatial>Winterswijk Brinkheurne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1487'">
                <dcterms:spatial>Winterswijk Corle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1488'">
                <dcterms:spatial>Hippolytushoef</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1489'">
                <dcterms:spatial>Den Oever</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1490'">
                <dcterms:spatial>Westerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1491'">
                <dcterms:spatial>Raalte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1492'">
                <dcterms:spatial>Heino</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1493'">
                <dcterms:spatial>Mariënheem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1494'">
                <dcterms:spatial>Luttenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1495'">
                <dcterms:spatial>Laag Zuthem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1496'">
                <dcterms:spatial>Lierderholthuis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1497'">
                <dcterms:spatial>Broekland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1498'">
                <dcterms:spatial>Heeten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1499'">
                <dcterms:spatial>Nieuw Heeten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1500'">
                <dcterms:spatial>Velp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1501'">
                <dcterms:spatial>Rheden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1502'">
                <dcterms:spatial>De Steeg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1503'">
                <dcterms:spatial>Ellecom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1504'">
                <dcterms:spatial>Dieren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1505'">
                <dcterms:spatial>Spankeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1506'">
                <dcterms:spatial>Laag-Soeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1507'">
                <dcterms:spatial>Hem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1508'">
                <dcterms:spatial>Hoogkarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1509'">
                <dcterms:spatial>Oosterblokker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1510'">
                <dcterms:spatial>Oosterleek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1511'">
                <dcterms:spatial>Schellinkhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1512'">
                <dcterms:spatial>Venhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1513'">
                <dcterms:spatial>Westwoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1514'">
                <dcterms:spatial>Wijdenes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1515'">
                <dcterms:spatial>Sevenum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1516'">
                <dcterms:spatial>Kronenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1517'">
                <dcterms:spatial>Evertsoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1518'">
                <dcterms:spatial>Muiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1519'">
                <dcterms:spatial>Muiderberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1520'">
                <dcterms:spatial>Drempt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1521'">
                <dcterms:spatial>Hoog-Keppel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1522'">
                <dcterms:spatial>Laag-Keppel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1523'">
                <dcterms:spatial>Hummelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1524'">
                <dcterms:spatial>Zelhem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1525'">
                <dcterms:spatial>Halle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1526'">
                <dcterms:spatial>Steenderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1527'">
                <dcterms:spatial>Baak</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1528'">
                <dcterms:spatial>Rha</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1529'">
                <dcterms:spatial>Olburgen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1530'">
                <dcterms:spatial>Bronkhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1531'">
                <dcterms:spatial>Toldijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1532'">
                <dcterms:spatial>Vierakker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1533'">
                <dcterms:spatial>Wichmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1534'">
                <dcterms:spatial>Vorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1535'">
                <dcterms:spatial>Hengelo (Gld)</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1536'">
                <dcterms:spatial>Keijenborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1537'">
                <dcterms:spatial>Ane</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1538'">
                <dcterms:spatial>Anerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1539'">
                <dcterms:spatial>Anevelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1540'">
                <dcterms:spatial>Balkbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1541'">
                <dcterms:spatial>Bergentheim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1542'">
                <dcterms:spatial>Brucht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1543'">
                <dcterms:spatial>Bruchterveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1544'">
                <dcterms:spatial>Collendoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1545'">
                <dcterms:spatial>De Krim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1546'">
                <dcterms:spatial>Dedemsvaart</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1547'">
                <dcterms:spatial>Den Velde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1548'">
                <dcterms:spatial>Diffelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1549'">
                <dcterms:spatial>Gramsbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1550'">
                <dcterms:spatial>Hardenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1551'">
                <dcterms:spatial>Heemserveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1552'">
                <dcterms:spatial>Holtheme</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1553'">
                <dcterms:spatial>Holthone</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1554'">
                <dcterms:spatial>Hoogenweg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1555'">
                <dcterms:spatial>Kloosterhaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1556'">
                <dcterms:spatial>Loozen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1557'">
                <dcterms:spatial>Lutten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1558'">
                <dcterms:spatial>Mariënberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1559'">
                <dcterms:spatial>Radewijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1560'">
                <dcterms:spatial>Rheeze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1561'">
                <dcterms:spatial>Rheezerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1562'">
                <dcterms:spatial>Schuinesloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1563'">
                <dcterms:spatial>Sibculo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1564'">
                <dcterms:spatial>Slagharen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1565'">
                <dcterms:spatial>Venebrugge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1566'">
                <dcterms:spatial>Rijssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1567'">
                <dcterms:spatial>Holten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1568'">
                <dcterms:spatial>Maarheeze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1569'">
                <dcterms:spatial>Soerendonk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1570'">
                <dcterms:spatial>Gastel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1571'">
                <dcterms:spatial>Budel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1572'">
                <dcterms:spatial>Budel-Schoot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1573'">
                <dcterms:spatial>Budel-Dorplein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1574'">
                <dcterms:spatial>Varsseveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1575'">
                <dcterms:spatial>Westendorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1576'">
                <dcterms:spatial>Heelweg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1577'">
                <dcterms:spatial>Terborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1578'">
                <dcterms:spatial>Silvolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1579'">
                <dcterms:spatial>Sinderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1580'">
                <dcterms:spatial>Ulft</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1581'">
                <dcterms:spatial>Etten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1582'">
                <dcterms:spatial>Varsselder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1583'">
                <dcterms:spatial>Netterden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1584'">
                <dcterms:spatial>Megchelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1585'">
                <dcterms:spatial>Gendringen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1586'">
                <dcterms:spatial>Voorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1587'">
                <dcterms:spatial>Breedenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1588'">
                <dcterms:spatial>Roosendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1589'">
                <dcterms:spatial>Wouw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1590'">
                <dcterms:spatial>Heerle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1591'">
                <dcterms:spatial>Nispen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1592'">
                <dcterms:spatial>Wouwse Plantage</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1593'">
                <dcterms:spatial>Moerstraten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1594'">
                <dcterms:spatial>Barendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1595'">
                <dcterms:spatial>'s-Hertogenbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1596'">
                <dcterms:spatial>Rosmalen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1597'">
                <dcterms:spatial>Hoofddorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1598'">
                <dcterms:spatial>Rozenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1599'">
                <dcterms:spatial>Oude Meer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1600'">
                <dcterms:spatial>Aalsmeerderbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1601'">
                <dcterms:spatial>Rijsenhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1602'">
                <dcterms:spatial>Nieuw-Vennep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1603'">
                <dcterms:spatial>Burgerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1604'">
                <dcterms:spatial>Schiphol-Rijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1605'">
                <dcterms:spatial>Leimuiderbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1606'">
                <dcterms:spatial>Weteringbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1607'">
                <dcterms:spatial>Buitenkaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1608'">
                <dcterms:spatial>Abbenes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1609'">
                <dcterms:spatial>Lisserbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1610'">
                <dcterms:spatial>Beinsdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1611'">
                <dcterms:spatial>Zwaanshoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1612'">
                <dcterms:spatial>Cruquius</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1613'">
                <dcterms:spatial>Vijfhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1614'">
                <dcterms:spatial>Zwanenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1615'">
                <dcterms:spatial>Boesingheliede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1616'">
                <dcterms:spatial>Lijnden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1617'">
                <dcterms:spatial>Badhoevedorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1618'">
                <dcterms:spatial>Schiphol</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1619'">
                <dcterms:spatial>Vught</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1620'">
                <dcterms:spatial>Cromvoirt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1621'">
                <dcterms:spatial>Leidschendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1622'">
                <dcterms:spatial>Voorburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1623'">
                <dcterms:spatial>Alem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1624'">
                <dcterms:spatial>Ammerzoden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1625'">
                <dcterms:spatial>Hedel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1626'">
                <dcterms:spatial>Heerewaarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1627'">
                <dcterms:spatial>Hoenzadriel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1628'">
                <dcterms:spatial>Hurwenen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1629'">
                <dcterms:spatial>Kerkdriel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1630'">
                <dcterms:spatial>Rossum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1631'">
                <dcterms:spatial>Velddriel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1632'">
                <dcterms:spatial>Well</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1633'">
                <dcterms:spatial>Alteveer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1634'">
                <dcterms:spatial>Een</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1635'">
                <dcterms:spatial>Een-West</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1636'">
                <dcterms:spatial>Foxwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1637'">
                <dcterms:spatial>Huis ter Heide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1638'">
                <dcterms:spatial>Langelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1639'">
                <dcterms:spatial>Leutingewolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1640'">
                <dcterms:spatial>Lieveren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1641'">
                <dcterms:spatial>Matsloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1642'">
                <dcterms:spatial>Nietap</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1643'">
                <dcterms:spatial>Nieuw-Roden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1644'">
                <dcterms:spatial>Norg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1645'">
                <dcterms:spatial>Peest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1646'">
                <dcterms:spatial>Peize</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1647'">
                <dcterms:spatial>Roden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1648'">
                <dcterms:spatial>Roderesch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1649'">
                <dcterms:spatial>Roderwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1650'">
                <dcterms:spatial>Steenbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1651'">
                <dcterms:spatial>Veenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1652'">
                <dcterms:spatial>Westervelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1653'">
                <dcterms:spatial>Zuidvelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1654'">
                <dcterms:spatial>Gelselaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1655'">
                <dcterms:spatial>Geesteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1656'">
                <dcterms:spatial>Neede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1657'">
                <dcterms:spatial>Rietmolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1658'">
                <dcterms:spatial>Ruurlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1659'">
                <dcterms:spatial>Borculo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1660'">
                <dcterms:spatial>Haarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1661'">
                <dcterms:spatial>Beltrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1662'">
                <dcterms:spatial>Eibergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1663'">
                <dcterms:spatial>Rekken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1664'">
                <dcterms:spatial>Amersfoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1665'">
                <dcterms:spatial>Hoogland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1666'">
                <dcterms:spatial>Hooglanderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1667'">
                <dcterms:spatial>Stoutenburg Noord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1668'">
                <dcterms:spatial>Oldenzaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1669'">
                <dcterms:spatial>Deurningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1670'">
                <dcterms:spatial>Beek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1671'">
                <dcterms:spatial>Spaubeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1672'">
                <dcterms:spatial>Maastricht-Airport</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1673'">
                <dcterms:spatial>Bakhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1674'">
                <dcterms:spatial>Balk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1675'">
                <dcterms:spatial>Elahuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1676'">
                <dcterms:spatial>Harich</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1677'">
                <dcterms:spatial>Kolderwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1678'">
                <dcterms:spatial>Mirns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1679'">
                <dcterms:spatial>Nijemirdum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1680'">
                <dcterms:spatial>Oudega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1681'">
                <dcterms:spatial>Oudemirdum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1682'">
                <dcterms:spatial>Rijs</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1683'">
                <dcterms:spatial>Ruigahuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1684'">
                <dcterms:spatial>Sloten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1685'">
                <dcterms:spatial>Sondel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1686'">
                <dcterms:spatial>Wijckel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1687'">
                <dcterms:spatial>Berkel en Rodenrijs</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1688'">
                <dcterms:spatial>Bergschenhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1689'">
                <dcterms:spatial>Bleiswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1690'">
                <dcterms:spatial>Banholt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1691'">
                <dcterms:spatial>Bemelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1692'">
                <dcterms:spatial>Cadier en Keer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1693'">
                <dcterms:spatial>Eckelrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1694'">
                <dcterms:spatial>Margraten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1695'">
                <dcterms:spatial>Mheer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1696'">
                <dcterms:spatial>Noorbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1697'">
                <dcterms:spatial>Scheulder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1698'">
                <dcterms:spatial>Sint Geertruid</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1699'">
                <dcterms:spatial>Kessel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1700'">
                <dcterms:spatial>Eijsden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1701'">
                <dcterms:spatial>Gronsveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1702'">
                <dcterms:spatial>Breda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1703'">
                <dcterms:spatial>Bavel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1704'">
                <dcterms:spatial>Ulvenhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1705'">
                <dcterms:spatial>Prinsenbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1706'">
                <dcterms:spatial>Teteringen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1707'">
                <dcterms:spatial>Katwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1708'">
                <dcterms:spatial>Rijnsburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1709'">
                <dcterms:spatial>Valkenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1710'">
                <dcterms:spatial>Hillegom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1711'">
                <dcterms:spatial>Beuningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1712'">
                <dcterms:spatial>Glane</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1713'">
                <dcterms:spatial>Losser</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1714'">
                <dcterms:spatial>de Lutte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1715'">
                <dcterms:spatial>Overdinkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1716'">
                <dcterms:spatial>Berg en Terblijt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1717'">
                <dcterms:spatial>Schin op Geul</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1718'">
                <dcterms:spatial>Valkenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1719'">
                <dcterms:spatial>Walem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1720'">
                <dcterms:spatial>Blesdijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1721'">
                <dcterms:spatial>Boijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1722'">
                <dcterms:spatial>De Blesse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1723'">
                <dcterms:spatial>De Hoeve</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1724'">
                <dcterms:spatial>Langelille</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1725'">
                <dcterms:spatial>Munnekeburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1726'">
                <dcterms:spatial>Nijeholtpade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1727'">
                <dcterms:spatial>Nijeholtwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1728'">
                <dcterms:spatial>Nijelamer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1729'">
                <dcterms:spatial>Nijetrijne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1730'">
                <dcterms:spatial>Noordwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1731'">
                <dcterms:spatial>Oldeholtpade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1732'">
                <dcterms:spatial>Oldeholtwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1733'">
                <dcterms:spatial>Oldelamer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1734'">
                <dcterms:spatial>Oldetrijne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1735'">
                <dcterms:spatial>Oosterstreek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1736'">
                <dcterms:spatial>Peperga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1737'">
                <dcterms:spatial>Scherpenzeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1738'">
                <dcterms:spatial>Slijkenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1739'">
                <dcterms:spatial>Sonnega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1740'">
                <dcterms:spatial>Spanga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1741'">
                <dcterms:spatial>Steggerda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1742'">
                <dcterms:spatial>Ter Idzard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1743'">
                <dcterms:spatial>Vinkega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1744'">
                <dcterms:spatial>Wolvega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1745'">
                <dcterms:spatial>Zandhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1746'">
                <dcterms:spatial>Heerhugowaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1747'">
                <dcterms:spatial>Edam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1748'">
                <dcterms:spatial>Volendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1749'">
                <dcterms:spatial>Purmer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1750'">
                <dcterms:spatial>Raamsdonksveer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1751'">
                <dcterms:spatial>Geertruidenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1752'">
                <dcterms:spatial>Raamsdonk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1753'">
                <dcterms:spatial>Appelscha</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1754'">
                <dcterms:spatial>Donkerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1755'">
                <dcterms:spatial>Elsloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1756'">
                <dcterms:spatial>Fochteloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1757'">
                <dcterms:spatial>Haule</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1758'">
                <dcterms:spatial>Haulerwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1759'">
                <dcterms:spatial>Langedijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1760'">
                <dcterms:spatial>Makkinga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1761'">
                <dcterms:spatial>Nijeberkoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1762'">
                <dcterms:spatial>Oldeberkoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1763'">
                <dcterms:spatial>Oosterwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1764'">
                <dcterms:spatial>Ravenswoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1765'">
                <dcterms:spatial>Waskemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1766'">
                <dcterms:spatial>Alphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1767'">
                <dcterms:spatial>Altforst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1768'">
                <dcterms:spatial>Appeltern</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1769'">
                <dcterms:spatial>Beneden-Leeuwen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1770'">
                <dcterms:spatial>Boven-Leeuwen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1771'">
                <dcterms:spatial>Dreumel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1772'">
                <dcterms:spatial>Maasbommel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1773'">
                <dcterms:spatial>Wamel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1774'">
                <dcterms:spatial>Deurne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1775'">
                <dcterms:spatial>Vlierden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1776'">
                <dcterms:spatial>Liessel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1777'">
                <dcterms:spatial>Neerkant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1778'">
                <dcterms:spatial>Helenaveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1779'">
                <dcterms:spatial>1e Exloërmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1780'">
                <dcterms:spatial>2e Exloërmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1781'">
                <dcterms:spatial>2e Valthermond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1782'">
                <dcterms:spatial>Borger</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1783'">
                <dcterms:spatial>Bronneger</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1784'">
                <dcterms:spatial>Bronnegerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1785'">
                <dcterms:spatial>Buinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1786'">
                <dcterms:spatial>Buinerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1787'">
                <dcterms:spatial>Drouwen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1788'">
                <dcterms:spatial>Drouwenermond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1789'">
                <dcterms:spatial>Drouwenerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1790'">
                <dcterms:spatial>Ees</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1791'">
                <dcterms:spatial>Eesergroen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1792'">
                <dcterms:spatial>Eeserveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1793'">
                <dcterms:spatial>Ellertshaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1794'">
                <dcterms:spatial>Exloërveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1795'">
                <dcterms:spatial>Exloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1796'">
                <dcterms:spatial>Klijndijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1797'">
                <dcterms:spatial>Nieuw-Buinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1798'">
                <dcterms:spatial>Odoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1799'">
                <dcterms:spatial>Odoornerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1800'">
                <dcterms:spatial>Valthe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1801'">
                <dcterms:spatial>Valthermond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1802'">
                <dcterms:spatial>Westdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1803'">
                <dcterms:spatial>Zandberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1804'">
                <dcterms:spatial>Hengelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1805'">
                <dcterms:spatial>Streefkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1806'">
                <dcterms:spatial>Groot-Ammers</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1807'">
                <dcterms:spatial>Nieuwpoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1808'">
                <dcterms:spatial>Langerak</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1809'">
                <dcterms:spatial>Waal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1810'">
                <dcterms:spatial>Alteveer gem Hoogeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1811'">
                <dcterms:spatial>Elim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1812'">
                <dcterms:spatial>Fluitenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1813'">
                <dcterms:spatial>Hollandscheveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1814'">
                <dcterms:spatial>Hoogeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1815'">
                <dcterms:spatial>Nieuweroord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1816'">
                <dcterms:spatial>Nieuwlande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1817'">
                <dcterms:spatial>Noordscheschut</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1818'">
                <dcterms:spatial>Pesse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1819'">
                <dcterms:spatial>Stuifzand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1820'">
                <dcterms:spatial>Tiendeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1821'">
                <dcterms:spatial>Wassenaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1822'">
                <dcterms:spatial>'s-Heer Arendskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1823'">
                <dcterms:spatial>'s-Heer Hendrikskinderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1824'">
                <dcterms:spatial>Goes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1825'">
                <dcterms:spatial>Kattendijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1826'">
                <dcterms:spatial>Kloetinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1827'">
                <dcterms:spatial>Wilhelminadorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1828'">
                <dcterms:spatial>Wolphaartsdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1829'">
                <dcterms:spatial>Ten Boer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1830'">
                <dcterms:spatial>Ten Post</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1831'">
                <dcterms:spatial>Garmerwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1832'">
                <dcterms:spatial>Thesinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1833'">
                <dcterms:spatial>Woltersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1834'">
                <dcterms:spatial>Sint Annen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1835'">
                <dcterms:spatial>Lellens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1836'">
                <dcterms:spatial>Winneweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1837'">
                <dcterms:spatial>Beets</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1838'">
                <dcterms:spatial>Hobrede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1839'">
                <dcterms:spatial>Kwadijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1840'">
                <dcterms:spatial>Middelie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1841'">
                <dcterms:spatial>Oosthuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1842'">
                <dcterms:spatial>Schardam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1843'">
                <dcterms:spatial>Warder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1844'">
                <dcterms:spatial>Elburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1845'">
                <dcterms:spatial>'t Harde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1846'">
                <dcterms:spatial>Doornspijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1847'">
                <dcterms:spatial>Uden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1848'">
                <dcterms:spatial>Volkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1849'">
                <dcterms:spatial>Odiliapeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1850'">
                <dcterms:spatial>Zeeland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1851'">
                <dcterms:spatial>Schaijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1852'">
                <dcterms:spatial>Reek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1853'">
                <dcterms:spatial>Klimmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1854'">
                <dcterms:spatial>Ransdaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1855'">
                <dcterms:spatial>Voerendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1856'">
                <dcterms:spatial>Blitterswijck</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1857'">
                <dcterms:spatial>Geijsteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1858'">
                <dcterms:spatial>Meerlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1859'">
                <dcterms:spatial>Swolgen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1860'">
                <dcterms:spatial>Tienray</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1861'">
                <dcterms:spatial>Wanssum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1862'">
                <dcterms:spatial>Andelst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1863'">
                <dcterms:spatial>Driel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1864'">
                <dcterms:spatial>Elst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1865'">
                <dcterms:spatial>Hemmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1866'">
                <dcterms:spatial>Herveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1867'">
                <dcterms:spatial>Heteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1868'">
                <dcterms:spatial>Homoet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1869'">
                <dcterms:spatial>Oosterhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1870'">
                <dcterms:spatial>Randwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1871'">
                <dcterms:spatial>Slijk-Ewijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1872'">
                <dcterms:spatial>Valburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1873'">
                <dcterms:spatial>Zetten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1874'">
                <dcterms:spatial>Hooge Mierde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1875'">
                <dcterms:spatial>Hulsel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1876'">
                <dcterms:spatial>Lage Mierde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1877'">
                <dcterms:spatial>Reusel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1878'">
                <dcterms:spatial>Assendelft</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1879'">
                <dcterms:spatial>Koog aan de Zaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1880'">
                <dcterms:spatial>Krommenie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1881'">
                <dcterms:spatial>Westknollendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1882'">
                <dcterms:spatial>Westzaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1883'">
                <dcterms:spatial>Wormerveer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1884'">
                <dcterms:spatial>Zaandam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1885'">
                <dcterms:spatial>Zaandijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1886'">
                <dcterms:spatial>Driehuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1887'">
                <dcterms:spatial>Grootschermer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1888'">
                <dcterms:spatial>Oterleek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1889'">
                <dcterms:spatial>Schermerhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1890'">
                <dcterms:spatial>Stompetoren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1891'">
                <dcterms:spatial>Ursem gem. S</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1892'">
                <dcterms:spatial>Zuidschermer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1893'">
                <dcterms:spatial>Scheemda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1894'">
                <dcterms:spatial>Nieuw Scheemda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1895'">
                <dcterms:spatial>'t Waar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1896'">
                <dcterms:spatial>Nieuwolda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1897'">
                <dcterms:spatial>Midwolda</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1898'">
                <dcterms:spatial>Oostwold</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1899'">
                <dcterms:spatial>Heiligerlee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1900'">
                <dcterms:spatial>Westerlee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1901'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1902'">
                <dcterms:spatial>De Rijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1903'">
                <dcterms:spatial>Graft</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1904'">
                <dcterms:spatial>Markenbinnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1905'">
                <dcterms:spatial>Noordeinde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1906'">
                <dcterms:spatial>Oost-Graftdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1907'">
                <dcterms:spatial>Starnmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1908'">
                <dcterms:spatial>West-Graftdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1909'">
                <dcterms:spatial>Hekendorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1910'">
                <dcterms:spatial>Oudewater</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1911'">
                <dcterms:spatial>Papekop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1912'">
                <dcterms:spatial>Snelrewaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1913'">
                <dcterms:spatial>Stein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1914'">
                <dcterms:spatial>Elsloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1915'">
                <dcterms:spatial>Urmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1916'">
                <dcterms:spatial>Thorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1917'">
                <dcterms:spatial>Heel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1918'">
                <dcterms:spatial>Beegden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1919'">
                <dcterms:spatial>Wessem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1920'">
                <dcterms:spatial>Maasbracht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1921'">
                <dcterms:spatial>Linne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1922'">
                <dcterms:spatial>Stevensweert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1923'">
                <dcterms:spatial>Ohé en Laak</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1924'">
                <dcterms:spatial>Almelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1925'">
                <dcterms:spatial>Aadorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1926'">
                <dcterms:spatial>Bornerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1927'">
                <dcterms:spatial>Noordwijkerhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1928'">
                <dcterms:spatial>De Zilk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1929'">
                <dcterms:spatial>Mijdrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1930'">
                <dcterms:spatial>Vinkeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1931'">
                <dcterms:spatial>Wilnis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1932'">
                <dcterms:spatial>Amstelhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1933'">
                <dcterms:spatial>Waverveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1934'">
                <dcterms:spatial>De Hoef</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1935'">
                <dcterms:spatial>Schiedam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1936'">
                <dcterms:spatial>Westervoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1937'">
                <dcterms:spatial>Bergen op Zoom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1938'">
                <dcterms:spatial>Halsteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1939'">
                <dcterms:spatial>Lepelstraat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1940'">
                <dcterms:spatial>Broek in Waterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1941'">
                <dcterms:spatial>Ilpendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1942'">
                <dcterms:spatial>Katwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1943'">
                <dcterms:spatial>Marken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1944'">
                <dcterms:spatial>Monnickendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1945'">
                <dcterms:spatial>Watergang</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1946'">
                <dcterms:spatial>Purmer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1947'">
                <dcterms:spatial>Uitdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1948'">
                <dcterms:spatial>Zuiderwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1949'">
                <dcterms:spatial>Reuver</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1950'">
                <dcterms:spatial>Beesel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1951'">
                <dcterms:spatial>Babyloniënbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1952'">
                <dcterms:spatial>Drongelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1953'">
                <dcterms:spatial>Eethen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1954'">
                <dcterms:spatial>Genderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1955'">
                <dcterms:spatial>Meeuwen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1956'">
                <dcterms:spatial>Veen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1957'">
                <dcterms:spatial>Wijk en Aalburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1958'">
                <dcterms:spatial>Benschop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1959'">
                <dcterms:spatial>Jaarsveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1960'">
                <dcterms:spatial>Lopik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1961'">
                <dcterms:spatial>Lopikerkapel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1962'">
                <dcterms:spatial>Polsbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1963'">
                <dcterms:spatial>Linschoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1964'">
                <dcterms:spatial>Montfoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1965'">
                <dcterms:spatial>Workum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1966'">
                <dcterms:spatial>Koudum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1967'">
                <dcterms:spatial>Molkwerum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1968'">
                <dcterms:spatial>Hindeloopen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1969'">
                <dcterms:spatial>Stavoren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1970'">
                <dcterms:spatial>Hemelum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1971'">
                <dcterms:spatial>Warns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1972'">
                <dcterms:spatial>Nijhuizum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1973'">
                <dcterms:spatial>It Heidenskip</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1974'">
                <dcterms:spatial>Weert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1975'">
                <dcterms:spatial>Stramproy</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1976'">
                <dcterms:spatial>Mill</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1977'">
                <dcterms:spatial>Sint Hubert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1978'">
                <dcterms:spatial>Langenboom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1979'">
                <dcterms:spatial>Wilbertoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1980'">
                <dcterms:spatial>Bleskensgraaf ca</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1981'">
                <dcterms:spatial>Brandwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1982'">
                <dcterms:spatial>Goudriaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1983'">
                <dcterms:spatial>Molenaarsgraaf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1984'">
                <dcterms:spatial>Ottoland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1985'">
                <dcterms:spatial>Oud-Alblas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1986'">
                <dcterms:spatial>Wijngaarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1987'">
                <dcterms:spatial>Barger-Compascuum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1988'">
                <dcterms:spatial>Emmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1989'">
                <dcterms:spatial>Emmer-Compascuum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1990'">
                <dcterms:spatial>Erica</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1991'">
                <dcterms:spatial>Klazienaveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1992'">
                <dcterms:spatial>Klazienaveen-Noord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1993'">
                <dcterms:spatial>Nieuw-Amsterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1994'">
                <dcterms:spatial>Nieuw-Dordrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1995'">
                <dcterms:spatial>Nieuw-Schoonebeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1996'">
                <dcterms:spatial>Nieuw-Weerdinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1997'">
                <dcterms:spatial>Roswinkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1998'">
                <dcterms:spatial>Schoonebeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='1999'">
                <dcterms:spatial>Veenoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2000'">
                <dcterms:spatial>Weiteveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2001'">
                <dcterms:spatial>Zandpol</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2002'">
                <dcterms:spatial>Zwartemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2003'">
                <dcterms:spatial>Sassenheim</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2004'">
                <dcterms:spatial>Voorhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2005'">
                <dcterms:spatial>Warmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2006'">
                <dcterms:spatial>Wageningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2007'">
                <dcterms:spatial>Putten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2008'">
                <dcterms:spatial>Baaium</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2009'">
                <dcterms:spatial>Baard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2010'">
                <dcterms:spatial>Bears</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2011'">
                <dcterms:spatial>Boazum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2012'">
                <dcterms:spatial>Britswert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2013'">
                <dcterms:spatial>Easterein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2014'">
                <dcterms:spatial>Easterlittens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2015'">
                <dcterms:spatial>Easterwierrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2016'">
                <dcterms:spatial>Hidaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2017'">
                <dcterms:spatial>Hilaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2018'">
                <dcterms:spatial>Hinnaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2019'">
                <dcterms:spatial>Húns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2020'">
                <dcterms:spatial>Iens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2021'">
                <dcterms:spatial>Itens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2022'">
                <dcterms:spatial>Jellum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2023'">
                <dcterms:spatial>Jorwert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2024'">
                <dcterms:spatial>Kûbaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2025'">
                <dcterms:spatial>Leons</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2026'">
                <dcterms:spatial>Lytsewierrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2027'">
                <dcterms:spatial>Mantgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2028'">
                <dcterms:spatial>Reahûs</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2029'">
                <dcterms:spatial>Rien</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2030'">
                <dcterms:spatial>Spannum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2031'">
                <dcterms:spatial>Waaksens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2032'">
                <dcterms:spatial>Weidum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2033'">
                <dcterms:spatial>Winsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2034'">
                <dcterms:spatial>Wiuwert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2035'">
                <dcterms:spatial>Wjelsryp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2036'">
                <dcterms:spatial>Wommels</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2037'">
                <dcterms:spatial>Nederweert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2038'">
                <dcterms:spatial>Ospel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2039'">
                <dcterms:spatial>Nederweert-Eind</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2040'">
                <dcterms:spatial>Leveroy</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2041'">
                <dcterms:spatial>Echt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2042'">
                <dcterms:spatial>Koningsbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2043'">
                <dcterms:spatial>Maria Hoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2044'">
                <dcterms:spatial>Sint Joost</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2045'">
                <dcterms:spatial>Susteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2046'">
                <dcterms:spatial>Roosteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2047'">
                <dcterms:spatial>Nieuwstadt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2048'">
                <dcterms:spatial>Veenendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2049'">
                <dcterms:spatial>Borne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2050'">
                <dcterms:spatial>Hertme</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2051'">
                <dcterms:spatial>Zenderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2052'">
                <dcterms:spatial>Culemborg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2053'">
                <dcterms:spatial>Houten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2054'">
                <dcterms:spatial>'t Goy</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2055'">
                <dcterms:spatial>Tull en 't Waal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2056'">
                <dcterms:spatial>Schalkwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2057'">
                <dcterms:spatial>Berlicum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2058'">
                <dcterms:spatial>Den Dungen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2059'">
                <dcterms:spatial>Gemonde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2060'">
                <dcterms:spatial>Sint-Michielsgestel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2061'">
                <dcterms:spatial>Noordwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2062'">
                <dcterms:spatial>Uithoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2063'">
                <dcterms:spatial>De Kwakel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2064'">
                <dcterms:spatial>Maarssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2065'">
                <dcterms:spatial>Tienhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2066'">
                <dcterms:spatial>Oud Zuilen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2067'">
                <dcterms:spatial>Gilze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2068'">
                <dcterms:spatial>Hulten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2069'">
                <dcterms:spatial>Molenschot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2070'">
                <dcterms:spatial>Rijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2071'">
                <dcterms:spatial>Tholen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2072'">
                <dcterms:spatial>Poortvliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2073'">
                <dcterms:spatial>Scherpenisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2074'">
                <dcterms:spatial>Sint-Maartensdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2075'">
                <dcterms:spatial>Stavenisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2076'">
                <dcterms:spatial>Sint-Annaland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2077'">
                <dcterms:spatial>Oud-Vossemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2078'">
                <dcterms:spatial>Sint Philipsland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2079'">
                <dcterms:spatial>Bolsward</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2080'">
                <dcterms:spatial>Landhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2081'">
                <dcterms:spatial>Ledeacker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2082'">
                <dcterms:spatial>Oploo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2083'">
                <dcterms:spatial>Sint Anthonis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2084'">
                <dcterms:spatial>Stevensbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2085'">
                <dcterms:spatial>Wanroij</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2086'">
                <dcterms:spatial>Westerbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2087'">
                <dcterms:spatial>Rijkevoort-De Walsert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2088'">
                <dcterms:spatial>Leiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2089'">
                <dcterms:spatial>Gemert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2090'">
                <dcterms:spatial>Bakel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2091'">
                <dcterms:spatial>Milheeze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2092'">
                <dcterms:spatial>Handel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2093'">
                <dcterms:spatial>De Mortel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2094'">
                <dcterms:spatial>De Rips</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2095'">
                <dcterms:spatial>Elsendorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2096'">
                <dcterms:spatial>Duiven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2097'">
                <dcterms:spatial>Groessen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2098'">
                <dcterms:spatial>Loo Gld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2099'">
                <dcterms:spatial>Amstenrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2100'">
                <dcterms:spatial>Doenrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2101'">
                <dcterms:spatial>Oirsbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2102'">
                <dcterms:spatial>Puth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2103'">
                <dcterms:spatial>Schinnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2104'">
                <dcterms:spatial>Sweikhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2105'">
                <dcterms:spatial>Lexmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2106'">
                <dcterms:spatial>Ameide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2107'">
                <dcterms:spatial>Tienhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2108'">
                <dcterms:spatial>Meerkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2109'">
                <dcterms:spatial>Hei- en Boeicop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2110'">
                <dcterms:spatial>Leerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2111'">
                <dcterms:spatial>Nieuwland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2112'">
                <dcterms:spatial>Leiderdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2113'">
                <dcterms:spatial>Oostzaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2114'">
                <dcterms:spatial>Aduard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2115'">
                <dcterms:spatial>Briltil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2116'">
                <dcterms:spatial>Den Ham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2117'">
                <dcterms:spatial>Den Horn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2118'">
                <dcterms:spatial>Grijpskerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2119'">
                <dcterms:spatial>Kommerzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2120'">
                <dcterms:spatial>Lauwerzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2121'">
                <dcterms:spatial>Niehove</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2122'">
                <dcterms:spatial>Niezijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2123'">
                <dcterms:spatial>Noordhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2124'">
                <dcterms:spatial>Oldehove</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2125'">
                <dcterms:spatial>Pieterzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2126'">
                <dcterms:spatial>Saaksum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2127'">
                <dcterms:spatial>Visvliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2128'">
                <dcterms:spatial>Zuidhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2129'">
                <dcterms:spatial>Ouderkerk aan de Amstel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2130'">
                <dcterms:spatial>Duivendrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2131'">
                <dcterms:spatial>Achthuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2132'">
                <dcterms:spatial>Den Bommel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2133'">
                <dcterms:spatial>Ooltgensplaat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2134'">
                <dcterms:spatial>Oude-Tonge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2135'">
                <dcterms:spatial>Scherpenzeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2136'">
                <dcterms:spatial>Nuenen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2137'">
                <dcterms:spatial>Hellevoetsluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2138'">
                <dcterms:spatial>Delft</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2139'">
                <dcterms:spatial>Schijndel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2140'">
                <dcterms:spatial>Meijel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2141'">
                <dcterms:spatial>Renswoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2142'">
                <dcterms:spatial>Doesburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2143'">
                <dcterms:spatial>Urk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2144'">
                <dcterms:spatial>Diemen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2145'">
                <dcterms:spatial>Aalden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2146'">
                <dcterms:spatial>Benneveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2147'">
                <dcterms:spatial>Coevorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2148'">
                <dcterms:spatial>Dalen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2149'">
                <dcterms:spatial>Dalerpeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2150'">
                <dcterms:spatial>Dalerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2151'">
                <dcterms:spatial>De Kiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2152'">
                <dcterms:spatial>Diphoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2153'">
                <dcterms:spatial>Erm</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2154'">
                <dcterms:spatial>Gees</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2155'">
                <dcterms:spatial>Geesbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2156'">
                <dcterms:spatial>Holsloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2157'">
                <dcterms:spatial>Meppen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2158'">
                <dcterms:spatial>Nieuwlande Coevorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2159'">
                <dcterms:spatial>Noord-Sleen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2160'">
                <dcterms:spatial>Oosterhesselen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2161'">
                <dcterms:spatial>Schoonoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2162'">
                <dcterms:spatial>Sleen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2163'">
                <dcterms:spatial>Stieltjeskanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2164'">
                <dcterms:spatial>'t Haantje</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2165'">
                <dcterms:spatial>Wachtum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2166'">
                <dcterms:spatial>Wezup</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2167'">
                <dcterms:spatial>Wezuperbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2168'">
                <dcterms:spatial>Zweeloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2169'">
                <dcterms:spatial>Zwinderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2170'">
                <dcterms:spatial>Middenbeemster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2171'">
                <dcterms:spatial>Noordbeemster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2172'">
                <dcterms:spatial>Westbeemster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2173'">
                <dcterms:spatial>Zuidoostbeemster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2174'">
                <dcterms:spatial>Doezum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2175'">
                <dcterms:spatial>Grootegast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2176'">
                <dcterms:spatial>Kornhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2177'">
                <dcterms:spatial>Lutjegast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2178'">
                <dcterms:spatial>Niekerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2179'">
                <dcterms:spatial>Oldekerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2180'">
                <dcterms:spatial>Opende</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2181'">
                <dcterms:spatial>Sebaldeburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2182'">
                <dcterms:spatial>Terband</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2183'">
                <dcterms:spatial>Luinjeberd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2184'">
                <dcterms:spatial>Tjalleberd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2185'">
                <dcterms:spatial>Gersloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2186'">
                <dcterms:spatial>Heerenveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2187'">
                <dcterms:spatial>Nieuweschoot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2188'">
                <dcterms:spatial>Oudeschoot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2189'">
                <dcterms:spatial>Oranjewoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2190'">
                <dcterms:spatial>De Knipe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2191'">
                <dcterms:spatial>Mildam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2192'">
                <dcterms:spatial>Katlijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2193'">
                <dcterms:spatial>Bontebok</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2194'">
                <dcterms:spatial>Nieuwehorne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2195'">
                <dcterms:spatial>Oudehorne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2196'">
                <dcterms:spatial>Jubbega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2197'">
                <dcterms:spatial>Hoornsterzwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2198'">
                <dcterms:spatial>Genemuiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2199'">
                <dcterms:spatial>Hasselt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2200'">
                <dcterms:spatial>Mastenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2201'">
                <dcterms:spatial>Zwartsluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2202'">
                <dcterms:spatial>Azewijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2203'">
                <dcterms:spatial>Beek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2204'">
                <dcterms:spatial>Braamt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2205'">
                <dcterms:spatial>Didam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2206'">
                <dcterms:spatial>'s-Heerenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2207'">
                <dcterms:spatial>Kilder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2208'">
                <dcterms:spatial>Lengel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2209'">
                <dcterms:spatial>Loerbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2210'">
                <dcterms:spatial>Stokkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2211'">
                <dcterms:spatial>Vethuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2212'">
                <dcterms:spatial>Wijnbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2213'">
                <dcterms:spatial>Zeddam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2214'">
                <dcterms:spatial>Dussen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2215'">
                <dcterms:spatial>Hank</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2216'">
                <dcterms:spatial>Nieuwendijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2217'">
                <dcterms:spatial>Sleeuwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2218'">
                <dcterms:spatial>Werkendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2219'">
                <dcterms:spatial>Loenen aan de Vecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2220'">
                <dcterms:spatial>Loenersloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2221'">
                <dcterms:spatial>Nieuwersluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2222'">
                <dcterms:spatial>Nigtevecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2223'">
                <dcterms:spatial>Vreeland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2224'">
                <dcterms:spatial>Groenlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2225'">
                <dcterms:spatial>Lievelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2226'">
                <dcterms:spatial>Vragender</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2227'">
                <dcterms:spatial>Lichtenvoorde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2228'">
                <dcterms:spatial>Harreveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2229'">
                <dcterms:spatial>Zieuwent</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2230'">
                <dcterms:spatial>Mariënvelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2231'">
                <dcterms:spatial>Bellingwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2232'">
                <dcterms:spatial>Blijham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2233'">
                <dcterms:spatial>Oudeschans</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2234'">
                <dcterms:spatial>Veelerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2235'">
                <dcterms:spatial>Vriescheloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2236'">
                <dcterms:spatial>Wedde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2237'">
                <dcterms:spatial>Bunschoten-Spakenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2238'">
                <dcterms:spatial>Eemdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2239'">
                <dcterms:spatial>Bantega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2240'">
                <dcterms:spatial>Delfstrahuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2241'">
                <dcterms:spatial>Echten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2242'">
                <dcterms:spatial>Echtenerbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2243'">
                <dcterms:spatial>Eesterga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2244'">
                <dcterms:spatial>Follega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2245'">
                <dcterms:spatial>Lemmer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2246'">
                <dcterms:spatial>Oosterzee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2247'">
                <dcterms:spatial>Apeldoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2248'">
                <dcterms:spatial>Klarenbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2249'">
                <dcterms:spatial>Hoog Soeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2250'">
                <dcterms:spatial>Lieren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2251'">
                <dcterms:spatial>Loenen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2252'">
                <dcterms:spatial>Radio Kootwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2253'">
                <dcterms:spatial>Wenum Wiesel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2254'">
                <dcterms:spatial>Beemte Broekland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2255'">
                <dcterms:spatial>Uddel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2256'">
                <dcterms:spatial>Ugchelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2257'">
                <dcterms:spatial>Hoenderloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2258'">
                <dcterms:spatial>Beekbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2259'">
                <dcterms:spatial>Boerakker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2260'">
                <dcterms:spatial>De Wilp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2261'">
                <dcterms:spatial>Jonkersvaart</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2262'">
                <dcterms:spatial>Lucaswolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2263'">
                <dcterms:spatial>Marum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2264'">
                <dcterms:spatial>Niebert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2265'">
                <dcterms:spatial>Noordwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2266'">
                <dcterms:spatial>Nuis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2267'">
                <dcterms:spatial>Akersloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2268'">
                <dcterms:spatial>Castricum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2269'">
                <dcterms:spatial>de Woude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2270'">
                <dcterms:spatial>Limmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2271'">
                <dcterms:spatial>Leerdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2272'">
                <dcterms:spatial>Schoonrewoerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2273'">
                <dcterms:spatial>Kedichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2274'">
                <dcterms:spatial>Oosterwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2275'">
                <dcterms:spatial>Borgercompagnie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2276'">
                <dcterms:spatial>Tripscompagnie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2277'">
                <dcterms:spatial>Meeden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2278'">
                <dcterms:spatial>Muntendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2279'">
                <dcterms:spatial>Noordbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2280'">
                <dcterms:spatial>Zuidbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2281'">
                <dcterms:spatial>Roermond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2282'">
                <dcterms:spatial>Herten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2283'">
                <dcterms:spatial>Swalmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2284'">
                <dcterms:spatial>Jisp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2285'">
                <dcterms:spatial>Oostknollendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2286'">
                <dcterms:spatial>Spijkerboor</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2287'">
                <dcterms:spatial>Wormer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2288'">
                <dcterms:spatial>Wijdewormer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2289'">
                <dcterms:spatial>Millingen aan de Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2290'">
                <dcterms:spatial>Berg en Dal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2291'">
                <dcterms:spatial>Groesbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2292'">
                <dcterms:spatial>Heilig Landstichting</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2293'">
                <dcterms:spatial>Zoetermeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2294'">
                <dcterms:spatial>Bourtange</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2295'">
                <dcterms:spatial>Sellingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2296'">
                <dcterms:spatial>Ter Apel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2297'">
                <dcterms:spatial>Ter Apelkanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2298'">
                <dcterms:spatial>Vlagtwedde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2299'">
                <dcterms:spatial>Gorinchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2300'">
                <dcterms:spatial>Dalem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2301'">
                <dcterms:spatial>Allingawier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2302'">
                <dcterms:spatial>Arum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2303'">
                <dcterms:spatial>Breezanddijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2304'">
                <dcterms:spatial>Burgwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2305'">
                <dcterms:spatial>Cornwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2306'">
                <dcterms:spatial>Dedgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2307'">
                <dcterms:spatial>Exmorra</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2308'">
                <dcterms:spatial>Ferwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2309'">
                <dcterms:spatial>Gaast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2310'">
                <dcterms:spatial>Hartwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2311'">
                <dcterms:spatial>Hichtum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2312'">
                <dcterms:spatial>Hieslum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2313'">
                <dcterms:spatial>Idsegahuizum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2314'">
                <dcterms:spatial>Kimswerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2315'">
                <dcterms:spatial>Kornwerderzand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2316'">
                <dcterms:spatial>Lollum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2317'">
                <dcterms:spatial>Longerhouw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2318'">
                <dcterms:spatial>Makkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2319'">
                <dcterms:spatial>Parrega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2320'">
                <dcterms:spatial>Piaam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2321'">
                <dcterms:spatial>Pingjum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2322'">
                <dcterms:spatial>Schettens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2323'">
                <dcterms:spatial>Schraard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2324'">
                <dcterms:spatial>Tjerkwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2325'">
                <dcterms:spatial>Witmarsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2326'">
                <dcterms:spatial>Wons</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2327'">
                <dcterms:spatial>Zurich</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2328'">
                <dcterms:spatial>Castenray</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2329'">
                <dcterms:spatial>Heide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2330'">
                <dcterms:spatial>Leunen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2331'">
                <dcterms:spatial>Merselo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2332'">
                <dcterms:spatial>Oirlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2333'">
                <dcterms:spatial>Oostrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2334'">
                <dcterms:spatial>Smakt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2335'">
                <dcterms:spatial>Venray</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2336'">
                <dcterms:spatial>Veulen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2337'">
                <dcterms:spatial>Vredepeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2338'">
                <dcterms:spatial>Ysselsteyn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2339'">
                <dcterms:spatial>Boerakker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2340'">
                <dcterms:spatial>Enumatil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2341'">
                <dcterms:spatial>Leek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2342'">
                <dcterms:spatial>Lettelbert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2343'">
                <dcterms:spatial>Midwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2344'">
                <dcterms:spatial>Oostwold</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2345'">
                <dcterms:spatial>Tolbert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2346'">
                <dcterms:spatial>Zevenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2347'">
                <dcterms:spatial>Haaksbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2348'">
                <dcterms:spatial>Alkmaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2349'">
                <dcterms:spatial>Koedijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2350'">
                <dcterms:spatial>Oudorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2351'">
                <dcterms:spatial>Dordrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2352'">
                <dcterms:spatial>Hendrik-Ido-Ambacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2353'">
                <dcterms:spatial>Asperen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2354'">
                <dcterms:spatial>Herwijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2355'">
                <dcterms:spatial>Heukelum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2356'">
                <dcterms:spatial>Spijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2357'">
                <dcterms:spatial>Vuren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2358'">
                <dcterms:spatial>Nuth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2359'">
                <dcterms:spatial>Hulsberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2360'">
                <dcterms:spatial>Schimmert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2361'">
                <dcterms:spatial>Wijnandsrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2362'">
                <dcterms:spatial>Rhoon</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2363'">
                <dcterms:spatial>Poortugaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2364'">
                <dcterms:spatial>Rotterdam-Albrandswaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2365'">
                <dcterms:spatial>Bergambacht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2366'">
                <dcterms:spatial>Berkenwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2367'">
                <dcterms:spatial>Ammerstol</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2368'">
                <dcterms:spatial>Brielle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2369'">
                <dcterms:spatial>Vierpolders</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2370'">
                <dcterms:spatial>Zwartewaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2371'">
                <dcterms:spatial>Klaaswaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2372'">
                <dcterms:spatial>Numansdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2373'">
                <dcterms:spatial>Ouderkerk aan den IJssel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2374'">
                <dcterms:spatial>Gouderak</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2375'">
                <dcterms:spatial>Krimpen aan de Lek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2376'">
                <dcterms:spatial>Lekkerkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2377'">
                <dcterms:spatial>Dirksland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2378'">
                <dcterms:spatial>Herkingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2379'">
                <dcterms:spatial>Melissant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2380'">
                <dcterms:spatial>'s-Gravendeel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2381'">
                <dcterms:spatial>Heinenoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2382'">
                <dcterms:spatial>Maasdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2383'">
                <dcterms:spatial>Mijnsheerenland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2384'">
                <dcterms:spatial>Puttershoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2385'">
                <dcterms:spatial>Westmaas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2386'">
                <dcterms:spatial>Alblasserdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2387'">
                <dcterms:spatial>Haastrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2388'">
                <dcterms:spatial>Stolwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2389'">
                <dcterms:spatial>Vlist</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2390'">
                <dcterms:spatial>Schoonhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2391'">
                <dcterms:spatial>Assen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2392'">
                <dcterms:spatial>Loon</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2393'">
                <dcterms:spatial>Rhee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2394'">
                <dcterms:spatial>Ter Aard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2395'">
                <dcterms:spatial>Ubbena</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2396'">
                <dcterms:spatial>Zeijerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2397'">
                <dcterms:spatial>Zeijerveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2398'">
                <dcterms:spatial>Heerjansdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2399'">
                <dcterms:spatial>Zwijndrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2400'">
                <dcterms:spatial>Abbega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2401'">
                <dcterms:spatial>Blauwhuis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2402'">
                <dcterms:spatial>Folsgare</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2403'">
                <dcterms:spatial>Gaastmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2404'">
                <dcterms:spatial>Gauw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2405'">
                <dcterms:spatial>Goënga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2406'">
                <dcterms:spatial>Greonterp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2407'">
                <dcterms:spatial>Heeg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2408'">
                <dcterms:spatial>Hommerts</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2409'">
                <dcterms:spatial>Idzega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2410'">
                <dcterms:spatial>IJlst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2411'">
                <dcterms:spatial>Indijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2412'">
                <dcterms:spatial>Jutrijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2413'">
                <dcterms:spatial>Koufurderrige</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2414'">
                <dcterms:spatial>Nijland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2415'">
                <dcterms:spatial>Oosthem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2416'">
                <dcterms:spatial>Oppenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2417'">
                <dcterms:spatial>Oudega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2418'">
                <dcterms:spatial>Sandfirden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2419'">
                <dcterms:spatial>Scharnegoutum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2420'">
                <dcterms:spatial>Smallebrugge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2421'">
                <dcterms:spatial>Tirns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2422'">
                <dcterms:spatial>Tjalhuizum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2423'">
                <dcterms:spatial>Uitwellingerga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2424'">
                <dcterms:spatial>Westhem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2425'">
                <dcterms:spatial>Wolsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2426'">
                <dcterms:spatial>Woudsend</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2427'">
                <dcterms:spatial>Ypecolsga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2428'">
                <dcterms:spatial>Schiermonnikoog</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2429'">
                <dcterms:spatial>Biddinghuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2430'">
                <dcterms:spatial>Dronten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2431'">
                <dcterms:spatial>Swifterbant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2432'">
                <dcterms:spatial>IJsselstein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2433'">
                <dcterms:spatial>Kreileroord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2434'">
                <dcterms:spatial>Middenmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2435'">
                <dcterms:spatial>Slootdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2436'">
                <dcterms:spatial>Wieringerwerf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2437'">
                <dcterms:spatial>Rozenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2438'">
                <dcterms:spatial>Sliedrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2439'">
                <dcterms:spatial>Boxtel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2440'">
                <dcterms:spatial>Liempde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2441'">
                <dcterms:spatial>Aldtsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2442'">
                <dcterms:spatial>Wyns</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2443'">
                <dcterms:spatial>Oentsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2444'">
                <dcterms:spatial>Gytsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2445'">
                <dcterms:spatial>Ryptsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2446'">
                <dcterms:spatial>Mûnein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2447'">
                <dcterms:spatial>Tytsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2448'">
                <dcterms:spatial>Suwâld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2449'">
                <dcterms:spatial>Hurdegaryp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2450'">
                <dcterms:spatial>Noardburgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2451'">
                <dcterms:spatial>Burgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2452'">
                <dcterms:spatial>Jistrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2453'">
                <dcterms:spatial>Sumar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2454'">
                <dcterms:spatial>Eastermar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2455'">
                <dcterms:spatial>Garyp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2456'">
                <dcterms:spatial>Earnewâld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2457'">
                <dcterms:spatial>Smilde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2458'">
                <dcterms:spatial>Heemskerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2459'">
                <dcterms:spatial>Rijswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2460'">
                <dcterms:spatial>Schagen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2461'">
                <dcterms:spatial>Hoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2462'">
                <dcterms:spatial>Blokker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2463'">
                <dcterms:spatial>Zwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2464'">
                <dcterms:spatial>Adorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2465'">
                <dcterms:spatial>Baflo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2466'">
                <dcterms:spatial>Den Andel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2467'">
                <dcterms:spatial>Ezinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2468'">
                <dcterms:spatial>Feerwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2469'">
                <dcterms:spatial>Garnwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2470'">
                <dcterms:spatial>Rasquert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2471'">
                <dcterms:spatial>Saaxumhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2472'">
                <dcterms:spatial>Sauwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2473'">
                <dcterms:spatial>Tinallinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2474'">
                <dcterms:spatial>Wetsinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2475'">
                <dcterms:spatial>Winsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2476'">
                <dcterms:spatial>Reeuwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2477'">
                <dcterms:spatial>Driebruggen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2478'">
                <dcterms:spatial>Waarder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2479'">
                <dcterms:spatial>Milsbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2480'">
                <dcterms:spatial>Ottersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2481'">
                <dcterms:spatial>Ven-Zelderheide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2482'">
                <dcterms:spatial>Gennep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2483'">
                <dcterms:spatial>Heijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2484'">
                <dcterms:spatial>Akmarijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2485'">
                <dcterms:spatial>Boornzwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2486'">
                <dcterms:spatial>Broek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2487'">
                <dcterms:spatial>Dijken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2488'">
                <dcterms:spatial>Doniaga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2489'">
                <dcterms:spatial>Goingarijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2490'">
                <dcterms:spatial>Haskerdijken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2491'">
                <dcterms:spatial>Haskerhorne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2492'">
                <dcterms:spatial>Idskenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2493'">
                <dcterms:spatial>Joure</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2494'">
                <dcterms:spatial>Langweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2495'">
                <dcterms:spatial>Legemeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2496'">
                <dcterms:spatial>Nieuwebrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2497'">
                <dcterms:spatial>Nijehaske</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2498'">
                <dcterms:spatial>Oldeouwer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2499'">
                <dcterms:spatial>Oudehaske</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2500'">
                <dcterms:spatial>Ouwsterhaule</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2501'">
                <dcterms:spatial>Ouwster-Nijega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2502'">
                <dcterms:spatial>Rohel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2503'">
                <dcterms:spatial>Rotstergaast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2504'">
                <dcterms:spatial>Rotsterhaule</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2505'">
                <dcterms:spatial>Rottum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2506'">
                <dcterms:spatial>Scharsterbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2507'">
                <dcterms:spatial>Sint Nicolaasga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2508'">
                <dcterms:spatial>Sintjohannesga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2509'">
                <dcterms:spatial>Snikzwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2510'">
                <dcterms:spatial>Terkaple</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2511'">
                <dcterms:spatial>Teroele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2512'">
                <dcterms:spatial>Tjerkgaast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2513'">
                <dcterms:spatial>Vegelinsoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2514'">
                <dcterms:spatial>Barsingerhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2515'">
                <dcterms:spatial>Haringhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2516'">
                <dcterms:spatial>Kolhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2517'">
                <dcterms:spatial>Lutjewinkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2518'">
                <dcterms:spatial>Nieuwe Niedorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2519'">
                <dcterms:spatial>Oude Niedorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2520'">
                <dcterms:spatial>'t Veld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2521'">
                <dcterms:spatial>Winkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2522'">
                <dcterms:spatial>Zijdewind</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2523'">
                <dcterms:spatial>Woudenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2524'">
                <dcterms:spatial>Eagum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2525'">
                <dcterms:spatial>Akkrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2526'">
                <dcterms:spatial>Dearsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2527'">
                <dcterms:spatial>Friens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2528'">
                <dcterms:spatial>Grou</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2529'">
                <dcterms:spatial>Idaerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2530'">
                <dcterms:spatial>Jirnsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2531'">
                <dcterms:spatial>Nes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2532'">
                <dcterms:spatial>Aldeboarn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2533'">
                <dcterms:spatial>Poppenwier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2534'">
                <dcterms:spatial>Raerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2535'">
                <dcterms:spatial>Reduzum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2536'">
                <dcterms:spatial>Sibrandabuorren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2537'">
                <dcterms:spatial>Terherne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2538'">
                <dcterms:spatial>Tersoal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2539'">
                <dcterms:spatial>Wergea</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2540'">
                <dcterms:spatial>Warstiens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2541'">
                <dcterms:spatial>Warten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2542'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2543'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2544'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2545'">
                <dcterms:spatial>Lent</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2546'">
                <dcterms:spatial>Ommen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2547'">
                <dcterms:spatial>Arriën</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2548'">
                <dcterms:spatial>Beerze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2549'">
                <dcterms:spatial>Beerzerveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2550'">
                <dcterms:spatial>Dalmsholte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2551'">
                <dcterms:spatial>Giethmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2552'">
                <dcterms:spatial>Lemele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2553'">
                <dcterms:spatial>Stegeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2554'">
                <dcterms:spatial>Vilsteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2555'">
                <dcterms:spatial>Vinkenbuurt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2556'">
                <dcterms:spatial>Witharen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2557'">
                <dcterms:spatial>Tiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2558'">
                <dcterms:spatial>Zennewijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2559'">
                <dcterms:spatial>Wadenoijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2560'">
                <dcterms:spatial>Kapel Avezaath</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2561'">
                <dcterms:spatial>Kerk Avezaath</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2562'">
                <dcterms:spatial>Ridderkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2563'">
                <dcterms:spatial>Heemstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2564'">
                <dcterms:spatial>Wijchen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2565'">
                <dcterms:spatial>Bergharen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2566'">
                <dcterms:spatial>Hernen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2567'">
                <dcterms:spatial>Leur</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2568'">
                <dcterms:spatial>Batenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2569'">
                <dcterms:spatial>Niftrik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2570'">
                <dcterms:spatial>Balgoij</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2571'">
                <dcterms:spatial>Aalsmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2572'">
                <dcterms:spatial>Kudelstaart</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2573'">
                <dcterms:spatial>Ermelo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2574'">
                <dcterms:spatial>Drimmelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2575'">
                <dcterms:spatial>Hooge Zwaluwe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2576'">
                <dcterms:spatial>Lage Zwaluwe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2577'">
                <dcterms:spatial>Made</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2578'">
                <dcterms:spatial>Terheijden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2579'">
                <dcterms:spatial>Wagenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2580'">
                <dcterms:spatial>Zevenbergschen Hoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2581'">
                <dcterms:spatial>Rucphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2582'">
                <dcterms:spatial>Schijf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2583'">
                <dcterms:spatial>Sprundel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2584'">
                <dcterms:spatial>St. Willebrord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2585'">
                <dcterms:spatial>Zegge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2586'">
                <dcterms:spatial>Hollum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2587'">
                <dcterms:spatial>Ballum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2588'">
                <dcterms:spatial>Nes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2589'">
                <dcterms:spatial>Buren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2590'">
                <dcterms:spatial>Herkenbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2591'">
                <dcterms:spatial>Melick</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2592'">
                <dcterms:spatial>Montfort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2593'">
                <dcterms:spatial>Posterholt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2594'">
                <dcterms:spatial>Sint Odiliënberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2595'">
                <dcterms:spatial>Vlodrop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2596'">
                <dcterms:spatial>Sprang-Capelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2597'">
                <dcterms:spatial>Waalwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2598'">
                <dcterms:spatial>Waspik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2599'">
                <dcterms:spatial>Son</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2600'">
                <dcterms:spatial>Breugel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2601'">
                <dcterms:spatial>Maasbree</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2602'">
                <dcterms:spatial>Baarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2603'">
                <dcterms:spatial>Bunnik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2604'">
                <dcterms:spatial>Odijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2605'">
                <dcterms:spatial>Werkhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2606'">
                <dcterms:spatial>Klarenbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2607'">
                <dcterms:spatial>Nijbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2608'">
                <dcterms:spatial>Steenenkamer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2609'">
                <dcterms:spatial>Terwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2610'">
                <dcterms:spatial>Teuge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2611'">
                <dcterms:spatial>Twello</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2612'">
                <dcterms:spatial>Voorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2613'">
                <dcterms:spatial>Wilp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2614'">
                <dcterms:spatial>Oosterend</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2615'">
                <dcterms:spatial>Hoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2616'">
                <dcterms:spatial>Lies</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2617'">
                <dcterms:spatial>Formerum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2618'">
                <dcterms:spatial>Landerum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2619'">
                <dcterms:spatial>Midsland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2620'">
                <dcterms:spatial>Striep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2621'">
                <dcterms:spatial>Baaiduinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2622'">
                <dcterms:spatial>Kaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2623'">
                <dcterms:spatial>Hee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2624'">
                <dcterms:spatial>Kinnum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2625'">
                <dcterms:spatial>West-Terschelling</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2626'">
                <dcterms:spatial>Aalst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2627'">
                <dcterms:spatial>Bern</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2628'">
                <dcterms:spatial>Brakel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2629'">
                <dcterms:spatial>Bruchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2630'">
                <dcterms:spatial>Delwijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2631'">
                <dcterms:spatial>Gameren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2632'">
                <dcterms:spatial>Kerkwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2633'">
                <dcterms:spatial>Nederhemert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2634'">
                <dcterms:spatial>Nieuwaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2635'">
                <dcterms:spatial>Poederoijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2636'">
                <dcterms:spatial>Zaltbommel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2637'">
                <dcterms:spatial>Zuilichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2638'">
                <dcterms:spatial>Leusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2639'">
                <dcterms:spatial>Stoutenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2640'">
                <dcterms:spatial>Achterveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2641'">
                <dcterms:spatial>Haarlemmerliede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2642'">
                <dcterms:spatial>Spaarndam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2643'">
                <dcterms:spatial>Halfweg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2644'">
                <dcterms:spatial>Zoeterwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2645'">
                <dcterms:spatial>Gelderswoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2646'">
                <dcterms:spatial>Oisterwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2647'">
                <dcterms:spatial>Moergestel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2648'">
                <dcterms:spatial>Heukelom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2649'">
                <dcterms:spatial>Kerkrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2650'">
                <dcterms:spatial>Eygelshoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2651'">
                <dcterms:spatial>Vogelenzang</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2652'">
                <dcterms:spatial>Aerdenhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2653'">
                <dcterms:spatial>Overveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2654'">
                <dcterms:spatial>Bloemendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2655'">
                <dcterms:spatial>De Lier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2656'">
                <dcterms:spatial>Honselersdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2657'">
                <dcterms:spatial>Kwintsheul</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2658'">
                <dcterms:spatial>Maasdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2659'">
                <dcterms:spatial>Monster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2660'">
                <dcterms:spatial>Naaldwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2661'">
                <dcterms:spatial>Poeldijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2662'">
                <dcterms:spatial>'s-Gravenzande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2663'">
                <dcterms:spatial>Ter Heijde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2664'">
                <dcterms:spatial>Wateringen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2665'">
                <dcterms:spatial>Bodegraven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2666'">
                <dcterms:spatial>Nieuwerbrug aan den Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2667'">
                <dcterms:spatial>Nieuwkoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2668'">
                <dcterms:spatial>Noorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2669'">
                <dcterms:spatial>Woerdense Verlaat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2670'">
                <dcterms:spatial>Nieuwveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2671'">
                <dcterms:spatial>Zevenhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2672'">
                <dcterms:spatial>Vrouwenakker</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2673'">
                <dcterms:spatial>Ter Aar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2674'">
                <dcterms:spatial>Brouwershaven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2675'">
                <dcterms:spatial>Bruinisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2676'">
                <dcterms:spatial>Burgh-Haamstede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2677'">
                <dcterms:spatial>Dreischor</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2678'">
                <dcterms:spatial>Ellemeet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2679'">
                <dcterms:spatial>Kerkwerve</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2680'">
                <dcterms:spatial>Nieuwerkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2681'">
                <dcterms:spatial>Noordgouwe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2682'">
                <dcterms:spatial>Noordwelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2683'">
                <dcterms:spatial>Oosterland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2684'">
                <dcterms:spatial>Ouwerkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2685'">
                <dcterms:spatial>Renesse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2686'">
                <dcterms:spatial>Scharendijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2687'">
                <dcterms:spatial>Serooskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2688'">
                <dcterms:spatial>Sirjansland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2689'">
                <dcterms:spatial>Zierikzee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2690'">
                <dcterms:spatial>Zonnemaire</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2691'">
                <dcterms:spatial>Baarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2692'">
                <dcterms:spatial>Baars</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2693'">
                <dcterms:spatial>Basse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2694'">
                <dcterms:spatial>Belt-Schutsloot</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2695'">
                <dcterms:spatial>Blankenham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2696'">
                <dcterms:spatial>Blokzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2697'">
                <dcterms:spatial>De Bult</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2698'">
                <dcterms:spatial>De Pol</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2699'">
                <dcterms:spatial>Eesveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2700'">
                <dcterms:spatial>Giethoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2701'">
                <dcterms:spatial>IJsselham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2702'">
                <dcterms:spatial>Kalenberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2703'">
                <dcterms:spatial>Kallenkote</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2704'">
                <dcterms:spatial>Kuinre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2705'">
                <dcterms:spatial>Marijenkampen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2706'">
                <dcterms:spatial>Nederland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2707'">
                <dcterms:spatial>Oldemarkt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2708'">
                <dcterms:spatial>Onna</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2709'">
                <dcterms:spatial>Ossenzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2710'">
                <dcterms:spatial>Paasloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2711'">
                <dcterms:spatial>Scheerwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2712'">
                <dcterms:spatial>Sint Jansklooster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2713'">
                <dcterms:spatial>Steenwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2714'">
                <dcterms:spatial>Steenwijkerwold</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2715'">
                <dcterms:spatial>Tuk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2716'">
                <dcterms:spatial>Vollenhove</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2717'">
                <dcterms:spatial>Wanneperveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2718'">
                <dcterms:spatial>Wetering</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2719'">
                <dcterms:spatial>Willemsoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2720'">
                <dcterms:spatial>Witte Paarden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2721'">
                <dcterms:spatial>Zuidveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2722'">
                <dcterms:spatial>Beetgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2723'">
                <dcterms:spatial>Beetgumermolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2724'">
                <dcterms:spatial>Berlikum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2725'">
                <dcterms:spatial>Blessum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2726'">
                <dcterms:spatial>Boksum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2727'">
                <dcterms:spatial>Deinum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2728'">
                <dcterms:spatial>Engelum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2729'">
                <dcterms:spatial>Dronrijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2730'">
                <dcterms:spatial>Marssum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2731'">
                <dcterms:spatial>Menaldum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2732'">
                <dcterms:spatial>Schingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2733'">
                <dcterms:spatial>Slappeterp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2734'">
                <dcterms:spatial>Wier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2735'">
                <dcterms:spatial>Beverwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2736'">
                <dcterms:spatial>Wijk aan Zee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2737'">
                <dcterms:spatial>De Cocksdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2738'">
                <dcterms:spatial>De Koog</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2739'">
                <dcterms:spatial>De Waal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2740'">
                <dcterms:spatial>Den Burg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2741'">
                <dcterms:spatial>Den Hoorn Texel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2742'">
                <dcterms:spatial>Oosterend Nh</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2743'">
                <dcterms:spatial>Oudeschild</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2744'">
                <dcterms:spatial>Oegstgeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2745'">
                <dcterms:spatial>Someren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2746'">
                <dcterms:spatial>Lierop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2747'">
                <dcterms:spatial>Geffen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2748'">
                <dcterms:spatial>Nuland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2749'">
                <dcterms:spatial>Vinkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2750'">
                <dcterms:spatial>Weurt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2751'">
                <dcterms:spatial>Beuningen Gld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2752'">
                <dcterms:spatial>Ewijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2753'">
                <dcterms:spatial>Winssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2754'">
                <dcterms:spatial>Enkhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2755'">
                <dcterms:spatial>Uitgeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2756'">
                <dcterms:spatial>Schagerbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2757'">
                <dcterms:spatial>Callantsoog</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2758'">
                <dcterms:spatial>'t Zand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2759'">
                <dcterms:spatial>Sint Maartensbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2760'">
                <dcterms:spatial>Sint Maartensvlotbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2761'">
                <dcterms:spatial>Petten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2762'">
                <dcterms:spatial>Burgerbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2763'">
                <dcterms:spatial>Oudesluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2764'">
                <dcterms:spatial>Ede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2765'">
                <dcterms:spatial>Lunteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2766'">
                <dcterms:spatial>Bennekom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2767'">
                <dcterms:spatial>Otterlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2768'">
                <dcterms:spatial>Harskamp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2769'">
                <dcterms:spatial>Ederveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2770'">
                <dcterms:spatial>De Klomp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2771'">
                <dcterms:spatial>Wekerom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2772'">
                <dcterms:spatial>Hoenderloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2773'">
                <dcterms:spatial>Deelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2774'">
                <dcterms:spatial>Aagtekerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2775'">
                <dcterms:spatial>Biggekerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2776'">
                <dcterms:spatial>Domburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2777'">
                <dcterms:spatial>Gapinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2778'">
                <dcterms:spatial>Grijpskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2779'">
                <dcterms:spatial>Koudekerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2780'">
                <dcterms:spatial>Meliskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2781'">
                <dcterms:spatial>Oostkapelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2782'">
                <dcterms:spatial>Serooskerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2783'">
                <dcterms:spatial>Veere</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2784'">
                <dcterms:spatial>Vrouwenpolder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2785'">
                <dcterms:spatial>Westkapelle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2786'">
                <dcterms:spatial>Zoutelande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2787'">
                <dcterms:spatial>Sint-Oedenrode</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2788'">
                <dcterms:spatial>Aartswoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2789'">
                <dcterms:spatial>De Weere</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2790'">
                <dcterms:spatial>Hoogwoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2791'">
                <dcterms:spatial>Opmeer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2792'">
                <dcterms:spatial>Spanbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2793'">
                <dcterms:spatial>Oud-Beijerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2794'">
                <dcterms:spatial>Kinderdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2795'">
                <dcterms:spatial>Nieuw-Lekkerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2796'">
                <dcterms:spatial>Mookhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2797'">
                <dcterms:spatial>Strijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2798'">
                <dcterms:spatial>Strijensas</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2799'">
                <dcterms:spatial>Dongen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2800'">
                <dcterms:spatial>'s Gravenmoer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2801'">
                <dcterms:spatial>Middelaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2802'">
                <dcterms:spatial>Molenhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2803'">
                <dcterms:spatial>Mook</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2804'">
                <dcterms:spatial>Plasmolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2805'">
                <dcterms:spatial>Schipluiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2806'">
                <dcterms:spatial>Maasland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2807'">
                <dcterms:spatial>Den Hoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2808'">
                <dcterms:spatial>Born</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2809'">
                <dcterms:spatial>Buchten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2810'">
                <dcterms:spatial>Einighausen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2811'">
                <dcterms:spatial>Geleen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2812'">
                <dcterms:spatial>Grevenbicht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2813'">
                <dcterms:spatial>Guttecoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2814'">
                <dcterms:spatial>Holtum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2815'">
                <dcterms:spatial>Limbricht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2816'">
                <dcterms:spatial>Munstergeleen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2817'">
                <dcterms:spatial>Obbicht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2818'">
                <dcterms:spatial>Papenhoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2819'">
                <dcterms:spatial>Sittard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2820'">
                <dcterms:spatial>Windraak</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2821'">
                <dcterms:spatial>Zeist</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2822'">
                <dcterms:spatial>Den Dolder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2823'">
                <dcterms:spatial>Bosch en Duin</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2824'">
                <dcterms:spatial>Austerlitz</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2825'">
                <dcterms:spatial>Huis ter Heide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2826'">
                <dcterms:spatial>Erp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2827'">
                <dcterms:spatial>Veghel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2828'">
                <dcterms:spatial>Blaricum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2829'">
                <dcterms:spatial>Bedum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2830'">
                <dcterms:spatial>Noordwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2831'">
                <dcterms:spatial>Onderdendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2832'">
                <dcterms:spatial>Zuidwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2833'">
                <dcterms:spatial>Velsen-Noord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2834'">
                <dcterms:spatial>Velsen-Zuid</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2835'">
                <dcterms:spatial>IJmuiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2836'">
                <dcterms:spatial>Driehuis NH</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2837'">
                <dcterms:spatial>Santpoort-Noord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2838'">
                <dcterms:spatial>Santpoort-Zuid</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2839'">
                <dcterms:spatial>Velserbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2840'">
                <dcterms:spatial>Everdingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2841'">
                <dcterms:spatial>Hagestein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2842'">
                <dcterms:spatial>Ossenwaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2843'">
                <dcterms:spatial>Vianen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2844'">
                <dcterms:spatial>Zijderveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2845'">
                <dcterms:spatial>Bingelrade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2846'">
                <dcterms:spatial>Jabeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2847'">
                <dcterms:spatial>Merkelbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2848'">
                <dcterms:spatial>Schinveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2849'">
                <dcterms:spatial>Brunssum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2850'">
                <dcterms:spatial>Koudekerk aan den Rijn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2851'">
                <dcterms:spatial>Hazerswoude-Rijndijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2852'">
                <dcterms:spatial>Hazerswoude-Dorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2853'">
                <dcterms:spatial>Benthuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2854'">
                <dcterms:spatial>Bathmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2855'">
                <dcterms:spatial>Colmschate</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2856'">
                <dcterms:spatial>Deventer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2857'">
                <dcterms:spatial>Diepenveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2858'">
                <dcterms:spatial>Lettele</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2859'">
                <dcterms:spatial>Okkenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2860'">
                <dcterms:spatial>Schalkhaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2861'">
                <dcterms:spatial>Sneek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2862'">
                <dcterms:spatial>Loënga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2863'">
                <dcterms:spatial>Offingawier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2864'">
                <dcterms:spatial>Ysbrechtum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2865'">
                <dcterms:spatial>Heerde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2866'">
                <dcterms:spatial>Veessen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2867'">
                <dcterms:spatial>Vorchten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2868'">
                <dcterms:spatial>Wapenveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2869'">
                <dcterms:spatial>Nijkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2870'">
                <dcterms:spatial>Nijkerkerveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2871'">
                <dcterms:spatial>Hoevelaken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2872'">
                <dcterms:spatial>Beek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2873'">
                <dcterms:spatial>Berg en Dal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2874'">
                <dcterms:spatial>Erlecom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2875'">
                <dcterms:spatial>Kekerdom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2876'">
                <dcterms:spatial>Leuth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2877'">
                <dcterms:spatial>Ooij</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2878'">
                <dcterms:spatial>Persingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2879'">
                <dcterms:spatial>Ubbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2880'">
                <dcterms:spatial>Goirle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2881'">
                <dcterms:spatial>Riel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2882'">
                <dcterms:spatial>Hattem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2883'">
                <dcterms:spatial>Baarle-Nassau</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2884'">
                <dcterms:spatial>Ulicoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2885'">
                <dcterms:spatial>Castelre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2886'">
                <dcterms:spatial>Goudswaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2887'">
                <dcterms:spatial>Nieuw-Beijerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2888'">
                <dcterms:spatial>Piershil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2889'">
                <dcterms:spatial>Zuid-Beijerland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2890'">
                <dcterms:spatial>Laren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2891'">
                <dcterms:spatial>Harlingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2892'">
                <dcterms:spatial>Midlum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2893'">
                <dcterms:spatial>Wijnaldum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2894'">
                <dcterms:spatial>Kampen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2895'">
                <dcterms:spatial>IJsselmuiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2896'">
                <dcterms:spatial>Grafhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2897'">
                <dcterms:spatial>Zalk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2898'">
                <dcterms:spatial>Wilsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2899'">
                <dcterms:spatial>'s-Heerenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2900'">
                <dcterms:spatial>Kamperveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2901'">
                <dcterms:spatial>Mastenbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2902'">
                <dcterms:spatial>Afferden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2903'">
                <dcterms:spatial>Deest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2904'">
                <dcterms:spatial>Druten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2905'">
                <dcterms:spatial>Horssen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2906'">
                <dcterms:spatial>Puiflijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2907'">
                <dcterms:spatial>Haarlem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2908'">
                <dcterms:spatial>Spaarndam gem. Haarlem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2909'">
                <dcterms:spatial>Eelde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2910'">
                <dcterms:spatial>Paterswolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2911'">
                <dcterms:spatial>Eelderwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2912'">
                <dcterms:spatial>Vries</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2913'">
                <dcterms:spatial>Zuidlaren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2914'">
                <dcterms:spatial>Bunne</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2915'">
                <dcterms:spatial>De Punt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2916'">
                <dcterms:spatial>Donderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2917'">
                <dcterms:spatial>Oudemolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2918'">
                <dcterms:spatial>Taarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2919'">
                <dcterms:spatial>Tynaarlo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2920'">
                <dcterms:spatial>Winde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2921'">
                <dcterms:spatial>Yde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2922'">
                <dcterms:spatial>Zeegse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2923'">
                <dcterms:spatial>Zeijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2924'">
                <dcterms:spatial>De Groeve</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2925'">
                <dcterms:spatial>Zuidlaarderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2926'">
                <dcterms:spatial>Midlaren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2927'">
                <dcterms:spatial>Rozendaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2928'">
                <dcterms:spatial>Asten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2929'">
                <dcterms:spatial>Heusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2930'">
                <dcterms:spatial>Ommel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2931'">
                <dcterms:spatial>Goedereede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2932'">
                <dcterms:spatial>Ouddorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2933'">
                <dcterms:spatial>Stellendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2934'">
                <dcterms:spatial>De Bilt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2935'">
                <dcterms:spatial>Bilthoven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2936'">
                <dcterms:spatial>Maartensdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2937'">
                <dcterms:spatial>Groenekan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2938'">
                <dcterms:spatial>Hollandsche Rading</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2939'">
                <dcterms:spatial>Westbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2940'">
                <dcterms:spatial>Baexem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2941'">
                <dcterms:spatial>Buggenum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2942'">
                <dcterms:spatial>Ell</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2943'">
                <dcterms:spatial>Grathem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2944'">
                <dcterms:spatial>Haelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2945'">
                <dcterms:spatial>Haler</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2946'">
                <dcterms:spatial>Heibloem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2947'">
                <dcterms:spatial>Heythuysen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2948'">
                <dcterms:spatial>Horn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2949'">
                <dcterms:spatial>Hunsel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2950'">
                <dcterms:spatial>Ittervoort</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2951'">
                <dcterms:spatial>Kelpen-Oler</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2952'">
                <dcterms:spatial>Neer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2953'">
                <dcterms:spatial>Neeritter</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2954'">
                <dcterms:spatial>Nunhem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2955'">
                <dcterms:spatial>Roggel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2956'">
                <dcterms:spatial>Oosterhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2957'">
                <dcterms:spatial>Oosteind</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2958'">
                <dcterms:spatial>Den Hout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2959'">
                <dcterms:spatial>Dorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2960'">
                <dcterms:spatial>Oostburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2961'">
                <dcterms:spatial>Aardenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2962'">
                <dcterms:spatial>Sluis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2963'">
                <dcterms:spatial>Waterlandkerkje</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2964'">
                <dcterms:spatial>Cadzand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2965'">
                <dcterms:spatial>Breskens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2966'">
                <dcterms:spatial>Eede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2967'">
                <dcterms:spatial>Groede</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2968'">
                <dcterms:spatial>Hoofdplaat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2969'">
                <dcterms:spatial>Nieuwvliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2970'">
                <dcterms:spatial>Retranchement</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2971'">
                <dcterms:spatial>Schoondijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2972'">
                <dcterms:spatial>Zuidzande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2973'">
                <dcterms:spatial>Sint Kruis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2974'">
                <dcterms:spatial>IJzendijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2975'">
                <dcterms:spatial>Biervliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2976'">
                <dcterms:spatial>Aerdt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2977'">
                <dcterms:spatial>Herwen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2978'">
                <dcterms:spatial>Lobith</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2979'">
                <dcterms:spatial>Pannerden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2980'">
                <dcterms:spatial>Spijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2981'">
                <dcterms:spatial>Tolkamer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2982'">
                <dcterms:spatial>Nunspeet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2983'">
                <dcterms:spatial>Elspeet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2984'">
                <dcterms:spatial>Hulshorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2985'">
                <dcterms:spatial>Vierhouten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2986'">
                <dcterms:spatial>Doorwerth</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2987'">
                <dcterms:spatial>Heelsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2988'">
                <dcterms:spatial>Heveadorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2989'">
                <dcterms:spatial>Wolfheze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2990'">
                <dcterms:spatial>Oosterbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2991'">
                <dcterms:spatial>Renkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2992'">
                <dcterms:spatial>Duizel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2993'">
                <dcterms:spatial>Eersel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2994'">
                <dcterms:spatial>Knegsel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2995'">
                <dcterms:spatial>Steensel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2996'">
                <dcterms:spatial>Vessem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2997'">
                <dcterms:spatial>Wintelre</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2998'">
                <dcterms:spatial>Boskoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='2999'">
                <dcterms:spatial>Ankeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3000'">
                <dcterms:spatial>'s-Graveland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3001'">
                <dcterms:spatial>Kortenhoef</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3002'">
                <dcterms:spatial>Loosdrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3003'">
                <dcterms:spatial>Nederhorst den Berg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3004'">
                <dcterms:spatial>Breukeleveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3005'">
                <dcterms:spatial>Balinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3006'">
                <dcterms:spatial>Beilen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3007'">
                <dcterms:spatial>Bovensmilde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3008'">
                <dcterms:spatial>Bruntinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3009'">
                <dcterms:spatial>Drijber</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3010'">
                <dcterms:spatial>Elp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3011'">
                <dcterms:spatial>Eursinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3012'">
                <dcterms:spatial>Garminge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3013'">
                <dcterms:spatial>Hijken</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3014'">
                <dcterms:spatial>Hoogersmilde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3015'">
                <dcterms:spatial>Hooghalen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3016'">
                <dcterms:spatial>Mantinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3017'">
                <dcterms:spatial>Nieuweroord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3018'">
                <dcterms:spatial>Nieuw-Balinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3019'">
                <dcterms:spatial>Oranje</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3020'">
                <dcterms:spatial>Orvelte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3021'">
                <dcterms:spatial>Smilde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3022'">
                <dcterms:spatial>Spier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3023'">
                <dcterms:spatial>Stuifzand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3024'">
                <dcterms:spatial>Tiendeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3025'">
                <dcterms:spatial>Westerbork</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3026'">
                <dcterms:spatial>Wijster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3027'">
                <dcterms:spatial>Witteveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3028'">
                <dcterms:spatial>Zuidveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3029'">
                <dcterms:spatial>Zwiggelte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3030'">
                <dcterms:spatial>Nijmegen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3031'">
                <dcterms:spatial>Hardinxveld-Giessendam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3032'">
                <dcterms:spatial>Spijkenisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3033'">
                <dcterms:spatial>Hekelingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3034'">
                <dcterms:spatial>Hoogmade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3035'">
                <dcterms:spatial>Kaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3036'">
                <dcterms:spatial>Leimuiden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3037'">
                <dcterms:spatial>Nieuwe Wetering</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3038'">
                <dcterms:spatial>Oud Ade</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3039'">
                <dcterms:spatial>Oude Wetering</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3040'">
                <dcterms:spatial>Rijnsaterwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3041'">
                <dcterms:spatial>Rijpwetering</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3042'">
                <dcterms:spatial>Roelofarendsveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3043'">
                <dcterms:spatial>Woubrugge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3044'">
                <dcterms:spatial>Groet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3045'">
                <dcterms:spatial>Schoorl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3046'">
                <dcterms:spatial>Bergen aan Zee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3047'">
                <dcterms:spatial>Bergen (NH)</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3048'">
                <dcterms:spatial>Egmond aan den Hoef</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3049'">
                <dcterms:spatial>Egmond aan Zee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3050'">
                <dcterms:spatial>Egmond-Binnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3051'">
                <dcterms:spatial>Waddinxveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3052'">
                <dcterms:spatial>Kaatsheuvel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3053'">
                <dcterms:spatial>Loon op Zand </dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3054'">
                <dcterms:spatial>De Moer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3055'">
                <dcterms:spatial>Landgraaf</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3056'">
                <dcterms:spatial>Den Helder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3057'">
                <dcterms:spatial>Huisduinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3058'">
                <dcterms:spatial>Julianadorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3059'">
                <dcterms:spatial>Heesch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3060'">
                <dcterms:spatial>Heeswijk-Dinther</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3061'">
                <dcterms:spatial>Loosbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3062'">
                <dcterms:spatial>Nistelrode</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3063'">
                <dcterms:spatial>Vinkel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3064'">
                <dcterms:spatial>Vorstenbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3065'">
                <dcterms:spatial>Simpelveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3066'">
                <dcterms:spatial>Bocholtz</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3067'">
                <dcterms:spatial>Baneheide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3068'">
                <dcterms:spatial>’t Loo Oldebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3069'">
                <dcterms:spatial>Hattemerbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3070'">
                <dcterms:spatial>Noordeinde Gld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3071'">
                <dcterms:spatial>Oldebroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3072'">
                <dcterms:spatial>Oosterwolde Gld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3073'">
                <dcterms:spatial>Wezep</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3074'">
                <dcterms:spatial>Stadskanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3075'">
                <dcterms:spatial>Musselkanaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3076'">
                <dcterms:spatial>Onstwedde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3077'">
                <dcterms:spatial>Mussel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3078'">
                <dcterms:spatial>Vledderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3079'">
                <dcterms:spatial>Alteveer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3080'">
                <dcterms:spatial>Botlek Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3081'">
                <dcterms:spatial>Europoort Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3082'">
                <dcterms:spatial>Hoek van Holland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3083'">
                <dcterms:spatial>Hoogvliet Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3084'">
                <dcterms:spatial>Maasvlakte Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3085'">
                <dcterms:spatial>Pernis Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3086'">
                <dcterms:spatial>Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3087'">
                <dcterms:spatial>Vondelingenplaat Rotterdam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3088'">
                <dcterms:spatial>Hulst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3089'">
                <dcterms:spatial>Sint Jansteen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3090'">
                <dcterms:spatial>Kapellebrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3091'">
                <dcterms:spatial>Heikant</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3092'">
                <dcterms:spatial>Clinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3093'">
                <dcterms:spatial>Nieuw Namen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3094'">
                <dcterms:spatial>Graauw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3095'">
                <dcterms:spatial>Vogelwaarde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3096'">
                <dcterms:spatial>Terhole</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3097'">
                <dcterms:spatial>Kuitaart</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3098'">
                <dcterms:spatial>Hengstdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3099'">
                <dcterms:spatial>Lamswaarde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3100'">
                <dcterms:spatial>Kloosterzande</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3101'">
                <dcterms:spatial>Walsoorden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3102'">
                <dcterms:spatial>Ossenisse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3103'">
                <dcterms:spatial>Purmerend</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3104'">
                <dcterms:spatial>Ansen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3105'">
                <dcterms:spatial>Boschoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3106'">
                <dcterms:spatial>Darp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3107'">
                <dcterms:spatial>Diever</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3108'">
                <dcterms:spatial>Dieverbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3109'">
                <dcterms:spatial>Doldersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3110'">
                <dcterms:spatial>Dwingeloo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3111'">
                <dcterms:spatial>Frederiksoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3112'">
                <dcterms:spatial>Geeuwenbrug</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3113'">
                <dcterms:spatial>Havelte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3114'">
                <dcterms:spatial>Havelterberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3115'">
                <dcterms:spatial>Hoogersmilde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3116'">
                <dcterms:spatial>Nijensleek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3117'">
                <dcterms:spatial>Oude Willem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3118'">
                <dcterms:spatial>Pesse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3119'">
                <dcterms:spatial>Ruinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3120'">
                <dcterms:spatial>Spier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3121'">
                <dcterms:spatial>Uffelte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3122'">
                <dcterms:spatial>Vledder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3123'">
                <dcterms:spatial>Vledderveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3124'">
                <dcterms:spatial>Wapse</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3125'">
                <dcterms:spatial>Wapserveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3126'">
                <dcterms:spatial>Wateren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3127'">
                <dcterms:spatial>Wilhelminaoord</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3128'">
                <dcterms:spatial>Wittelte</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3129'">
                <dcterms:spatial>Zorgvlied</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3130'">
                <dcterms:spatial>Huissen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3131'">
                <dcterms:spatial>Bemmel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3132'">
                <dcterms:spatial>Gendt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3133'">
                <dcterms:spatial>Angeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3134'">
                <dcterms:spatial>Doornenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3135'">
                <dcterms:spatial>Loo Gld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3136'">
                <dcterms:spatial>Haalderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3137'">
                <dcterms:spatial>Ressen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3138'">
                <dcterms:spatial>Boekel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3139'">
                <dcterms:spatial>Venhorst</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3140'">
                <dcterms:spatial>Arcen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3141'">
                <dcterms:spatial>Lomm</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3142'">
                <dcterms:spatial>Velden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3143'">
                <dcterms:spatial>Middelharnis</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3144'">
                <dcterms:spatial>Nieuwe-Tonge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3145'">
                <dcterms:spatial>Sommelsdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3146'">
                <dcterms:spatial>Stad aan 't Haringvliet</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3147'">
                <dcterms:spatial>Abbekerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3148'">
                <dcterms:spatial>Benningbroek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3149'">
                <dcterms:spatial>Hauwert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3150'">
                <dcterms:spatial>Lambertschaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3151'">
                <dcterms:spatial>Medemblik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3152'">
                <dcterms:spatial>Midwoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3153'">
                <dcterms:spatial>Nibbixwoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3154'">
                <dcterms:spatial>Oostwoud</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3155'">
                <dcterms:spatial>Opperdoes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3156'">
                <dcterms:spatial>Sijbekarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3157'">
                <dcterms:spatial>Twisk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3158'">
                <dcterms:spatial>Wognum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3159'">
                <dcterms:spatial>Zwaagdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3160'">
                <dcterms:spatial>Alteveer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3161'">
                <dcterms:spatial>Ansen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3162'">
                <dcterms:spatial>de Wijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3163'">
                <dcterms:spatial>Drogteropslagen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3164'">
                <dcterms:spatial>Echten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3165'">
                <dcterms:spatial>Eursinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3166'">
                <dcterms:spatial>Kerkenveld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3167'">
                <dcterms:spatial>Koekange</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3168'">
                <dcterms:spatial>Linde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3169'">
                <dcterms:spatial>Ruinen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3170'">
                <dcterms:spatial>Ruinerwold</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3171'">
                <dcterms:spatial>Veeningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3172'">
                <dcterms:spatial>Zuidwolde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3173'">
                <dcterms:spatial>Augustinusga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3174'">
                <dcterms:spatial>Boelenslaan</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3175'">
                <dcterms:spatial>Buitenpost</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3176'">
                <dcterms:spatial>Drogeham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3177'">
                <dcterms:spatial>Gerkesklooster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3178'">
                <dcterms:spatial>Harkema</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3179'">
                <dcterms:spatial>Kootstertille</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3180'">
                <dcterms:spatial>Stroobos</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3181'">
                <dcterms:spatial>Surhuisterveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3182'">
                <dcterms:spatial>Surhuizum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3183'">
                <dcterms:spatial>Twijzel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3184'">
                <dcterms:spatial>Twijzelerheide</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3185'">
                <dcterms:spatial>Anna Paulowna</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3186'">
                <dcterms:spatial>Breezand</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3187'">
                <dcterms:spatial>Wieringerwaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3188'">
                <dcterms:spatial>Kamperland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3189'">
                <dcterms:spatial>Kortgene</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3190'">
                <dcterms:spatial>Colijnsplaat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3191'">
                <dcterms:spatial>Wissenkerke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3192'">
                <dcterms:spatial>Kats</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3193'">
                <dcterms:spatial>Geersdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3194'">
                <dcterms:spatial>***</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3195'">
                <dcterms:spatial>Escharen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3196'">
                <dcterms:spatial>Gassel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3197'">
                <dcterms:spatial>Grave</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3198'">
                <dcterms:spatial>Velp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3199'">
                <dcterms:spatial>Blije</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3200'">
                <dcterms:spatial>Burdaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3201'">
                <dcterms:spatial>Ferwert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3202'">
                <dcterms:spatial>Ginnum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3203'">
                <dcterms:spatial>Hallum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3204'">
                <dcterms:spatial>Hegebeintum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3205'">
                <dcterms:spatial>Jannum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3206'">
                <dcterms:spatial>Jislum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3207'">
                <dcterms:spatial>Lichtaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3208'">
                <dcterms:spatial>Marrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3209'">
                <dcterms:spatial>Reitsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3210'">
                <dcterms:spatial>Wânswert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3211'">
                <dcterms:spatial>Acquoy</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3212'">
                <dcterms:spatial>Rhenoy</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3213'">
                <dcterms:spatial>Beesd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3214'">
                <dcterms:spatial>Gellicum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3215'">
                <dcterms:spatial>Rumpt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3216'">
                <dcterms:spatial>Enspijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3217'">
                <dcterms:spatial>Deil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3218'">
                <dcterms:spatial>Geldermalsen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3219'">
                <dcterms:spatial>Meteren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3220'">
                <dcterms:spatial>Tricht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3221'">
                <dcterms:spatial>Buurmalsen </dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3222'">
                <dcterms:spatial>Harderwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3223'">
                <dcterms:spatial>Hierden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3224'">
                <dcterms:spatial>Alde Leie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3225'">
                <dcterms:spatial>Britsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3226'">
                <dcterms:spatial>Feinsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3227'">
                <dcterms:spatial>Hijum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3228'">
                <dcterms:spatial>Jelsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3229'">
                <dcterms:spatial>Koarnjum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3230'">
                <dcterms:spatial>Stiens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3231'">
                <dcterms:spatial>Voorschoten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3232'">
                <dcterms:spatial>Bierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3233'">
                <dcterms:spatial>Borgsweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3234'">
                <dcterms:spatial>Delfzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3235'">
                <dcterms:spatial>Farmsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3236'">
                <dcterms:spatial>Godlinze</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3237'">
                <dcterms:spatial>Holwierde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3238'">
                <dcterms:spatial>Krewerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3239'">
                <dcterms:spatial>Losdorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3240'">
                <dcterms:spatial>Meedhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3241'">
                <dcterms:spatial>Spijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3242'">
                <dcterms:spatial>Termunten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3243'">
                <dcterms:spatial>Termunterzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3244'">
                <dcterms:spatial>Wagenborgen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3245'">
                <dcterms:spatial>Woldendorp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3246'">
                <dcterms:spatial>Doetinchem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3247'">
                <dcterms:spatial>Gaanderen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3248'">
                <dcterms:spatial>Wehl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3249'">
                <dcterms:spatial>Oss</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3250'">
                <dcterms:spatial>Berghem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3251'">
                <dcterms:spatial>Megen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3252'">
                <dcterms:spatial>Macharen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3253'">
                <dcterms:spatial>Haren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3254'">
                <dcterms:spatial>Ravenstein</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3255'">
                <dcterms:spatial>Herpen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3256'">
                <dcterms:spatial>Deursen-Dennenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3257'">
                <dcterms:spatial>Huisseling</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3258'">
                <dcterms:spatial>Koolwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3259'">
                <dcterms:spatial>Dieden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3260'">
                <dcterms:spatial>Demen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3261'">
                <dcterms:spatial>Neerlangel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3262'">
                <dcterms:spatial>Neerloon</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3263'">
                <dcterms:spatial>Overlangel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3264'">
                <dcterms:spatial>Keent</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3265'">
                <dcterms:spatial>Broeksterwâld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3266'">
                <dcterms:spatial>Damwâld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3267'">
                <dcterms:spatial>De Falom</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3268'">
                <dcterms:spatial>Driezum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3269'">
                <dcterms:spatial>Wâlterswâld</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3270'">
                <dcterms:spatial>Rinsumageast</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3271'">
                <dcterms:spatial>Feanwâlden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3272'">
                <dcterms:spatial>Readtsjerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3273'">
                <dcterms:spatial>De Westereen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3274'">
                <dcterms:spatial>Sibrandahûs</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3275'">
                <dcterms:spatial>Boornbergum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3276'">
                <dcterms:spatial>De Tike</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3277'">
                <dcterms:spatial>De Veenhoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3278'">
                <dcterms:spatial>De Wilgen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3279'">
                <dcterms:spatial>Drachten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3280'">
                <dcterms:spatial>Drachtstercompagnie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3281'">
                <dcterms:spatial>Goëngahuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3282'">
                <dcterms:spatial>Houtigehage</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3283'">
                <dcterms:spatial>Kortehemmen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3284'">
                <dcterms:spatial>Nijega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3285'">
                <dcterms:spatial>Opeinde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3286'">
                <dcterms:spatial>Oudega</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3287'">
                <dcterms:spatial>Rottevalle</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3288'">
                <dcterms:spatial>Smalle Ee </dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3289'">
                <dcterms:spatial>Dirkshorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3290'">
                <dcterms:spatial>Oudkarspel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3291'">
                <dcterms:spatial>Sint Maarten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3292'">
                <dcterms:spatial>Tuitjenhorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3293'">
                <dcterms:spatial>Waarland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3294'">
                <dcterms:spatial>Warmenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3295'">
                <dcterms:spatial>Utrecht</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3296'">
                <dcterms:spatial>Vleuten</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3297'">
                <dcterms:spatial>De Meern</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3298'">
                <dcterms:spatial>Haarzuilens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3299'">
                <dcterms:spatial>Vlieland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3300'">
                <dcterms:spatial>Almkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3301'">
                <dcterms:spatial>Andel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3302'">
                <dcterms:spatial>Giessen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3303'">
                <dcterms:spatial>Rijswijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3304'">
                <dcterms:spatial>Uitwijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3305'">
                <dcterms:spatial>Waardhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3306'">
                <dcterms:spatial>Woudrichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3307'">
                <dcterms:spatial>Augsbuurt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3308'">
                <dcterms:spatial>Burum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3309'">
                <dcterms:spatial>Kollum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3310'">
                <dcterms:spatial>Kollumerpomp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3311'">
                <dcterms:spatial>Kollumerzwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3312'">
                <dcterms:spatial>Munnekezijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3313'">
                <dcterms:spatial>Oudwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3314'">
                <dcterms:spatial>Triemen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3315'">
                <dcterms:spatial>Veenklooster</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3316'">
                <dcterms:spatial>Warfstermolen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3317'">
                <dcterms:spatial>Westergeest</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3318'">
                <dcterms:spatial>Zwagerbosch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3319'">
                <dcterms:spatial>Blauwe Stad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3320'">
                <dcterms:spatial>Bruinehaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3321'">
                <dcterms:spatial>Den Ham</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3322'">
                <dcterms:spatial>Geerdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3323'">
                <dcterms:spatial>Kloosterhaar</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3324'">
                <dcterms:spatial>Sibculo</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3325'">
                <dcterms:spatial>Vriezenveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3326'">
                <dcterms:spatial>Vroomshoop</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3327'">
                <dcterms:spatial>Westerhaar-Vriezenveensewijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3328'">
                <dcterms:spatial>Oudebildtzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3329'">
                <dcterms:spatial>Westhoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3330'">
                <dcterms:spatial>Nij Altoenae</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3331'">
                <dcterms:spatial>St.-Annaparochie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3332'">
                <dcterms:spatial>St.-Jacobiparochie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3333'">
                <dcterms:spatial>Minnertsga</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3334'">
                <dcterms:spatial>Vrouwenparochie</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3335'">
                <dcterms:spatial>Driebergen-Rijsenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3336'">
                <dcterms:spatial>Leersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3337'">
                <dcterms:spatial>Amerongen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3338'">
                <dcterms:spatial>Overberg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3339'">
                <dcterms:spatial>Doorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3340'">
                <dcterms:spatial>Maarn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3341'">
                <dcterms:spatial>Maarsbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3342'">
                <dcterms:spatial>Bakkeveen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3343'">
                <dcterms:spatial>Beetsterzwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3344'">
                <dcterms:spatial>Drachten-Azeven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3345'">
                <dcterms:spatial>Frieschepalen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3346'">
                <dcterms:spatial>Gorredijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3347'">
                <dcterms:spatial>Hemrik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3348'">
                <dcterms:spatial>Jonkerslân</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3349'">
                <dcterms:spatial>Langezwaag</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3350'">
                <dcterms:spatial>Lippenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3351'">
                <dcterms:spatial>Luxwoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3352'">
                <dcterms:spatial>Nij Beets</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3353'">
                <dcterms:spatial>Olterterp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3354'">
                <dcterms:spatial>Siegerswoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3355'">
                <dcterms:spatial>Terwispel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3356'">
                <dcterms:spatial>Tijnje</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3357'">
                <dcterms:spatial>Ureterp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3358'">
                <dcterms:spatial>Wijnjewoude</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3359'">
                <dcterms:spatial>Est</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3360'">
                <dcterms:spatial>Haaften</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3361'">
                <dcterms:spatial>Heesselt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3362'">
                <dcterms:spatial>Hellouw</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3363'">
                <dcterms:spatial>Neerijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3364'">
                <dcterms:spatial>Ophemert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3365'">
                <dcterms:spatial>Opijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3366'">
                <dcterms:spatial>Tuil</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3367'">
                <dcterms:spatial>Varik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3368'">
                <dcterms:spatial>Waardenburg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3369'">
                <dcterms:spatial>Zennewijnen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3370'">
                <dcterms:spatial>Asch</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3371'">
                <dcterms:spatial>Beusichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3372'">
                <dcterms:spatial>Buren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3373'">
                <dcterms:spatial>Buurmalsen </dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3374'">
                <dcterms:spatial>Eck en Wiel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3375'">
                <dcterms:spatial>Erichem</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3376'">
                <dcterms:spatial>Ingen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3377'">
                <dcterms:spatial>Kapel-Avezaath</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3378'">
                <dcterms:spatial>Kerk-Avezaath</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3379'">
                <dcterms:spatial>Lienden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3380'">
                <dcterms:spatial>Maurik</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3381'">
                <dcterms:spatial>Ommeren</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3382'">
                <dcterms:spatial>Ravenswaaij</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3383'">
                <dcterms:spatial>Rijswijk (GLD)</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3384'">
                <dcterms:spatial>Zoelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3385'">
                <dcterms:spatial>Zoelmond</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3386'">
                <dcterms:spatial>Appingedam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3387'">
                <dcterms:spatial>Aalsum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3388'">
                <dcterms:spatial>Anjum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3389'">
                <dcterms:spatial>Bornwird</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3390'">
                <dcterms:spatial>Brantgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3391'">
                <dcterms:spatial>Dokkum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3392'">
                <dcterms:spatial>Ee</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3393'">
                <dcterms:spatial>Engwierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3394'">
                <dcterms:spatial>Foudgum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3395'">
                <dcterms:spatial>Hantum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3396'">
                <dcterms:spatial>Hantumeruitburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3397'">
                <dcterms:spatial>Hantumhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3398'">
                <dcterms:spatial>Hiaure</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3399'">
                <dcterms:spatial>Holwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3400'">
                <dcterms:spatial>Jouswier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3401'">
                <dcterms:spatial>Lioessens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3402'">
                <dcterms:spatial>Metslawier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3403'">
                <dcterms:spatial>Moddergat</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3404'">
                <dcterms:spatial>Morra</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3405'">
                <dcterms:spatial>Nes</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3406'">
                <dcterms:spatial>Niawier</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3407'">
                <dcterms:spatial>Oosternijkerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3408'">
                <dcterms:spatial>Oostrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3409'">
                <dcterms:spatial>Paesens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3410'">
                <dcterms:spatial>Raard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3411'">
                <dcterms:spatial>Ternaard</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3412'">
                <dcterms:spatial>Waaxens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3413'">
                <dcterms:spatial>Wetsens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3414'">
                <dcterms:spatial>Wierum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3415'">
                <dcterms:spatial>Eenrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3416'">
                <dcterms:spatial>Hornhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3417'">
                <dcterms:spatial>Houwerzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3418'">
                <dcterms:spatial>Kloosterburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3419'">
                <dcterms:spatial>Lauwersoog</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3420'">
                <dcterms:spatial>Leens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3421'">
                <dcterms:spatial>Mensingeweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3422'">
                <dcterms:spatial>Niekerk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3423'">
                <dcterms:spatial>Pieterburen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3424'">
                <dcterms:spatial>Schouwerzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3425'">
                <dcterms:spatial>Ulrum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3426'">
                <dcterms:spatial>Vierhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3427'">
                <dcterms:spatial>Warfhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3428'">
                <dcterms:spatial>Wehe-den Hoorn</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3429'">
                <dcterms:spatial>Westernieland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3430'">
                <dcterms:spatial>Zoutkamp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3431'">
                <dcterms:spatial>Zuurdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3432'">
                <dcterms:spatial>Alphen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3433'">
                <dcterms:spatial>Chaam</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3434'">
                <dcterms:spatial>Galder</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3435'">
                <dcterms:spatial>Strijbeek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3436'">
                <dcterms:spatial>Ulvenhout AC</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3437'">
                <dcterms:spatial>Bavel AC</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3438'">
                <dcterms:spatial>Beringe</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3439'">
                <dcterms:spatial>Egchel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3440'">
                <dcterms:spatial>Grashoek</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3441'">
                <dcterms:spatial>Helden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3442'">
                <dcterms:spatial>Koningslust</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3443'">
                <dcterms:spatial>Panningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3444'">
                <dcterms:spatial>Eenum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3445'">
                <dcterms:spatial>Garrelsweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3446'">
                <dcterms:spatial>Garsthuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3447'">
                <dcterms:spatial>Huizinge</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3448'">
                <dcterms:spatial>Leermens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3449'">
                <dcterms:spatial>Loppersum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3450'">
                <dcterms:spatial>Middelstum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3451'">
                <dcterms:spatial>Oosterwijtwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3452'">
                <dcterms:spatial>Startenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3453'">
                <dcterms:spatial>Stedum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3454'">
                <dcterms:spatial>Toornwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3455'">
                <dcterms:spatial>Westeremden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3456'">
                <dcterms:spatial>Westerwijtwerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3457'">
                <dcterms:spatial>Wirdum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3458'">
                <dcterms:spatial>'t Zandt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3459'">
                <dcterms:spatial>Zeerijp</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3460'">
                <dcterms:spatial>Zijldijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3461'">
                <dcterms:spatial>Hansweert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3462'">
                <dcterms:spatial>Krabbendijke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3463'">
                <dcterms:spatial>Kruiningen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3464'">
                <dcterms:spatial>Oostdijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3465'">
                <dcterms:spatial>Rilland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3466'">
                <dcterms:spatial>Waarde</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3467'">
                <dcterms:spatial>Yerseke</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3468'">
                <dcterms:spatial>Lith</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3469'">
                <dcterms:spatial>Lithoijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3470'">
                <dcterms:spatial>Oijen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3471'">
                <dcterms:spatial>Maren-Kessel</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3472'">
                <dcterms:spatial>Teeffelen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3473'">
                <dcterms:spatial>Eemshaven</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3474'">
                <dcterms:spatial>Eppenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3475'">
                <dcterms:spatial>Kantens</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3476'">
                <dcterms:spatial>Oldenzijl</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3477'">
                <dcterms:spatial>Oosternieland</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3478'">
                <dcterms:spatial>Oudeschip</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3479'">
                <dcterms:spatial>Roodeschool</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3480'">
                <dcterms:spatial>Rottum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3481'">
                <dcterms:spatial>Startenhuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3482'">
                <dcterms:spatial>Stitswerd</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3483'">
                <dcterms:spatial>Uithuizen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3484'">
                <dcterms:spatial>Uithuizermeeden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3485'">
                <dcterms:spatial>Usquert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3486'">
                <dcterms:spatial>Warffum</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3487'">
                <dcterms:spatial>Zandeweer</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3488'">
                <dcterms:spatial>Meerstad</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3489'">
                <dcterms:spatial>Zundert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3490'">
                <dcterms:spatial>Rijsbergen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3491'">
                <dcterms:spatial>Wernhout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3492'">
                <dcterms:spatial>Klein Zundert</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3493'">
                <dcterms:spatial>Achtmaal</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3494'">
                <dcterms:spatial>Malden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3495'">
                <dcterms:spatial>Heumen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3496'">
                <dcterms:spatial>Overasselt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3497'">
                <dcterms:spatial>Nederasselt</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3498'">
                <dcterms:spatial>Drunen</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3499'">
                <dcterms:spatial>Elshout</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3500'">
                <dcterms:spatial>Haarsteeg</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3501'">
                <dcterms:spatial>Heusden Gem. Heusden</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3502'">
                <dcterms:spatial>Nieuwkuijk</dcterms:spatial>
              </xsl:when>
              <xsl:when test="$waarde='3503'">
                <dcterms:spatial>Vlijmen</dcterms:spatial>
              </xsl:when>
              <xsl:otherwise>
                <dcterms:spatial>
                  <xsl:value-of select="."/>
                </dcterms:spatial>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:toponiem">
            <dcterms:spatial>
              <xsl:value-of select="."/>
            </dcterms:spatial>
          </xsl:for-each>
          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:kaartblad">
            <dcterms:spatial>
              <xsl:value-of select="."/>
              <xsl:text> (kaartblad)</xsl:text>
            </dcterms:spatial>
          </xsl:for-each>

        </xsl:for-each>
        <!-- projectlocatie  -->

        <!-- temporal -->
        <!-- unieke waarden van beginperiode en/of eindperiode -->
        <xsl:for-each select="sikb:vindplaats">
          <xsl:variable name="van" select="sikb:beginperiode"/>
          <xsl:if test="$van">
            <xsl:for-each
              select="sikb:beginperiode[(not(text()=../preceding-sibling::sikb:vindplaats/sikb:beginperiode) or (text()=../preceding-sibling::sikb:vindplaats/sikb:eindperiode))]">
              <xsl:call-template name="dateringen">
                <xsl:with-param name="waarde" select="$van"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:if>
          <xsl:variable name="tot" select="sikb:eindperiode"/>
          <xsl:if test="($tot) and ($van != $tot)">
            <xsl:for-each
              select="sikb:eindperiode[(not(text()=../preceding-sibling::sikb:vindplaats/sikb:beginperiode) or (text()=../preceding-sibling::sikb:vindplaats/sikb:eindperiode))]">
              <xsl:call-template name="dateringen">
                <xsl:with-param name="waarde" select="$tot"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:if>
        </xsl:for-each>

        <!-- spatial_point -->
        <!-- project locatie -->
        <xsl:for-each select="sikb:project/sikb:projectlocatieId">
          <xsl:variable name="locatieId" select="."/>
          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:geolocatieId">
            <xsl:variable name="geoId" select="."/>
            <xsl:variable name="punt" select="/sikb:sikb0102/sikb:geolocatie[@sikb:id = $geoId]/sikb:geometrie/gml:pos"/>
            <xsl:variable name="loceast" select="substring-before($punt,' ')"/>
            <xsl:variable name="locnorth" select="substring-after($punt,' ')"/>
            <xsl:if test="($locnorth) and ($loceast)">
              <eas:spatial>
                <eas:point eas:scheme="RD" eas:schemeId="archaeology.eas.spatial">
                  <eas:x>
                    <xsl:value-of select="$loceast"/>
                  </eas:x>
                  <eas:y>
                    <xsl:value-of select="$locnorth"/>
                  </eas:y>
                </eas:point>
              </eas:spatial>
            </xsl:if>
          </xsl:for-each>
        </xsl:for-each>

      </emd:coverage>

      <!-- emd:rights -->
      <emd:rights>

        <xsl:for-each select="sikb:project/sikb:uitvoerder/sikb:organisatieId">
          <xsl:variable name="opgraver" select="."/>
          <dc:rights>
            <xsl:value-of select="/sikb:sikb0102/sikb:organisatie[@sikb:id = $opgraver]/sikb:naam"/>
          </dc:rights>
        </xsl:for-each>

        <!-- default toegangscategorie -->
        <dcterms:accessRights eas:scheme="" eas:schemeId="common.dcterms.accessrights">GROUP_ACCESS</dcterms:accessRights>

        <!-- license agreements accepted -->
        <dcterms:license eas:scheme="EASY version 1">accept</dcterms:license>

      </emd:rights>

      <!-- audience -->
      <emd:audience>
        <dcterms:audience eas:schemeId="custom.disciplines">easy-discipline:2</dcterms:audience>
      </emd:audience>

      <!--  emd:other -->
      <emd:other>
        <eas:application-specific>
          <eas:metadataformat>ARCHAEOLOGY</eas:metadataformat>
        </eas:application-specific>

        <!-- emd:etc -->
        <eas:etc>
          <property-list>
            <comment>Metadata conversion from archaeological exchange format depots</comment>
            <entry key="conversion.date">2012</entry>
          </property-list>
        </eas:etc>
      </emd:other>

    </emd:easymetadata>

  </xsl:template>


  <xsl:template name="dateringen">
    <xsl:param name="waarde"/>
    <xsl:choose>
      <xsl:when test="$waarde='PALEO'">
        <!--Paleolithicum: tot 8800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PALEOV'">
        <!--Paleolithicum vroeg: tot 300000 C14 -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PALEOM'">
        <!--Paleolithicum midden: 300000 - 35000 C14 -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PALEOL'">
        <!-- Paleolithicum laat: 35000 C14 - 8800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PALEOLA'">
        <!--Paleolithicum laat A: 35000 - 18000 C14 -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PALEOLB'">
        <!-- Paleolithicum laat B: 18000 C14 -8800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MESO'">
        <!-- Mesolithicum: 8800 - 4900 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MESOV'">
        <!-- Mesolithicum vroeg: 8800 - 7100 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MESOM'">
        <!-- Mesolithicum midden: 7100 - 6450 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MESOL'">
        <!-- Mesolithicum laat: 6450 - 4900 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEO'">
        <!-- Neolithicum: 5300 - 2000 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOV'">
        <!-- Neolithicum vroeg: 5300 - 4200 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOVA'">
        <!-- Neolithicum vroeg A: 5300 - 4900 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOVB'">
        <!-- Neolithicum vroeg B: 4900 - 4200 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOM'">
        <!-- Neolithicum midden: 4200 - 2850 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOMA'">
        <!-- Neolithicum midden A: 4200 - 3400 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOMB'">
        <!-- Neolithicum midden B: 3400 - 2850 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOL'">
        <!-- Neolithicum laat: 2850 - 2000 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOLA'">
        <!-- Neolithicum laat A: 2850 - 2450 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NEOLB'">
        <!-- Neolithicum laat B: 2450 - 2000 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONS'">
        <!-- Bronstijd: 2000 - 800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONSV'">
        <!-- Bronstijd vroeg: 2000 - 1800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONSM'">
        <!-- Bronstijd midden: 1800 - 1100 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONSMA'">
        <!-- Bronstijd midden A: 1800 - 1500 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONSMB'">
        <!-- Bronstijd midden B: 1500 - 1100 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='BRONSL'">
        <!-- Bronstijd laat: 1100 - 800 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='IJZ'">
        <!-- IJzertijd: 800 - 12 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='IJZV'">
        <!-- IJzertijd vroeg: 800 - 500 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='IJZM'">
        <!-- IJzertijd midden: 500 - 250 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='IJZL'">
        <!-- IJzertijd laat: 250 - 12 vC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROM'">
        <!-- Romeinse tijd: 12 vC - 450 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMV'">
        <!-- Romeinse tijd vroeg: 12 vC - 70 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMVA'">
        <!-- Romeinse tijd vroeg A: 12 vC - 25 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMVB'">
        <!-- Romeinse tijd vroeg B: 25 - 70 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMM'">
        <!-- Romeinse tijd midden: 70 - 270 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMMA'">
        <!-- Romeinse tijd midden A: 70 - 150 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMMB'">
        <!-- Romeinse tijd midden B: 150 - 270 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROML'">
        <!-- Romeinse tijd laat: 270 - 450 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMLA'">
        <!-- Romeinse tijd laat A: 270 - 350 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='ROMLB'">
        <!-- Romeinse tijd laat B: 350 - 450 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='XME' or $waarde='ME'">
        <!-- Middeleeuwen 450 - 1500 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">XME</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='VME' or $waarde='MEV'">
        <!-- Middeleeuwen vroeg: 450 - 1050 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">VME></dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='VMEA' or $waarde='MEVA'">
        <!-- Middeleeuwen vroeg A: 450 - 525 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">VMEA</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='VMEB' or $waarde='MEVB'">
        <!-- Middeleeuwen vroeg B: 525 - 725 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">VMEB</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='VMEC' or $waarde='MEVC'">
        <!-- Middeleeuwen vroeg C: 725 - 900 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">VMEC></dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='VMED' or $waarde='MEVD'">
        <!-- Middeleeuwen vroeg D: 900 - 1050 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">VMED</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='LME' or $waarde='MEL'">
        <!-- Middeleeuwen laat: 1050 - 1500 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">LME</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='LMEA' or $waarde='MELA'">
        <!-- Mideleeuwen laat A: 1050 - 1250 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">LMEA</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='LMEB' or $waarde='MELB'">
        <!-- Middeleeuwen laat B: 1250 - 1500 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">LMEB</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NT'">
        <!-- Nieuwe tijd: 1500 - heden -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NTA' or $waarde='NTV'">
        <!-- Nieuwe tijd A: 1500 - 1650 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">NTA</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NTB' or $waarde='NTM'">
        <!-- Nieuwe tijd B: 1650 - 1850 nC -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">NTB</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NTC' or $waarde='NTL'">
        <!-- Nieuwe tijd C: 1850 - heden -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">NTC</dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='XXX'">
        <!-- Onbekend -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$waarde"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:otherwise>
        <!-- niet voorkomend in de Archis-ABR lijst van perioden -->
        <dcterms:temporal>
          <xsl:value-of select="$waarde"/>
          <xsl:text> (ABR_Periode)</xsl:text>
        </dcterms:temporal>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
