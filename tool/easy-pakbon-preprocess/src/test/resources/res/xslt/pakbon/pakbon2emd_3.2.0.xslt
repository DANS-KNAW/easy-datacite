<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" 
 xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/" 
   xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/" 
   xmlns:gml="http://www.opengis.net/gml/3.2"
   xmlns:sikb="http://www.sikb.nl/sikb0102/3.2.0" 
   xmlns:p2e="java:nl.knaw.dans.platform.language.pakbon.Pakbon2EmdFunctions"
   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
   xmlns:sikbl="http://www.sikb.nl/codelijst/1.0" 
   exclude-result-prefixes="sikb gml p2e">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:variable name="lookup-doc" select="document('SIKB0102_Lookup_3.2.0.xml')"/>

  <xsl:key name="documents-by-bestandId" match="sikb:document" use="sikb:bestandId" />

  <xsl:template match="sikb:sikb0102">

    <emd:easymetadata xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
      xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://easy.dans.knaw.nl/easy/easymetadata/ http://easy.dans.knaw.nl/schemas/md/emd/2012/11/emd.xsd" emd:version="0.1">
      <!-- emd:title -->
      <emd:title>
        <!-- Note that there is always one project and it must have a projectNaam -->
        <xsl:for-each select="sikb:project/sikb:projectnaam">
          <dc:title>
            <xsl:value-of select="."/>
          </dc:title>
        </xsl:for-each>
        <!-- unique titles, ISSUE-704 -->
        <xsl:for-each select="sikb:document/sikb:titel[../sikb:documenttype='EINDRAP' and not(.=/sikb:sikb0102/sikb:project/sikb:projectnaam) and not(.=preceding::sikb:titel)]/ .">
          <dcterms:alternative>
            <xsl:value-of select="."/>
          </dcterms:alternative>
        </xsl:for-each>
      </emd:title>

      <!-- emd:creator  -->
      <emd:creator>
        <!-- get the unique auteur names of EINDRAP document -->
        <!-- Using depricated dc:creator, because sikb:auteur cannot be mapped to eas:creator without 'intelligent' string splitting -->
        <xsl:for-each
          select="sikb:document/sikb:auteur[../sikb:documenttype='EINDRAP' and not(.=preceding::sikb:auteur)]/ .">
          <dc:creator>
        <xsl:value-of select="." />
       </dc:creator>
      </xsl:for-each>

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
          <xsl:variable name="code" select="."/>
          <xsl:variable name="waarde">
            <xsl:call-template name="verwerving">
              <xsl:with-param name="code" select="$code"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="$waarde!=''">
              <xsl:element name="dc:subject"><xsl:value-of select="$waarde"/></xsl:element>
            </xsl:when>
            <xsl:otherwise>
              <xsl:element name="dc:subject"><xsl:value-of select="$code"/></xsl:element>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

        <!-- ABR - complextype -->
        <xsl:for-each select="sikb:vindplaats/sikb:vindplaatstype">
          <xsl:variable name="sikbCode" select="."/>
          <!-- issue EASY-747, don't map 'unknown' with code XXX --> 
          <xsl:if test="not($sikbCode='XXX')">
            <xsl:variable name="easyCode">
                <xsl:call-template name="easyComplexType">
                  <xsl:with-param name="code" select="$sikbCode"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="$easyCode!=''">
                <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                  <xsl:value-of select="$easyCode"/>
                </dc:subject>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dc:subject"><xsl:value-of select="$sikbCode"/></xsl:element>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:if>
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
                  <!-- <xsl:text>-01-01T00:00:00+01:00</xsl:text> -->
                  <xsl:text>-01-01T00:00:00.000Z</xsl:text>
                </eas:created>
              </xsl:when>
              <xsl:when test="string-length(.)=7">
                <eas:created eas:scheme="W3CDTF" eas:format="MONTH">
                  <xsl:value-of select="."/>
                  <!-- <xsl:text>-01T00:00:00+01:00</xsl:text> -->
                  <xsl:text>-01T00:00:00.000Z</xsl:text>
                </eas:created>
              </xsl:when>
              <xsl:when test="string-length(.)=10">
                <eas:created eas:scheme="W3CDTF" eas:format="DAY">
                  <xsl:value-of select="."/>
                  <!-- <xsl:text>T00:00:00+01:00</xsl:text> -->
                  <xsl:text>T00:00:00.000Z</xsl:text>
                </eas:created>
              </xsl:when>
            </xsl:choose>
          </xsl:if>
        </xsl:for-each>

        <!-- date_available is standaard datum, te veranderen op moment van conversie-->
        <eas:available eas:scheme="W3CDTF" eas:format="DAY">
          <xsl:value-of select="current-dateTime()"/>
        </eas:available>
      </emd:date>

      <!-- emd:type -->
      <emd:type>
        <xsl:variable name="bestanden" select="sikb:bestand"/>
        <xsl:variable name="documenten" select="sikb:document/sikb:bestandId"/>
        <xsl:variable name="fotos" select="sikb:foto/sikb:bestandId"/>
        <xsl:variable name="tekeningen" select="sikb:tekening/sikb:bestandId"/>
        <!-- get the list of bestanden that don't have a document refering to them -->
        <xsl:variable name="nonDocBestanden" select="sikb:bestand[not(key('documents-by-bestandId', @sikb:id))]" />
        <!-- only Text when the list of nonDocBestanden is empty -->
        <xsl:choose>
          <xsl:when test="$documenten and not($nonDocBestanden)">
            <dc:type eas:scheme="DCMI" eas:schemeId="common.dc.type">
              <xsl:text>Text</xsl:text>
            </dc:type>
          </xsl:when>
          <xsl:otherwise>
            <dc:type eas:scheme="DCMI" eas:schemeId="common.dc.type">
              <xsl:text>Dataset</xsl:text>
            </dc:type>
          </xsl:otherwise>
        </xsl:choose>
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

      <!-- emd:language -->
      <emd:language>
        <!-- Nederlands is als default waarde gekozen-->
        <dc:language eas:scheme="ISO 639" eas:schemeId="common.dc.language">dut/nld</dc:language>
      </emd:language>

      <!-- emd: relation -->
      <emd:relation></emd:relation>

      <!-- emd:coverage -->
      <emd:coverage>
        <!-- spatial -->  
        <xsl:for-each select="sikb:project/sikb:projectlocatieId">
          <xsl:variable name="locatieId" select="."/>
          
          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:provinciecode">
            <xsl:variable name="code" select="."/>
            <xsl:variable name="waarde">
              <xsl:call-template name="provincie">
                <xsl:with-param name="code" select="$code"/>
              </xsl:call-template>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="$waarde!=''">
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$waarde"/></xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$code"/></xsl:element>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <!-- plaatscode and gemeentecode are translated to the human readable or common name (omschrijving)
               because there are a lot of them we keep them in a separate lookup file. 
               For now we use the file provided  by the SIKB -->
          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:gemeentecode">
            <xsl:variable name="code" select="."/>
            <xsl:variable name="waarde" select="$lookup-doc//sikbl:gemeente/sikbl:code[text()=$code]/../sikbl:omschrijving"></xsl:variable>
            <xsl:choose>
              <xsl:when test="$waarde!=''">
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$waarde"/></xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$code"/></xsl:element>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:plaatscode">
            <xsl:variable name="code" select="."/>
            <xsl:variable name="waarde" select="$lookup-doc/sikbl:lookup/sikbl:plaatsCodelijst/sikbl:plaats/sikbl:code[text()=$code]/../sikbl:omschrijving"></xsl:variable>
            <xsl:choose>
              <xsl:when test="$waarde!=''">
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$waarde"/></xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$code"/></xsl:element>
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

        <!-- temporal -->
        <!-- unieke waarden van beginperiode en/of eindperiode -->
         <xsl:for-each select="sikb:vindplaats">
          <xsl:variable name="van" select="sikb:beginperiode"/>
          <xsl:if test="$van">
            <xsl:for-each
              select="sikb:beginperiode[(not((text()=../preceding-sibling::sikb:vindplaats/sikb:beginperiode) or (text()=../preceding-sibling::sikb:vindplaats/sikb:eindperiode)))]">
              <xsl:call-template name="dateringen">
                <xsl:with-param name="code" select="$van"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:if>
          <xsl:variable name="tot" select="sikb:eindperiode"/>
          <xsl:if test="($tot) and ($van != $tot)">
            <xsl:for-each
              select="sikb:eindperiode[(not((text()=../preceding-sibling::sikb:vindplaats/sikb:beginperiode) or (text()=../preceding-sibling::sikb:vindplaats/sikb:eindperiode)))]">
              <xsl:call-template name="dateringen">
                <xsl:with-param name="code" select="$tot"/>
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
        <!-- default toegangscategorie -->
        <dcterms:accessRights eas:schemeId="common.dcterms.accessrights">GROUP_ACCESS</dcterms:accessRights>
        <!-- license agreements accepted -->
        <dcterms:license eas:scheme="EASY version 1">accept</dcterms:license>
        <xsl:for-each select="sikb:project/sikb:uitvoerder/sikb:organisatieId">
          <xsl:variable name="opgraver" select="."/>
          <dcterms:rightsHolder>
            <xsl:value-of select="/sikb:sikb0102/sikb:organisatie[@sikb:id = $opgraver]/sikb:naam"/>
          </dcterms:rightsHolder>
        </xsl:for-each>
      </emd:rights>

      <!-- audience -->
      <emd:audience>
        <dcterms:audience eas:schemeId="custom.disciplines">easy-discipline:2</dcterms:audience>
      </emd:audience>

      <!--  emd:other -->
      <emd:other>
        <eas:application-specific>
          <eas:metadataformat>ANY_DISCIPLINE</eas:metadataformat>
        </eas:application-specific>

        <!-- emd:etc -->
        <eas:etc>
          <property-list>
            <comment>Metadata conversion from archaeological exchange format (pakbon).</comment>
            <entry key="conversion.date">
              <!--
              <xsl:value-of select="p2e:currentDateTime()"/>
              -->
              <xsl:value-of select="current-dateTime()"/>
            </entry>
          </property-list>
        </eas:etc>
      </emd:other>

    </emd:easymetadata>

  </xsl:template>

  <!-- Code mapping and translations below -->

  <!-- resulting text is taken from the 'Omschrijving' -->
  <xsl:template name="verwerving">
    <xsl:param name="code"/>
    <xsl:choose>
      <xsl:when test="$code = 'ABE'">
        <xsl:text>Archeologisch: non-destructief: begeleiding</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'ABO'">
        <xsl:text>Archeologisch: non-destructief: boring</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'ABU'">
        <xsl:text>Archeologisch: non-destructief: bureauonderzoek</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'ADU'">
        <xsl:text>Archeologisch: non-destructief: duikactiviteiten</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AGO'">
        <xsl:text>Archeologisch: non-destructief: geofysisch onderzoek</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AHN'">
        <xsl:text>Aktueel hoogtemodel</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AIN'">
        <xsl:text>Archeologisch: non-destructief: inspectie</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AKA'">
        <xsl:text>Archeologisch: non-destructief: verwachtingskaart</xsl:text>
      </xsl:when>
        <xsl:when test="$code = 'AOP'">
          <xsl:text>Archeologisch: destructief: opgraving</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AOW'">
        <xsl:text>Archeologisch: destructief:onderwaterarcheologie</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APP'">
        <xsl:text>Archeologisch: destructief:proefputten\proefsleuven</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AVE'">
        <xsl:text>Archeologisch: destructief: (veld)kartering</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'AXX'">
        <xsl:text>Archeologisch (nieuw, systematiek ABR volgend)</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IAR'">
        <xsl:text>Indirect: archief</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'ICO'">
        <xsl:text>Indirect: collectiebeschrijving</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'ILI'">
        <xsl:text>Indirect: literatuur</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IXX'">
        <xsl:text>Indirect: onbepaald</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'LUC'">
        <xsl:text>Luchtfoto/remote sensing</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NBA'">
        <xsl:text>Niet-archeologisch: baggerwerk</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NBO'">
        <xsl:text>Niet-archeologisch: boring</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NDE'">
        <xsl:text>Niet-archeologisch: metaaldetector</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NDU'">
        <xsl:text>Niet-archeologisch: duikactiviteiten</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NGR'">
        <xsl:text>Niet-archeologisch: graafwerk</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NKA'">
        <xsl:text>Niet-archeologisch: kartering</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NWA'">
        <xsl:text>Niet-archeologisch: waterwerk en exploitatie</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'NXX'">
        <xsl:text>Niet-archeologisch</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'XXX'">
        <xsl:text>Onbekend</xsl:text>
      </xsl:when>

      <xsl:otherwise>
        <xsl:text/>
      </xsl:otherwise>
      <!-- empty string indicates no match-->
    </xsl:choose>
  </xsl:template>

  <!-- Provincie -->
  <xsl:template name="provincie">
    <xsl:param name="code"/>
    <xsl:choose>
      <xsl:when test="$code = '20'">
        <xsl:text>Groningen</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '21'">
        <xsl:text>Friesland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '22'">
        <xsl:text>Drenthe</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '23'">
        <xsl:text>Overijssel</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '24'">
        <xsl:text>Flevoland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '25'">
        <xsl:text>Gelderland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '26'">
        <xsl:text>Utrecht</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '27'">
        <xsl:text>Noord-Holland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '28'">
        <xsl:text>Zuid-Holland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '29'">
        <xsl:text>Zeeland</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '30'">
        <xsl:text>Noord-Brabant</xsl:text>
      </xsl:when>
      <xsl:when test="$code = '31'">
        <xsl:text>Limburg</xsl:text>
      </xsl:when>

      <xsl:otherwise>
        <xsl:text/>
      </xsl:otherwise>
      <!-- empty string indicates no match-->
    </xsl:choose>
  </xsl:template>

  <!-- ABR complex type is what we get from sikb0102 -->
  <xsl:template name="easyComplexType">
    <xsl:param name="code"/>
    <xsl:choose>
      <xsl:when test="$code = 'APVV.AK'">
        <xsl:text>ELA</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.CF'">
        <xsl:text>ELCF</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.DP'">
        <xsl:text>ELDP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.EK'">
        <xsl:text>ELEK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.LA'">
        <xsl:text>ELA</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.PDEK'">
        <xsl:text>ELA</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.STEL'">
        <xsl:text>EX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.TUIN'">
        <xsl:text>ELA</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.VK'">
        <xsl:text>ELVK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.VS'">
        <xsl:text>EVX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.VW'">
        <xsl:text>EVX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'APVV.X'">
        <xsl:text>EX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV'">
        <xsl:text>VLP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.AW'">
        <xsl:text>VWAL</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.BEXT'">
        <xsl:text>NX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.BW'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.CSTL'">
        <xsl:text>VLP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.CT'">
        <xsl:text>VLP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.DUMP'">
        <xsl:text>NX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.FORT'">
        <xsl:text>VLP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.HP'">
        <xsl:text>NHP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.HT'">
        <xsl:text>NHT</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.INKA'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.KAZE'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.KWB'">
        <xsl:text>VK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.LG'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.LW'">
        <xsl:text>VLW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.MBH'">
        <xsl:text>NMS</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.N'">
        <xsl:text>NX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.RV'">
        <xsl:text>NRV</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.SCH'">
        <xsl:text>VSCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.SK'">
        <xsl:text>NS</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.STEL'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.SV'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.TH'">
        <xsl:text>NX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.TW'">
        <xsl:text>NT</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.VIC'">
        <xsl:text>NKD</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.VKM'">
        <xsl:text>VKM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.VX'">
        <xsl:text>VX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.WB'">
        <xsl:text>VWB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.WP'">
        <xsl:text>VWP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BEWV.X'">
        <xsl:text>NX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.BHV'">
        <xsl:text>GVC</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.CJBP'">
        <xsl:text>GVIK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.DIER'">
        <xsl:text>GD</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.GH'">
        <xsl:text>GHX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.GHV'">
        <xsl:text>GVX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.GVX'">
        <xsl:text>GVX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.GX'">
        <xsl:text>GX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.KH'">
        <xsl:text>GVIK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.MEG'">
        <xsl:text>GMEG</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.RGV'">
        <xsl:text>GVIR</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.TPGB'">
        <xsl:text>GX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.UV'">
        <xsl:text>GVCU</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.VG'">
        <xsl:text>GXV</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.VGV'">
        <xsl:text>GVX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'BGV.X'">
        <xsl:text>GX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD'">
        <xsl:text>RCP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.KERK'">
        <xsl:text>RKER</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.KLO'">
        <xsl:text>RKLO</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.KPL'">
        <xsl:text>RKAP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.OLOC'">
        <xsl:text>RCP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.SGMW'">
        <xsl:text>RCP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.TEMP'">
        <xsl:text>RCP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'CTHD.X'">
        <xsl:text>RCP</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'DEPO.EV'">
        <xsl:text>DEPO</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'DEPO.MV'">
        <xsl:text>DEPO</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'DEPO.X'">
        <xsl:text>DEPO</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.GRIW'">
        <xsl:text>EGX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.HOUT'">
        <xsl:text>EGX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.IJW'">
        <xsl:text>EGYW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.KW'">
        <xsl:text>EGKW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.MW'">
        <xsl:text>EGMW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.VSW'">
        <xsl:text>EGVU</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.VW'">
        <xsl:text>EGVW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.X'">
        <xsl:text>EGX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'GW.ZW'">
        <xsl:text>EGX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDH.TN'">
        <xsl:text>EITN</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.BB'">
        <xsl:text>EIBB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.BR'">
        <xsl:text>EIB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.GP'">
        <xsl:text>EIGB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.HB'">
        <xsl:text>EIHB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.HKB'">
        <xsl:text>EIHK</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.KB'">
        <xsl:text>EIKB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.LL'">
        <xsl:text>EILL</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.M'">
        <xsl:text>EIM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.MB'">
        <xsl:text>EIMB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.MBF'">
        <xsl:text>EIMB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.MBNF'">
        <xsl:text>EIMB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.PB'">
        <xsl:text>EIPB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.ROM'">
        <xsl:text>EIM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.SB'">
        <xsl:text>EISB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.TK'">
        <xsl:text>EIX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.VB'">
        <xsl:text>EIVB</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.WAM'">
        <xsl:text>EIM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.WIM'">
        <xsl:text>EIM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.X'">
        <xsl:text>EIX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'IDNH.ZP'">
        <xsl:text>EGZW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.BRUG'">
        <xsl:text>IBRU</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.DAM'">
        <xsl:text>IDAM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.DIJ'">
        <xsl:text>IDIJ</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.DOK'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.DUI'">
        <xsl:text>IDUI</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.GEM'">
        <xsl:text>IGEM</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.HAV'">
        <xsl:text>IHAV</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.KADE'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.KAN'">
        <xsl:text>IKAN</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.KSLU'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.PER'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.REDE'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.SLU'">
        <xsl:text>ISLU</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.SPRE'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.STREK'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.VIJV'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.VRDE'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.VS'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.VWEG'">
        <xsl:text>IVW</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.WEG'">
        <xsl:text>IWEG</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.WERF'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'INFR.X'">
        <xsl:text>IX</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.BSB'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.H'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.HIJZ'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.IJZ'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.LAD'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.OBSB'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.VORG'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'SV.X'">
        <xsl:text>ESCH</xsl:text>
      </xsl:when>
      <xsl:when test="$code = 'XXX'">
        <xsl:text>XXX</xsl:text>
      </xsl:when>

      <xsl:otherwise>
        <xsl:text/>
      </xsl:otherwise>
      <!-- empty string indicates no match-->
    </xsl:choose>
  </xsl:template>

  <!-- Periode mappen; van ABR nieuw (RCE en SIKB) naar oud (EASY) -->
  <xsl:template name="dateringen">
    <xsl:param name="code"/>
    <xsl:variable name="waarde" select="$code"/>
    <xsl:choose>
      <xsl:when test="$waarde='PALEOLB'">
        <!-- foutieve codering in Easy nog steeds niet gecorrigeerd. -->
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'PALEOB'"/>
        </dcterms:temporal>
      </xsl:when>

      <!-- Map middeleeuwen -->
      <xsl:when test="$waarde='ME'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'XME'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEV'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'VME'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEVA'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'VMEA'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEVB'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'VMEB'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEVC'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'VMEC'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEVD'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'VMED'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MEL'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'LME'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MELA'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'LMEA'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='MELB'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'LMEB'"/>
        </dcterms:temporal>
      </xsl:when>

      <!-- Map nieuwe tijd, Vroeg, Midden, Laat -->
      <xsl:when test="$waarde='NTV'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'NTA'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NTM'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'NTB'"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='NTL'">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="'NTC'"/>
        </dcterms:temporal>
      </xsl:when>

      <!-- we cannot simply map the ABR+, so we put it in free text -->
      <xsl:when test="$waarde='HIST'">
        <dcterms:temporal>
          <xsl:text>Historie 450 - heden (Pakbon_Periode: HIST)</xsl:text>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PROTO'">
        <dcterms:temporal>
          <xsl:text>Protohistorie: 12vC - 449 nC (Pakbon_Periode: PROTO)</xsl:text>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='STEEN'">
        <dcterms:temporal>
          <xsl:text>Steentijd: 1.000.000 - 2001 vC (Pakbon_Periode: STEEN)</xsl:text>
        </dcterms:temporal>
      </xsl:when>
      <xsl:when test="$waarde='PREH'">
        <dcterms:temporal>
          <xsl:text>Prehistorie: 1.000.000 - 13 vC (Pakbon_Periode: PREH)</xsl:text>
        </dcterms:temporal>
      </xsl:when>

      <xsl:when test="$waarde!=''">
        <dcterms:temporal eas:scheme="ABR" eas:schemeId="archaeology.dcterms.temporal">
          <xsl:value-of select="$code"/>
        </dcterms:temporal>
      </xsl:when>
      <xsl:otherwise>
        <!-- niet voorkomend in de Archis-ABR lijst van perioden -->
        <dcterms:temporal>
          <xsl:value-of select="$code"/>
          <xsl:text> (ABR_Periode)</xsl:text>
        </dcterms:temporal>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
