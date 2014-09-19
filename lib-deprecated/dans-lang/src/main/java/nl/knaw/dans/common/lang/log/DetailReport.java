package nl.knaw.dans.common.lang.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;

import nl.knaw.dans.common.lang.os.OS;

public class DetailReport implements Report {

    private File reportLocation;
    private boolean allRW;

    private String currentFileName;
    private RandomAccessFile raf;

    private EventPrinter printer;

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
        writeIfDetails(event);
    }

    @Override
    public void warn(Event event) {
        writeIfDetails(event);
    }

    @Override
    public void error(Event event) {
        writeIfDetails(event);
    }

    private void writeIfDetails(Event event) {
        if (event.hasDetails()) {
            write(event);
        }
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

    private void write(Event event) {
        String text = getPrinter().print(event);
        try {
            RandomAccessFile raFile = getRafFor(event);
            raFile.writeBytes(text);
        }
        catch (IOException e) {
            close();
            throw new RLRuntimeException("Unable to write report: " + currentFileName, e);
        }
    }

    private RandomAccessFile getRafFor(Event event) throws IOException {
        File detailReportLocation = new File(getReportLocation(), event.getDetails().getDetailReportLocation());
        RL.prepareReportLocation(detailReportLocation, allRW);
        File rafFile = new File(detailReportLocation, event.getDetails().getReportName());

        String fileName = rafFile.getAbsolutePath();
        if (!fileName.equals(currentFileName)) {
            close();
            currentFileName = fileName;
            raf = new RandomAccessFile(currentFileName, "rw");
            raf.writeBytes(getPrinter().printHeader(event));
            if (allRW) {
                OS.setAllRWX(currentFileName);
            }
        }
        return raf;
    }

    public static class Printer implements EventPrinter {

        @Override
        public String printHeader(Event event) {
            StringBuilder sb = new StringBuilder();
            sb.append(LONG_LINE).append(NL) //
                    .append(event.getDetails().getReportName()).append(NL) //
                    .append(LONG_LINE).append(NL);
            return sb.toString();
        }

        @Override
        public String print(Event event) {
            StringBuilder sb = new StringBuilder();
            sb //
            .append(event.getLevel()).append(SPACE) //
                    .append(event.getDate().toString("yyyy-MM-dd HH:mm:ss.SSS")).append(SPACE) //
                    .append(event.getEventName()).append(NL) //
                    .append("From: ").append(event.getSourceLink()).append(NL); //

            sb.append("Messages:").append(NLT);
            for (String msg : event.getMessages()) {
                sb.append(msg).append(NLT);
            }

            if (event.hasCause()) {
                sb.append(NL);
                sb.append("Stacktrace:").append(NL);
                StringWriter sWriter = new StringWriter();
                PrintWriter pWriter = new PrintWriter(sWriter);
                event.getCause().printStackTrace(pWriter);
                sb.append(sWriter.toString());
            }
            sb.append(NL).append(LONG_LINE).append(NL);
            return sb.toString();
        }

    }

}
