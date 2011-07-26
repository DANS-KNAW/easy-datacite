package nl.knaw.dans.easy.domain.model.disciplinecollection;

import java.net.URI;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

public class DisciplineMetadataImpl extends AbstractTimestampedJiBXObject<EasyMetadata> implements DisciplineMetadata
{
 	private static final long serialVersionUID = 7370147776626533158L;
    
    private int order = -1;
    
    private String OICode = "";
    
    private String Easy1BranchID;
 
	public String getUnitFormat()
	{
		return UNIT_FORMAT;
	}

	public URI getUnitFormatURI()
	{
		return UNIT_FORMAT_URI;
	}

	public String getUnitId()
	{
		return UNIT_ID;
	}

	public String getUnitLabel()
	{
		return UNIT_LABEL;
	}

	public boolean isVersionable()
	{
		return false;
	}

	public void setVersionable(boolean versionable)
	{
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata#setOICode(java.lang.String)
	 */
	public void setOICode(String oICode)
	{
		OICode = oICode;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata#getOICode()
	 */
	public String getOICode()
	{
		return OICode;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata#setOrder(int)
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata#getOrder()
	 */
	public int getOrder()
	{
		return order;
	}

	public void setEasy1BranchID(String easy1BranchID)
	{
		Easy1BranchID = easy1BranchID;
	}

	public String getEasy1BranchID()
	{
		return Easy1BranchID;
	}

}
