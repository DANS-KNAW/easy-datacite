package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.i.dmo.collections.DmoCollection;


public class ECollectionEntry implements Serializable
{

    private static final long serialVersionUID = -6365235991824244529L;

    private final DmoStoreId collectionId;
    private final String label;
    private final String shortName;
    private final boolean oaiSet;
    private final int level;
    
    private boolean member;
    private boolean publishedAsOAISet;
    
    public ECollectionEntry(DmoCollection collection, int level)
    {
        if (!ECollection.isECollection(collection))
        {
            throw new IllegalArgumentException("Not a ECollection: " + collection.getStoreId());
        }
        this.collectionId = collection.getDmoStoreId();
        this.label = collection.getLabel();
        this.shortName = collection.getShortName();
        this.oaiSet = collection.isPublishedAsOAISet();
        this.level = level;
    }

    public boolean isMember()
    {
        return member;
    }

    public void setMember(boolean member)
    {
        this.member = member;
    }

    public boolean isPublishedAsOAISet()
    {
        return publishedAsOAISet;
    }

    public void setPublishedAsOAISet(boolean publishedAsOAISet)
    {
        if (isOaiSet())
        {
            this.publishedAsOAISet = publishedAsOAISet;
        }
    }

    public DmoStoreId getCollectionId()
    {
        return collectionId;
    }

    public String getLabel()
    {
        return label;
    }

    public String getShortName()
    {
        return shortName;
    }
    
    public String getLevelName()
    {
        return StringUtils.repeat("-", level -1) + shortName;
    }

    public boolean isOaiSet()
    {
        return oaiSet;
    }
    
    public int getLevel()
    {
        return level;
    }
   
    @Override
    public String toString()
    {
        return new StringBuilder(this.getClass().getSimpleName()) //
            .append(" [") //
            .append(collectionId) //
            .append("] ") //
            .append(label) //
            .append(" [levelName=") //
            .append(getLevelName()) //
            .append("] [level=") //
            .append(level) //
            .append("] [isOAISet=") //
            .append(oaiSet) //
            .append("] [isMember=") //
            .append(member) //
            .append("] [isPublishedAsOAISet=") //
            .append(publishedAsOAISet) //
            .append("]") //
            .toString();
    }

}
