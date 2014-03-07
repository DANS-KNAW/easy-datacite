package nl.knaw.dans.common.jibx.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.XMLLangString;

/**
 * A java implementation of the Dublin Core element set. The Dublin Core Metadata Element Set is a
 * vocabulary of fifteen properties for use in resource description. The name "Dublin" is due to its
 * origin at a 1995 invitational workshop in Dublin, Ohio; "core" because its elements are broad and
 * generic, usable for describing a wide range of resources.
 * <p/>
 * This class is observable. However, if changes are made directly to the lists of values this class is
 * controlling, no notifications will be send.
 * 
 * @author ecco
 */
public class JiBXDublinCoreMetadata extends AbstractTimestampedJiBXObject<DublinCoreMetadata> implements DublinCoreMetadata
{

    private static final long serialVersionUID = -8608043922156674515L;

    private boolean versionable;

    private String unitId;

    private List<XMLLangString> xlContributor = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlCoverage = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlCreator = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlDate = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlDescription = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlFormat = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlIdentifier = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlLanguage = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlPublisher = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlRelation = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlRights = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlSource = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlSubject = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlTitle = new ArrayList<XMLLangString>();
    private List<XMLLangString> xlType = new ArrayList<XMLLangString>();

    public JiBXDublinCoreMetadata()
    {

    }

