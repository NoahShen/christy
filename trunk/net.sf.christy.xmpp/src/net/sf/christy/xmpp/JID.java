package net.sf.christy.xmpp;

/**
 * 
 * @author noah
 * 
 */
public class JID
{

	private String node;

	private String domain;

	private String resource;

	private String nodePreped;
	
	private String domainPreped;
	
	private String resourcePreped;
	    
	/**
	 * Escapes the node portion of a JID according to "JID Escaping"
	 * (JEP-0106). Escaping replaces characters prohibited by node-prep
	 * with escape sequences, as follows:
	 * <p>
	 * 
	 * <table border="1">
	 * <tr>
	 * <td><b>Unescaped Character</b></td>
	 * <td><b>Encoded Sequence</b></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;space&gt;</td>
	 * <td>\20</td>
	 * </tr>
	 * <tr>
	 * <td>"</td>
	 * <td>\22</td>
	 * </tr>
	 * <tr>
	 * <td>&</td>
	 * <td>\26</td>
	 * </tr>
	 * <tr>
	 * <td>'</td>
	 * <td>\27</td>
	 * </tr>
	 * <tr>
	 * <td>/</td>
	 * <td>\2f</td>
	 * </tr>
	 * <tr>
	 * <td>:</td>
	 * <td>\3a</td>
	 * </tr>
	 * <tr>
	 * <td>&lt;</td>
	 * <td>\3c</td>
	 * </tr>
	 * <tr>
	 * <td>&gt;</td>
	 * <td>\3e</td>
	 * </tr>
	 * <tr>
	 * <td>@</td>
	 *      <td>\40</td>
	 *      </tr>
	 *      <tr>
	 *      <td>\</td>
	 *      <td>\5c</td>
	 *      </tr>
	 *      </table>
	 *      <p>
	 * 
	 * This process is useful when the node comes from an external source
	 * that doesn't conform to nodeprep. For example, a username in LDAP
	 * may be "Joe Smith". Because the &lt;space&gt; character isn't a
	 * valid part of a node, the username should be escaped to
	 * "Joe\20Smith" before being made into a JID (e.g.
	 * "joe\20smith@example.com" after case-folding, etc. has been
	 * applied).
	 * <p>
	 * 
	 * All node escaping and un-escaping must be performed manually at the
	 * appropriate time; the JID class will not escape or un-escape
	 * automatically.
	 * 
	 * @param node
	 *                  the node.
	 * @return the escaped version of the node.
	 */
	public static String escapeNode(String node)
	{
		if (node == null)
		{
			return null;
		}
		StringBuilder buf = new StringBuilder(node.length() + 8);
		for (int i = 0, n = node.length(); i < n; i++)
		{
			char c = node.charAt(i);
			switch (c)
			{
				case '"':
					buf.append("\\22");
					break;
				case '&':
					buf.append("\\26");
					break;
				case '\'':
					buf.append("\\27");
					break;
				case '/':
					buf.append("\\2f");
					break;
				case ':':
					buf.append("\\3a");
					break;
				case '<':
					buf.append("\\3c");
					break;
				case '>':
					buf.append("\\3e");
					break;
				case '@':
					buf.append("\\40");
					break;
				case '\\':
					buf.append("\\5c");
					break;
				default:
				{
					if (Character.isWhitespace(c))
					{
						buf.append("\\20");
					}
					else
					{
						buf.append(c);
					}
				}
			}
		}
		return buf.toString();
	}

