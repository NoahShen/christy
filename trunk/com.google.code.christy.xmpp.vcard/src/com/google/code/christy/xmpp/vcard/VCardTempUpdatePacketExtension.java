package com.google.code.christy.xmpp.vcard;

import com.google.code.christy.xmpp.PacketExtension;

/**
 * @author noah
 * 
 */
public class VCardTempUpdatePacketExtension implements PacketExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5032266970105037961L;

	public static final String ELEMENTNAME = "x";

	public static final String NAMESPACE = "vcard-temp:x:update";

	private String photo;

	public VCardTempUpdatePacketExtension(String photo)
	{
		this.photo = photo;
	}

	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	/**
	 * @return the photo
	 */
	public String getPhoto()
	{
		return photo;
	}

	/**
	 * @param photo
	 *                  the photo to set
	 */
	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\">");
		if (getPhoto() != null)
		{
			buf.append("<photo>").append(getPhoto()).append("</photo>");
		}
		buf.append("</" + getElementName() + ">");
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		VCardTempUpdatePacketExtension extension = (VCardTempUpdatePacketExtension) super.clone();
		extension.photo = this.photo;
		return extension;
	}

}