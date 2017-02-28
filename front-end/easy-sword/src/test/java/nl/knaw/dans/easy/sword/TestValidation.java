package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.sword.util.Fixture;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.api.easymock.PowerMock;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

@RunWith(Parameterized.class)
public class TestValidation extends Fixture {
    private final String metadataFileName;
    private final String messageContent;

    public TestValidation(final String metadataFileName, final String messageContent) {
        this.metadataFileName = metadataFileName;
        this.messageContent = messageContent;
    }

    @BeforeClass
    public static void mockNow() {
        DateTimeUtils.setCurrentMillisFixed(new DateTime("2012-10-29T14:42:08").getMillis());
    }

    @BeforeClass
    public static void mockService() throws Exception {
        mockEasyStore(mockRootDiscipline(mockSubDisciplines()));
        PowerMock.replayAll();
    }

    /**
     * Documents expected content of &lt;atom:summary type="text"> in the HTTP response 400 Bad Request. The metadata files won't cause a draft dataset when
     * submitted. Input errors not detected by the validation (such as trailing or leading white space) will pass a NoOp submission but might cause a draft
     * dataset when submitted for real.
     */
    @Parameters
    public static Collection<String[]> createParameters() throws Exception {
        // TODO these test seem to belong in the EMD module
        final List<String[]> constructorSignatureInstances = new ArrayList<String[]>();

        // used to cause a draft dataset because the notification message could not be created
        constructorSignatureInstances.add(new String[] {"disciplineWithWhiteSpace.xml", null});

        // in this example a schema id is added manually after download of the xml from a test dataset
        constructorSignatureInstances.add(new String[] {"SpatialPoint.xml", null});

        // just as downloaded from a test dataset
        // doesn't fail since merging deposit formats and reorganizing easy-business/src/main/java/nl/knaw/dans/easy/domain/emd/validation
        // constructorSignatureInstances.add(new String[] {"SpatialPointWithoutSchemaId.xml", "Expected is 'common.eas.spatial'"});

        // TODO make error message mores specific, needs architectural solution see also EBIU workaround
        constructorSignatureInstances.add(new String[] {"SpatialPointWithoutX.xml", "invalid"});
        constructorSignatureInstances.add(new String[] {"SpatialPointWithoutY.xml", "invalid"});

        constructorSignatureInstances.add(new String[] {"InvalidFormat.xml", "Value 'nonsense' is not facet-valid"});
        constructorSignatureInstances.add(new String[] {"InvalidDiscipline.xml", "999 not found"});

        // TODO mock the system date for more precise boundary checks
        // constructorSignatureInstances.add(new String[] {"embargoPast.xml", "in the past"});
        // constructorSignatureInstances.add(new String[] {"embargoFuture.xml", "more than two years"});

        return constructorSignatureInstances;
    }

    @Test
    public void executeValidation() throws Exception {
        final byte[] fileContent = FileUtil.readFile(new File("src/test/resources/input/" + metadataFileName));
        try {
            validate(fileContent);
        }
        catch (final SWORDErrorException se) {
            if (messageContent == null)
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString(), se);
            if (!se.getMessage().contains(messageContent))
                throw new Exception("\n" + metadataFileName + " expected a message containing " + messageContent + "\nbut got " + se.getMessage(), se);
            return;
        }
        catch (final Exception se) {
            if (messageContent == null)
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString());
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got " + se.toString(), se);
        }
        if (messageContent != null)
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got no exception");
    }

    /** Just a wrapper for exceptions. */
    private static EasyMetadata unmarshallEasyMetaData(final byte[] data) throws SWORDErrorException {
        final EasyMetadata metadata;
        try {
            metadata = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, data);
        }
        catch (final XMLDeserializationException exception) {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("EASY metadata unmarshall exception: " + exception.getMessage()));
        }
        return metadata;
    }

    public static EasyMetadata validate(final byte[] easyMetaData) throws SWORDErrorException, SWORDException {
        EasyMetadataFacade.validateSyntax(easyMetaData);
        final EasyMetadata unmarshalled = unmarshallEasyMetaData(easyMetaData);
        EasyMetadataFacade.validateMandatoryFields(unmarshalled);
        for (BasicString audience : unmarshalled.getEmdAudience().getTermsAudience())
            try {
                DisciplineCollectionImpl.getInstance().getDisciplineBySid(new DmoStoreId(audience.getValue()));
            }
            catch (ObjectNotFoundException e) {
                throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Audience " + audience.toString() + " not found " + e.getMessage());
            }
            catch (DomainException e) {
                throw new SWORDException("discipline validation problem: " + e.getMessage(), e);
            }
        return unmarshalled;
    }

    private static void mockEasyStore(final DisciplineContainerImpl value) throws ObjectNotInStoreException, RepositoryException {
        final EasyStore easyStore = PowerMock.createMock(EasyStore.class);
        new Data().setEasyStore(easyStore);
        EasyMock.expect(easyStore.retrieve(EasyMock.isA(DmoStoreId.class))).andStubReturn(value);
    }

    private static DisciplineContainerImpl mockRootDiscipline(final List<DisciplineContainer> list) throws DomainException, RepositoryException {
        final DisciplineContainerImpl value = PowerMock.createMock(DisciplineContainerImpl.class);
        EasyMock.expect(value.getSubDisciplines()).andStubReturn(list);
        EasyMock.expect(value.isInvalidated()).andStubReturn(true);
        return value;
    }

    private static List<DisciplineContainer> mockSubDisciplines() {
        final List<DisciplineContainer> list = new ArrayList<DisciplineContainer>();
        list.add(new DisciplineContainerImpl("easy-discipline:1"));
        list.add(new DisciplineContainerImpl("easy-discipline:2"));
        return list;
    }
}
