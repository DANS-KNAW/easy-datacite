package nl.knaw.dans.easy.data.audit;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.joda.time.DateTime;

public interface AuditRecord<T> {

    DateTime getDate();

    EasyUser getSessionUser();

    T getTracedObject();

    String getMethodSignature();

    Object[] getArguments();

    String getRecord();

}
