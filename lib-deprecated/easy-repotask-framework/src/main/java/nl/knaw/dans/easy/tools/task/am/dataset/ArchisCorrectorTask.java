package nl.knaw.dans.easy.tools.task.am.dataset;

import java.net.URI;
import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.PropertyList;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.joda.time.DateTime;

public class ArchisCorrectorTask extends AbstractDatasetTask {

    private static String COMMENT = "New migration of Archeology metadata (archis OMN, audience)";

    public static final URI ARCHIS_URI = URI.create("http://archis2.archis.nl");

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        Dataset dataset = joint.getDataset();
        EasyMetadata emd = dataset.getEasyMetadata();
        MetadataFormat mdf = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        if (!MetadataFormat.ARCHAEOLOGY.equals(mdf)) {
            return; // not a archaeological dataset
        }

        List<PropertyList> propertyLists = emd.getEmdOther().getPropertyListCollection();
        if (propertyLists == null || propertyLists.isEmpty()) {
            return; // not a migration dataset
        }

        PropertyList propertyList = propertyLists.get(0);
        String comment = propertyList.getComment();
        if (COMMENT.equals(comment)) {
            return; // did this the previous time
        }

        if (propertyList.getValue(this.getClass().getName(), null) != null) {
            return; // did this a previous time
        }

        String storeId = dataset.getStoreId();
        List<BasicIdentifier> biList = emd.getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);

        if (biList.size() > 0) {
            joint.setCycleSubjectDirty(true);
            propertyList.addProperty(this.getClass().getName(), new DateTime().toString());
        }

        for (BasicIdentifier bi : biList) {
            String multipleAomn = bi.getValue();
            reportStrangeStrings(multipleAomn, storeId);
            String[] aomns = multipleAomn.split(";");
            RL.info(new Event(getTaskName(), "Found archis omn", storeId, multipleAomn));
            // <dc:identifier eas:scheme="Archis_onderzoek_m_nr" eas:schemeId="archaeology.dc.identifier"
            // eas:identification-system="http://archis2.archis.nl">33264</dc:identifier>
            for (String aomn : aomns) {
                BasicIdentifier newbi = new BasicIdentifier(aomn.trim());
                newbi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
                newbi.setSchemeId("archaeology.dc.identifier");
                newbi.setIdentificationSystem(ARCHIS_URI);
                emd.getEmdIdentifier().add(newbi);
            }
        }

    }

    private void reportStrangeStrings(String multipleAomn, String storeId) {
        boolean strange = false;
        String subject = multipleAomn.trim();
        for (int i = 0; i < subject.length(); i++) {
            char c = subject.charAt(i);
            if (!Character.isDigit(c) && c != ' ' & c != ';') {
                strange = true;
            }
        }
        if (strange) {
            RL.warn(new Event(getTaskName(), "Strange number", storeId, multipleAomn));
        }
    }

}
