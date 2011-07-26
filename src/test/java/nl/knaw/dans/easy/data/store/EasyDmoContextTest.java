package nl.knaw.dans.easy.data.store;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.easy.data.store.EasyDmoContext;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;

import org.junit.Test;

public class EasyDmoContextTest
{

	@Test
	public void testNamespaceByClass()
	{
		EasyDmoContext r = EasyDmoContext.getInstance();
		
		assertEquals(FileItem.NAMESPACE, r.getNamespaceByClass(FileItemImpl.class));
		assertEquals(FolderItem.NAMESPACE, r.getNamespaceByClass(FolderItemImpl.class));
		assertEquals(Dataset.NAMESPACE, r.getNamespaceByClass(DatasetImpl.class));
		assertEquals(DisciplineContainer.NAMESPACE, r.getNamespaceByClass(DisciplineContainerImpl.class));
	}
}
