/**
 * 
 */
package net.sf.christy.xmpp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author noah
 *
 */
public abstract class Packet extends AbstractXmlStanza
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2212958546542495776L;

	private JID to;

	private JID from;

	private String language;
	
	private List<PacketExtension> packetExtensions = new CopyOnWriteArrayList<PacketExtension>();
	
	private XmppError error = null;
	
	
	/**
	 * @return the to
	 */
	public JID getTo()
	{
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(JID to)
	{
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public JID getFrom()
	{
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(JID from)
	{
		this.from = from;
	}

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * @return the error
	 */
	public XmppError getError()
	{
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(XmppError error)
	{
		this.error = error;
	}

	/**
	 * Returns an unmodifiable collection of the packet extensions
	 * attached to the packet.
	 * 
	 * @return the packet extensions.
	 */
	public synchronized Collection<PacketExtension> getExtensions()
	{
		return Collections.unmodifiableCollection(packetExtensions);
	}

	/**
	 * Returns the first extension of this packet that has the given
	 * namespace.
	 * 
	 * @param namespace
	 *                  the namespace of the extension that is desired.
	 * @return the packet extension with the given namespace.
	 */
	public PacketExtension getExtension(String namespace)
	{
		return getExtension(null, namespace);
	}

	/**
	 * Returns the first packet extension that matches the specified
	 * element name and namespace, or <tt>null</tt> if it doesn't exist.
	 * 
	 * @param elementName
	 *                  the XML element name of the packet extension. (May
	 *                  be null)
	 * @param namespace
	 *                  the XML element namespace of the packet extension.
	 * @return the extension, or <tt>null</tt> if it doesn't exist.
	 */
	public PacketExtension getExtension(String elementName, String namespace)
	{
		if (namespace == null)
		{
			return null;
		}
		for (PacketExtension ext : packetExtensions)
		{
			if ((elementName == null || elementName.equals(ext.getElementName())) 
					&& namespace.equals(ext.getNamespace()))
			{
				return ext;
			}
		}
		return null;
	}

	/**
	 * Adds a packet extension to the packet.
	 * 
	 * @param extension
	 *                  a packet extension.
	 */
	public void addExtension(PacketExtension extension)
	{
		if (extension != null)
		{
			packetExtensions.add(extension);
		}
	}

	/**
	 * Removes a packet extension from the packet.
	 * 
	 * @param extension
	 *                  the packet extension to remove.
	 */
	public void removeExtension(PacketExtension extension)
	{
		packetExtensions.remove(extension);
	}

	/**
	 * Returns the extension sub-packets (including properties data) as an
	 * XML String, or the Empty String if there are no packet extensions.
	 * 
	 * @return the extension sub-packets as XML or the Empty String if
	 *         there are no packet extensions.
	 */
	protected synchronized String getExtensionsXML()
	{
		StringBuilder buf = new StringBuilder();
		// Add in all standard extension sub-packets.
		for (PacketExtension extension : getExtensions())
		{
			buf.append(extension.toXml());
		}

		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgixmppbundle.AbstractXMLStanza#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Packet packet = (Packet) super.clone();
		
		packet.to = this.to;
		packet.from = this.from;
		packet.language = this.language;
		if (this.error != null)
		{
			packet.error = (XmppError) this.error.clone();
		}
		
		packet.packetExtensions = new CopyOnWriteArrayList<PacketExtension>();
		for (PacketExtension extension : packetExtensions)
		{
			packet.addExtension((PacketExtension) extension.clone());
		}
		
		return packet;
	}
	
}
