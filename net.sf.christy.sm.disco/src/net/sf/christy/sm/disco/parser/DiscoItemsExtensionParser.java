package net.sf.christy.sm.disco.parser;


import net.sf.christy.sm.disco.DiscoItemsExtension;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.PacketExtension;
import net.sf.christy.xmppparser.ExtensionParser;
import net.sf.christy.xmppparser.XmppParser;

import org.xmlpull.v1.XmlPullParser;


/**
 * @author noah
 *
 */
public class DiscoItemsExtensionParser implements ExtensionParser
{
        
        @Override
        public String getElementName()
        {
                return "query";
        }


        @Override
        public String getNamespace()
        {
                return "http://jabber.org/protocol/disco#items";
        }


        @Override
        public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception
        {
                DiscoItemsExtension discoItem = new DiscoItemsExtension();
                String node = parser.getAttributeValue("", "node");
                discoItem.setNode(node);
                
                boolean done = false;
                while (!done)
                {
                        int eventType = parser.next();
                        String elementName = parser.getName();
                        if (eventType == XmlPullParser.START_TAG)
                        {
                                String namespace = parser.getNamespace(null);
                                if ("item".equals(elementName))
                                {
                              	  DiscoItemsExtension.Item item =
                                                new DiscoItemsExtension.Item(new JID(parser.getAttributeValue("", "jid")));
                                        item.setName(parser.getAttributeValue("", "name"));
                                        item.setNode(parser.getAttributeValue("", "node"));
                                        discoItem.addItem(item);
                                }
                                else
                                {
                                        ExtensionParser xparser = xmppParser.getExtensionParser(elementName, namespace);
                                        if (xparser != null)
                                        {
                                                PacketExtension packetX = xparser.parseExtension(parser, xmppParser);
                                                discoItem.addExtension(packetX);
                                        }
                                        else
                                        {
                                                PacketExtension packetX = xmppParser.parseUnknownExtension(parser, elementName, namespace);
                                                discoItem.addExtension(packetX);
                                        }
                                }
                        }
                        else if (eventType == XmlPullParser.END_TAG)
                        {
                                if (getElementName().equals(elementName))
                                {
                                        done = true;
                                }
                        }
                        
                }
                
                return discoItem;
        }

}