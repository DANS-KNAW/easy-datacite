package nl.knaw.dans.easy.business.dataset;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionDispatcherFactory
{

    public enum Style
    {
        WEB, BATCH_INGEST, SWORD_INGEST
    }

    public static final Style DEFAULT_STYLE = Style.WEB;

    private static final Logger logger = LoggerFactory.getLogger(SubmissionDispatcherFactory.class);

    private static SubmissionDispatcherFactory INSTANCE;

    private Style style = DEFAULT_STYLE;

    private SubmissionDispatcherFactory()
    {
        this(DEFAULT_STYLE);
    }

    /**
     * NO PUBLIC CONSTRUCTOR, use with Spring context.
     * 
     * @param style
     */
    public SubmissionDispatcherFactory(Style style)
    {
        this.style = style;
        INSTANCE = this;
        logger.info("Created " + this + " in style " + this.style);
    }

    private static SubmissionDispatcherFactory getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new SubmissionDispatcherFactory();
        }
        return INSTANCE;
    }

    public static SubmissionDispatcher newSubmissionDispatcher()
    {
        return getInstance().newDispatcher();
    }

    private SubmissionDispatcher newDispatcher()
    {
        List<SubmissionProcessor> processors = new ArrayList<SubmissionProcessor>();
        List<SubmissionProcessor> threadedProcessors = new ArrayList<SubmissionProcessor>();

        if (Style.WEB.equals(style))
        {
            processors.add(new WebDepositFormMetadataValidator());
            processors.add(new MetadataPidGenerator());
            processors.add(new MetadataLicenseGenerator());
            threadedProcessors.add(new DatasetIngester(true));
            threadedProcessors.add(new MailSender());
        }
        else if (Style.SWORD_INGEST.equals(style))
        {
            processors.add(new MetadataPidGenerator());
            processors.add(new MetadataLicenseGenerator());
            processors.add(new DatasetIngester(true));
            processors.add(new MailSender());
        }
        else if (Style.BATCH_INGEST.equals(style))
        {
            processors.add(new MetadataPidGenerator());
            processors.add(new MetadataLicenseGenerator());
            processors.add(new DatasetIngester(false));
        }
        else
        {
            throw new IllegalStateException("No style defined for " + style);
        }

        SubmissionDispatcher dispatcher = new SubmissionDispatcher();
        dispatcher.setProcessors(processors);
        dispatcher.setThreadedProcessors(threadedProcessors);
        return dispatcher;
    }

}
