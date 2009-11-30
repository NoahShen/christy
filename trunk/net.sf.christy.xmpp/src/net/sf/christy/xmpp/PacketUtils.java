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
}
