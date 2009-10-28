package net.sf.christy.sm.consistenthashinginterceptor;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.SmToRouterInterceptor;

public class ConsistentHashingInterceptor implements SmToRouterInterceptor
{

	@Override
	public boolean smMessageReceived(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		if (onlineUser != null)
		{
			return false;
		}
		
		
		return false;
	}

	@Override
	public boolean smMessageSent(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
