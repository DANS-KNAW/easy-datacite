package nl.knaw.dans.easy.tools.task.dump;

import fedora.server.types.gen.ObjectFields;

public interface DatasetFinder {
    public ObjectFields next();
}
