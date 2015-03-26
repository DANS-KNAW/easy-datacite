package nl.knaw.dans.easy.business.dataset;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.BRI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.DOI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_PID;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.util.ApacheHttpClientFacade;
import nl.knaw.dans.easy.util.HttpClientFacade;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MetadataPidGeneratorTest {

    private static final String OLD_URN = "oldURN";
    private static final String NEW_URN = "newURN";
    private static final String NEW_DOI = "newDOI";
    private static final String OLD_DOI = "oldDOI";
    private PidClient pidClient;

    private void expectNoDuplicatePids() throws RepositoryException {
        EasyStore easyStore = createMock(EasyStore.class);
        expect(easyStore.getRelations((String) anyObject(), isA(String.class), isA(String.class))).andStubReturn(new ArrayList<Relation>());;
        new Data().setEasyStore(easyStore);
    }

    private void expectGeneratedPids() throws Exception {
        expect(pidClient.getPid(PidClient.Type.doi)).andStubReturn(NEW_DOI);
        expect(pidClient.getPid(PidClient.Type.urn)).andStubReturn(NEW_URN);
    }

    @Before
    public void prepare() {
        pidClient = createMock(PidClient.class);
    }

    @After
    public void cleanup() {
        resetAll();
    }

    @Ignore
    @Test
    public void justMockFacade() throws Exception {
        HttpClientFacade clientFacade = createMock(HttpClientFacade.class);
        expect(clientFacade.post(isA(URL.class))).andStubReturn(NEW_DOI.getBytes());// but getting null
        String result = new PidClient(new URL("http://localhost:8084"), clientFacade).getPid(PidClient.Type.doi);
        assertThat(result, is(NEW_DOI));
    }

    @Test
    public void errorHandling() throws Exception {
        pidClient = new PidClient(new URL("http://"), new ApacheHttpClientFacade());
        // log will show:
        // Can't generate PIDs URI does not specify a valid host name: http:/pids?type=urn for a:b
        assertThat(execute(new DatasetImpl("a:b")), is(false));
    }

    @Test
    public void bothNew() throws Exception {
        expectGeneratedPids();
        expectNoDuplicatePids();
        DatasetImpl dataset = new DatasetImpl("a:b");
        assertURN(dataset, null);
        assertDOI(dataset, null);

        boolean result = execute(dataset);

        assertThat(result, is(true));
        assertURN(dataset, NEW_URN);
        assertDOI(dataset, NEW_DOI);
    }

    @Test
    public void newDOI() throws Exception {
        expectGeneratedPids();
        expectNoDuplicatePids();
        DatasetImpl dataset = new DatasetImpl("a:b");
        List<BasicIdentifier> dcIdentifier = dataset.getEasyMetadata().getEmdIdentifier().getDcIdentifier();
        dcIdentifier.add(createBasicIdentifier(OLD_URN, BRI_RESOLVER, SCHEME_PID));

        assertURN(dataset, OLD_URN);
        assertDOI(dataset, null);

        boolean result = execute(dataset);

        assertThat(result, is(true));
        assertURN(dataset, OLD_URN);
        assertDOI(dataset, NEW_DOI);
    }

    @Test
    public void newURN() throws Exception {
        expectGeneratedPids();
        expectNoDuplicatePids();
        DatasetImpl dataset = new DatasetImpl("a:b");
        List<BasicIdentifier> dcIdentifier = dataset.getEasyMetadata().getEmdIdentifier().getDcIdentifier();
        dcIdentifier.add(createBasicIdentifier(OLD_DOI, DOI_RESOLVER, SCHEME_DOI));

        assertURN(dataset, null);
        assertDOI(dataset, OLD_DOI);

        boolean result = execute(dataset);

        assertThat(result, is(true));
        assertURN(dataset, NEW_URN);
        assertDOI(dataset, OLD_DOI);
    }

    private boolean execute(DatasetImpl dataset) {
        replayAll();
        return new MetadataPidGenerator(pidClient).process(new DatasetSubmissionImpl(null, dataset, null));
    }

    private void assertURN(DatasetImpl dataset, String expected) {
        assertThat(dataset.getEncodedPersistentIdentifier(), equalTo(expected));
        assertThat(dataset.getRelations().getPersistentIdentifier(), equalTo(expected == OLD_URN ? null : expected));
        assertThat(dataset.getEasyMetadata().getEmdIdentifier().getPersistentIdentifier(), equalTo(expected));
    }

    private void assertDOI(DatasetImpl dataset, String expected) {
        assertThat(dataset.getEncodedDansManagedDoi(), equalTo(expected));
        assertThat(dataset.getEasyMetadata().getEmdIdentifier().getDansManagedDoi(), equalTo(expected));

        // wasn't it a bad design to have to change both (fedora)Relations and emd?
        assertThat(dataset.getRelations().getDansManagedDOI(), equalTo(expected == OLD_DOI ? null : expected));
    }

    private BasicIdentifier createBasicIdentifier(String urn, String resolver, String scheme) {
        BasicIdentifier bi = new BasicIdentifier(urn);
        bi.setIdentificationSystem(URI.create(resolver));
        bi.setScheme(scheme);
        return bi;
    }
}
