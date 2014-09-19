package nl.knaw.dans.common.lang.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.util.FileUtil;

/**
 * Tests unzipping class by unzipping files and comparing them with preunzipped directories
 * 
 * @author lobo
 */
public class UnzipTest {

    // private static final Logger logger = LoggerFactory.getLogger(UnzipTest.class);

    private boolean filesEqual(File file1, File file2) {
        if (file1.isDirectory() || file2.isDirectory())
            return false;
        if (file1.length() != file2.length())
            return false;

        InputStream in1 = null;
        InputStream in2 = null;
        boolean result = true;
        try {
            in1 = new BufferedInputStream(new FileInputStream(file1));
            in2 = new BufferedInputStream(new FileInputStream(file2));

            byte[] buf1 = new byte[1024];
            byte[] buf2 = new byte[1024];
            while (in1.read(buf1) > 0 && (in2.read(buf2) > 0)) {
                if (!Arrays.equals(buf1, buf2)) {
                    result = false;
                    break;
                }
            }
        }
        catch (IOException e) {
            result = false;
        }
        finally {
            try {
                if (in1 != null)
                    in1.close();
                if (in2 != null)
                    in2.close();
            }
            catch (IOException e) {}
        }

        return result;
    }

    private int countFiles(File path) {
        File[] files = path.listFiles(new TestFilter());
        return files.length;
    }

    // private void listFiles(File path)
    // {
    // File[] files = path.listFiles(new FilenameFilter()
    // {
    //
    // public boolean accept(File dir, String name)
    // {
    // return !name.equals(".svn") && !name.equals(".DS_Store");
    // }
    //
    // });
    // System.out.println(path.getAbsolutePath());
    // for (File file : files)
    // {
    // System.out.println(file.getName());
    // }
    // }

    private boolean directoriesEqual(File dir1, File dir2) {
        assertTrue(dir1.isDirectory());
        assertTrue(dir2.isDirectory());

        assertEquals(countFiles(dir1), countFiles(dir2));

        // compare dir contents recursively
        File[] files = dir1.listFiles(new TestFilter());
        for (File file : files) {
            File file2 = new File(dir2.getAbsolutePath() + File.separator + file.getName());
            if (!file2.exists())
                return false;

            if (file.isDirectory()) {
                if (!directoriesEqual(file, file2))
                    return false;
            } else {
                if (!filesEqual(file, file2))
                    return false;
            }
        }
        return true;
    }

    private List<File> listFilesRecursive(File path) {
        List<File> files = new ArrayList<File>();
        List<File> filesAdded = new ArrayList<File>();
        File[] filesArr = path.listFiles(new TestFilter());
        files.addAll(Arrays.asList(filesArr));
        for (File file : files) {
            if (file.isDirectory())
                filesAdded.addAll(listFilesRecursive(file));
        }
        files.addAll(filesAdded);
        return files;
    }

    public void testUnzip(File zipfile, File path_unzipped) throws Exception {
        assertTrue("could not find " + zipfile.getAbsolutePath(), zipfile.exists());
        assertTrue("could not find " + path_unzipped.getAbsolutePath(), path_unzipped.exists());

        File destPath = FileUtil.createTempDirectory("unziptest");
        try {
            List<File> files1 = listFilesRecursive(path_unzipped);
            List<File> files2 = UnzipUtil.unzip(zipfile, destPath.getPath());

            assertEquals(files1.size(), files2.size());
            assertTrue("unzipped directory not equal to pre-unzipped directory", directoriesEqual(path_unzipped, destPath));

        }
        finally {
            FileUtil.deleteDirectory(destPath);
        }
    }

    private static class TestFilter extends UnzipUtil.DefaultUnzipFilenameFilter {

        public boolean accept(File dir, String name) {
            return !name.equals(".svn") && super.accept(dir, name);
        }

    }

}
