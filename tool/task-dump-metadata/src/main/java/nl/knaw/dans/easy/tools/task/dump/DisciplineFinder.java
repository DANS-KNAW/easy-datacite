package nl.knaw.dans.easy.tools.task.dump;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadataImpl;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.MIMETypedStream;

public class DisciplineFinder {
    private static final Logger logger = LoggerFactory.getLogger(DisciplineFinder.class);

    private Map<String, DisciplineMetadata> idToMetadata = new HashMap<String, DisciplineMetadata>();

    List<String> find(final List<String> disciplineIds) throws FatalException {
        final List<String> disciplines = new LinkedList<String>();

        for (final String disciplineId : disciplineIds) {
            disciplines.add(findDiscipline(disciplineId).getOICode());
        }

        return disciplines;
    }

    private DisciplineMetadata findDiscipline(final String disciplineId) throws FatalException {
        try {
            logger.debug(String.format("Looking for discpine with id '%s'", disciplineId));

            if (!idToMetadata.containsKey(disciplineId)) {
                logger.debug("... not in cache, getting from repository ...");

                final MIMETypedStream stream = Application.getFedora().getDatastreamAccessor()
                        .getDatastreamDissemination(disciplineId, DisciplineMetadataImpl.UNIT_ID, null);
                idToMetadata.put(disciplineId, (DisciplineMetadata) JiBXObjectFactory.unmarshal(DisciplineMetadataImpl.class, stream.getStream()));
            }

            return idToMetadata.get(disciplineId);
        }
        catch (RepositoryException e) {
            logger.error("Could not read discipline metadata");
            throw new FatalException("Could not read discipline metadata");
        }
        catch (XMLDeserializationException e) {
            logger.error("Could not read discipline metadata");
            throw new FatalException("Could not read discipline metadata");
        }
    }

}
