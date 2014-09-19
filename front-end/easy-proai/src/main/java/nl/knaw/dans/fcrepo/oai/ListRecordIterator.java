package nl.knaw.dans.fcrepo.oai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import proai.driver.RemoteIterator;
import proai.error.RepositoryException;
import fedora.common.Constants;
import fedora.services.oaiprovider.FedoraMetadataFormat;
import fedora.services.oaiprovider.FedoraRecord;

public class ListRecordIterator implements RemoteIterator<FedoraRecord> {

    private static final Logger logger = Logger.getLogger(ListRecordIterator.class);

    private final SparqlQueryComposer composer;
    private final RiConnector riConnector;

    private final FedoraMetadataFormat fmdFormat;

    private File tempFile;

    private BufferedReader reader;

    private String nextLine;

    private int recordCount;

    public ListRecordIterator(SparqlQueryComposer composer, RiConnector riConnector, File tempFile, FedoraMetadataFormat format) {
        logger.info(format.getPrefix() + " record iterator with tempFile [" + tempFile + "]");
        this.composer = composer;
        this.riConnector = riConnector;
        this.tempFile = tempFile;
        this.fmdFormat = format;

        if (tempFile != null) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
            }
            catch (FileNotFoundException e) {
                throw new RepositoryException("Could not read " + tempFile, e);
            }
            nextLine = readLine();
        }
    }

    @Override
    public boolean hasNext() throws RepositoryException {
        boolean hasNext = nextLine != null;
        if (hasNext) {
            recordCount++;
        }
        if (recordCount % 500 == 0) {
            logger.info("Returning records for format " + fmdFormat.getPrefix() + ". Count=" + recordCount);
        }
        if (!hasNext) {
            logger.info("Total record count for format " + fmdFormat.getPrefix() + " is " + recordCount);
        }
        return hasNext;
    }

    @Override
    public FedoraRecord next() throws RepositoryException {
        FedoraRecord nextRecord;
        try {
            nextRecord = getRecord(nextLine);
        }
        finally {
            nextLine = nextLine == null ? null : readLine();
        }
        return nextRecord;
    }

    @Override
    public synchronized void close() throws RepositoryException {
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException e) {
            throw new RepositoryException("Could not close reader", e);
        }
        finally {
            if (tempFile != null) {
                boolean deleted = tempFile.delete();
                logger.debug("Cleaned up = " + deleted + " tempFile was [" + tempFile + "]");
                if (deleted) {
                    tempFile = null;
                }
            }
        }
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Ensure resources are freed up at garbage collection time.
     */
    protected void finalize() {
        close();
    }

    // itemId= oai:easy.dans.knaw.nl:easy-dataset:161
    // predix= didl
    // sourceInfo= info:fedora/easy-dataset:161/easy-sdef:oai-item1/getDidl
    // null
    // false
    // 2012-10-09T12:31:47Z
    // D30000:D34000 driver
    //
    // line = info:fedora/easy-dataset:3028, oai:easy.dans.knaw.nl:easy-dataset:3028,
    // 2012-12-03T09:42:48.963Z, info:fedora/fedora-system:def/model#Inactive
    protected FedoraRecord getRecord(String line) {
        String parts[] = line.split(",");
        String objectId = parts[0];
        String itemId = parts[1];
        String date = formatDatetime(parts[2]);
        boolean deleted = !parts[3].equals(Constants.MODEL.ACTIVE.uri);

        String mdPrefix = fmdFormat.getPrefix();
        String recordDiss = getRecordDiss(fmdFormat.getMetadataSpec().getDisseminationType(), objectId);
        String[] setSpecs = getSetSpecs(objectId).toArray(new String[0]);
        String aboutDiss = null;

        return new FedoraRecord(itemId, mdPrefix, recordDiss, date, deleted, setSpecs, aboutDiss);
    }

    /**
     * Get the next line of output, or null if we've reached the end.
     * 
     * @throws IOException
     */
    private String readLine() {
        String line = null;
        try {
            line = nextLine(reader);
        }
        catch (IOException e) {
            logger.error("Could not read line of " + tempFile, e);
        }
        if (line == null) {
            close();
        }
        return line;
    }

    @SuppressWarnings("rawtypes")
    protected Set<String> getSetSpecs(String objectId) {
        Set<String> setSpecSet = new HashSet<String>();
        String query = composer.getSetSpecQuery(objectId);
        logger.debug("Calling RiConnector.getTuples() with query:\n" + query);
        TupleIterator tupleIter = riConnector.getTuples(query);
        try {
            while (tupleIter.hasNext()) {
                Map map = tupleIter.next();
                for (Object o : map.keySet()) {
                    Node node = (Node) map.get(o);
                    if (node.isLiteral()) {
                        String lexicalForm = ((Literal) node).getLexicalForm();
                        logger.debug("Adding setSpect for " + objectId + ": " + lexicalForm);
                        setSpecSet.add(lexicalForm);
                    } else {
                        logger.error("Expected an RDF literal as setSpec, but got " + node.getClass());
                    }
                }
            }
        }
        catch (TrippiException e) {
            close();
            throw new RepositoryException("Error getting setSpecs: ", e);
        }
        filterOutParents(setSpecSet);
        return setSpecSet;
    }

    /**
     * Get the next line, skipping any that start with " or are blank.
     * 
     * @throws IOException
     */
    private static String nextLine(BufferedReader r) throws IOException {
        String line = r.readLine();
        while (line != null && (line.startsWith("\"") || (line.trim().equals("")))) {
            line = r.readLine();
        }
        if (line == null) {
            return null;
        } else {
            return line.trim();
        }
    }

    // set:1
    // set:1:2 |
    // set:1:2:3 -->| set:1:2:3
    // set:4 | set:4:5
    // set:4:5 | set:6
    // set:6
    public static void filterOutParents(Set<String> setSpecSet) {
        Set<String> parents = new HashSet<String>();
        for (String setSpec : setSpecSet) {
            String[] p = setSpec.split(":");
            String previous = "";
            for (int i = 0; i < p.length - 1; i++) {
                previous += p[i];
                parents.add(previous);
                previous += ":";
            }
        }
        setSpecSet.removeAll(parents);
    }

    // info:fedora/easy-dataset:1/easy-sdef:oai-item1/getOAI_DC
    public static String getRecordDiss(String mDissType, String objectId) {
        // info:fedora/*/easy-sdef:oai-item1/getOAI_DC
        String[] parts = mDissType.split("\\*");
        return objectId + parts[1];
    }

    /**
     * OAI requires second-level precision at most, but Fedora provides millisecond precision. Fedora only uses UTC dates, so ensure UTC dates are indicated
     * with a trailing 'Z'.
     * 
     * @param datetime
     * @return datetime string such as 2004-01-31T23:11:00Z
     */
    public static String formatDatetime(String datetime) {
        StringBuffer sb = new StringBuffer(datetime);
        // length() - 5 b/c at most we're dealing with ".SSSZ"
        int i = sb.indexOf(".", sb.length() - 5);
        if (i != -1) {
            sb.delete(i, sb.length());
        }
        // Kowari's XSD.Datetime isn't timezone aware
        if (sb.charAt(sb.length() - 1) != 'Z') {
            sb.append('Z');
        }
        return sb.toString();
    }

}
