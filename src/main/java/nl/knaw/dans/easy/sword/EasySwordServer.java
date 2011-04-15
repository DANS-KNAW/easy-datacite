/**
 * Copyright (c) 2009, Aberystwyth University All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are
 * met: - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of the Centre for Advanced Software and
 * Intelligent Systems (CASIS) nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.knaw.dans.easy.sword;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

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

/**
 * A 'dummy server' which acts as dumb repository which implements the SWORD ServerInterface. It accepts
 * any type of deposit, and tries to return appropriate responses. It supports authentication: if the
 * username and password match (case sensitive) it authenticates the user, if not, the authentication
 * fails.
 * 
 * @author Stuart Lewis
 * @author J. Pol (extracted some repeated code into methods, added EASY authentication)
 */
public class EasySwordServer implements SWORDServer
{
    /** A counter to count submissions, so the response to a deposit can increment */
    private static int    counter = 0;

    /** Logger */
    private static Logger log     = LoggerFactory.getLogger(EasySwordServer.class);

    /**
     * Provides a dumb but plausible service document - it contains an anonymous workspace and
     * collection, and one personalised for the onBehalfOf user.
     * 
     * @param onBehalfOf
     *        The user that the client is acting on behalf of
     * @throws SWORDAuthenticationException
     *         If the credentials are bad
     * @throws SWORDErrorException
     *         If something goes wrong, such as
     */
    public ServiceDocument doServiceDocument(final ServiceDocumentRequest sdr) throws SWORDAuthenticationException, SWORDErrorException, SWORDException
    {
        // Authenticate the user
        final String userID = sdr.getUsername();
        final String password = sdr.getPassword();
        if (userID != null) {
            if (null == SwordDatasetUtil.getUser(userID, password))
                throw new SWORDAuthenticationException(userID + " not authenticated");
        }

        // Allow users to force the throwing of a SWORD error exception by setting
        // the OnBehalfOf user to 'error'
        if ((sdr.getOnBehalfOf() != null) && (sdr.getOnBehalfOf().equals("error")))
        {
            // Throw the error exception
            throw new SWORDErrorException(ErrorCodes.MEDIATION_NOT_ALLOWED, "Mediated deposits not allowed");
        }

        // Create and return a dummy ServiceDocument
        final ServiceDocument document = new ServiceDocument();
        final Service service = new Service("1.3", true, true);
        document.setService(service);
        final String locationBase = toLocationBase(sdr.getLocation());

        if (sdr.getLocation().contains("?nested="))
        {
            final Collection collection = createDummyCollection(1);
            collection.setTitle("Nested collection: " + sdr.getLocation().substring(sdr.getLocation().indexOf('?') + 1));
            collection.setLocation(locationBase + "/deposit/nested");
            collection.setCollectionPolicy("No guarantee of service, or that deposits will be retained for any length of time.");
            service.addWorkspace(createWorkSpace(collection, "Nested service document workspace"));
        }
        else
        {
            Collection collection = createDummyCollection(1);
            collection.setTitle("Anonymous submitters collection");
            collection.setLocation(locationBase + "/deposit/anon");
            collection.setAbstract("A collection that anonymous users can deposit into");
            collection.setService(locationBase + "/servicedocument?nested=anon");
            Workspace workspace = createWorkSpace(collection, "Anonymous submitters workspace");
            collection = createDummyCollection(1);
            collection.setTitle("Anonymous submitters other collection");
            collection.setLocation(locationBase + "/deposit/anonymous");
            collection.setAbstract("Another collection that anonymous users can deposit into");
            workspace.addCollection(collection);
            service.addWorkspace(workspace);

            if (sdr.getUsername() != null)
            {
                collection = createDummyCollection(0.8f);
                collection.setTitle("Authenticated collection for " + userID);
                collection.setLocation(locationBase + "/deposit/" + userID);
                collection.setAbstract("A collection that " + userID + " can deposit into");
                collection.setService(locationBase + "/servicedocument?nested=authenticated");
                workspace = createWorkSpace(collection, "Authenticated workspace for " + userID);
                collection = createDummyCollection(0.123f);
                collection.setTitle("Second authenticated collection for " + userID);
                collection.setLocation(locationBase + "/deposit/" + userID + "-2");
                collection.setAbstract("A collection that " + userID + " can deposit into");
                workspace.addCollection(collection);
                service.addWorkspace(workspace);
            }
        }

        final String onBehalfOf = sdr.getOnBehalfOf();
        if ((onBehalfOf != null) && (!onBehalfOf.equals("")))
        {
            final Collection collection = createDummyCollection(0.8f);
            collection.setTitle("Personal collection for " + onBehalfOf);
            collection.setLocation(locationBase + "/deposit?user=" + onBehalfOf);
            collection.setAbstract("An abstract goes in here");
            collection.setMediation(true);
            service.addWorkspace(createWorkSpace(collection, "Personal workspace for " + onBehalfOf));
        }
        // log.debug("document is: " + document.toString());
        return document;
    }

