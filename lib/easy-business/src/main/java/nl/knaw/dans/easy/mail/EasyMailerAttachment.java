package nl.knaw.dans.easy.mail;

import javax.activation.DataSource;

public interface EasyMailerAttachment
{
    DataSource getDataSource();

    String getName();

    String getDescription();

}
