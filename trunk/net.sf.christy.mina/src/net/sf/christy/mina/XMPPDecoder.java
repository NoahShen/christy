package net.sf.christy.mina;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decoder class that parses ByteBuffers and generates XML stanzas. Generated
 * stanzas are then passed to the next filters.
 * 
 * @author Gaston Dombiak
 */
public class XMPPDecoder extends CumulativeProtocolDecoder
{

	protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		// Get the XML light parser from the IoSession
		XMLLightweightParser parser = (XMLLightweightParser) session.getAttribute("XMLLightweightParser");
		if (parser == null)
		{
			parser = new XMLLightweightParser("UTF-8");
			session.setAttribute("XMLLightweightParser", parser);
		}		
		// Parse as many stanzas as possible from the received data
		parser.read(in);
		
		if (parser.areThereMsgs())
		{
			for (String stanza : parser.getMsgs())
			{
				out.write(stanza);
			}
		}
		return true;
	}
}