	/**
	 * Un-escapes the node portion of a JID according to "JID Escaping"
	 * (JEP-0106).
	 * <p>
	 * Escaping replaces characters prohibited by node-prep with escape
	 * sequences, as follows:
	 * <p>
	 * 
	 * <table border="1">
	 * <tr>
	 * <td><b>Unescaped Character</b></td>
	 * <td><b>Encoded Sequence</b></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;space&gt;</td>
	 * <td>\20</td>
	 * </tr>
	 * <tr>
	 * <td>"</td>
	 * <td>\22</td>
	 * </tr>
	 * <tr>
	 * <td>&</td>
	 * <td>\26</td>
	 * </tr>
	 * <tr>
	 * <td>'</td>
	 * <td>\27</td>
	 * </tr>
	 * <tr>
	 * <td>/</td>
	 * <td>\2f</td>
	 * </tr>
	 * <tr>
	 * <td>:</td>
	 * <td>\3a</td>
	 * </tr>
	 * <tr>
	 * <td>&lt;</td>
	 * <td>\3c</td>
	 * </tr>
	 * <tr>
	 * <td>&gt;</td>
	 * <td>\3e</td>
	 * </tr>
	 * <tr>
	 * <td>@</td>
	 *      <td>\40</td>
	 *      </tr>
	 *      <tr>
	 *      <td>\</td>
	 *      <td>\5c</td>
	 *      </tr>
	 *      </table>
	 *      <p>
	 * 
	 * This process is useful when the node comes from an external source
	 * that doesn't conform to nodeprep. For example, a username in LDAP
	 * may be "Joe Smith". Because the &lt;space&gt; character isn't a
	 * valid part of a node, the username should be escaped to
	 * "Joe\20Smith" before being made into a JID (e.g.
	 * "joe\20smith@example.com" after case-folding, etc. has been
	 * applied).
	 * <p>
	 * 
	 * All node escaping and un-escaping must be performed manually at the
	 * appropriate time; the JID class will not escape or un-escape
	 * automatically.
	 * 
	 * @param node
	 *                  the escaped version of the node.
	 * @return the un-escaped version of the node.
	 */
	public static String unescapeNode(String node)
	{
		if (node == null)
		{
			return null;
		}
		char[] nodeChars = node.toCharArray();
		StringBuilder buf = new StringBuilder(nodeChars.length);
		for (int i = 0, n = nodeChars.length; i < n; i++)
		{
			compare:
			{
				char c = node.charAt(i);
				if (c == '\\' && i + 2 < n)
				{
					char c2 = nodeChars[i + 1];
					char c3 = nodeChars[i + 2];
					if (c2 == '2')
					{
						switch (c3)
						{
							case '0':
								buf.append(' ');
								i += 2;
								break compare;
							case '2':
								buf.append('"');
								i += 2;
								break compare;
							case '6':
								buf.append('&');
								i += 2;
								break compare;
							case '7':
								buf.append('\'');
								i += 2;
								break compare;
							case 'f':
								buf.append('/');
								i += 2;
								break compare;
						}
					}
					else if (c2 == '3')
					{
						switch (c3)
						{
							case 'a':
								buf.append(':');
								i += 2;
								break compare;
							case 'c':
								buf.append('<');
								i += 2;
								break compare;
							case 'e':
								buf.append('>');
								i += 2;
								break compare;
						}
					}
					else if (c2 == '4')
					{
						if (c3 == '0')
						{
							buf.append("@");
							i += 2;
							break compare;
						}
					}
					else if (c2 == '5')
					{
						if (c3 == 'c')
						{
							buf.append("\\");
							i += 2;
							break compare;
						}
					}
				}
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * Constructs a JID from it's String representation.
	 * 
	 * @param jid
	 *                  a valid JID.
	 * @throws IllegalArgumentException
	 *                   if the JID is not valid.
	 */
	public JID(String jid)
	{
		if (jid == null)
		{
			throw new NullPointerException("JID cannot be null");
		}
		String[] parts = getParts(jid);

		init(parts[0], parts[1], parts[2]);
	}

	/**
	 * Constructs a JID given a node, domain, and resource.
	 * 
	 * @param node
	 *                  the node.
	 * @param domain
	 *                  the domain, which must not be <tt>null</tt>.
	 * @param resource
	 *                  the resource.
	 * @throws IllegalArgumentException
	 *                   if the JID is not valid.
	 */
	public JID(String node, String domain, String resource)
	{
		if (domain == null)
		{
			throw new NullPointerException("Domain cannot be null");
		}
		init(node, domain, resource);
	}

	/**
	 * Returns a String array with the parsed node, domain and resource.
	 * No Stringprep is performed while parsing the textual
	 * representation.
	 * 
	 * @param jid
	 *                  the textual JID representation.
	 * @return a string array with the parsed node, domain and resource.
	 */
	public static String[] getParts(String jid)
	{
		String[] parts = new String[3];
		String node = null, domain, resource;
		if (jid == null)
		{
			return parts;
		}

		int atIndex = jid.indexOf("@");
		int slashIndex = jid.indexOf("/");

		// Node
		if (atIndex > 0)
		{
			node = jid.substring(0, atIndex);
		}

		// Domain
		if (atIndex + 1 > jid.length())
		{
			throw new IllegalArgumentException("JID with empty domain not valid");
		}
		if (atIndex < 0)
		{
			if (slashIndex > 0)
			{
				domain = jid.substring(0, slashIndex);
			}
			else
			{
				domain = jid;
			}
		}
		else
		{
			if (slashIndex > 0)
			{
				domain = jid.substring(atIndex + 1, slashIndex);
			}
			else
			{
				domain = jid.substring(atIndex + 1);
			}
		}

		// Resource
		if (slashIndex + 1 > jid.length() || slashIndex < 0)
		{
			resource = null;
		}
		else
		{
			resource = jid.substring(slashIndex + 1);
		}
		parts[0] = node;
		parts[1] = domain;
		parts[2] = resource;
		return parts;
	}

	/**
	 * Transforms the JID parts using the appropriate Stringprep profiles,
	 * then validates them. If they are fully valid, the field values are
	 * saved, otherwise an IllegalArgumentException is thrown.
	 * 
	 * @param node
	 *                  the node.
	 * @param domain
	 *                  the domain.
	 * @param resource
	 *                  the resource.
	 */
	private void init(String node, String domain, String resource)
	{
		// Set node and resource to null if they are the empty
		// string.
		if (node != null && node.equals(""))
		{
			node = null;
		}
		if (resource != null && resource.equals(""))
		{
			resource = null;
		}

		try
		{
			if (node != null && node.length() * 2 > 1023)
			{
				throw new IllegalArgumentException("Node cannot be larger than 1023 bytes. " + "Size is "
						+ (this.node.length() * 2) + " bytes.");
			}
			if (node != null)
			{
				this.nodePreped = node.toLowerCase();
			}
			this.node = node;

			
			if (domain.length() * 2 > 1023)
			{
				throw new IllegalArgumentException("Domain cannot be larger than 1023 bytes. " + "Size is "
						+ (this.domain.length() * 2) + " bytes.");
			}
			this.domainPreped = domain.toLowerCase();
			this.domain = domain;
			
			// Validate field is not greater than 1023 bytes.
			// UTF-8 characters use two bytes.
			if (resource != null && resource.length() * 2 > 1023)
			{
				throw new IllegalArgumentException("Resource cannot be larger than 1023 bytes. " + "Size is "
						+ (resource.length() * 2) + " bytes.");
			}
			if (resource != null)
			{
				this.resourcePreped = resource;
			}
			this.resource = resource;
			
		}
		catch (Exception e)
		{
			StringBuilder buf = new StringBuilder();
			if (node != null)
			{
				buf.append(node).append("@");
			}
			buf.append(domain);
			if (resource != null)
			{
				buf.append("/").append(resource);
			}
			throw new IllegalArgumentException("Illegal JID: " + buf.toString(), e);
		}
	}

	/**
	 * Returns the node, or <tt>null</tt> if this JID does not contain
	 * node information.
	 * 
	 * @return the node.
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * Returns the domain.
	 * 
	 * @return the domain.
	 */
	public String getDomain()
	{
		return domain;
	}

	/**
	 * Returns the resource, or <tt>null</tt> if this JID does not
	 * contain resource information.
	 * 
	 * @return the resource.
	 */
	public String getResource()
	{
		return resource;
	}

	/**
	 * Returns the String representation of the bare JID, which is the JID
	 * with resource information removed.
	 * 
	 * @return the bare JID.
	 */
	public String toBareJID()
	{
		StringBuilder buf = new StringBuilder(40);
		if (node != null)
		{
			buf.append(node).append("@");
		}
		buf.append(domain);
		return buf.toString();
	}

	/**
	 * Returns a String representation of the JID.
	 * 
	 * @return a String representation of the JID.
	 */
	public String toFullJID()
	{
		StringBuilder buf = new StringBuilder(40);
		if (node != null)
		{
			buf.append(node).append("@");
		}
		buf.append(domain);

		if (resource != null)
		{
			buf.append("/").append(resource);
		}
		return buf.toString();
	}

	public String toPrepedFullJID()
	{
		StringBuilder buf = new StringBuilder(40);
		if (nodePreped != null)
		{
			buf.append(nodePreped).append("@");
		}
		buf.append(domainPreped);

		if (resourcePreped != null)
		{
			buf.append("/").append(resourcePreped);
		}
		return buf.toString();
	}
	
	public String toPrepedBareJID()
	{
		StringBuilder buf = new StringBuilder(40);
		if (nodePreped != null)
		{
			buf.append(nodePreped).append("@");
		}
		buf.append(domainPreped);
		return buf.toString();
	}
	
	public String toString()
	{
		return toFullJID();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainPreped == null) ? 0 : domainPreped.hashCode());
		result = prime * result + ((nodePreped == null) ? 0 : nodePreped.hashCode());
		result = prime * result + ((resourcePreped == null) ? 0 : resourcePreped.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JID other = (JID) obj;
		if (domainPreped == null)
		{
			if (other.domainPreped != null)
				return false;
		}
		else if (!domainPreped.equals(other.domainPreped))
			return false;
		if (nodePreped == null)
		{
			if (other.nodePreped != null)
				return false;
		}
		else if (!nodePreped.equals(other.nodePreped))
			return false;
		if (resourcePreped == null)
		{
			if (other.resourcePreped != null)
				return false;
		}
		else if (!resourcePreped.equals(other.resourcePreped))
			return false;
		return true;
	}
	
	public boolean equalsWithBareJid(JID otherJid)
	{
		if (domainPreped == null)
		{
			if (otherJid.domainPreped != null)
				return false;
		}
		else if (!domainPreped.equals(otherJid.domainPreped))
			return false;
		if (nodePreped == null)
		{
			if (otherJid.nodePreped != null)
				return false;
		}
		else if (!nodePreped.equals(otherJid.nodePreped))
			return false;
		
		return true;
	}
}