package nl.knaw.dans.easy.domain.emd.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.emd.validation.archaeology.EasSpatialValidator;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.Validator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import static nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat.ARCHAEOLOGY;
import static nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat.HISTORY;
import static nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat.SOCIOLOGY;
import static nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat.UNSPECIFIED;

/**
 * Helper class that creates the lists of validators used to initialize the validators per discipline.
 */
public class ValidatorLists
{
    private static final String                               XPATH_RIGHTS   = "/emd:easymetadata/emd:rights/dcterms:accessRights/";
    private static final String                               XPATH_RELATION = "/emd:easymetadata/emd:relation/dcterms:relation/";
    private static final MetadataFormat[]                     ALL_FORMATS    = MetadataFormat.values();
    private static final MetadataFormat[]                     COMMON_FORMATS = {HISTORY, SOCIOLOGY, UNSPECIFIED};
    private static final Map<MetadataFormat, List<Validator>> validatorLists = new HashMap<MetadataFormat, List<Validator>>();

    /**
     * TODO add validators for the unchecked xml files
     * 
     * <pre>
     *     discipline/emd
     *     |-- choicelist
     *     |   |-- archaeology
     *     |   |   |-- dc
     *     |   |   |   |-- identifier.xml
     *     |   |   |   `-- subject.xml
     *     |   |   |-- dcterms
     *  V  |   |   |   |-- accessrights.xml 
     *     |   |   |   |-- date.xml
     *     |   |   |   `-- temporal.xml
     *     |   |   `-- eas
     *  V  |   |       |-- spatial.xml
     *     |   |       `-- spatial_en.xml
     *     |   `-- common
     *     |       |-- dc
     *     |       |   |-- format.xml
     *     |       |   |-- language.xml
     *     |       |   `-- type.xml
     *     |       `-- dcterms
     *  V  |           |-- accessrights.xml
     *     |           |-- audience.xml                old easy
     *     |           |-- date.xml
     *     |           `-- relation.xml                more like spatial than a simple choice list
     *     `-- recursivelist
     *         `-- archaeology
     *             |-- dc
     *             |   `-- subject.xml
     *             `-- dcterms
     *                 `-- temporal.xml
     * </pre>
     */
    static
    {
        /*
         * TODO please review: This helper class is used to initialize singletons with a synchronized
         * instance method. According to the discussions below, this static initializer is thread safe
         * but is called once per class loader. Do we bother about reloading and is the overhead of
         * synchronization really needed?
         * http://stackoverflow.com/questions/878577/are-java-static-initializers-thread-safe
         * http://java-x.blogspot.com/2006/03/singleton-pattern-in-java.html
         */

        for (final MetadataFormat type : ALL_FORMATS)
            validatorLists.put(type, new ArrayList<Validator>());
        addValidators();
    }

    private static void addValidators()
    {
        validatorLists.get(ARCHAEOLOGY).add(new EasSpatialValidator());
        for (final MetadataFormat type : COMMON_FORMATS)
            validatorLists.get(type).add(createRelationsValidator("common.dcterms.relation", XPATH_RELATION));
        for (final MetadataFormat type : ALL_FORMATS)
            validatorLists.get(type).add(createRightsValidator((src(type) + ".dcterms.accessrights"), XPATH_RIGHTS));
    }

    private static String src(final MetadataFormat type)
    {
        if (type == ARCHAEOLOGY)
            return ARCHAEOLOGY.name().toLowerCase();
        return "common";
    }

    private static ChoiceListValidator createRightsValidator(final String listId, final String xPathStub)
    {
        return new ChoiceListValidator(listId, xPathStub)
        {
            @Override
            public String getValidatedValue(final EasyMetadata emd)
            {
                final List<String> values = emd.getEmdRights().getValues();
                if (values == null || values.size() == 0)
                    return null;
                return values.get(0);
            }
        };
    }

    private static ChoiceListValidator createRelationsValidator(final String listId, final String xPathStub)
    {
        return new ChoiceListValidator(listId, xPathStub)
        {
            @Override
            public String getValidatedValue(final EasyMetadata emd)
            {
                final List<String> values = emd.getEmdRelation().getValues();
                if (values == null || values.size() == 0)
                    return null;
                return values.get(0);
            }
        };
    }

    public static List<Validator> getArchaeologyValidators(final MetadataFormat metadataFormat)
    {
        return validatorLists.get(metadataFormat);
    }
}
