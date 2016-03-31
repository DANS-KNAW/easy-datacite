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
    public void empty () throws Exception {
        Relation model = relationFrom("<dc:relation></dc:relation>");
        assertThat(model.getSubjectTitle().toString(), is(""));
        assertThat(model.getSubjectLink(), nullValue());
    }

    @Test
    public void streamingSurrogate () throws Exception {
        Relation model = relationFrom("<dc:relation eas:schemeId='STREAMING_SURROGATE_RELATION'>/domain/dans/user/somebody/collection/test1/presentation/testA</dc:relation>");
        assertThat(model.getSubjectTitle().toString(), is("/domain/dans/user/somebody/collection/test1/presentation/testA"));
        assertThat(model.getSubjectLink(), nullValue());
    }

    @Test
    public void titleBeforeUrl () throws Exception {
        Relation model = relationFrom("<dct:conformsTo>title=someDoi url=http://doi.org/10.1111/sode.12120</dct:conformsTo>");
        assertThat(model.getSubjectTitle().toString(), is("conformsTo: someDoi"));
        assertThat(model.getSubjectLink().toString(), is("http://doi.org/10.1111/sode.12120"));
    }

    @Test
    public void titleAfterUrl () throws Exception {
        Relation model = relationFrom("<dct:conformsTo>url=http://doi.org/10.1111/sode.12120 title=someDoi </dct:conformsTo>");
        assertThat(model.getSubjectTitle().toString(), is("conformsTo: someDoi"));
        assertThat(model.getSubjectLink().toString(), is("http://doi.org/10.1111/sode.12120"));
    }

    @Test
    public void typeAndUrl () throws Exception {
        Relation model = relationFrom("<dct:conformsTo>http://doi.org/10.1111/sode.12120</dct:conformsTo>");
        assertThat(model.getSubjectTitle().toString(), is("conformsTo"));
        assertThat(model.getSubjectLink().toString(), is("http://doi.org/10.1111/sode.12120"));
    }

    @Test
    public void typeAndText () throws Exception {
        Relation model = relationFrom("<dct:replaces>A descriptin of object 7</dct:replaces>");
        assertThat(model.getSubjectTitle().toString(), is("replaces: A descriptin of object 7"));
        assertThat(model.getSubjectLink(), nullValue());
    }

    @Test
    public void viaWebForm () throws Exception {
        Relation model = relationFrom("<eas:relation><eas:subject-title>blabla</eas:subject-title><eas:subject-link>http://easy.dans.knaw.nl</eas:subject-link></eas:relation>");
        assertThat(model.getSubjectTitle().toString(), is("blabla"));
        assertThat(model.getSubjectLink().toString(), is("http://easy.dans.knaw.nl"));
    }

    @Test
    public void invalidUrlViaDdm () throws Exception {
        Relation model = relationFrom("<dct:conformsTo>url=://</dct:conformsTo>");
        assertThat(model.getSubjectTitle().toString(), is("conformsTo: url=://"));
        assertThat(model.getSubjectLink(), nullValue());
    }

    @Test
    public void invalidUrlViaDdmWithTitle () throws Exception {
        Relation model = relationFrom("<dct:conformsTo>title=rubish url=://</dct:conformsTo>");
        assertThat(model.getSubjectTitle().toString(), is("conformsTo: rubish"));
        assertThat(model.getSubjectLink(), nullValue());
    }
}
