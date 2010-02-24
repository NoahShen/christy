package com.google.code.christy.sm.impl;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.Presence;
import com.google.code.christy.xmpp.PrivacyList;
import com.google.code.christy.xmpp.XmlStanza;


public class UserResourceImpl extends AbstractPropertied implements UserResource
{
	private OnlineUserImpl onlineUser;
	
	private String resource;
	
	private String relatedC2s;
	
	private String streamId;

	private boolean sessionBinded = false;
	
	private Presence presence;

	private PrivacyList activePrivacyList;
	
	private SmManagerImpl smManager;
	
	private JID fullJid;
	
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
		this.fullJid = new JID(onlineUser.getNode(), smManager.getDomain(), resource);
	}

	@Override
	public JID getFullJid()
	{
		return fullJid;
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
		if (!isAvailable())
		{
			return;
		}
		
		if (stanza instanceof Packet)
		{
			if (smManager.getPrivacyManager().shouldBlockSend2OtherPacket(onlineUser, this, (Packet) stanza))
			{
				return;
			}
		}
		
		
		RouteMessage routeMessage = 
			new RouteMessage(smManager.getName());
		routeMessage.setXmlStanza(stanza);
		if (stanza instanceof Packet)
		{
			Packet packet = (Packet) stanza;
			JID jid = packet.getTo();
			
			if (jid.getDomain().equals(smManager.getDomain()))
			{
				routeMessage.setToUserNode(jid.getNode());
			}
			
			JID from = packet.getFrom();
			if (from == null)
			{
				packet.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), getResource()));
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
	public PrivacyList getActivePrivacyList()
	{
		return activePrivacyList;
	}
	
	public void setActivePrivacyList(PrivacyList activePrivacyList)
	{
		this.activePrivacyList = activePrivacyList;
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
