package nl.knaw.dans.common.lang.collect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.progress.ProgressSubject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Collector that reads entries from file(s). It can operate on a single file, a directory or a directory tree.
 * <p/>
 * Read actions executed upon {@link #collect()}:
 * <ul>
 * <li>If the given file is a single file --> will read that file.</li>
 * <li>If the given file is a directory and this FileEntryCollector is not recursive --> will read all the files in the given directory.</li>
 * <li>If the given file is a directory and this FileEntryCollector is recursive --> will read all the files in the given directory and child directories.</li>
 * </ul>
 */
public class FileEntryCollector extends ProgressSubject implements Collector<List<String>> {

    private static final Logger logger = LoggerFactory.getLogger(FileEntryCollector.class);

    private final File file;

    private boolean recursive;

    /**
     * Constructor that creates a {@link File} from the given path.
     * 
     * @param path
     *        pathname for file or directory
     */
    public FileEntryCollector(String path) {
        file = new File(path);
    }

    /**
     * Constructor that takes a {@link File} as parameter.
     * 
     * @param file
     *        abstract pathname for file or directory
     */
    public FileEntryCollector(File file) {
        this.file = file;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    public List<String> collect() {
        onStartProcess();
        List<String> entries = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            int totalDirs = files.length;
            int currentDir = 0;

            for (File file2read : files) {
                collect(entries, file2read);
                // we only report intermediate progress on first level directories.
                onProgress(totalDirs, ++currentDir);
            }
        } else {
            collect(entries, file);
        }
        onEndProcess();
        return entries;
    }

    private void collect(List<String> entries, File file2read) {
        if (file2read.isFile() && !file2read.isHidden()) {
            try {
                read(entries, file2read);
            }
            catch (IOException e) {
                logger.error("Could not read or close " + file2read.getPath());
                throw new ApplicationException(e);
            }
        } else if (file2read.isDirectory() && isRecursive()) {
            for (File file : file2read.listFiles()) {
                collect(entries, file);
            }
        }

    }

    private void read(List<String> entries, File file2read) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file2read, "r");
            String entry;
            while ((entry = raf.readLine()) != null) {
                entries.add(entry);
            }
        }
        catch (FileNotFoundException e) {
            logger.error("Could not open " + file2read.getPath());
            throw e;
        }
        finally {
            if (raf != null) {
                raf.close();
            }
        }

    }

}
