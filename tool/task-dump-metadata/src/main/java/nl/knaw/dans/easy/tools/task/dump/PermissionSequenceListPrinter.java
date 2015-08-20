package nl.knaw.dans.easy.tools.task.dump;

import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;

public class PermissionSequenceListPrinter {
    private PermissionSequenceList permSequenceList;

    PermissionSequenceListPrinter(PermissionSequenceList permSequenceList) {
        this.permSequenceList = permSequenceList;
    }

    private String property(String id, String key, String value) {
        return String.format("PERMREQ[%s]:%s=%s\n", id, key, new CrlfEscapedString(value));
    }

    @Override
    public String toString() {
        List<PermissionSequence> sequences = permSequenceList.getPermissionSequences();
        Iterator<PermissionSequence> iterator = sequences.iterator();
        StringBuilder stringBuilder = new StringBuilder();

        while (iterator.hasNext()) {
            appendSequence(iterator.next(), stringBuilder);
        }

        return stringBuilder.toString();
    }

    private void appendSequence(PermissionSequence s, StringBuilder b) {
        b.append(property(s.getRequesterId(), "requesterId", s.getRequesterId()));
        b.append(property(s.getRequesterId(), "requestTitle", s.getRequestTitle()));
        b.append(property(s.getRequesterId(), "isAcceptingConditionsOfUse", Boolean.toString(s.isAcceptingConditionsOfUse())));
        b.append(property(s.getRequesterId(), "requestTheme", s.getRequestTheme()));
        b.append(property(s.getRequesterId(), "replyText", s.getReplyText()).toString());
        b.append(property(s.getRequesterId(), "state", s.getState().toString().toLowerCase()));
    }
}
