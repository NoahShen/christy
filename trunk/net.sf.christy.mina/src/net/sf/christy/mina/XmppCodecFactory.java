package net.sf.christy.mina;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Factory that specifies the encode and decoder to use for parsing XMPP stanzas.
 *
 * @author Gaston Dombiak
 */
public class XmppCodecFactory implements ProtocolCodecFactory {

    private final XmppEncoder encoder;
    private final XmppDecoder decoder;

    public XmppCodecFactory() {
        encoder = new XmppEncoder();
        decoder = new XmppDecoder();
    }

    public ProtocolEncoder getEncoder() throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder() throws Exception {
        return decoder;
    }
}
