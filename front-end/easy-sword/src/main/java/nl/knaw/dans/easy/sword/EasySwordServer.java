package nl.knaw.dans.easy.sword;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.purl.sword.atom.Author;
import org.purl.sword.atom.Content;
import org.purl.sword.atom.Contributor;
import org.purl.sword.atom.Generator;
import org.purl.sword.atom.InvalidMediaTypeException;
import org.purl.sword.atom.Link;
import org.purl.sword.atom.Summary;
import org.purl.sword.atom.Title;
import org.purl.sword.base.AtomDocumentRequest;
import org.purl.sword.base.AtomDocumentResponse;
import org.purl.sword.base.Collection;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;
import org.purl.sword.base.Workspace;
import org.purl.sword.server.SWORDServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasySwordServer implements SWORDServer {
    private static Logger log = LoggerFactory.getLogger(EasySwordServer.class);

    /**
     * See {@linkplain http://www.swordapp.org/docs/sword-profile-1.3.html#b.5.5}<br>
     * Only a published state would be appropriate for code 201
     */
    public ServiceDocument doServiceDocument(final ServiceDocumentRequest sdr) throws SWORDAuthenticationException, SWORDErrorException, SWORDException {
        log.info(MessageFormat.format("SERVICE DOCUMENT user={0}; IP={1}; location={2}; onBehalfOf={3}", sdr.getUsername(), sdr.getIPAddress(),
                sdr.getLocation(), sdr.getOnBehalfOf()));

        final EasyUser user = null;
        try {
            EasyBusinessFacade.getUser(sdr.getUsername(), sdr.getPassword());
        }
        catch (final SWORDAuthenticationException e) {
            // we are not afraid of empty place holders in treatment
        }

        if (sdr.getOnBehalfOf() != null) {
            // see also org.purl.sword.server.DummyServer
            /*
             * Allow users to force the throwing of a SWORD error exception by setting the OnBehalfOf user to 'error'
             */
            // un-comment next line as as soon as mediation gets implemented:
            // if (sdr.getOnBehalfOf().equals("error"))
            throw new SWORDErrorException(ErrorCodes.MEDIATION_NOT_ALLOWED, "Mediated deposits not allowed");
        }

        final Service service = new Service("1.3", true, true);
        service.setGenerator(wrapGenerator(sdr.getLocation()));
        service.addWorkspace(createWorkSpace(createCollection(sdr.getLocation(), user)));

        final ServiceDocument document = new ServiceDocument(service);
        log.debug("returned service document:\n" + document.toString());
        return document;
    }

    private Collection createCollection(final String location, final EasyUser user) throws SWORDErrorException, SWORDException {
        final Collection collection = new Collection();

        // DEMO client does not process HTML
        collection.setTitle(Context.getCollectionTitle());

        // DEMO client does process HTML
        collection.setCollectionPolicy("<div>" + Context.getCollectionPolicy() + "</div>");
        collection.setTreatment("<div>" + EasyBusinessFacade.composeCollectionTreatment(user) + "</div>");
        collection.setAbstract("<div>" + MessageFormat.format(Context.getCollectionAbstract(), Context.getEasyHome()) + "</div>");

        collection.addAccepts("application/zip");
        collection.setMediation(false);

        // qualityValue indicates this is the preferred format
        collection.addAcceptPackaging(Context.getAcceptPackaging(), 1f);

        final boolean jetty = !location.contains("/" + Context.getServletName() + "/");
        collection.setLocation(Context.getProviderURL() + (jetty ? "" : Context.getServletName() + "/") + "deposit");
        return collection;
    }

    private static Workspace createWorkSpace(final Collection collection) throws SWORDException {
        final Workspace workspace = new Workspace();
        workspace.setTitle(Context.getWorkspaceTitle());
        workspace.addCollection(collection);
        return workspace;
    }

    public DepositResponse doDeposit(final Deposit deposit) throws SWORDAuthenticationException, SWORDErrorException, SWORDException {
        log.info(MessageFormat.format("DEPOSIT user={0}; IP={1}; location={2}; fileName={3}", deposit.getUsername(), deposit.getIPAddress(),
                deposit.getLocation(), deposit.getFilename()));

        /*
         * TODO authentication too late for "Expect: 100-Continue" ?
         * http://git.661346.n2.nabble.com/PATCH-smart-http-Don-t-use-Expect-100-Continue-td6028355.html http://htmlhelp.com/reference/html40/forms/form.html
         */
        final EasyUser user = EasyBusinessFacade.getUser(deposit.getUsername(), deposit.getPassword());
        if (Context.getSystemReadOnlyStatus().getReadOnly())
            throw EasyBusinessFacade.READ_ONLY_EXCEPTION;
        checkOnBehalfOf(deposit);

        final Dataset dataset;
        final RequestContent requestContent = new RequestContent(deposit.getFile());
        try {
            final EasyMetadata metadata = requestContent.getEasyMetadata();
            final File folder = requestContent.getDataFolder();
            final List<File> files = requestContent.getFiles();
            dataset = EasyBusinessFacade.submitNewDataset(deposit.isNoOp(), user, metadata, folder, files);
        }
        finally {
            requestContent.clearTemp();
        }

        final String datasetUrl = Context.getDatasetPath() + dataset.getStoreId();
        final SWORDEntry swordEntry = wrapSwordEntry(deposit, user, dataset, datasetUrl);
        return wrapResponse(swordEntry, datasetUrl);
    }

    private static void checkOnBehalfOf(final Deposit deposit) throws SWORDErrorException {
        // Check this is a collection that takes "on behalf of" deposits, else throw an error
        if (((deposit.getOnBehalfOf() != null) && (!deposit.getOnBehalfOf().equals(""))) && (!deposit.getLocation().contains("deposit?user=")))
            throw new SWORDErrorException(ErrorCodes.MEDIATION_NOT_ALLOWED, "Mediated deposit not allowed to this collection");
    }

    private static SWORDEntry wrapSwordEntry(final Deposit deposit, final EasyUser user, final Dataset dataset, final String datasetUrl) throws SWORDException,
            SWORDErrorException
    {
        final SWORDEntry swordEntry = new SWORDEntry();
        final EasyMetadata metadata = dataset.getEasyMetadata();
        swordEntry.setTitle(wrapTitle(dataset.getPreferredTitle()));
        swordEntry.setSummary(wrapSummary(metadata.getEmdDescription().toString()));
        swordEntry.addCategory(EasyBusinessFacade.formatAudience(metadata));
        swordEntry.setId(dataset.getPersistentIdentifier());
        swordEntry.setUpdated(metadata.getEmdDate().toString());
        swordEntry.addAuthors(wrapAuthor(user));

        // This element SHOULD be included. If the POST request results in the creation of packaged
        // resource, the server MAY use this element to declare the packaging type. If used it SHOULD
        // take a value from [SWORD-TYPES].
        // swordEntry.setPackaging("?");

        // we won't support updating (task for archivists) so skip MediaLink
        // swordEntry.addLink(wrapEditMediaLink());
        swordEntry.addLink(wrapLink("edit", datasetUrl));
        swordEntry.setGenerator(wrapGenerator(Context.getProviderURL() + Context.getServletName()));
        swordEntry.setContent(wrapContent(datasetUrl));

        // http://validator.swordapp.org doesn't like a complex element
        swordEntry.setTreatment("<div>" + EasyBusinessFacade.composeDepositTreatment(user, dataset) + "</div>");

        swordEntry.setNoOp(deposit.isNoOp());
        // TODO swordEntry.setRights(rights);
        if (deposit.getOnBehalfOf() != null)
            swordEntry.addContributor(wrapContributor(deposit.getOnBehalfOf()));
        if (deposit.isVerbose())
            swordEntry.setVerboseDescription("<div>" + EasyBusinessFacade.verboseInfo(user, dataset) + "</div>");

        return swordEntry;
    }

    private static DepositResponse wrapResponse(final SWORDEntry swordEntry, final String storeID) {
        final DepositResponse depostiResponse = new DepositResponse(Deposit.ACCEPTED);
        depostiResponse.setEntry(swordEntry);
        depostiResponse.setLocation(storeID);
        depostiResponse.setHttpResponse(swordEntry.isNoOp() ? SC_OK : SC_ACCEPTED);
        return depostiResponse;
    }

    private static Title wrapTitle(final String value) {
        final Title title = new Title();
        title.setContent(value);
        return title;
    }

    private static Summary wrapSummary(final String value) {
        final Summary summary = new Summary();
        summary.setContent(value);
        return summary;
    }

    /**
     * See {@linkplain http://www.swordapp.org/docs/sword-profile-1.3.html#b.9.6}<br>
     * If a server provides an edit-media link it SHOULD allow media resource updating with PUT as described in [AtomPub] sections 9.3 and 9.6.
     */
    @SuppressWarnings("unused")
    private static Link wrapEditMediaLink() {
        final Link em = new Link();
        em.setRel("edit-media");
        em.setHref("http://www.myrepository.ac.uk/sdl/workflow/my deposit");
        return em;
    }

    private static Link wrapLink(final String rel, final String location) {
        final Link link = new Link();
        link.setRel(rel);
        link.setHref(location);
        return link;
    }

    private static Generator wrapGenerator(final String server) {
        final Generator generator = new Generator();
        generator.setContent("Easy SWORD Server");
        generator.setUri(server);
        generator.setVersion("1.3");
        return generator;
    }

    private static Content wrapContent(final String url) throws SWORDException {
        final Content content = new Content();
        final String mediaType = "text/html";
        try {
            content.setType(mediaType);
        }
        catch (final InvalidMediaTypeException exception) {
            throw new SWORDException("", exception);
        }
        content.setSource(url);
        return content;
    }

    private static Contributor wrapContributor(final String onBehalfOf) {
        final Contributor contributor = new Contributor();
        contributor.setName(onBehalfOf);
        contributor.setEmail(onBehalfOf + "@myrepository.ac.uk");
        return contributor;
    }

    private static Author wrapAuthor(final EasyUser user) {
        final Author author = new Author();
        author.setName(user.getDisplayName());
        author.setEmail(user.getEmail());
        return author;
    }

    public AtomDocumentResponse doAtomDocument(final AtomDocumentRequest adr) throws SWORDAuthenticationException, SWORDErrorException, SWORDException {
        log.info(MessageFormat.format("ATOM DOC user={0}; IP={1}; location={2}", adr.getUsername(), adr.getIPAddress(), adr.getLocation()));
        EasyBusinessFacade.getUser(adr.getUsername(), adr.getPassword());

        return new AtomDocumentResponse(SC_NOT_IMPLEMENTED);
    }
}
