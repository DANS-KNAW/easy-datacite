package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadHistoryConverter extends AbstractDobConverter<DownloadHistory> {

    private static final Logger logger = LoggerFactory.getLogger(DownloadHistoryConverter.class);

    public DownloadHistoryConverter() {
        super(DownloadHistory.NAMESPACE);
    }

    @Override
    public void deserialize(DigitalObject digitalObject, DownloadHistory downloadHistory) throws ObjectDeserializationException {
        checkNamespace(digitalObject, downloadHistory);

        digitalObject.writeObjectProperties(downloadHistory);

        try {
            DatastreamVersion downloadListVersion = digitalObject.getLatestVersion(DownloadList.UNIT_ID);
            if (downloadListVersion != null) {
                Element element = downloadListVersion.getXmlContentElement();
                DownloadList downloadList = (DownloadList) JiBXObjectFactory.unmarshal(DownloadList.class, element);
                downloadList.setTimestamp(downloadListVersion.getTimestamp());
                downloadHistory.setDownloadList(downloadList);
            } else {
                logger.warn("No downloadList found on retrieved digital object. sid=" + digitalObject.getSid());
            }
        }
        catch (XMLDeserializationException e) {
            throw new ObjectDeserializationException(e);
        }

        deserializeRelationships(digitalObject, downloadHistory);
    }

}
