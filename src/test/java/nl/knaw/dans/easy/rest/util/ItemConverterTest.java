package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

import org.junit.Before;
import org.junit.Test;

public class ItemConverterTest
{
    private List<ItemVO> items;

    @Before
    public void setUp()
    {
        items = new ArrayList<ItemVO>();
        items.add(setUpFile());
        items.add(setUpFolder());
    }

    private FileItemVO setUpFile()
    {
        FileItemVO file = mock(FileItemVO.class);
        when(file.getName()).thenReturn("filename");
        when(file.getSid()).thenReturn("easy-file:1");
        when(file.getPath()).thenReturn("/path/foo");
        when(file.getMimetype()).thenReturn("text/plain");
        return file;
    }

    private FolderItemVO setUpFolder()
    {
        FolderItemVO folder = mock(FolderItemVO.class);
        when(folder.getName()).thenReturn("foldername");
        when(folder.getSid()).thenReturn("easy-folder:1");
        when(folder.getPath()).thenReturn("/path/bar");
        return folder;
    }

    @Test(expected = AssertionError.class)
    public void notInstantiable()
    {
        new ItemConverter();
    }

    @Test
    public void conversion()
    {
        String xml = ItemConverter.convert(items);
        String expectedXml = "<list>" + "<file>" + "<name>filename</name>" + "<sid>easy-file:1</sid>" + "<path>/path/foo</path>"
                + "<mediatype>text/plain</mediatype>" + "</file>" + "<folder>" + "<name>foldername</name>" + "<sid>easy-folder:1</sid>"
                + "<path>/path/bar</path>" + "</folder>" + "</list>";
        assertEquals(expectedXml, xml);
    }

}