    private String toLocationBase(final String fullLocation) throws SWORDErrorException
    {
        final URL url;
        try
        {
            url = new URL(fullLocation);
        }
        catch (final MalformedURLException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, fullLocation + " Invalid location: " + exception.getMessage());
        }
        final String subPath = new File(url.getPath()).getParent();
        final String location = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + subPath;
        log.debug("location is: " + location + "    " + fullLocation);

        return location;
    }

    private Workspace createWorkSpace(final Collection collection, final String title)
    {
        final Workspace workspace = new Workspace();
        workspace.setTitle(title);
        workspace.addCollection(collection);
        return workspace;
    }

    private Collection createDummyCollection(final float qualityValue)
    {
        final Collection collection = new Collection();
        collection.setCollectionPolicy("No guarantee of service, or that deposits will be retained for any length of time.");
        collection.setTreatment("This is a dummy server");
        collection.addAccepts("application/zip");
        collection.addAccepts("application/xml");
        collection.addAcceptPackaging("http://purl.org/net/sword-types/METSDSpaceSIP");
        collection.addAcceptPackaging("http://purl.org/net/sword-types/bagit", qualityValue);
        return collection;
    }

    public DepositResponse doDeposit(final Deposit deposit) throws SWORDAuthenticationException, SWORDErrorException, SWORDException
    {
        final EasyUser user = SwordDatasetUtil.getUser(deposit.getUsername(), deposit.getPassword());
        if (user==null)
            throw new SWORDAuthenticationException(deposit.getUsername() + " not authenticated");

        // Check this is a collection that takes "on behalf of" deposits, else thrown an error
        if (((deposit.getOnBehalfOf() != null) && (!deposit.getOnBehalfOf().equals(""))) && (!deposit.getLocation().contains("deposit?user=")))
            throw new SWORDErrorException(ErrorCodes.MEDIATION_NOT_ALLOWED, "Mediated deposit not allowed to this collection");

        final UnzipResult unzipped = new UnzipResult(deposit.getFile());
        if (!deposit.isNoOp())
        {
            // Handle the deposit
            unzipped.submit(user);
            counter++;
        }
        final Summary wrapSummary = wrapSummary(deposit.getFilename(), deposit.getSlug(), unzipped.getFiles());
        return wrapResponse(wrapSwordEntry(deposit, wrapSummary));
    }

    private static SWORDEntry wrapSwordEntry(final Deposit deposit, final Summary summary) throws SWORDException
    {
        final SWORDEntry swordEntry = new SWORDEntry();

        swordEntry.setTitle(wrapTitle());
        swordEntry.addCategory("Category");
        swordEntry.setId(wrapID(deposit.getSlug()));
        swordEntry.setUpdated(getDateTime());
        swordEntry.setSummary(summary);
        swordEntry.addAuthors(wrapAuthor(deposit.getUsername()));
        swordEntry.addLink(wrapEditMediaLink());
        swordEntry.addLink(wrapEditLink());
        swordEntry.setGenerator(wrapGenerator());
        swordEntry.setContent(wrapContent());
        swordEntry.setTreatment("Short back and sides");
        swordEntry.setNoOp(deposit.isNoOp());
        if (deposit.getOnBehalfOf() != null)
            swordEntry.addContributor(wrapContributor(deposit.getOnBehalfOf()));
        if (deposit.isVerbose())
            swordEntry.setVerboseDescription("I've done a lot of hard work to get this far!");

        return swordEntry;
    }

    private DepositResponse wrapResponse(final SWORDEntry swordEntry)
    {
        final DepositResponse depostiResponse = new DepositResponse(Deposit.CREATED);
        depostiResponse.setEntry(swordEntry);
        depostiResponse.setLocation("http://www.myrepository.ac.uk/atom/" + counter);
        return depostiResponse;
    }

    private static Title wrapTitle()
    {
        final Title title = new Title();
        title.setContent("DummyServer Deposit: #" + counter);
        return title;
    }

    private static String wrapID(final String slug)
    {
        final String id;
        if (slug != null)
        {
            id = slug + " - ID: " + counter;
        }
        else
        {
            id = "ID: " + counter;
        }
        return id;
    }

    private static Link wrapEditMediaLink()
    {
        final Link em = new Link();
        em.setRel("edit-media");
        em.setHref("http://www.myrepository.ac.uk/sdl/workflow/my deposit");
        return em;
    }

    private static Link wrapEditLink()
    {
        final Link e = new Link();
        e.setRel("edit");
        e.setHref("http://www.myrepository.ac.uk/sdl/workflow/my deposit.atom");
        return e;
    }

    private static Summary wrapSummary(final String filename, final String slug, final List<File> fileList)
    {
        final Summary summary = new Summary();
        final StringBuffer fileNames = new StringBuffer("Deposit file contained: ");

        if (filename != null)
        {
            fileNames.append("(filename = " + filename + ") ");
        }
        if (slug != null)
        {
            // Slug may be used to supply a deposit identifier for use as the <atom:id> value.
            fileNames.append("(slug = " + slug + ") ");
        }
        fileNames.append(Arrays.deepToString(fileList.toArray()));

        summary.setContent(fileNames.toString());
        return summary;
    }

    private static Generator wrapGenerator()
    {
        final Generator generator = new Generator();
        generator.setContent("Stuart's Dummy SWORD Server");
        generator.setUri("http://dummy-sword-server.example.com/");
        generator.setVersion("1.3");
        return generator;
    }

    private static Content wrapContent()
    {
        final Content content = new Content();
        final String mediaType = "application/zip";
        try
        {
            content.setType(mediaType);
        }
        catch (final InvalidMediaTypeException exception)
        {
            log.error(mediaType,exception);
        }
        content.setSource("http://www.myrepository.ac.uk/sdl/uploads/upload-" + counter + ".zip");
        return content;
    }

    private static Contributor wrapContributor(final String onBehalfOf)
    {
        final Contributor contributor = new Contributor();
        contributor.setName(onBehalfOf);
        contributor.setEmail(onBehalfOf + "@myrepository.ac.uk");
        return contributor;
    }

    private static Author wrapAuthor(final String username)
    {
        final Author author = new Author();
        if (username != null)
            author.setName(username);
        else
            author.setName("unknown");
        return author;
    }

    private static String getDateTime()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        return sdf.format(new Date());
    }

    public AtomDocumentResponse doAtomDocument(final AtomDocumentRequest adr) throws SWORDAuthenticationException, SWORDErrorException, SWORDException
    {
        if (null == SwordDatasetUtil.getUser(adr.getUsername(), adr.getPassword()))
            throw new SWORDAuthenticationException(adr.getUsername() + " not authenticated");

        return new AtomDocumentResponse(HttpServletResponse.SC_OK);
    }
}
