package nl.knaw.dans.easy.tools;

import java.util.List;

import nl.knaw.dans.easy.tools.exceptions.FatalException;

public interface IdConverter {

    List<String> convert(String otherId) throws FatalException;

}
