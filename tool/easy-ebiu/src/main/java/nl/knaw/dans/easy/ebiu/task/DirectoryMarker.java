package nl.knaw.dans.easy.ebiu.task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalRuntimeException;

import org.joda.time.DateTime;

public class DirectoryMarker {

    /**
     * A filename that indicates a resource directory is ingested as dataset in state DRAFT.
     */
    public static final String INGESTED_INDICATOR_FILE_NAME = "ingested";

    /**
     * A filename that indicates a resource directory is fully processed.
     */
    public static final String PROCESSED_INDICATOR_FILE_NAME = "processed";

    public static void markAsIngested(JointMap joint) {
        writeMarker(joint, INGESTED_INDICATOR_FILE_NAME);
    }

    public static void markAsProcessed(JointMap joint) {
        writeMarker(joint, PROCESSED_INDICATOR_FILE_NAME);
    }

    public static boolean isIngested(JointMap joint) {
        return containsFilename(joint, INGESTED_INDICATOR_FILE_NAME);
    }

    public static boolean isProcessed(JointMap joint) {
        return containsFilename(joint, PROCESSED_INDICATOR_FILE_NAME);
    }

    private static boolean containsFilename(JointMap joint, String filename) {
        boolean contains = false;
        File currentDirectory = joint.getCurrentDirectory();
        for (File f : currentDirectory.listFiles()) {
            if (filename.equals(f.getName())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private static void writeMarker(JointMap joint, String filename) {
        try {
            writeMarkerFile(joint, filename);
        }
        catch (IOException e) {
            throw new FatalRuntimeException(e);
        }
    }

    private static void writeMarkerFile(JointMap joint, String filename) throws IOException {
        File currentDirectory = joint.getCurrentDirectory();

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(currentDirectory, filename), "rw");
            raf.writeBytes(new DateTime().toString("yyyy-MM-dd-HH:mm:ss"));
        }
        finally {
            if (raf != null) {
                raf.close();
            }
        }

    }

}
