the following emd files are in this resources-dir:

* emd.xml
* emd_62559.xml
* emd_66365.xml
* incomplete-relations-emd.xml
* maxi-emd.xml

and one xsl file:
* empty.xsl


**purpose of the test resources:**

*emd.xml*
used in DataciteResourceBuilderTest to test snippets of the conversion to datacite

*emd_62559.xml*
actual emd for easy-dataset:62559. only the PID and DOI have been changed.
used in DataciteXmlValidatorTest to validate an entire converted emd file against the datacite-xsd.
this conversion failed previously on the geoLocationPoint

*emd_66365.xml*
actual emd for easy-dataset:66365. only the PID and DOI have been changed.
used in DataciteXmlValidatorTest to validate an entire converted emd file against the datacite-xsd.
this conversion failed previously on the geoLocationBox

*incomplete-relations-emd.xml*
used in DataciteServiceTest.
relations that are not specified with a relation-type can not be mapped to datacite, since a relationType is required there.
the following relationtypes are mapped:

*maxi-emd.xml*
This emd file SHOULD contain all possible data-elements. is not yet the case.

*empty.xsl*
An empty xsl file to test exceptions thrown when data-elements are missing from the emd-xml.


**The relation-types that are available in EMD and the mapping to the datacite equivalents:**

no known mapping available
*    relation 
*    conformsTo

done:
*    isVersionOf = IsDerivedFrom
*    isReplacedBy = isPreviousVersionOf
*    replaces = IsNewVersionOf
*    isPartOf = IsPartOf
*    hasPart = HasPart
*    isReferencedBy = IsReferencedBy

TODO:
*    isRequiredBy = IsSupplementTo
*    requires = IsSupplementedBy
*    hasVersion = IsSourceOf
*    references = References
*    isFormatOf = IsVariantFormOf
*    hasFormat = IsVariantFormOf
