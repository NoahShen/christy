package net.sf.christy.c2s;

public class StreamFeature
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 467310243624816368L;

	private String elementName;

	private String namespace;

	private boolean required = false;
	
	private SupportedFeatureType type;

	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @param type
	 */
	public StreamFeature(String elementName, String namespace, SupportedFeatureType type)
	{
		this.elementName = elementName;
		this.namespace = namespace;
		this.type = type;
	}

	public String getElementName()
	{
		return elementName;
	}

	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public boolean isRequired()
	{
		return required;
	}

	public void setRequired(boolean required)
	{
		this.required = required;
	}

	/**
	 * @return the type
	 */
	public SupportedFeatureType getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SupportedFeatureType type)
	{
		this.type = type;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		StreamFeature feature = (StreamFeature) super.clone();
		feature.elementName = this.elementName;
		feature.namespace = this.namespace;
		feature.required = this.required;
		return feature;
	}
	

	/**
	 * @author noah
	 *
	 */
	public enum SupportedFeatureType
	{
		afterConnected,
		
		afterTLS,
		
		afterAuth
	}
}
