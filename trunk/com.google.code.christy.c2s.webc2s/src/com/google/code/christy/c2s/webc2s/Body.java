/**
 * 
 */
package com.google.code.christy.c2s.webc2s;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.StreamError;
import com.google.code.christy.xmpp.XmlStanza;

/**
 * @author Noah
 *
 */
public class Body extends AbstractPropertied implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1648908342146692129L;
	
	
	private List<XmlStanza> stanzas = new CopyOnWriteArrayList<XmlStanza>();
	

	public void addStanza(XmlStanza stanza)
	{
		if (stanza instanceof StreamError)
		{
			setProperty("xmlns:stream", "http://etherx.jabber.org/streams");
		}
		this.stanzas.add(stanza);
	}

	public void removeStanza(XmlStanza stanza) {
		this.stanzas.remove(stanza);
	}
	
	public XmlStanza[] getStanzas()
	{
	    	return this.stanzas.toArray(new XmlStanza[]{});
	}
	
	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
	    	
		buf.append("<body");
	    	
	    	for (String key : getPropertyKeys())
	    	{	    		
	    		buf.append(" ").append(key).append("=\"").append(getProperty(key)).append("\"");
	    	}

	    	buf.append(" xmlns=\"http://jabber.org/protocol/httpbind\"");
	    	
	    	if (this.stanzas.isEmpty())
	    	{
	    		buf.append(" />");	
	    	}
	    	else
	    	{
	    		buf.append(">");	
	    		for (XmlStanza stanza : stanzas)
	    		{
	    			buf.append(stanza.toXml());
	    			
			}
	    		
	    		buf.append("</body>");
	    		
	    	}
	    	
	    	return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Body body = (Body) super.clone();
		body.stanzas = new CopyOnWriteArrayList<XmlStanza>();
		for (XmlStanza stanza : stanzas)
    		{
			body.addStanza((XmlStanza)stanza.clone());
		}
		return body;
	}

}
