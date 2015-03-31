package nl.knaw.dans.easy.business.dataset;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.DataciteService;
import nl.knaw.dans.easy.DataciteServiceConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionDispatcherFactory {

    public enum Style {
        WEB, BATCH_INGEST, SWORD_INGEST
    }

    private static final Logger logger = LoggerFactory.getLogger(SubmissionDispatcherFactory.class);

    private static SubmissionDispatcherFactory INSTANCE;

    private final Style style;
    private final PidClient pidClient;
    private final DataciteService dataciteService;

    /**
     * NO PUBLIC CONSTRUCTOR, use with Spring context.
     * 
     * @param style
     * @param pidService
     *        the URL (protocol,host,port) of the RESTfull PID service
     * @param dataciteServiceConfiguration
     * @throws MalformedURLException
     */
    public SubmissionDispatcherFactory(Style style, URL pidService, DataciteServiceConfiguration dataciteServiceConfiguration) {
        this.style = style;
        pidClient = new PidClient(pidService);
        dataciteService = new DataciteService(dataciteServiceConfiguration);
        INSTANCE = this;
        logger.info(String.format("Created %s in style %s with PID service %s, dataciteService %s and datacite style-sheet %s",
                SubmissionDispatcherFactory.class.getName(), style, pidService, dataciteServiceConfiguration.getDoiRegistrationUri(),
                dataciteServiceConfiguration.getXslEmd2datacite()));
    }

    public static SubmissionDispatcher newSubmissionDispatcher() throws ServiceException {
        if (INSTANCE == null)
            throw new IllegalStateException("SubmissionDispatcherFactory is not configured");
        return INSTANCE.newDispatcher();
    }

    private SubmissionDispatcher newDispatcher() {
        List<SubmissionProcessor> processors = new ArrayList<SubmissionProcessor>();
        List<SubmissionProcessor> threadedProcessors = new ArrayList<SubmissionProcessor>();

        switch (style) {
        case WEB:
            processors.add(new WebDepositFormMetadataValidator());
            processors.add(new MetadataPidGenerator(pidClient));
            processors.add(new MetadataLicenseGenerator());
            threadedProcessors.add(new DatasetIngester(true));
            threadedProcessors.add(new MailSender());
            break;
        case SWORD_INGEST:
            processors.add(new MetadataPidGenerator(pidClient));
            processors.add(new MetadataLicenseGenerator());
            processors.add(new DatasetIngester(true));
            processors.add(new MailSender());
            break;
        case BATCH_INGEST:
            processors.add(new MetadataPidGenerator(pidClient));
            processors.add(new MetadataLicenseGenerator());
            processors.add(new DatasetIngester(false));
            processors.add(new EbiuDoiSubmitter(dataciteService));
            break;
        default:
            throw new IllegalStateException("No style defined for " + style);
        }
        SubmissionDispatcher dispatcher = new SubmissionDispatcher();
        dispatcher.setProcessors(processors);
        dispatcher.setThreadedProcessors(threadedProcessors);
        return dispatcher;
    }
}
