<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" 
 xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/" 
   xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/" 
   xmlns:gml="http://www.opengis.net/gml/3.2"
   xmlns:sikb="http://www.sikb.nl/sikb0102/2.1.0" 
   xmlns:p2e="java:nl.knaw.dans.platform.language.pakbon.Pakbon2EmdFunctions"
   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
   exclude-result-prefixes="sikb gml p2e">
  

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  
  <xsl:variable name="enum-doc" select="document('enumeraties.xsd')"/>
  
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
        <!-- 
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:titel">
         -->
        <!-- unique titles, ISSUE-704 -->
        <xsl:for-each select="sikb:document/sikb:titel[../sikb:documenttype='EINDRAP' and not(.=/sikb:sikb0102/sikb:project/sikb:projectnaam) and not(.=preceding::sikb:titel)]/ .">
          <dcterms:alternative>
            <xsl:value-of select="."/>
          </dcterms:alternative>
        </xsl:for-each>
        
        <!-- ISSUE-705, @bronId and sikb:serie is not considered as alternative title
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
        -->
      </emd:title>

      <!-- emd:creator  -->
      <emd:creator>
        <!-- get the unique auteur names of EINDRAP document -->
        <!-- Using depricated dc:creator, because sikb:auteur cannot be mapped to eas:creator without 'intelligent' string splitting -->
        <!--
        <xsl:for-each select="sikb:document/sikb:documenttype[text()='EINDRAP']/../sikb:auteur">
        -->
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
          <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='VerwervingValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation"></xsl:variable>
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
          <xsl:variable name="code" select="."/>
          <!-- issue EASY-747, don't map 'unknown' with code XXX --> 
          <xsl:if test="not($code='XXX')">
            <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='ComplextypeValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation"></xsl:variable>
            <xsl:choose>
              <xsl:when test="$waarde!=''">
                <dc:subject eas:scheme="ABR" eas:schemeId="archaeology.dc.subject">
                  <xsl:value-of select="$code"/>
                </dc:subject>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dc:subject"><xsl:value-of select="$code"/></xsl:element>
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
        <!-- date_submitted wordt door EASY toegevoegd -->
        <!--<eas:dateSubmitted eas:scheme="W3CDTF" eas:format="DAY"><xsl:value-of select="p2e:currentDateTime()"/></eas:dateSubmitted>-->

        <!-- date_available is standaard datum, te veranderen op moment van conversie-->
        <eas:available eas:scheme="W3CDTF" eas:format="DAY">
          <xsl:value-of select="p2e:currentDateTime()"/> 
        </eas:available>

        <!-- dcterms:issued voor het publicatie jaar van de rapport(en) -->
        <!-- NOT added, see issue EASY-711 -->
 
      </emd:date>

      <!-- emd:type -->
      <emd:type>
        <xsl:variable name="bestanden" select="sikb:bestand"/>
        <xsl:variable name="documenten" select="sikb:document/sikb:bestandId"/>
        <xsl:variable name="fotos" select="sikb:foto/sikb:bestandId"/>
        <xsl:variable name="tekeningen" select="sikb:tekening/sikb:bestandId"/>
        <!-- ISSUE-708 previous approach was: Just tick the box when a type is there
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
       -->
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
      <emd:relation>
      </emd:relation>

      <!-- emd:coverage -->
      <emd:coverage>

        <!-- spatial -->
        
        <xsl:for-each select="sikb:project/sikb:projectlocatieId">
          <xsl:variable name="locatieId" select="."/>
          
          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:provinciecode">
            <xsl:variable name="code" select="."/>
            <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='ProvincieValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation"></xsl:variable>
            <xsl:choose>
              <xsl:when test="$waarde!=''">
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$waarde"/></xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="dcterms:spatial"><xsl:value-of select="$code"/></xsl:element>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

          <xsl:for-each select="/sikb:sikb0102/sikb:projectlocatie[@sikb:id = $locatieId]/sikb:gemeentecode">
            <xsl:variable name="code" select="."/>
            <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='GemeenteValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation"></xsl:variable>
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
            <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='PlaatsValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation"></xsl:variable>
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
        <!-- projectlocatie  -->

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
          <eas:metadataformat>ARCHAEOLOGY</eas:metadataformat>
        </eas:application-specific>

        <!-- emd:etc -->
        <eas:etc>
          <property-list>
            <comment>Metadata conversion from archaeological exchange format (pakbon).</comment>
            <entry key="conversion.date">
              <xsl:value-of select="p2e:currentDateTime()"/>
            </entry>
          </property-list>
        </eas:etc>
      </emd:other>

    </emd:easymetadata>

  </xsl:template>


  <xsl:template name="dateringen">
    <xsl:param name="code"/>
    
    <xsl:variable name="waarde" select="$enum-doc/xsd:schema/xsd:simpleType[@name='PeriodeValueType']/xsd:restriction/xsd:enumeration[@value=$code]/@value"></xsl:variable>
    <xsl:variable name="documentation" select="$enum-doc/xsd:schema/xsd:simpleType[@name='PeriodeValueType']/xsd:restriction/xsd:enumeration[@value=$code]/xsd:annotation/xsd:documentation/text()"></xsl:variable>
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
      
      <!--  -->
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
