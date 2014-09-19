package nl.knaw.dans.common.lang.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import nl.knaw.dans.common.lang.os.OS;

public class OverviewReport implements Report {

    public static final int DEFAULT_MAX_LENGTH = 5;
    public static final String DEFAULT_FILENAME = "overview.csv";

    private final String fileName;

    private File reportLocation;
    private boolean allRW;

    private int fileCounter;
    private String currentFileName;
    private RandomAccessFile raf;
    private int maxLength;

    private EventPrinter printer;

    public OverviewReport() {
        this(DEFAULT_FILENAME, null);
    }

    public OverviewReport(String fileName) {
        this(fileName, null);
    }

    public OverviewReport(EventPrinter eventPrinter) {
        this(DEFAULT_FILENAME, eventPrinter);
    }

    public OverviewReport(String fileName, EventPrinter eventPrinter) {
        this.fileName = fileName;
        this.printer = eventPrinter;
    }

    @Override
    public void setReportLocation(File reportLocation, boolean allRW) {
        this.reportLocation = reportLocation;
        this.allRW = allRW;
    }

    private File getReportLocation() {
        if (reportLocation == null) {
            reportLocation = new File(".");
        }
        return reportLocation;
    }

    private EventPrinter getPrinter() {
        if (printer == null) {
            printer = new Printer();
        }
        return printer;
    }

    @Override
    public void info(Event event) {
        write(event);
    }

    @Override
    public void warn(Event event) {
        write(event);
    }

    @Override
    public void error(Event event) {
        write(event);
    }

    public int getMaxLength() {
        if (maxLength <= 0) {
            maxLength = DEFAULT_MAX_LENGTH;
        }
        return maxLength;
    }

    /**
     * Set maximum file length in MB.
     * 
     * @param maxLength
     *        maximum file length in MB
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void close() {
        if (raf != null) {
            try {
                raf.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to close report file: " + currentFileName, e);
            }
        }
        raf = null;
    }

    protected void write(Event event) {
        String line = getPrinter().print(event);
        RandomAccessFile raFile = getRaf(event);
        try {
            raFile.writeBytes(line);
            if (raFile.length() > getMaxLength() * 1000000) {
                close();
            }
        }
        catch (IOException e) {
            close();
            throw new RLRuntimeException("Unable to write report: " + currentFileName, e);
        }
    }

    protected RandomAccessFile getRaf(Event event) {
        if (raf == null) {
            currentFileName = composeFileName(getFileName(), fileCounter++);
            File currentFile = new File(getReportLocation(), currentFileName);

            try {
                raf = new RandomAccessFile(currentFile, "rw");
                raf.writeBytes(getPrinter().printHeader(event));
                if (allRW) {
                    OS.setAllRWX(currentFile);
                }
            }
            catch (FileNotFoundException e) {
                close();
                throw new RuntimeException("Unable to open report file: " + currentFileName, e);
            }
            catch (IOException e) {
                close();
                throw new RuntimeException("Unable to write report: " + currentFileName, e);
            }
        }
        return raf;
    }

    protected static String composeFileName(String name, int i) {
        String filename;
        String extension;
        int p = name.lastIndexOf(".");
        if (p > 0) {
            filename = name.substring(0, p);
            extension = name.substring(p);
        } else {
            filename = name;
            extension = "";
        }
        return filename + "_" + i + extension;
    }

    public static class Printer implements EventPrinter {

        @Override
        public String printHeader(Event event) {
            StringBuilder sb = new StringBuilder() //
                    .append("Date").append(SEPARATOR) //
                    .append("Level").append(SEPARATOR) //
                    .append("SourceLink").append(SEPARATOR) //
                    .append("Cause").append(SEPARATOR) //
                    .append("Event").append(SEPARATOR) //
                    .append("Details").append(SEPARATOR) //
                    .append("Messages...").append(SEPARATOR) //
                    .append(NL);

            return sb.toString();
        }

        @Override
        public String print(Event event) {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getDate().toString("yyyy-MM-dd HH:mm:ss.SSS")).append(SEPARATOR) //
                    .append(event.getLevel()).append(SEPARATOR) //
                    .append(event.getSourceLink()).append(SEPARATOR); //

            if (event.hasCause()) {
                sb.append(event.getCause().getClass().getSimpleName());
            } else {
                sb.append(EMPTY_CELL);
            }
            sb.append(SEPARATOR) //
                    .append(event.getEventName()).append(SEPARATOR);

            if (event.hasDetails()) {
                sb.append(event.getDetails().getDetailLink());
            } else {
                sb.append(EMPTY_CELL);
            }
            sb.append(SEPARATOR);

            for (String msg : event.getMessages()) {
                if (msg != null) {
                    sb.append(msg.replaceAll("\n", " | ")).append(SEPARATOR);
                } else {
                    sb.append(msg).append(SEPARATOR);
                }
            }
            sb.append(NL);
            return sb.toString();
        }

    }

}
