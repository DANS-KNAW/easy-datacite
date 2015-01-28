package nl.knaw.dans.easy.business.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class DataFileInstructions {
    private static final String EASY_DATA_FILE_INSTRUCTIONS_DECLARATION = "easy.data-file-instructions";
    private static final String FILE_DATA_URL = "easy.file.data.url";
    private static final String FILE_NAME = "easy.file.name";
    private static final String STREAMING_SURROGATE_URL = "easy.data-file-instructions.streaming-surrogate-urls";

    private final Properties properties;

    private DataFileInstructions(File f) {
        properties = readPropertiesFrom(f);
    }

    public static DataFileInstructions fromFile(File f) {
        return new DataFileInstructions(f);
    }

    public static boolean isDataFileInstructions(File f) {
        return f.getName().endsWith(".properties") && "yes".equals(readPropertiesFrom(f).getProperty(EASY_DATA_FILE_INSTRUCTIONS_DECLARATION));
    }

    private static Properties readPropertiesFrom(File f) {
        Properties p = new Properties();
        try {
            p.load(new InputStreamReader(new FileInputStream(f)));
        }
        catch (FileNotFoundException e) {
            return new Properties();
        }
        catch (IOException e) {
            return new Properties();
        }
        return p;
    }

    public String getFileDataUrl() {
        return properties.getProperty(FILE_DATA_URL);
    }

    public String getFileName() {
        return properties.getProperty(FILE_NAME);
    }

    public String getStreamingSurrogateUrl() {
        return properties.getProperty(STREAMING_SURROGATE_URL);
    }
}
