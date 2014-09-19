/**
 * Class for mount encoders that uses paths and forward slashes.
 * 
 * @author Eko Indarto
 */

package nl.knaw.dans.easy.web.download;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.AbstractRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.resource.ISharedResourceRequestTarget;
import org.apache.wicket.request.target.resource.SharedResourceRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloadRequestTargetUrlCodingStrategy extends AbstractRequestTargetUrlCodingStrategy {

    private String keyPrefix;
    private static final String PARAMS = "params";
    private static final Logger logger = LoggerFactory.getLogger(FileDownloadRequestTargetUrlCodingStrategy.class);

    public FileDownloadRequestTargetUrlCodingStrategy(String mountPath, String namePrefix) {
        super(mountPath);
        this.keyPrefix = EasyWicketApplication.WICKET_APPLICATION_ALIAS + "/" + namePrefix;
    }

    public IRequestTarget decode(RequestParameters requestParameters) {
        if (requestParameters != null) {
            String downloadParams = requestParameters.getPath().substring(getMountPath().length() + 1);
            String str = urlDecodePathComponent(downloadParams);
            Map<String, String> map = new HashMap<String, String>();
            map.put(PARAMS, str);
            requestParameters.setParameters(map);
            requestParameters.setResourceKey(keyPrefix);
            return new SharedResourceRequestTarget(requestParameters);
        } else {
            logger.error("No request parameters defined");
            // in this case, HTTP ERROR 404 will display.
            return null;
        }
    }

    public CharSequence encode(IRequestTarget requestTarget) {
        String key = ((ISharedResourceRequestTarget) requestTarget).getResourceKey();
        CharSequence sc = getMountPath() + key.substring(keyPrefix.length());
        return sc;
    }

    public boolean matches(IRequestTarget requestTarget) {
        if (!(requestTarget instanceof ISharedResourceRequestTarget))
            return false;
        String key = ((ISharedResourceRequestTarget) requestTarget).getResourceKey();
        return key.startsWith(keyPrefix);
    }
}
