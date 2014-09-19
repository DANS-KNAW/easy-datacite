package nl.knaw.dans.common.lang.repo.collections;

/**
 * This object is a container and a container item. It might therefore be called recursive as it may contain itself. A folder is a good example of a recursive
 * item as in a filesystem it can be contained by another folder and may also contain folders and files.
 * 
 * @author lobo
 */
public interface DmoRecursiveItem extends DmoContainer, DmoContainerItem {

}
