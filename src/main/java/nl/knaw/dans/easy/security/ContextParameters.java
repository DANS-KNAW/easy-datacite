package nl.knaw.dans.easy.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class ContextParameters implements Serializable
{

    private static final long           serialVersionUID = -6253904234826916147L;

    private static final int            STARTCHAR        = 65;

    private EasyUser                    sessionUser;
    private Dataset                     dataset;
    private FileItem                    fileItem;
    private EasyUser                    userUnderEdit;
    private Object                      result;

    private int                         ch;

    private Map<Class<?>, List<Object>> parameterMap;

    private Map<Object, Character>      characterMap;

    /**
     * Construct new ContextParameters. The second -optional- parameter of this constructor takes an
     * array of objects. The fist {@link Dataset} thrown in is retrievable by {@link #getDataset()}. The 
     * first {@link FileItem} can be retrieved by {@link #getFileItem()}. The
     * first {@link EasyUser} in the array can be obtained by {@link #getUserUnderEdit()}. Other objects
     * in the array are stored under their implementation class name and can be recalled by
     * {@link #getObject(Class, int)}.
     * 
     * @param sessionUser
     *        the user of the session, might be <code>null</code>
     * @param objects
     *        an array of objects
     */
    public ContextParameters(EasyUser sessionUser, Object... objects)
    {
        this.sessionUser = sessionUser;
        extractParameters(objects);
    }

    public ContextParameters(Object... objects)
    {
        extractParameters(objects);
    }

    private void extractParameters(Object... objects)
    {
        if (objects != null)
        {
            for (Object object : objects)
            {
                if (object != null)
                {
                    if (object instanceof EasyUser && this.sessionUser == null)
                    {
                        this.sessionUser = (EasyUser) object;
                    }
                    else if (object instanceof EasyUser && this.userUnderEdit == null)
                    {
                        this.userUnderEdit = (EasyUser) object;
                    }
                    else if (object instanceof Dataset && this.dataset == null)
                    {
                        this.dataset = (Dataset) object;
                    }
                    else if (object instanceof FileItem && this.fileItem == null)
                    {
                        this.fileItem = (FileItem) object;
                    }
                    else
                    {
                        List<Object> list = getParameterMap().get(object.getClass());
                        if (list == null)
                        {
                            list = new ArrayList<Object>();
                            parameterMap.put(object.getClass(), list);
                        }
                        list.add(object);
                    }
                }
            }
        }
    }

    /**
     * Get the result of a method.
     * 
     * @return the result of a method
     */
    public Object getResult()
    {
        return result;
    }

    /**
     * Set the result of a method. Could be used by a security aspect to add the result of a method to
     * this context and with this context get the advise of a SecurityOfficer.
     * 
     * @param result
     *        the result of a method returning other than void.
     */
    public void setResult(Object result)
    {
        this.result = result;
        extractParameters(result);
    }

    public EasyUser getSessionUser()
    {
        return sessionUser;
    }

    public Dataset getDataset()
    {
        return dataset;
    }

    public FileItem getFileItem()
    {
        return fileItem;
    }

    public EasyUser getUserUnderEdit()
    {
        return userUnderEdit;
    }

    /**
     * Retrieve an object that was thrown in at construction time. (The fist {@link Dataset} thrown in is
     * retrievable by {@link #getDataset()}. The first {@link EasyUser} in the array can be obtained by
     * {@link #getUserUnderEdit()}.)
     * 
     * @param clazz
     *        the implementation class of the object
     * @param index
     *        the index of the object on a per class-basis
     * @return object thrown in at construction time or <code>null</code> if no object of the given class
     *         at the per-class-index is available
     */
    public Object getObject(Class<?> clazz, int index)
    {
        Object object = null;
        if (parameterMap != null)
        {
            List<Object> list = parameterMap.get(clazz);
            if (list != null && index < list.size())
            {
                object = list.get(index);
            }
        }
        return object;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionUser=");
        sb.append(sessionUser == null ? "null" : sessionUser.toString());
        if (dataset != null)
        {
            sb.append(" Dataset=");
            sb.append(dataset.toString());
        }
        if (userUnderEdit != null)
        {
            sb.append(" UserUnderEdit=");
            sb.append(userUnderEdit.toString());
        }
        if (parameterMap != null)
        {
            for (List<Object> list : parameterMap.values())
            {
                for (Object object : list)
                {
                    sb.append(" ");
                    sb.append(object.toString());
                }
            }
        }
        return sb.toString();
    }

    public char nextChar(Object caller)
    {
        Character c = getCharacterMap().get(caller);
        if (c == null)
        {
            c = new Character((char) (STARTCHAR + ch++));
            getCharacterMap().put(caller, c);
        }
        return c.charValue();
    }

    public char charFor(Object forObject)
    {
        Character c = getCharacterMap().get(forObject);
        if (c == null)
        {
            c = new Character('?');
        }
        return c.charValue();
    }

    private Map<Class<?>, List<Object>> getParameterMap()
    {
        if (parameterMap == null)
        {
            parameterMap = new LinkedHashMap<Class<?>, List<Object>>();
        }
        return parameterMap;
    }

    private Map<Object, Character> getCharacterMap()
    {
        if (characterMap == null)
        {
            characterMap = new HashMap<Object, Character>();
        }
        return characterMap;
    }

}
