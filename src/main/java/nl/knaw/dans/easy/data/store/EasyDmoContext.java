package nl.knaw.dans.easy.data.store;

import nl.knaw.dans.common.lang.dataset.CommonDataset;
import nl.knaw.dans.common.lang.repo.AbstractDmoContext;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;

public class EasyDmoContext extends AbstractDmoContext
{
	private static final long serialVersionUID = -1588199644281978032L;

	private static final String EASY_NAMESPACE_SID = "easy-namespace:1";

	private static final EasyDmoContext INSTANCE = new EasyDmoContext();
	
	public EasyDmoContext()
	{
		super(EASY_NAMESPACE_SID);
		
		addToRegistry(Dataset.NAMESPACE, DatasetImpl.class);
		addToRegistry(FolderItem.NAMESPACE, FolderItemImpl.class);
		addToRegistry(FileItem.NAMESPACE, FileItemImpl.class);
		addToRegistry(DisciplineContainer.NAMESPACE, DisciplineContainerImpl.class);
		addToRegistry(DownloadHistory.NAMESPACE, DownloadHistory.class);
		addToRegistry(JumpoffDmo.NAMESPACE, JumpoffDmo.class);
		addToRegistry("dccd", CommonDataset.class);
	}
	
	public static EasyDmoContext getInstance()
	{
		return INSTANCE;		
	}
	
	@Override
	public String getStoreId()
	{
		return EASY_NAMESPACE_SID;
	}
	
	public String getObjectNamespace()
	{
		return "easy-namespace";
	}

	public boolean isDeletable()
	{
		return false;
	}

}
