package com.google.code.christy.mina;


import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.google.code.christy.xmpp.XmlStanza;

/**
 * Encoder that does nothing. We are already writing ByteBuffers so there is no
 * need to encode them.
 * <p>
 * 
 * This class exists as a counterpart of {@link XmppDecoder}. Unlike that class
 * this class does nothing.
 * 
 * @author Gaston Dombiak
 */
public class XmppEncoder extends ProtocolEncoderAdapter
{

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
	{
		if (message instanceof XmlStanza)
		{
			XmlStanza stanza = (XmlStanza) message;
			ByteBuffer btyeBuffer = ByteBuffer.wrap(stanza.toXml().getBytes("UTF-8"));
			out.write(btyeBuffer);
			out.flush();
		}
		else if (message instanceof String)
		{
			String str = (String) message;
			ByteBuffer btyeBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
			out.write(btyeBuffer);
			out.flush();
		}
	}
}
