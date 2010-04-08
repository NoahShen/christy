package com.google.code.christy.sm.vcard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.PacketExtension;

/**
 * @author noah
 * 
 */
public class VCardPacketExtension implements PacketExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 581251969815051720L;

	public static final String ELEMENTNAME = "vCard";

	public static final String NAMESPACE = "vcard-temp";

	private String familyName;

	private String givenName;

	private String middleName;

	private String fullName;

	private String nickName;

	private String url;

	private String birthday;

	private String organizationName;

	private String organizationUnit;

	private String title;

	private String role;

	/**
	 * Phone types: VOICE?, FAX?, PAGER?, MSG?, CELL?, VIDEO?, BBS?,
	 * MODEM?, ISDN?, PCS?, PREF?
	 */
	private Map<String, String> workPhones = new HashMap<String, String>();

	/**
	 * Phone types: VOICE?, FAX?, PAGER?, MSG?, CELL?, VIDEO?, BBS?,
	 * MODEM?, ISDN?, PCS?, PREF?
	 */
	private Map<String, String> workAddress = new HashMap<String, String>();

	private Map<String, String> homePhones = new HashMap<String, String>();

	private Map<String, String> homeAddress = new HashMap<String, String>();

	private String email;

	private String jabberID;

	private String description;

	private String photoType;

	private String photoBinval;

	public VCardPacketExtension()
	{
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName()
	{
		return organizationName;
	}

	/**
	 * @param organizationName
	 *                  the organizationName to set
	 */
	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
	}

	/**
	 * @return the organizationUnit
	 */
	public String getOrganizationUnit()
	{
		return organizationUnit;
	}

	/**
	 * @param organizationUnit
	 *                  the organizationUnit to set
	 */
	public void setOrganizationUnit(String organizationUnit)
	{
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title
	 *                  the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the role
	 */
	public String getRole()
	{
		return role;
	}

	/**
	 * @param role
	 *                  the role to set
	 */
	public void setRole(String role)
	{
		this.role = role;
	}

	/**
	 * @return the familyName
	 */
	public String getFamilyName()
	{
		return familyName;
	}

	/**
	 * @param familyName
	 *                  the familyName to set
	 */
	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName()
	{
		return givenName;
	}

	/**
	 * @param givenName
	 *                  the givenName to set
	 */
	public void setGivenName(String givenName)
	{
		this.givenName = givenName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 * @param middleName
	 *                  the middleName to set
	 */
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getFullName()
	{
		return fullName;
	}

	/**
	 * @return the nickName
	 */
	public String getNickName()
	{
		return nickName;
	}

	/**
	 * @param nickName
	 *                  the nickName to set
	 */
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *                  the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday()
	{
		return birthday;
	}

	/**
	 * @param birthday
	 *                  the birthday to set
	 */
	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	/**
	 * Set work phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 * @param phoneNum
	 *                  phone number
	 */
	public void setWorkPhone(String phoneType, String phoneNum)
	{
		workPhones.put(phoneType, phoneNum);
	}

	/**
	 * Get work phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 */
	public String getWorkPhone(String phoneType)
	{
		return workPhones.get(phoneType);
	}

	/**
	 * Get work address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	public String getWorkAddress(String addrField)
	{
		return workAddress.get(addrField);
	}

	/**
	 * Set work address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	public void setWorkAddress(String addrField, String value)
	{
		workAddress.put(addrField, value);
	}

	/**
	 * Set home phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 * @param phoneNum
	 *                  phone number
	 */
	public void setHomePhone(String phoneType, String phoneNum)
	{
		homePhones.put(phoneType, phoneNum);
	}

	/**
	 * Get home phone number
	 * 
	 * @param phoneType
	 *                  one of VOICE, FAX, PAGER, MSG, CELL, VIDEO, BBS,
	 *                  MODEM, ISDN, PCS, PREF
	 */
	public String getHomePhone(String phoneType)
	{
		return homePhones.get(phoneType);
	}

	/**
	 * Get home address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	public String getHomeAddress(String addrField)
	{
		return homeAddress.get(addrField);
	}

	/**
	 * Set home address field
	 * 
	 * @param addrField
	 *                  one of POSTAL, PARCEL, (DOM | INTL), PREF, POBOX,
	 *                  EXTADR, STREET, LOCALITY, REGION, PCODE, CTRY
	 */
	public void setHomeAddress(String addrField, String value)
	{
		homeAddress.put(addrField, value);
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *                  the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the jabberID
	 */
	public String getJabberID()
	{
		return jabberID;
	}

	/**
	 * @param jabberID
	 *                  the jabberID to set
	 */
	public void setJabberID(String jabberID)
	{
		this.jabberID = jabberID;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *                  the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the photoType
	 */
	public String getPhotoType()
	{
		return photoType;
	}

	/**
	 * @param photoType
	 *                  the photoType to set
	 */
	public void setPhotoType(String photoType)
	{
		this.photoType = photoType;
	}

	/**
	 * @return the photoBinval
	 */
	public String getPhotoBinval()
	{
		return photoBinval;
	}

	/**
	 * @param photoBinval
	 *                  the photoBinval to set
	 */
	public void setPhotoBinval(String photoBinval)
	{
		this.photoBinval = photoBinval;
	}

	private boolean hasPhoto()
	{
		return getPhotoType() != null || getPhotoBinval() != null;
	}

	private boolean hasOrg()
	{
		return getOrganizationName() != null || getOrganizationUnit() != null;
	}

	private boolean hasNameField()
	{
		return familyName != null || givenName != null || middleName != null;
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

	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\">");
		if (getFullName() != null && !getFullName().isEmpty())
		{
			buf.append("<FN>").append(getFullName()).append("</FN>");
		}
		if (hasNameField())
		{
			buf.append("<N>");
			if (getFamilyName() != null && !getFamilyName().isEmpty())
			{
				buf.append("<FAMILY>").append(StringUtils.escapeForXML(getFamilyName())).append("</FAMILY>");
			}
			if (getGivenName() != null && !getGivenName().isEmpty())
			{
				buf.append("<GIVEN>").append(StringUtils.escapeForXML(getGivenName())).append("</GIVEN>");
			}
			if (getMiddleName() != null && !getMiddleName().isEmpty())
			{
				buf.append("<MIDDLE>").append(StringUtils.escapeForXML(getMiddleName())).append("</MIDDLE>");
			}
			buf.append("</N>");
		}
		if (getNickName() != null && !getNickName().isEmpty())
		{
			buf.append("<NICKNAME>").append(StringUtils.escapeForXML(getNickName())).append("</NICKNAME>");
		}
		if (getUrl() != null && !getUrl().isEmpty())
		{
			buf.append("<URL>").append(getUrl()).append("</URL>");
		}
		if (getBirthday() != null && !getBirthday().isEmpty())
		{
			buf.append("<BDAY>").append(getBirthday()).append("</BDAY>");
		}
		if (hasOrg())
		{
			buf.append("<ORG>");
			if (getOrganizationName() != null && !getOrganizationName().isEmpty())
			{
				buf.append("<ORGNAME>").append(StringUtils.escapeForXML(getOrganizationName())).append("</ORGNAME>");
			}
			if (getOrganizationUnit() != null && !getOrganizationUnit().isEmpty())
			{
				buf.append("<ORGUNIT>").append(StringUtils.escapeForXML(getOrganizationUnit())).append("</ORGUNIT>");
			}
			buf.append("</ORG>");
		}
		if (getTitle() != null && !getTitle().isEmpty())
		{
			buf.append("<TITLE>").append(getTitle()).append("</TITLE>");
		}
		if (getRole() != null && !getRole().isEmpty())
		{
			buf.append("<ROLE>").append(getRole()).append("</ROLE>");
		}
		if (!workPhones.isEmpty())
		{
			Iterator<Map.Entry<String, String>> it = workPhones.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, String> entry = it.next();
				buf.append(" <TEL>").append("<WORK/>").append("<" + entry.getKey().toUpperCase() + "/>").append("<NUMBER>")
						.append(entry.getValue()).append("</NUMBER>").append("</TEL>");
			}
		}
		if (!workAddress.isEmpty())
		{
			buf.append("<ADR>").append("<WORD/>");

			Iterator<Map.Entry<String, String>> it = workAddress.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, String> entry = it.next();
				buf.append("<" + entry.getKey() + ">").append(StringUtils.escapeForXML(entry.getValue())).append(
						"</" + entry.getKey() + ">");
			}

			buf.append("</ADR>");
		}
		if (!homePhones.isEmpty())
		{
			Iterator<Map.Entry<String, String>> it = homePhones.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, String> entry = it.next();
				buf.append(" <TEL>").append("<HOME/>").append("<" + entry.getKey().toUpperCase() + "/>").append("<NUMBER>")
						.append(entry.getValue()).append("</NUMBER>").append("</TEL>");
			}
		}
		if (!homeAddress.isEmpty())
		{
			buf.append("<ADR>").append("<HOME/>");

			Iterator<Map.Entry<String, String>> it = homeAddress.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, String> entry = it.next();
				buf.append("<" + entry.getKey() + ">").append(StringUtils.escapeForXML(entry.getValue())).append(
						"</" + entry.getKey() + ">");
			}

			buf.append("</ADR>");
		}
		if (getEmail() != null && !getEmail().isEmpty())
		{
			buf.append("<EMAIL>").append("<INTERNET/>").append("<PREF/>").append("<USERID>").append(getEmail()).append("</USERID>")
					.append("</EMAIL>");
		}
		if (getJabberID() != null && !getJabberID().isEmpty())
		{
			buf.append("<JABBERID>").append(getJabberID()).append("</JABBERID>");
		}
		if (getDescription() != null && !getDescription().isEmpty())
		{
			buf.append("<DESC>").append(StringUtils.escapeForXML(getDescription())).append("</DESC>");
		}
		if (hasPhoto())
		{
			buf.append("<PHOTO>");
			if (getPhotoType() != null && !getPhotoType().isEmpty())
			{
				buf.append("<TYPE>").append(StringUtils.escapeForXML(getPhotoType())).append("</TYPE>");
			}
			if (getPhotoBinval() != null && !getPhotoBinval().isEmpty())
			{
				buf.append("<BINVAL>").append(StringUtils.escapeForXML(getPhotoBinval())).append("</BINVAL>");
			}
			buf.append("</PHOTO>");

		}

		buf.append("</vCard>");

		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		VCardPacketExtension vCard = (VCardPacketExtension) super.clone();
		vCard.familyName = this.familyName;
		vCard.givenName = this.givenName;
		vCard.middleName = this.middleName;
		vCard.fullName = this.fullName;
		vCard.nickName = this.nickName;
		vCard.url = this.url;
		vCard.birthday = this.birthday;
		vCard.organizationName = this.organizationName;
		vCard.organizationUnit = this.organizationUnit;
		vCard.title = this.title;
		vCard.role = this.role;

		vCard.workPhones = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : this.workPhones.entrySet())
		{
			vCard.workPhones.put(entry.getKey(), entry.getValue());
		}

		vCard.workAddress = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : this.workAddress.entrySet())
		{
			vCard.workAddress.put(entry.getKey(), entry.getValue());
		}

		vCard.homePhones = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : this.homePhones.entrySet())
		{
			vCard.homePhones.put(entry.getKey(), entry.getValue());
		}

		vCard.homeAddress = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : this.homeAddress.entrySet())
		{
			vCard.homeAddress.put(entry.getKey(), entry.getValue());
		}

		vCard.email = this.email;
		vCard.jabberID = this.jabberID;
		vCard.description = this.description;
		vCard.photoType = this.photoType;
		vCard.photoBinval = this.photoBinval;

		return vCard;
	}

}