package nl.knaw.dans.easy.domain.model.disciplinecollection;

import java.net.URI;

import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;

public interface DisciplineMetadata extends MetadataUnitXMLBean
{
	String UNIT_ID = "DMD";
    
    String UNIT_LABEL = "Discipline metadata";
    
    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/disciplinemetadata/";
    
    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);
	
	void setOICode(String oICode);

	String getOICode();

	void setOrder(int order);

	int getOrder();

	void setEasy1BranchID(String easy1BranchID);

	String getEasy1BranchID();

}
