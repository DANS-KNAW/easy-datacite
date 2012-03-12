package nl.knaw.dans.easy.domain.emd.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.emd.validation.archaeology.EasSpatialValidator;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.Validator;

public class ValidatorLists
{
    private static final String                       ARCHAEOLOGY    = "archaeology";
    private static final String                       COMMON         = "common";
    private static final String[]                     validatorTypes = {COMMON, ARCHAEOLOGY};
    private static final Map<String, List<Validator>> validatorLists = new HashMap<String, List<Validator>>();

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

        for (String type : validatorTypes)
        {
            validatorLists.put(type, new ArrayList<Validator>());
        }
        validatorLists.get(ARCHAEOLOGY).add(new EasSpatialValidator());
        addChoiclistValidators();
    }

    private static void addChoiclistValidators()
    {
        validatorLists.get(COMMON).add(new ChoiceListValidator(COMMON + ".dcterms.relation", "/emd:easymetadata/emd:relation/dcterms:relation/")
        {
            @Override
            public String getValidatedValue(EasyMetadata emd)
            {
                return emd.getEmdRelation().getValues().get(0);
            }
        });
        for (String type : validatorTypes)
        {
            validatorLists.get(type).add(new ChoiceListValidator(type + ".dcterms.accessrights", "/emd:easymetadata/emd:rights/dcterms:accessRights/")
            {
                @Override
                public String getValidatedValue(EasyMetadata emd)
                {
                    return emd.getEmdRights().getValues().get(0);
                }
            });
        }
    }

    public static List<Validator> getCommonValidators()
    {
        return validatorLists.get(COMMON);
    }

    public static List<Validator> getArchaeologyValidators()
    {
        return validatorLists.get(ARCHAEOLOGY);
    }
}
