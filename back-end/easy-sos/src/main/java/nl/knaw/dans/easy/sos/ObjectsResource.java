package nl.knaw.dans.easy.sos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.InjectParam;

@Path("/objects")
public class ObjectsResource
{
    private static final String AUTHENTICATION_TYPE = "Basic ";
    private static final Logger log = LoggerFactory.getLogger(ObjectsResource.class);

    @Context
    private HttpHeaders requestHeaders;

    @InjectParam(value = "rootDirectory")
    private File rootDirectory;

    @InjectParam(value = "objectsBaseUrl")
    private URL objectsBaseUrl;

    @InjectParam(value = "user")
    private String user;

    @InjectParam(value = "password")
    private String password;

    @GET
    @Produces("text/plain")
    public String getObjects()
    {
        return "Simple Object Storage";
    }

    @GET
    @Path("{uuid}")
    @Produces("application/octet-stream")
    public File getObject(@PathParam("uuid")
    String uuid)
    {
        authenticate();
        return new File(rootDirectory, new StoredObject(uuid).getRelativePath());
    }

    @HEAD
    @Path("{uuid}")
    @Produces("application/octet-stream")
    public Response getObjectStatus(@PathParam("uuid")
    String uuid)
    {
        authenticate();
        if (!new File(rootDirectory, new StoredObject(uuid).getRelativePath()).exists())
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        else
            return Response.status(HttpServletResponse.SC_OK).build();
    }

    @POST
    @Consumes("application/octet-stream")
    public Response createObject(InputStream data, @HeaderParam("Content-MD5")
    String contentMd5)
    {
        authenticate();
        StoredObject so = new StoredObject();
        log.debug("Generated UUID for new object: {}", so.getUuid());
        try
        {
            String relativePath = so.getRelativePath();
            File targetFile = new File(rootDirectory, relativePath);
            log.debug("Copying data to target file: {}", targetFile);
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileUtils.copyInputStreamToFile(new DigestInputStream(data, md), targetFile);
            byte[] digest = md.digest();
            if (!Arrays.equals(digest, Base64.decodeBase64(contentMd5)))
            {
                log.warn("Provided Content-MD5 does not check out. Deleting file from storage (though not deleting directories)");
                targetFile.delete();
                throw new WebApplicationException(Response.status(HttpServletResponse.SC_BAD_REQUEST)
                        .entity("Content-MD5 header does not match calculated content MD5").build());
            }
            FileUtils.write(new File(rootDirectory, relativePath + ".md5"), Hex.encodeHexString(digest));
            log.debug("Copy SUCCEEDED");
            return Response.status(HttpServletResponse.SC_CREATED).location(buildObjectUriFor(so)).build();
        }
        catch (IOException e)
        {
            log.error("I/O Error storing object", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Message digest algorithm not supported", e);
        }
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    @DELETE
    @Path("{uuid}")
    @Produces("application/octet-stream")
    public Response deleteObject(@PathParam("uuid")
    String uuid)
    {
        authenticate();
        File f = new File(rootDirectory, new StoredObject(uuid).getRelativePath());
        if (!f.exists())
        {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        else if (f.delete())
        {
            return Response.ok().build();
        }
        else
        {
            return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    private URI buildObjectUriFor(StoredObject so)
    {
        try
        {
            return new URL(objectsBaseUrl, so.getUuid()).toURI();
        }
        catch (URISyntaxException e)
        {
            log.error("Invalid URI syntax", e);
        }
        catch (MalformedURLException e)
        {
            log.error("Invalid URL syntax", e);
        }
        return null;
    }

    private void authenticate()
    {
        List<String> authHeaders = requestHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null && !authHeaders.isEmpty() && authHeaders.get(0).startsWith(AUTHENTICATION_TYPE))
        {
            String decodedAuthHeader = decodeBase64EncodedAsciiString(authHeaders.get(0).substring(AUTHENTICATION_TYPE.length()));
            if (decodedAuthHeader.contains(":"))
            {
                String[] auth = decodedAuthHeader.split(":");
                authenticate(auth[0], auth[1]);
            }
        }
        else
        {
            throw new WebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private String decodeBase64EncodedAsciiString(String encoded)
    {
        try
        {
            return new String(Base64.decodeBase64(encoded), "US-ASCII");
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("FATAL: US-ASCII NOT SUPPORTED ???");
        }
        return null;
    }

    private void authenticate(String u, String p)
    {
        if (user.equals(u) && password.equals(p))
        {
            log.info("User {} authenticated", u);
        }
        else
        {
            log.warn("Authentication for user {} failed, wrong password", u);
            throw new WebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