    public JiBXDublinCoreMetadata(String unitId)
    {
        this.unitId = unitId;
    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId()
    {
        return unitId == null ? UNIT_ID : unitId;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
        setModified();
    }

    public void addContributor(String contributor)
    {
        this.xlContributor.add(new JiBXLangString(contributor));
        setModified(PropertyName.Contributor);
    }

    public void addCoverage(String coverage)
    {
        this.xlCoverage.add(new JiBXLangString(coverage));
        setModified(PropertyName.Coverage);
    }

    public void addCreator(String creator)
    {
        this.xlCreator.add(new JiBXLangString(creator));
        setModified(PropertyName.Creator);
    }

    public void addDate(String date)
    {
        this.xlDate.add(new JiBXLangString(date));
        setModified(PropertyName.Date);
    }

    public void addDescription(String description)
    {
        this.xlDescription.add(new JiBXLangString(description));
        setModified(PropertyName.Description);
    }

    public void addFormat(String format)
    {
        this.xlFormat.add(new JiBXLangString(format));
        setModified(PropertyName.Format);
    }

    public void addIdentifier(String identifier)
    {
        this.xlIdentifier.add(new JiBXLangString(identifier));
        setModified(PropertyName.Identifier);
    }

    public void addLanguage(String language)
    {
        this.xlLanguage.add(new JiBXLangString(language));
        setModified(PropertyName.Language);
    }

    public void addPublisher(String publisher)
    {
        this.xlPublisher.add(new JiBXLangString(publisher));
        setModified(PropertyName.Publisher);
    }

    public void addRelation(String relation)
    {
        this.xlRelation.add(new JiBXLangString(relation));
        setModified(PropertyName.Relation);
    }

    public void addRights(String rights)
    {
        this.xlRights.add(new JiBXLangString(rights));
        setModified(PropertyName.Rights);
    }

    public void addSource(String source)
    {
        this.xlSource.add(new JiBXLangString(source));
        setModified(PropertyName.Source);
    }

    public void addSubject(String subject)
    {
        this.xlSubject.add(new JiBXLangString(subject));
        setModified(PropertyName.Subject);
    }

    public void addTitle(String title)
    {
        this.xlTitle.add(new JiBXLangString(title));
        setModified(PropertyName.Title);
    }

    public void addType(String type)
    {
        this.xlType.add(new JiBXLangString(type));
        setModified(PropertyName.Type);
    }

    public List<String> getContributor()
    {
        return convertToStringList(xlContributor);
    }

    public List<String> getCoverage()
    {
        return convertToStringList(xlCoverage);
    }

    public List<String> getCreator()
    {
        return convertToStringList(xlCreator);
    }

    public List<String> getDate()
    {
        return convertToStringList(xlDate);
    }

    public List<String> getDescription()
    {
        return convertToStringList(xlDescription);
    }

    public List<String> getFormat()
    {
        return convertToStringList(xlFormat);
    }

    public List<String> getIdentifier()
    {
        return convertToStringList(xlIdentifier);
    }

    public List<String> getLanguage()
    {
        return convertToStringList(xlLanguage);
    }

    public List<String> getPublisher()
    {
        return convertToStringList(xlPublisher);
    }

    public List<String> getRelation()
    {
        return convertToStringList(xlRelation);
    }

    public List<String> getRights()
    {
        return convertToStringList(xlRights);
    }

    public List<String> getSource()
    {
        return convertToStringList(xlSource);
    }

    public List<String> getSubject()
    {
        return convertToStringList(xlSubject);
    }

    public List<String> getTitle()
    {
        return convertToStringList(xlTitle);
    }

    public List<String> getType()
    {
        return convertToStringList(xlType);
    }

    public void setContributor(List<String> contributor)
    {
        this.xlContributor = convertToXMLLangStringList(contributor);
        setModified(PropertyName.Contributor);
    }

    public void setCoverage(List<String> coverage)
    {
        this.xlCoverage = convertToXMLLangStringList(coverage);
        setModified(PropertyName.Coverage);
    }

    public void setCreator(List<String> creator)
    {
        this.xlCreator = convertToXMLLangStringList(creator);
        setModified(PropertyName.Creator);
    }

    public void setDate(List<String> date)
    {
        this.xlDate = convertToXMLLangStringList(date);
        setModified(PropertyName.Date);
    }

    public void setDescription(List<String> description)
    {
        this.xlDescription = convertToXMLLangStringList(description);
        setModified(PropertyName.Description);
    }

    public void setFormat(List<String> format)
    {
        this.xlFormat = convertToXMLLangStringList(format);
        setModified(PropertyName.Format);
    }

    public void setIdentifier(List<String> identifier)
    {
        this.xlIdentifier = convertToXMLLangStringList(identifier);
        setModified(PropertyName.Identifier);
    }

    public void setLanguage(List<String> language)
    {
        this.xlLanguage = convertToXMLLangStringList(language);
        setModified(PropertyName.Language);
    }

    public void setPublisher(List<String> publisher)
    {
        this.xlPublisher = convertToXMLLangStringList(publisher);
        setModified(PropertyName.Publisher);
    }

    public void setRelation(List<String> relation)
    {
        this.xlRelation = convertToXMLLangStringList(relation);
        setModified(PropertyName.Relation);
    }

    public void setRights(List<String> rights)
    {
        this.xlRights = convertToXMLLangStringList(rights);
        setModified(PropertyName.Rights);
    }

    public void setSource(List<String> source)
    {
        this.xlSource = convertToXMLLangStringList(source);
        setModified(PropertyName.Source);
    }

    public void setSubject(List<String> subject)
    {
        this.xlSubject = convertToXMLLangStringList(subject);
        setModified(PropertyName.Subject);
    }

    public void setTitle(List<String> title)
    {
        this.xlTitle = convertToXMLLangStringList(title);
        setModified(PropertyName.Title);
    }

    public void setType(List<String> type)
    {
        this.xlType = convertToXMLLangStringList(type);
        setModified(PropertyName.Type);
    }

    public List<XMLLangString> getXlContributor()
    {
        return xlContributor;
    }

    public void setXlContributor(List<XMLLangString> xlContributor)
    {
        this.xlContributor = xlContributor;
        setModified(PropertyName.Contributor);
    }

    public List<XMLLangString> getXlCoverage()
    {
        return xlCoverage;
    }

    public void setXlCoverage(List<XMLLangString> xlCoverage)
    {
        this.xlCoverage = xlCoverage;
        setModified(PropertyName.Coverage);
    }

    public List<XMLLangString> getXlCreator()
    {
        return xlCreator;
    }

    public void setXlCreator(List<XMLLangString> xlCreator)
    {
        this.xlCreator = xlCreator;
        setModified(PropertyName.Creator);
    }

    public List<XMLLangString> getXlDate()
    {
        return xlDate;
    }

    public void setXlDate(List<XMLLangString> xlDate)
    {
        this.xlDate = xlDate;
        setModified(PropertyName.Date);
    }

    public List<XMLLangString> getXlDescription()
    {
        return xlDescription;
    }

    public void setXlDescription(List<XMLLangString> xlDescription)
    {
        this.xlDescription = xlDescription;
        setModified(PropertyName.Description);
    }

    public List<XMLLangString> getXlFormat()
    {
        return xlFormat;
    }

    public void setXlFormat(List<XMLLangString> xlFormat)
    {
        this.xlFormat = xlFormat;
        setModified(PropertyName.Format);
    }

    public List<XMLLangString> getXlIdentifier()
    {
        return xlIdentifier;
    }

    public void setXlIdentifier(List<XMLLangString> xlIdentifier)
    {
        this.xlIdentifier = xlIdentifier;
        setModified(PropertyName.Identifier);
    }

    public List<XMLLangString> getXlLanguage()
    {
        return xlLanguage;
    }

    public void setXlLanguage(List<XMLLangString> xlLanguage)
    {
        this.xlLanguage = xlLanguage;
        setModified(PropertyName.Language);
    }

    public List<XMLLangString> getXlPublisher()
    {
        return xlPublisher;
    }

    public void setXlPublisher(List<XMLLangString> xlPublisher)
    {
        this.xlPublisher = xlPublisher;
        setModified(PropertyName.Publisher);
    }

    public List<XMLLangString> getXlRelation()
    {
        return xlRelation;
    }

    public void setXlRelation(List<XMLLangString> xlRelation)
    {
        this.xlRelation = xlRelation;
        setModified(PropertyName.Relation);
    }

    public List<XMLLangString> getXlRights()
    {
        return xlRights;
    }

    public void setXlRights(List<XMLLangString> xlRights)
    {
        this.xlRights = xlRights;
        setModified(PropertyName.Rights);
    }

    public List<XMLLangString> getXlSource()
    {
        return xlSource;
    }

    public void setXlSource(List<XMLLangString> xlSource)
    {
        this.xlSource = xlSource;
        setModified(PropertyName.Source);
    }

    public List<XMLLangString> getXlSubject()
    {
        return xlSubject;
    }

    public void setXlSubject(List<XMLLangString> xlSubject)
    {
        this.xlSubject = xlSubject;
        setModified(PropertyName.Subject);
    }

    public List<XMLLangString> getXlTitle()
    {
        return xlTitle;
    }

    public void setXlTitle(List<XMLLangString> xlTitle)
    {
        this.xlTitle = xlTitle;
        setModified(PropertyName.Title);
    }

    public List<XMLLangString> getXlType()
    {
        return xlType;
    }

    public void setXlType(List<XMLLangString> xlType)
    {
        this.xlType = xlType;
        setModified(PropertyName.Type);
    }

    @Override
    public void set(PropertyName name, String value)
    {
        List<String> values = new ArrayList<String>();
        if (value != null)
        {
            values.add(value);
        }
        set(name, values);
    }

    public void set(PropertyName name, List<String> values)
    {
        try
        {
            final Method method = this.getClass().getMethod("set" + name.toString(), List.class);
            method.invoke(this, values);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setXl(PropertyName name, List<XMLLangString> values)
    {
        try
        {
            final Method method = this.getClass().getMethod("setXl" + name.toString(), List.class);
            method.invoke(this, values);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> get(PropertyName name)
    {
        List<String> values = null;
        try
        {
            final Method method = this.getClass().getMethod("get" + name.toString());
            values = (List<String>) method.invoke(this);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public List<XMLLangString> getXl(PropertyName name)
    {
        List<XMLLangString> values = null;
        try
        {
            final Method method = this.getClass().getMethod("getXl" + name.toString());
            values = (List<XMLLangString>) method.invoke(this);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public String getFirst(PropertyName name)
    {
        String value = null;
        try
        {
            final Method method = this.getClass().getMethod("get" + name.toString());
            List<String> values = (List<String>) method.invoke(this);
            if (values != null && values.size() > 0)
            {
                value = values.get(0);
            }
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public XMLLangString getFirstXl(PropertyName name)
    {
        XMLLangString value = null;
        try
        {
            final Method method = this.getClass().getMethod("getXl" + name.toString());
            List<XMLLangString> values = (List<XMLLangString>) method.invoke(this);
            if (values != null && values.size() > 0)
            {
                value = values.get(0);
            }
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        return value;
    }

    public void add(PropertyName name, String value)
    {
        try
        {
            final Method method = this.getClass().getMethod("add" + name.toString(), String.class);
            method.invoke(this, value);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void add(PropertyName name, String value, String language)
    {
        add(name, new JiBXLangString(value, language));
    }

    @SuppressWarnings("unchecked")
    public void add(PropertyName name, XMLLangString xmlLangString)
    {
        try
        {
            final Method listMethod = this.getClass().getMethod("getXl" + name.toString());
            List<XMLLangString> values = (List<XMLLangString>) listMethod.invoke(this);
            values.add(xmlLangString);
            setModified(name);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void add(PropertyName name, String value, Locale locale)
    {
        try
        {
            final Method listMethod = this.getClass().getMethod("getXl" + name.toString());
            List<XMLLangString> values = (List<XMLLangString>) listMethod.invoke(this);
            values.add(new JiBXLangString(value, locale));
            setModified(name);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static List<String> convertToStringList(List<XMLLangString> xmlLangList)
    {
        List<String> sl = new ArrayList<String>();
        for (XMLLangString xls : xmlLangList)
        {
            sl.add(xls.getValue());
        }
        return sl;
    }

    private static List<XMLLangString> convertToXMLLangStringList(List<String> stringList)
    {
        List<XMLLangString> xls = new ArrayList<XMLLangString>();
        for (String s : stringList)
        {
            xls.add(new JiBXLangString(s));
        }
        return xls;
    }

}
