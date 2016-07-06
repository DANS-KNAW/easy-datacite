package nl.knaw.dans.easy.web.deposit.repeasy;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.emd.types.Relation;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class RelationListWrapperTest {

    private Relation relationFrom(String relation) throws Exception {
        // @formatter:off
        String xml = "<emd:easymetadata " +
                "xmlns:emd='http://easy.dans.knaw.nl/easy/easymetadata/' " +
                "xmlns:dc='http://purl.org/dc/elements/1.1/' " +
                "xmlns:dct='http://purl.org/dc/terms/' " +
                "xmlns:eas='http://easy.dans.knaw.nl/easy/easymetadata/eas/' " +
                "emd:version='0.1'>" +
                "<emd:relation>" + relation + "</emd:relation>" +
                "</emd:easymetadata>";
        // @formatter:on
        EasyMetadata emd = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
        return new RelationListWrapper(emd.getEmdRelation()).getInitialItems().get(0).getRelation();
    }

    @Test
    public void viaWebForm() throws Exception {
        Relation model = relationFrom("<eas:relation><eas:subject-title>blabla</eas:subject-title><eas:subject-link>http://easy.dans.knaw.nl</eas:subject-link></eas:relation>");
        assertThat(model.getSubjectTitle().toString(), is("blabla"));
        assertThat(model.getSubjectLink().toString(), is("http://easy.dans.knaw.nl"));
    }
}
