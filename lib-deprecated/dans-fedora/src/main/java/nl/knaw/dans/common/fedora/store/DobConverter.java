package nl.knaw.dans.common.fedora.store;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;

public interface DobConverter<T extends DataModelObject> {
    DmoNamespace getObjectNamespace();

    /**
     * Serialize the given dmo to a {@link DigitalObject}.
     * 
     * @param dmo
     *        the dmo to serialize
     * @return DigitalObject representation of the given dmo
     * @throws ObjectSerializationException
     *         if the dmo could not be serialized
     */
    DigitalObject serialize(T dmo) throws ObjectSerializationException;

    /**
     * Deserialize the given DigitalObject to the given dmo.
     * 
     * @param dob
     *        DigitalObject as input
     * @param dmo
     *        DataModelObject as output
     * @throws ObjectDeserializationException
     *         if the DigitalObject could not be deserialized
     */
    void deserialize(DigitalObject dob, T dmo) throws ObjectDeserializationException;

    // hack: update of dmo bypasses dobconverter and setting of parentDisciplines
    void prepareForUpdate(T dmo) throws ObjectSerializationException;

}
