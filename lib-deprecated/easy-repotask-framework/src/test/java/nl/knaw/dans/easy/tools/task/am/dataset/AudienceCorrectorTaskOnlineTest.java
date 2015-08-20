package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.PropertyList;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;

import org.junit.Test;

public class AudienceCorrectorTaskOnlineTest {

    @Test
    public void testTaskStamp() throws Exception {
        AudienceCorrectorTask acTask = new AudienceCorrectorTask("/mnt/hgfs/ecco/Public/AIPstore/data");

        JointMap joint = new JointMap();
        Dataset dataset = new DatasetImpl("easy-dataset:123");
        joint.setDataset(dataset);

        EasyMetadata emd = dataset.getEasyMetadata();
        PropertyList pList = new PropertyList();
        emd.getEmdOther().getPropertyListCollection().add(pList);

        acTask.setTaskStamp(joint);

        System.err.println(new EmdMarshaller(emd).getXmlString());

    }

}
