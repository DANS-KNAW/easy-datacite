package nl.knaw.dans.easy.web.search.pages;

import java.io.Serializable;
import java.lang.reflect.Field;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.search.simple.SimpleField;

import org.apache.wicket.model.Model;

public class SearchFieldModel extends Model
{
	private static final long serialVersionUID = -6033853618498949502L;
	
	private final String propertyName;

	public SearchFieldModel(Serializable data, String propertyName)
	{
		if (propertyName == null)
			throw new RuntimeException("SearchFieldModel cannot have null value");
		this.propertyName = propertyName;
		super.setObject(data);
	}
    
	@SuppressWarnings("unchecked")
	public void setObject(Serializable input)
	{
		try
		{
			Object data = super.getObject();
			Field field = data.getClass().getDeclaredField(propertyName);
			if (ClassUtil.instanceOf(field.getType(), SimpleField.class))
			{
				SimpleField simpleField;
				simpleField = (SimpleField) field.get(data);
				simpleField.setValue(input);
			}
			else
				throw new RuntimeException("programmer error. Property '"+ propertyName +"' does not correspond to a search field.");
		} catch (Exception e)
		{
			throw new RuntimeException("programmer error. Property '"+ propertyName +"' does not exist or is not accessible.");
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Serializable getObject()
	{
		try
		{
			AdvSearchData data = (AdvSearchData) super.getObject();
			Field field = data.getClass().getDeclaredField(propertyName);
			if (ClassUtil.instanceOf(field.getType(), SimpleField.class))
			{
				SimpleField<Serializable> simpleField;
				simpleField = (SimpleField) field.get(data);
				return simpleField.getValue();
			}
			else
				throw new RuntimeException("programmer error. Property '"+ propertyName +"' does not correspond to a search field.");
		} catch (Exception e)
		{
			throw new RuntimeException("programmer error. Property '"+ propertyName +"' does not exist or is not accessible.");
		}
	}
}	
