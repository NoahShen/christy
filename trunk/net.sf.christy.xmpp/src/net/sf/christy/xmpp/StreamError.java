package net.sf.christy.xmpp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a stream error packet.
 * 
 * @see http://www.ietf.org/rfc/rfc3920.txt
 * @author noah
 */
public class StreamError implements XMLStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5855243879205275751L;

	private Condition condition;
	
	private ErrorText text;
	
	private Map<String, AppCondition> applicationConditions = new HashMap<String, AppCondition>();

	/**
	 * 
	 */
	public StreamError()
	{
	}


	public StreamError(Condition condition)
	{
		super();
		this.condition = condition;
	}

	
	/**
	 * @return the condition
	 */
	public Condition getCondition()
	{
		return condition;
	}


	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Condition condition)
	{
		this.condition = condition;
	}


	/**
	 * @return the text
	 */
	public ErrorText getText()
	{
		return text;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(ErrorText text)
	{
		this.text = text;
	}

	public void setText(String text, String lang)
	{
		this.text = new ErrorText(text);
		if (lang != null)
		{
			this.text.setLang(lang);
		}
	}
	
	public AppCondition addApplicationCondition(String elementName, String namespace)
	{
		AppCondition appCondition = new AppCondition(elementName, namespace);
		return applicationConditions.put(getKey(appCondition.getElementName(), appCondition.getNamespace()), appCondition);
	}

	private String getKey(String elementName, String namespace)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(elementName).append(">")
			.append("<").append(namespace).append(">");
		return buf.toString();
	}


	public AppCondition removeAppCondition(String elementName, String namespace)
	{
		return applicationConditions.remove(getKey(elementName, namespace));
	}
	
	public Collection<AppCondition> getAppConditions()
	{
		return Collections.unmodifiableCollection(applicationConditions.values());
	}
	
	@Override
	public String toXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<stream:error>");
		if (getCondition() != null)
		{
			buf.append("<").append(getCondition().toXMPP()).append(" xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"").append("/>");
		}
		ErrorText text = getText();
		if (text != null)
		{
			buf.append(text.toXML());
		}
		if (!applicationConditions.isEmpty())
		{
			for (AppCondition appCondition : applicationConditions.values())
			{
				buf.append(appCondition.toXML());
			}
		}
		buf.append(" </stream:error>");
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		StreamError streamError = (StreamError) super.clone();
		streamError.condition = this.condition;
		if (this.text != null)
		{
			streamError.text = (ErrorText) this.text.clone();
		}
		streamError.applicationConditions = new HashMap<String, AppCondition>();
		for (Map.Entry<String, AppCondition> entry : this.applicationConditions.entrySet())
		{
			streamError.applicationConditions.put(entry.getKey(), (AppCondition) entry.getValue().clone());
		}
		return streamError;
	}

	/**
	 * Type-safe enumeration for the error condition.
	 * <p>
	 * 
	 * Implementation note: XMPP error conditions use "-" characters in
	 * their names such as "bad-request". Because "-" characters are not
	 * valid identifier parts in Java, they have been converted to "_"
	 * characters in the enumeration names, such as <tt>bad_request</tt>.
	 * The {@link #toXMPP()} and {@link #fromXMPP(String)} methods can be
	 * used to convert between the enumertation values and XMPP error code
	 * strings.
	 */
	public enum Condition
	{

		/**
		 * The entity has sent XML that cannot be processed; this
		 * error MAY be used instead of the more specific
		 * XML-related errors, such as
		 * &lt;bad-namespace-prefix/&gt;, &lt;invalid-xml/&gt;,
		 * &lt;restricted-xml/&gt;, &lt;unsupported-encoding/&gt;,
		 * and &lt;xml-not-well-formed/&gt;, although the more
		 * specific errors are preferred.
		 */
		bad_format("bad-format"),

		/**
		 * The entity has sent a namespace prefix that is
		 * unsupported, or has sent no namespace prefix on an
		 * element that requires such a prefix.
		 */
		bad_namespace_prefix("bad-namespace-prefix"),

		/**
		 * The server is closing the active stream for this entity
		 * because a new stream has been initiated that conflicts
		 * with the existing stream.
		 */
		conflict("conflict"),

		/**
		 * The entity has not generated any traffic over the stream
		 * for some period of time (configurable according to a
		 * local service policy).
		 */
		connection_timeout("connection-timeout"),

		/**
		 * The value of the 'to' attribute provided by the
		 * initiating entity in the stream header corresponds to a
		 * hostname that is no longer hosted by the server.
		 */
		host_gone("host-gone"),

		/**
		 * The value of the 'to' attribute provided by the
		 * initiating entity in the stream header does not
		 * correspond to a hostname that is hosted by the server.
		 */
		host_unknown("host-unknown"),

		/**
		 * A stanza sent between two servers lacks a 'to' or 'from'
		 * attribute (or the attribute has no value).
		 */
		improper_addressing("improper-addressing"),

		/**
		 * The server has experienced a misconfiguration or an
		 * otherwise-undefined internal error that prevents it from
		 * servicing the stream.
		 */
		internal_server_error("internal-server-error"),

		/**
		 * The JID or hostname provided in a 'from' address does not
		 * match an authorized JID or validated domain negotiated
		 * between servers via SASL or dialback, or between a client
		 * and a server via authentication and resource binding.
		 */
		invalid_from("invalid-from"),

		/**
		 * The stream ID or dialback ID is invalid or does not match
		 * an ID previously provided.
		 */
		invalid_id("invalid-id"),

		/**
		 * the streams namespace name is something other than
		 * "http://etherx.jabber.org/streams" or the dialback
		 * namespace name is something other than
		 * "jabber:server:dialback".
		 */
		invalid_namespace("invalid-namespace"),

		/**
		 * The entity has sent invalid XML over the stream to a
		 * server that performs validation.
		 */
		invalid_xml("invalid-xml"),

		/**
		 * The entity has attempted to send data before the stream
		 * has been authenticated, or otherwise is not authorized to
		 * perform an action related to stream negotiation; the
		 * receiving entity MUST NOT process the offending stanza
		 * before sending the stream error.
		 */
		not_authorized("not-authorized"),

		/**
		 * The entity has violated some local service policy; the
		 * server MAY choose to specify the policy in the <text/>
		 * element or an application-specific condition element.
		 */
		policy_violation("policy-violation"),

		/**
		 * The server is unable to properly connect to a remote
		 * entity that is required for authentication or
		 * authorization.
		 */
		remote_connection_failed("remote-connection-failed"),

		/**
		 * The server lacks the system resources necessary to
		 * service the stream.
		 */
		resource_constraint("resource-constraint"),

		/**
		 * The entity has attempted to send restricted XML features
		 * such as a comment, processing instruction, DTD, entity
		 * reference, or unescaped character.
		 */
		restricted_xml("restricted-xml"),

		/**
		 * The server will not provide service to the initiating
		 * entity but is redirecting traffic to another host; the
		 * server SHOULD specify the alternate hostname or IP
		 * address (which MUST be a valid domain identifier) as the
		 * XML character data of the &lt;see-other-host/&gt;
		 * element.
		 */
		see_other_host("see-other-host"),

		/**
		 * The server is being shut down and all active streams are
		 * being closed.
		 */
		system_shutdown("system-shutdown"),

		/**
		 * The error condition is not one of those defined by the
		 * other conditions in this list; this error condition
		 * SHOULD be used only in conjunction with an
		 * application-specific condition.
		 */
		undefined_condition("undefined-condition"),

		/**
		 * The initiating entity has encoded the stream in an
		 * encoding that is not supported by the server.
		 */
		unsupported_encoding("unsupported-encoding"),

		/**
		 * The initiating entity has sent a first-level child of the
		 * stream that is not supported by the server.
		 */
		unsupported_stanza_type("unsupported-stanza-type"),

		/**
		 * the value of the 'version' attribute provided by the
		 * initiating entity in the stream header specifies a
		 * version of XMPP that is not supported by the server; the
		 * server MAY specify the version(s) it supports in the
		 * &lt;text/&gt; element.
		 */
		unsupported_version("unsupported-version"),

		/**
		 * The initiating entity has sent XML that is not
		 * well-formed.
		 */
		xml_not_well_formed("xml-not-well-formed");

		/**
		 * Converts a String value into its Condition
		 * representation.
		 * 
		 * @param condition
		 *                  the String value.
		 * @return the condition corresponding to the String.
		 */
		public static Condition fromXMPP(String condition)
		{
			if (condition == null)
			{
				throw new NullPointerException();
			}
			condition = condition.toLowerCase();
			if (bad_format.toXMPP().equals(condition))
			{
				return bad_format;
			}
			else if (bad_namespace_prefix.toXMPP().equals(condition))
			{
				return bad_namespace_prefix;
			}
			else if (conflict.toXMPP().equals(condition))
			{
				return conflict;
			}
			else if (connection_timeout.toXMPP().equals(condition))
			{
				return connection_timeout;
			}
			else if (host_gone.toXMPP().equals(condition))
			{
				return host_gone;
			}
			else if (host_unknown.toXMPP().equals(condition))
			{
				return host_unknown;
			}
			else if (improper_addressing.toXMPP().equals(condition))
			{
				return improper_addressing;
			}
			else if (internal_server_error.toXMPP().equals(condition))
			{
				return internal_server_error;
			}
			else if (invalid_from.toXMPP().equals(condition))
			{
				return invalid_from;
			}
			else if (invalid_id.toXMPP().equals(condition))
			{
				return invalid_id;
			}
			else if (invalid_namespace.toXMPP().equals(condition))
			{
				return invalid_namespace;
			}
			else if (invalid_xml.toXMPP().equals(condition))
			{
				return invalid_xml;
			}
			else if (not_authorized.toXMPP().equals(condition))
			{
				return not_authorized;
			}
			else if (policy_violation.toXMPP().equals(condition))
			{
				return policy_violation;
			}
			else if (remote_connection_failed.toXMPP().equals(condition))
			{
				return remote_connection_failed;
			}
			else if (resource_constraint.toXMPP().equals(condition))
			{
				return resource_constraint;
			}
			else if (restricted_xml.toXMPP().equals(condition))
			{
				return restricted_xml;
			}
			else if (see_other_host.toXMPP().equals(condition))
			{
				return see_other_host;
			}
			else if (system_shutdown.toXMPP().equals(condition))
			{
				return system_shutdown;
			}
			else if (undefined_condition.toXMPP().equals(condition))
			{
				return undefined_condition;
			}
			else if (unsupported_encoding.toXMPP().equals(condition))
			{
				return unsupported_encoding;
			}
			else if (unsupported_stanza_type.toXMPP().equals(condition))
			{
				return unsupported_stanza_type;
			}
			else if (unsupported_version.toXMPP().equals(condition))
			{
				return unsupported_version;
			}
			else if (xml_not_well_formed.toXMPP().equals(condition))
			{
				return xml_not_well_formed;
			}
			else
			{
				throw new IllegalArgumentException("Condition invalid:" + condition);
			}
		}

		private String value;

		private Condition(String value)
		{
			this.value = value;
		}

		/**
		 * Returns the error code as a valid XMPP error code string.
		 * 
		 * @return the XMPP error code value.
		 */
		public String toXMPP()
		{
			return value;
		}
	}

	
	private class AppCondition implements XMLStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2153874882451976927L;

		private String elementName;
		
		private String namespace;

		/**
		 * @param elementName
		 * @param namespace
		 */
		public AppCondition(String elementName, String namespace)
		{
			this.elementName = elementName;
			this.namespace = namespace;
		}

		/**
		 * @return the elementName
		 */
		public String getElementName()
		{
			return elementName;
		}

		/**
		 * @param elementName the elementName to set
		 */
		public void setElementName(String elementName)
		{
			this.elementName = elementName;
		}

		/**
		 * @return the namespace
		 */
		public String getNamespace()
		{
			return namespace;
		}

		/**
		 * @param namespace the namespace to set
		 */
		public void setNamespace(String namespace)
		{
			this.namespace = namespace;
		}

		@Override
		public String toXML()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\"/>");
			return buf.toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			AppCondition appCondition = (AppCondition) super.clone();
			appCondition.elementName = this.elementName;
			appCondition.namespace = this.namespace;
			return appCondition;
		}
		
		
	}
	
	public class ErrorText implements XMLStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1256842179213623617L;

		private String lang;
		
		private String text;

		/**
		 * @param text
		 */
		public ErrorText(String text)
		{
			this.text = text;
		}

		/**
		 * @return the lang
		 */
		public String getLang()
		{
			return lang;
		}

		/**
		 * @param lang the lang to set
		 */
		public void setLang(String lang)
		{
			this.lang = lang;
		}

		/**
		 * @return the text
		 */
		public String getText()
		{
			return text;
		}

		/**
		 * @param text the text to set
		 */
		public void setText(String text)
		{
			this.text = text;
		}

		/* (non-Javadoc)
		 * @see net.sf.mxlosgi.mxlosgixmppbundle.XMLStanza#toXML()
		 */
		@Override
		public String toXML()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("<text xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"");
			if (this.getLang() != null)
			{
				buf.append(" xml:lang=\"").append(this.getLang()).append("\"");
			}
			buf.append(">");
			buf.append(this.getText());
			buf.append("</text>");
			
			return buf.toString();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException
		{
			ErrorText errorText = (ErrorText) super.clone();
			errorText.lang = this.lang;
			errorText.text = this.text;
			return errorText;
		}
		
		
	}
}
