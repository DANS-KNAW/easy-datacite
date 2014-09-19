package nl.knaw.dans.common.lang.os;

import java.io.File;
import java.io.IOException;

public interface OsStrategy {

    int setAllRWX(File file, Appendable out, Appendable err) throws IOException;

    int setAllRWX(String filename, Appendable out, Appendable err) throws IOException;

}
