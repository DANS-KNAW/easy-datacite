<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:car="http://www.carare.eu/carareSchema" 
  xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/" 
  xmlns:eas="http://easy.dans.knaw.nl/easy/easymetadata/eas/"
  exclude-result-prefixes="emd dc dcterms eas car">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <!-- alleen archeologische datasets worden aan Carare aangeboden, -->
    <!-- alleen datsets met TEXT documenten (eerste dc:type _DCMI) worden aan Carare aangeboden, -->
    <!-- alleen datasets met Open Access for registered users (dcterms:accessRights) worden aan Carare aangeboden -->
    <!-- en alleen met coordinaten (eas:point of eas:box) worden aan Carare aangeboden -->
    <xsl:variable name="archaeological_aip" select="emd:easymetadata/emd:other/eas:application-specific/eas:metadataformat[.='ARCHAEOLOGY']"/>
    <xsl:variable name="text_aip" select="emd:easymetadata/emd:type/dc:type[.='Text' and @eas:scheme='DCMI']"/>
    <xsl:variable name="open_toegang" select="emd:easymetadata/emd:rights/dcterms:accessRights[.='OPEN_ACCESS']"/>
    <xsl:variable name="pointX" select="emd:easymetadata/emd:coverage/eas:spatial/eas:point/eas:x != ''"/>
    <xsl:variable name="boxX" select="emd:easymetadata/emd:coverage/eas:spatial/eas:box/eas:west != ''"/>

    <xsl:if test="$archaeological_aip and $text_aip and $open_toegang and ($pointX or $boxX)">
      <xsl:apply-templates select="emd:easymetadata"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="emd:easymetadata">
    
    <xsl:element name="car:carareWrap">            
      <xsl:attribute name="xsi:schemaLocation" select="'http://www.carare.eu/carareSchema http://www.carare.eu/carareSchema'"/>

      <xsl:variable name="pid"
        select="emd:identifier/dc:identifier[@eas:scheme='PID' or @eas:identification-system='http://www.persistent-identifier.nl']"/>
      <car:carare>
        <xsl:attribute name="id">
          <xsl:value-of select="$pid"/>
        </xsl:attribute>
        <car:collectionInformation>
          <car:title lang="en">e-archive Dutch Archaeology (DANS-EDNA)</car:title>
          <car:title lang="nl" preferred="true">e-depot Nederlandse archeologie (DANS-EDNA)</car:title>
          <car:keywords lang="en">data archive; datasets; publications; archaeological research; Archaeology; the
            Netherlands</car:keywords>
          <car:contacts>
            <car:name>Drs. Hella Hollander</car:name>
            <car:role lang="en">data archivist archaeology</car:role>
            <car:organization>Data Archiving and Networked Services (DANS)</car:organization>
            <car:address>Anna van Saksenlaan 10, 2593 HT The Hague, the Netherlands</car:address>
            <car:phone>+31 70 3446484 </car:phone>
            <car:fax>+31 70 3446482</car:fax>
            <car:email>hella.hollander@dans.knaw.nl</car:email>
            <car:email>info@dans.knaw.nl</car:email>
          </car:contacts>
          <car:rights>
            <car:reproductionRights>
              <car:statement lang="en">allowed for research and educational use only</car:statement>
              <car:statement lang="en">for personal reuse only, reproduction or redistribution in any form is not
                allowed, no commercial use allowed</car:statement>
              <car:statement lang="en">attribution compulsory</car:statement>
            </car:reproductionRights>
            <car:licence>http://www.dans.knaw.nl/en/content/data-archive/terms-and-conditions</car:licence>
          </car:rights>
          <car:source>DANS-KNAW</car:source>
          <car:language>nl</car:language>
          <car:coverage>
            <car:spatial>
              <car:locationSet>
                <car:geopoliticalArea>
                  <car:geopoliticalAreaName lang="en">the Netherlands</car:geopoliticalAreaName>
                  <car:geopoliticalAreaType lang="en">country</car:geopoliticalAreaType>
                </car:geopoliticalArea>
              </car:locationSet>
            </car:spatial>
          </car:coverage>
        </car:collectionInformation>


        <!-- metadata_taal variable, default nl, since currently all datasets with RD coordinates have language nl -->
        <xsl:variable name="dataset_taal" select="emd:language/dc:language[@eas:scheme='ISO 639']"/>
        <xsl:variable name="meta_taal">
          <xsl:choose>
            <xsl:when test="$dataset_taal='dut/nld'">nl</xsl:when>
            <xsl:when test="$dataset_taal='eng'">en</xsl:when>
            <xsl:when test="$dataset_taal='fre/fra'">fr</xsl:when>
            <xsl:when test="$dataset_taal='ger/deu'">de</xsl:when>
            <xsl:otherwise>nl</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <car:digitalResource>

          <car:recordInformation>
            <car:id>
              <xsl:value-of select="$pid"/>
            </car:id>
            <car:source>DANS-KNAW</car:source>
            <car:country>NLD</car:country>
            <car:creation>
              <car:date>
                <xsl:value-of select="substring(emd:date/eas:dateSubmitted[1],1,10)"/>
              </car:date>
              <car:actor>
                <car:name lang="en">dataset depositor (locally known)</car:name>
                <car:actorType lang="en">individual</car:actorType>
                <car:roles lang="en">depositor</car:roles>
              </car:actor>
            </car:creation>
            <car:language>
              <xsl:value-of select="$meta_taal"/>
            </car:language>
            <car:rights>
              <car:accessRights>
                <car:grantedTo lang="en">everyone</car:grantedTo>
                <car:statement lang="en">metadata of the archived dataset is freely available to everyone (open
                    access)</car:statement>
              </car:accessRights>
            </car:rights>
          </car:recordInformation>

          <car:appellation>
            <!-- dataset title(s) -->
            <xsl:for-each select="emd:title/dc:title">
              <car:name>
                <xsl:attribute name="lang"><xsl:value-of select="$meta_taal"/></xsl:attribute>
                <xsl:value-of select="."/>
              </car:name>
            </xsl:for-each>
            <xsl:for-each select="emd:title/dcterms:alternative">
              <car:name>
                <xsl:attribute name="lang"><xsl:value-of select="$meta_taal"/></xsl:attribute>
                <xsl:value-of select="."/>
              </car:name>
            </xsl:for-each>
            <car:id>
              <xsl:value-of select="$pid"/>
            </car:id>
          </car:appellation>

          <!-- actors -->
          <!-- dc:creator -->
          <xsl:for-each select="emd:creator/dc:creator">
            <car:actors>
              <car:name>
                <xsl:value-of select="."/>
              </car:name>
              <car:actorType lang="en">individual</car:actorType>
              <car:roles lang="en">creator</car:roles>
            </car:actors>
          </xsl:for-each>
          
          <!-- eas:creator ============================== -->
          <xsl:for-each select="emd:creator/eas:creator">
            <xsl:element name="car:actors">
              <xsl:call-template name="nameAndActorType"/>
              <car:roles lang="en">creator</car:roles>
            </xsl:element>
          </xsl:for-each>
          
          <!-- dc:contributor -->
          <xsl:for-each select="emd:contributor/dc:contributor">
            <car:actors>
              <car:name>
                <xsl:value-of select="."/>
              </car:name>
              <car:actorType lang="en">individual</car:actorType>
              <car:roles lang="en">contributor</car:roles>
            </car:actors>
          </xsl:for-each>
          
          <!-- eas:contributor ========================== -->
          <xsl:for-each select="emd:contributor/eas:contributor">
            <xsl:element name="car:actors">
              <xsl:call-template name="nameAndActorType"/>
              <car:roles lang="en">contributor</car:roles>
            </xsl:element>
          </xsl:for-each>

          <!-- dc:format, IMT, multiple -->
          <car:format lang="en">text</car:format>
          <xsl:for-each select="emd:format/dc:format[@eas:scheme='IMT']">
            <car:format>
              <xsl:value-of select="."/>
            </car:format>
          </xsl:for-each>


          <!-- medium and extent -->
          <car:medium lang="en">webresource</car:medium>
          <car:extent lang="en">one or more digital files</car:extent>

          <!-- coverage spatial van de dataset vertaald naar  spatial van de digitalResource -->
          <car:spatial>
            <car:locationSet>
              <xsl:for-each select="emd:coverage/dcterms:spatial">
                <car:namedLocation>
                  <xsl:value-of select="."/>
                </car:namedLocation>
              </xsl:for-each>

              <car:geopoliticalArea>
                <car:geopoliticalAreaName lang="en">the Netherlands</car:geopoliticalAreaName>
                <car:geopoliticalAreaType lang="en">country</car:geopoliticalAreaType>
              </car:geopoliticalArea>

            </car:locationSet>

            <!-- spatial location - first documented point (preferred) or box -->
            <xsl:apply-templates select="emd:coverage"/>
          </car:spatial>

          <car:subject>archeologie</car:subject>
          <!-- dc:subject  -->
          <xsl:for-each select="emd:subject/dc:subject">
            <xsl:variable name="abrsubject" select="@eas:scheme"/>
            <xsl:if test="not ($abrsubject) or $abrsubject != 'ABR'">
              <car:subject>
                <xsl:value-of select="."/>
              </car:subject>
            </xsl:if>
          </xsl:for-each>

          <!-- dc:subject scheme=ABR_Complex naar English Heritage  top level Monument type-->
          <!-- vereenvoudigd tot één hoofdniveau vermelding -->
          <xsl:variable name="ind" select="emd:subject/dc:subject[@eas:scheme='ABR' and (substring(.,1,2)='EI' or substring(.,1,2)='EG')]"/>
          <xsl:if test="$ind">
            <car:subject>INDUSTRIAL</car:subject>
          </xsl:if>
          <xsl:variable name="aas" select="emd:subject/dc:subject[@eas:scheme='ABR' and (substring(.,1,2)='EX' or substring(.,1,2)='EL')]"/>
          <xsl:if test="$aas">
            <car:subject>AGRICULTURE_AND_SUBSISTENCE</car:subject>
          </xsl:if>
          <xsl:variable name="def" select="emd:subject/dc:subject[@eas:scheme='ABR' and (substring(.,1,1)='V')]"/>
          <xsl:if test="$def">
            <car:subject>DEFENCE</car:subject>
          </xsl:if>
          <xsl:variable name="dom" select="emd:subject/dc:subject[@eas:scheme='ABR' and (substring(.,1,1)='N')]"/>
          <xsl:if test="$dom">
            <car:subject>DOMESTIC</car:subject>
          </xsl:if>
          <xsl:variable name="rel" select="emd:subject/dc:subject[@eas:scheme='ABR' and (.='DEPO' or substring(.,1,1)='G' or substring(.,1,1)='R')]"/>
          <xsl:if test="$rel">
            <car:subject>RELIGIOUS_RITUAL_AND_FUNERARY</car:subject>
          </xsl:if>
          <xsl:variable name="mar" select="emd:subject/dc:subject[@eas:scheme='ABR' and (.='EVX' or .='ESCH')]"/>
          <xsl:if test="$mar">
            <car:subject>MARITIME</car:subject>
          </xsl:if>
          <xsl:variable name="mbf" select="emd:subject/dc:subject[@eas:scheme='ABR' and (.='GMEG' or substring(.,1,2)='RK')]"/>
          <xsl:if test="$mbf">
            <car:subject>MONUMENT_BY_FORM</car:subject>
          </xsl:if>
          <xsl:variable name="trans" select="emd:subject/dc:subject[@eas:scheme='ABR' and (substring(.,1,1)='I')]"/>
          <xsl:if test="$trans">
            <car:subject>TRANSPORT</car:subject>
          </xsl:if>
          <xsl:variable name="unk" select="emd:subject/dc:subject[@eas:scheme='ABR' and (.='XXX')]"/>
          <xsl:if test="$unk">
            <car:subject>UNASSIGNED</car:subject>
          </xsl:if>

          <!-- dcterms:temporal  -->
          <car:temporal>
            <!-- dcterms:temporal scheme=ABR Periode mapped to one unique periodName-->
            <xsl:variable name="xxx" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (.='XXX')]"/>
            <xsl:if test="$xxx">
              <car:periodName lang="en">UNCERTAIN</car:periodName>
            </xsl:if>
            <xsl:variable name="paleolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,5)='PALEO')]"/>
            <xsl:if test="$paleolithicum">
              <car:periodName lang="en">PALAEOLITHIC</car:periodName>
            </xsl:if>
            <xsl:variable name="mesolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,4)='MESO')]"/>
            <xsl:if test="$mesolithicum">
              <car:periodName lang="en">MESOLITHIC</car:periodName>
            </xsl:if>
            <xsl:variable name="neolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='NEO')]"/>
            <xsl:if test="$neolithicum">
              <car:periodName lang="en">NEOLITHIC</car:periodName>
            </xsl:if>
            <xsl:variable name="bronstijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,5)='BRONS')]"/>
            <xsl:if test="$bronstijd">
              <car:periodName lang="en">BRONZE_AGE</car:periodName>
            </xsl:if>
            <xsl:variable name="ijzertijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='IJZ')]"/>
            <xsl:if test="$ijzertijd">
              <car:periodName lang="en">IRON_AGE</car:periodName>
            </xsl:if>
            <xsl:variable name="romeins" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='ROM')]"/>
            <xsl:if test="$romeins">
              <car:periodName lang="en">ROMAN</car:periodName>
            </xsl:if>
            <xsl:variable name="me" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,2,2)='ME')]"/>
            <xsl:if test="$me">
              <car:periodName lang="en">MEDIEVAL</car:periodName>
            </xsl:if>
            <xsl:variable name="nieuwetijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,2)='NT')]"/>
            <xsl:if test="$nieuwetijd">
              <car:periodName lang="en">MODERN</car:periodName>
            </xsl:if>

            <xsl:for-each select="emd:coverage/dcterms:temporal">
              <xsl:variable name="abrtemporal" select="@eas:scheme"/>
              <xsl:if test="not ($abrtemporal) or $abrtemporal != 'ABR'">
                <car:displayDate lang="nl">
                  <xsl:value-of select="."/>
                </car:displayDate>
              </xsl:if>
            </xsl:for-each>

            <!-- dcterms:temporal scheme=ABR Periode mapped to one unique timeSpan-->
            <xsl:variable name="t_paleolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,5)='PALEO')]"/>
            <xsl:if test="$t_paleolithicum">
              <car:displayDate lang="en">Paleolithic (before 8800 BP)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_mesolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,4)='MESO')]"/>
            <xsl:if test="$t_mesolithicum">
              <car:displayDate lang="en">Mesolithic (8800 BP - 4900 BP)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_neolithicum" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='NEO')]"/>
            <xsl:if test="$t_neolithicum">
              <car:displayDate lang="en">Neolithic (4900 BP - 2000 BP)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_bronstijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,5)='BRONS')]"/>
            <xsl:if test="$t_bronstijd">
              <car:displayDate lang="en">Bronze Age (2000 BP - 800 BP)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_ijzertijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='IJZ')]"/>
            <xsl:if test="$t_ijzertijd">
              <car:displayDate lang="en">Iron Age (800 BP - 12 BP)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_romeins" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,3)='ROM')]"/>
            <xsl:if test="$t_romeins">
              <car:displayDate lang="en">Roman Era (12 BP - 450)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_me" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,2,2)='ME')]"/>
            <xsl:if test="$t_me">
              <car:displayDate lang="en">Middle Ages (450 - 1500)</car:displayDate>
            </xsl:if>
            <xsl:variable name="t_nieuwetijd" select="emd:coverage/dcterms:temporal[@eas:scheme='ABR' and (substring(.,1,2)='NT')]"/>
            <xsl:if test="$t_nieuwetijd">
              <car:displayDate lang="en">Modern Era (after 1500)</car:displayDate>
            </xsl:if>

          </car:temporal>

          <!-- publication statements -->
          <car:publicationStatement>
            <xsl:for-each select="emd:publisher/dc:publisher">
              <car:publisher>
                <xsl:value-of select="."/>
              </car:publisher>
            </xsl:for-each>
            <xsl:if test="not (emd:publisher/dc:publisher)">
              <xsl:for-each select="emd:rights/dc:rights">
                <car:publisher>
                  <xsl:value-of select="."/>
                </car:publisher>
              </xsl:for-each>
            </xsl:if>
            <!-- dc:date_created digits 1-4 -->
            <car:date>
              <xsl:variable name="jaar" select="emd:date/eas:created"/>
              <xsl:value-of select="substring($jaar,1,10)"/>
            </car:date>
          </car:publicationStatement>

          <!-- alleen dc:type DCMI, multiple types not allowed -->
          <xsl:variable name="Ntypes" select="count(emd:type/dc:type[@eas:scheme='DCMI'])"/>
          <xsl:choose>
            <xsl:when test="$Ntypes>0">
              <car:type namespace="http://purl.org/dc/dcmitype/">
                <!-- Note that the legacy DCMIType in Easy has false entries like
                    'Interactive_Resource' instead of the proper 'InteractiveResource' -->
                <xsl:for-each select="emd:type/dc:type[@eas:scheme='DCMI']">
                  <xsl:value-of select="."/>
                  <xsl:if test="position() != $Ntypes">
                    <xsl:text> </xsl:text>
                  </xsl:if>
                </xsl:for-each>
              </car:type>
            </xsl:when>
            <xsl:otherwise>
              <car:type namespace="http://purl.org/dc/dcmitype/">Dataset</car:type>
            </xsl:otherwise>
          </xsl:choose>

          <!-- dc:description -->
          <xsl:variable name="Ndescriptions" select="count(emd:description/dc:description)"/>
          <car:description>
            <xsl:attribute name="lang"><xsl:value-of select="$meta_taal"/></xsl:attribute>
            <xsl:for-each select="emd:description/dc:description">
              <xsl:value-of select="."/>
              <xsl:if test="position() != $Ndescriptions">
                <xsl:text>&#13;</xsl:text>
              </xsl:if>
            </xsl:for-each>
          </car:description>

          <!-- dcterms:created -->
          <car:created>
            <xsl:variable name="creatiedatum" select="emd:date/eas:created"/>
            <xsl:variable name="datumvorm" select="emd:date/eas:created/@eas:format"/>
            <xsl:choose>
              <xsl:when test="$datumvorm='DAY'">
                <xsl:value-of select="substring($creatiedatum,1,10)"/>
              </xsl:when>
              <xsl:when test="$datumvorm='MONTH'">
                <xsl:value-of select="substring($creatiedatum,1,7)"/>
              </xsl:when>
              <xsl:when test="$datumvorm='YEAR'">
                <xsl:value-of select="substring($creatiedatum,1,4)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$creatiedatum"/>
              </xsl:otherwise>
            </xsl:choose>
          </car:created>

          <!-- dc:language -->
          <xsl:for-each select="emd:language/dc:language[@eas:scheme='ISO 639']">
            <xsl:choose>
              <xsl:when test=".='dut/nld'">
                <car:language>nl</car:language>
              </xsl:when>
              <xsl:when test=".='eng'">
                <car:language>en</car:language>
              </xsl:when>
              <xsl:when test=".='fre/fra'">
                <car:language>fr</car:language>
              </xsl:when>
              <xsl:when test=".='ger/deu'">
                <car:language>de</car:language>
              </xsl:when>
            </xsl:choose>
          </xsl:for-each>
          <xsl:if test="not (emd:language/dc:language[@eas:scheme='ISO 639'])">
            <car:language>nl</car:language>
          </xsl:if>

          <car:link>
            <xsl:value-of
              select="emd:identifier/dc:identifier[@eas:scheme='PID' or @eas:identification-system='http://www.persistent-identifier.nl']/@eas:identification-system"/>
            <xsl:text>/?identifier=</xsl:text>
            <xsl:value-of select="emd:identifier/dc:identifier[@eas:scheme='PID' or @eas:identification-system='http://www.persistent-identifier.nl']"/>
          </car:link>

          <xsl:for-each select="emd:identifier/dc:identifier[@eas:identification-system='http://archis2.archis.nl']">
          <car:relations>
              <car:sourceOfRelation>Archis - Research Notification</car:sourceOfRelation>
              <car:typeOfRelation>hasEvent</car:typeOfRelation>
              <car:targetOfRelation>
                <xsl:value-of select="."/>
              </car:targetOfRelation>
          </car:relations>
          </xsl:for-each>

          <car:rights>
            <car:copyright>
              <xsl:for-each select="emd:rights/dc:rights">
                <car:rightsHolder>
                  <xsl:value-of select="."/>
                </car:rightsHolder>
              </xsl:for-each>
              <xsl:if test="not (emd:rights/dc:rights)">
                <xsl:for-each select="emd:publisher/dc:publisher">
                  <car:rightsHolder>
                    <xsl:value-of select="."/>
                  </car:rightsHolder>
                </xsl:for-each>
              </xsl:if>
            </car:copyright>

            <car:accessRights>
              <xsl:variable name="accessrights"
                select="emd:rights/dcterms:accessRights[@eas:schemeId='common.dcterms.accessrights' or @eas:schemeId='archaeology.dcterms.accessrights']"/>
              <car:grantedTo lang="en">
                <xsl:choose>
                  <xsl:when test="$accessrights='OPEN_ACCESS_FOR_REGISTERED_USERS'">Registered EASY users</xsl:when>
                  <xsl:when test="$accessrights='GROUP_ACCESS'"
                    >Registered EASY users, but
                      belonging to the group of professional Dutch archaeologists only</xsl:when>
                  <xsl:when test="$accessrights='REQUEST_PERMISSION'"
                    >Registered EASY users, but
                      after permission is granted by the depositor </xsl:when>
                  <xsl:when test="$accessrights='NO_ACCESS'"
                    >Registered EASY users, permission is
                      granted occasionally after special mediation</xsl:when>
                </xsl:choose>
              </car:grantedTo>

              <car:conditions lang="en">Allowed for research and educational use, no commercial use allowed,
                  attribution compulsory</car:conditions>

              <!-- dcterms:date_available -->
              <car:dateFrom>
                <xsl:value-of select="substring(emd:date/eas:available,1,10)"/>
              </car:dateFrom>
            </car:accessRights>

            <car:reproductionRights>
              <car:statement lang="en">for personal use only, reproduction or redistribution in any form is not
                  allowed, no commercial use is allowed</car:statement>
            </car:reproductionRights>

          </car:rights>

        </car:digitalResource>

      </car:carare>

      </xsl:element>
  </xsl:template>
  
  <xsl:template name="nameAndActorType">
    <!-- displayForm -->
    <xsl:variable name="titles">
      <xsl:choose>
        <xsl:when test="eas:title = ''">
          <xsl:value-of select="''"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat(' ', eas:title)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--  -->
    <xsl:variable name="initials">
      <xsl:choose>
        <xsl:when test="eas:initials = ''">
          <xsl:value-of select="''"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat(' ', eas:initials)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--  -->
    <xsl:variable name="prefix">
      <xsl:choose>
        <xsl:when test="eas:prefix=''">
          <xsl:value-of select="''"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat(' ', eas:prefix)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--  -->
    <xsl:variable name="organization">
      <xsl:choose>
        <xsl:when test="eas:organization = ''">
          <xsl:value-of select="''"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat(' (', eas:organization, ')')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--  -->
    <xsl:choose>
      <xsl:when test="eas:surname = ''">
        <xsl:element name="car:name">
          <xsl:value-of select="eas:organization"/>
        </xsl:element>
        <xsl:element name="car:actorType">
          <xsl:attribute name="lang" select="'en'"/>
          <xsl:value-of select="'organization'"/>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="car:name">
          <xsl:value-of select="concat(eas:surname, ',', $titles, $initials, $prefix, $organization)"/>
        </xsl:element>
        <xsl:element name="car:actorType">
          <xsl:attribute name="lang" select="'en'"/>
          <xsl:value-of select="'individual'"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="setPoint">
    <xsl:param name="x"/>
    <xsl:param name="y"/>
    <car:spatialReferenceSystem>WGS84</car:spatialReferenceSystem>
    <car:geometry>
      <xsl:variable name="dx" select="(-155000.00+$x) div 100000"/>
      <xsl:variable name="dy" select="(-463000.00+$y) div 100000"/>
      <!-- df en dl -->
      <xsl:variable name="df"
        select="(($dy*3235.65389)+($dx*$dx*-32.58297)+($dy*$dy*-0.24750)+($dx*$dx*$dy*-0.84978)+($dy*$dy*$dy*-0.06550)+($dx*$dx*$dy*$dy*-0.01709)+($dx*-0.00738)) div 3600"/>
      <xsl:variable name="dl"
        select="(($dx*5260.52916)+($dx*$dy*105.94684)+($dx*$dy*$dy*2.45656)+($dx*$dx*$dx*-0.81885)+($dx*$dy*$dy*$dy*0.05594)+($dx*$dx*$dx*$dy*-0.05606)) div 3600"/>
      <xsl:variable name="f" select="52.15517440+$df"/>
      <xsl:variable name="l" select="5.38720621+$dl"/>
      
      <!-- Spatial quickpoint in WGS 84-->
      <car:quickpoint>
        <car:x>
          <xsl:value-of select="$l"/>
        </car:x>
        <car:y>
          <xsl:value-of select="$f"/>
        </car:y>
      </car:quickpoint>
      <car:storedPrecision>10</car:storedPrecision>
    </car:geometry>
    <car:representations>point</car:representations>
  </xsl:template>
  
  <xsl:template match="emd:coverage">
    <xsl:choose>
      <xsl:when test="eas:spatial/eas:point[@eas:scheme = 'RD']">
        <xsl:choose>
          <xsl:when test="eas:spatial[1]/eas:point[@eas:scheme = 'RD']">
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="eas:spatial[1]/eas:point[@eas:scheme = 'RD']/eas:x"/>
              <xsl:with-param name="y" select="eas:spatial[1]/eas:point[@eas:scheme = 'RD']/eas:y"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[2]/eas:point[@eas:scheme = 'RD']">
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="eas:spatial[2]/eas:point[@eas:scheme = 'RD']/eas:x"/>
              <xsl:with-param name="y" select="eas:spatial[2]/eas:point[@eas:scheme = 'RD']/eas:y"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[3]/eas:point[@eas:scheme = 'RD']">
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="eas:spatial[3]/eas:point[@eas:scheme = 'RD']/eas:x"/>
              <xsl:with-param name="y" select="eas:spatial[3]/eas:point[@eas:scheme = 'RD']/eas:y"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="eas:spatial[1]/eas:box[@eas:scheme = 'RD']">
            <xsl:variable name="n" select="number(eas:spatial[1]/eas:box[@eas:scheme = 'RD']/eas:north)"/>
            <xsl:variable name="e" select="number(eas:spatial[1]/eas:box[@eas:scheme = 'RD']/eas:east)"/>
            <xsl:variable name="s" select="number(eas:spatial[1]/eas:box[@eas:scheme = 'RD']/eas:south)"/>
            <xsl:variable name="w" select="number(eas:spatial[1]/eas:box[@eas:scheme = 'RD']/eas:west)"/>
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="round(($w+$e) div 2)"/>
              <xsl:with-param name="y" select="round(($n+$s) div 2)"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[2]/eas:box[@eas:scheme = 'RD']">
            <xsl:variable name="n" select="number(eas:spatial[2]/eas:box[@eas:scheme = 'RD']/eas:north)"/>
            <xsl:variable name="e" select="number(eas:spatial[2]/eas:box[@eas:scheme = 'RD']/eas:east)"/>
            <xsl:variable name="s" select="number(eas:spatial[2]/eas:box[@eas:scheme = 'RD']/eas:south)"/>
            <xsl:variable name="w" select="number(eas:spatial[2]/eas:box[@eas:scheme = 'RD']/eas:west)"/>
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="round(($w+$e) div 2)"/>
              <xsl:with-param name="y" select="round(($n+$s) div 2)"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[3]/eas:box[@eas:scheme = 'RD']">
            <xsl:variable name="n" select="number(eas:spatial[3]/eas:box[@eas:scheme = 'RD']/eas:north)"/>
            <xsl:variable name="e" select="number(eas:spatial[3]/eas:box[@eas:scheme = 'RD']/eas:east)"/>
            <xsl:variable name="s" select="number(eas:spatial[3]/eas:box[@eas:scheme = 'RD']/eas:south)"/>
            <xsl:variable name="w" select="number(eas:spatial[3]/eas:box[@eas:scheme = 'RD']/eas:west)"/>
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="round(($w+$e) div 2)"/>
              <xsl:with-param name="y" select="round(($n+$s) div 2)"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[4]/eas:box[@eas:scheme = 'RD']">
            <xsl:variable name="n" select="number(eas:spatial[4]/eas:box[@eas:scheme = 'RD']/eas:north)"/>
            <xsl:variable name="e" select="number(eas:spatial[4]/eas:box[@eas:scheme = 'RD']/eas:east)"/>
            <xsl:variable name="s" select="number(eas:spatial[4]/eas:box[@eas:scheme = 'RD']/eas:south)"/>
            <xsl:variable name="w" select="number(eas:spatial[4]/eas:box[@eas:scheme = 'RD']/eas:west)"/>
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="round(($w+$e) div 2)"/>
              <xsl:with-param name="y" select="round(($n+$s) div 2)"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="eas:spatial[5]/eas:box[@eas:scheme = 'RD']">
            <xsl:variable name="n" select="number(eas:spatial[5]/eas:box[@eas:scheme = 'RD']/eas:north)"/>
            <xsl:variable name="e" select="number(eas:spatial[5]/eas:box[@eas:scheme = 'RD']/eas:east)"/>
            <xsl:variable name="s" select="number(eas:spatial[5]/eas:box[@eas:scheme = 'RD']/eas:south)"/>
            <xsl:variable name="w" select="number(eas:spatial[5]/eas:box[@eas:scheme = 'RD']/eas:west)"/>
            <xsl:call-template name="setPoint">
              <xsl:with-param name="x" select="round(($w+$e) div 2)"/>
              <xsl:with-param name="y" select="round(($n+$s) div 2)"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>    
  </xsl:template>
</xsl:stylesheet>
