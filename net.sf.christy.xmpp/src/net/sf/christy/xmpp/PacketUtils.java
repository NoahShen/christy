/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author Noah
 *
 */
public class PacketUtils
{
	public static Iq createResultIq(Iq iq)
	{
		Iq result = new Iq(Iq.Type.result);
		result.setTo(iq.getFrom());
		result.setFrom(iq.getTo());
		result.setStanzaId(iq.getStanzaId());
		return result;
	}
	
	public static Iq createErrorIq(Iq iq)
	{
		Iq iqError = null;
		try
		{
			iqError = (Iq) iq.clone();
			iqError.setFrom(iq.getTo());
			iqError.setTo(iq.getFrom());
			iqError.setType(Iq.Type.error);
			iqError.setStanzaId(iq.getStanzaId());
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return iqError;
	}
	
	public static Presence createErrorPresence(Presence presence)
	{
		Presence presenceError = null;
		try
		{
			presenceError = (Presence) presence.clone();
			presenceError.setFrom(presence.getTo());
			presenceError.setTo(presence.getFrom());
			presenceError.setType(Presence.Type.error);
			presenceError.setStanzaId(presence.getStanzaId());
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return presenceError;
	}
	
	public static Message createErrorMessage(Message message)
	{
		Message messageError = null;
		try
		{
			messageError = (Message) message.clone();
			messageError.setFrom(message.getTo());
			messageError.setTo(message.getFrom());
			messageError.setType(Message.Type.error);
			messageError.setStanzaId(message.getStanzaId());
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return messageError;
	}
}
