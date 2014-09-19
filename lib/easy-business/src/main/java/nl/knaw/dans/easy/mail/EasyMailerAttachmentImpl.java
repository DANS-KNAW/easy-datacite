package nl.knaw.dans.easy.mail;

import javax.activation.DataSource;

public class EasyMailerAttachmentImpl implements EasyMailerAttachment {
    private final DataSource dataSource;
    private final String name;
    private final String description;

    public EasyMailerAttachmentImpl(DataSource dataSource, String name, String description) {
        this.dataSource = dataSource;
        this.name = name;
        this.description = description;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
