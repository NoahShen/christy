package net.sf.christy.sm.impl;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.UserResource;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.XmlStanza;

public class UserResourceImpl extends AbstractPropertied implements UserResource
{
	private OnlineUserImpl onlineUser;
	
	private String resource;
	
	private String relatedC2s;
	
	private String streamId;

	private boolean sessionBinded = false;
	
	private Presence presence;

	private SmManagerImpl smManager;
	/**
	 * @param node
	 * @param resource
	 */
	public UserResourceImpl(OnlineUserImpl onlineUser, String resource, 
								String relatedC2s, 
								String streamId,
								SmManagerImpl smManager)
	{
		this.onlineUser = onlineUser;
		this.resource = resource;
		this.relatedC2s = relatedC2s;
		this.streamId = streamId;
		this.smManager = smManager;
	}

	@Override
	public OnlineUser getOnlineUser()
	{
		return onlineUser;
	}

	@Override
	public String getResource()
	{
		return resource;
	}

	@Override
	public String getRelatedC2s()
	{
		return relatedC2s;
	}

	@Override
	public String getStreamId()
	{
		return streamId;
	}

	@Override
	public Presence getPresence()
	{
		return presence;
	}

	@Override
	public void setPresence(Presence presence)
	{
		this.presence = presence;
	}

	@Override
	public boolean isAvailable()
	{
		if (presence != null)
		{
			return presence.isAvailable();
		}
		return false;
	}
	
	@Override
	public int getPriority()
	{
		if (presence != null)
		{
			return presence.getPriority();
		}
		return Integer.MIN_VALUE;
	}
	
	@Override
	public void sendToSelfClient(XmlStanza stanza)
	{
		RouteMessage routeMessage = 
			new RouteMessage(smManager.getName(), getRelatedC2s(), getStreamId());
		routeMessage.setXmlStanza(stanza);
		smManager.sendToRouter(routeMessage);
		
	}

	@Override
	public void sendToOtherUser(XmlStanza stanza)
	{
		// TODO privacy
		RouteMessage routeMessage = 
			new RouteMessage(smManager.getName());
		routeMessage.setXmlStanza(stanza);
		if (stanza instanceof Packet)
		{
			JID jid = ((Packet) stanza).getTo();
			
			if (jid.getDomain().equals(smManager.getDomain()))
			{
				routeMessage.setToUserNode(jid.getNode());
			}
		}
		
		smManager.sendToRouter(routeMessage);
	}
	
	
	@Override
	public boolean isSessionBinded()
	{
		return sessionBinded;
	}

	public void setSessionBinded(boolean sessionBinded)
	{
		this.sessionBinded = sessionBinded;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((streamId == null) ? 0 : streamId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserResourceImpl other = (UserResourceImpl) obj;
		if (resource == null)
		{
			if (other.resource != null)
				return false;
		}
		else if (!resource.equals(other.resource))
			return false;
		if (streamId == null)
		{
			if (other.streamId != null)
				return false;
		}
		else if (!streamId.equals(other.streamId))
			return false;
		return true;
	}

	@Override
	public void logOut()
	{
		onlineUser.removeUserResource(this);
	}
	
	@Override
	public String toString()
	{
		return "UserResourceImpl [node=" + onlineUser.getNode() + ", resource=" + resource + ", sessionBinded=" + sessionBinded + ", streamId=" + streamId + "]";
	}


	


	
}
