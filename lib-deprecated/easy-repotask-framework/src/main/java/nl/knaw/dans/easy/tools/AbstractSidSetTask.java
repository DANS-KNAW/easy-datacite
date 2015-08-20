package nl.knaw.dans.easy.tools;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

/**
 * Abstract task that can read or convert a set of system id's from files.
 */
public abstract class AbstractSidSetTask extends AbstractTask {

    private final String[] idFilenames;
    private final IdConverter idConverter;

    private int originalIdNotFoundCount;

    public AbstractSidSetTask(String... idFilenames) {
        this(null, idFilenames);
    }

    public AbstractSidSetTask(IdConverter idConverter, String... idFilenames) {
        this.idConverter = idConverter;
        this.idFilenames = idFilenames;
    }

    protected Set<String> loadSids() throws FatalTaskException, IOException, FatalException {
        Set<String> sidSet = new LinkedHashSet<String>();
        for (String idFilename : idFilenames) {
            readFile(idFilename, sidSet);
        }
        return sidSet;
    }

    private void readFile(String idFilename, Set<String> sidSet) throws FatalTaskException, IOException, FatalException {
        System.out.print("Reading file: " + idFilename);
        int count = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(idFilename, "r");
            String id = null;
            while ((id = raf.readLine()) != null) {
                if (!id.startsWith("#")) {
                    addSid(idFilename, sidSet, id);
                    if (count++ % 10 == 0) {
                        System.out.print(".");
                    }
                }
            }
            System.out.println();
        }
        catch (IOException e) {
            throw new FatalTaskException("Unable to read id list: " + idFilename, this);
        }
        finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    private void addSid(String idFilename, Set<String> sidSet, String id) throws FatalException {
        if (idConverter == null) {
            sidSet.add(id);
        } else {
            List<String> list = idConverter.convert(id);
            if (list.isEmpty()) {
                onOriginalIdNotConverted(idFilename, id);
            } else {
                sidSet.addAll(list);
            }
        }
    }

    protected void onOriginalIdNotConverted(String idFilename, String originalId) {
        originalIdNotFoundCount++;
        RL.info(new Event("original id not converted", idFilename, originalId));
    }

    public int getOriginalIdNotFoundCount() {
        return originalIdNotFoundCount;
    }

    public String[] getIdFilenames() {
        return idFilenames;
    }

    public String getIdFilenamesToString() {
        return Arrays.deepToString(idFilenames);
    }

    public IdConverter getIdConverter() {
        return idConverter;
    }

}
