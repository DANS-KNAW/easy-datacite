#!/bin/sh

./add-datastream.sh dans-xsl:1 \
					emd-mods.xsl \
					"transformation:+emd+to+mods"\
					fedoraAdmin \
					$1 \
					localhost \
					8080			