package net.sf.christy.sm.consistenthashinginterceptor;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.SmToRouterInterceptor;

public class ConsistentHashingInterceptor implements SmToRouterInterceptor
{
	private SmManagerTracker smManagerTracker;
	
	public ConsistentHashingInterceptor(SmManagerTracker smManagerTracker)
	{
		this.smManagerTracker = smManagerTracker;
	}

	@Override
	public boolean smMessageReceived(RouteMessage routeMessage)
	{
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean smMessageSent(RouteMessage routeMessage)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
