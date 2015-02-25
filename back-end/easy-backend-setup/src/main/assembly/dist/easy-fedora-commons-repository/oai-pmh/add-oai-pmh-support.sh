#!/bin/sh

#
# Adds the files needed for OAI-PMH support.  Call with password of fedoraAdmin
#

fedora-ingest.sh f \
  "dans-xsl:1.xml" \
  "info:fedora/fedora-system:FOXML-1.1" \
  "localhost:8080" \
  fedoraAdmin \
  $1 \
  http


./add-datastream.sh dans-xsl:1 \
  emd-mods.xsl \
  "transformation:+emd+to+mods"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh easy-sdep:oai-item1 \
  EMD_carare.xsl \
  "Stylesheet+to+convert+from+EMD+to+CARARE"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh easy-sdep:oai-item1 \
  EMD_acdm.xsl \
  "Stylesheet+to+convert+from+EMD+to+acdm+ARIADNE"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh easy-sdep:oai-item1 \
  EMD_oai_dc.xsl \
  "Stylesheet+to+convert+from+EMD+to+OAI+DC"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh easy-sdep:oai-item1 \
  EMD_oai_datacite.xsl \
  "Stylesheet+to+convert+from+EMD+to+OAI+DATACITE"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh easy-sdep:oai-item1 \
  EMD_nl_didl.xsl \
  "Stylesheet+to+convert+from+EMD+to+NL+DIDL"\
  fedoraAdmin \
  $1 \
  localhost \
  8080

./add-datastream.sh dans-xsl:1 \
  emd-mods.xsl \
  "transformation:+emd+to+mods"\
  fedoraAdmin \
  $1 \
  localhost \
  8080
