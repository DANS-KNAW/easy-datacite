package nl.knaw.dans.easy.data.store;

import nl.knaw.dans.common.lang.repo.AbstractUnitOfWork;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoContext;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class EasyUnitOfWork extends AbstractUnitOfWork
{
	private static final long serialVersionUID = 1L;

	private EasyUser user; 
		
	public EasyUnitOfWork(EasyUser user)
	{
		super(user.isAnonymous()? null : user.getId());
		this.user = user;
	}
	
	@Override 
	public DmoStore getStore()
	{
		return Data.getEasyStore();
	}

	public static String createIngestMessage(String storeId, EasyUser user)
	{
		return "Ingested "+ storeId +" by "+ getUserName(user);
	}
	
	@Override
	protected String getIngestLogMessage(DataModelObject dmo)
	{
		return createIngestMessage(dmo.getStoreId(), user);
	}


	private static String getUserName(EasyUser user)
	{
		return user != null && !user.isAnonymous() ? 
				user.getDisplayName() +" ("+ user.getId() +")" : 
				"anonymous user";
	}

	public static String createPurgeMessage(String storeId, EasyUser user)
	{
		return "Purged "+ storeId +" by "+ getUserName(user);
	}

	@Override
	protected String getPurgeLogMessage(DataModelObject dmo)
	{
		return createPurgeMessage(dmo.getStoreId(), user);
	}

	public static String createUpdateMessage(String storeId, EasyUser user)
	{
		return "Update of "+ storeId +" by "+ getUserName(user);
	}

	@Override
	protected String getUpdateLogMessage(DataModelObject dmo)
	{
		return createUpdateMessage(dmo.getStoreId(), user);
	}


	public DmoContext getContext()
	{
		return EasyDmoContext.getInstance();
	}

}
