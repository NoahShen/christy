package net.sf.christy.sm.consistenthashinginterceptor;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.routemessage.searchextension.CheckedNode;
import net.sf.christy.routemessage.searchextension.SearchCompletedExtension;
import net.sf.christy.routemessage.searchextension.SearchRouteExtension;
import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.SmToRouterInterceptor;
import net.sf.christy.sm.UserResource;

public class ConsistentHashingInterceptor implements SmToRouterInterceptor
{

	@Override
	public boolean smMessageReceived(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{

		if (!routeMessage.containExtension(SearchRouteExtension.ELEMENTNAME, SearchRouteExtension.NAMESPACE))
		{
			return false;
		}
		
		SearchRouteExtension searchExtension = 
			(SearchRouteExtension) routeMessage.getRouteExtension(SearchRouteExtension.ELEMENTNAME, 
										SearchRouteExtension.NAMESPACE);
		
		int times = searchExtension.getTimes();
		int total = searchExtension.getTotal();
		
		// first search and onlineUser exist,so no need to check
		if (times == 0 && onlineUser != null)
		{
			return false;
		}
		
		
		// search completed
		if (times > total)
		{
			String userNode = routeMessage.getToUserNode();
			CheckedNode[] checkedNodes = searchExtension.getCheckedNode();
			for (CheckedNode checkedNode : checkedNodes)
			{
				for (CheckedNode.BindedResouce bindedRes : checkedNode.getBindedResouces())
				{
					String resource = bindedRes.getName();
					String relatedC2s = bindedRes.getRelatedC2s();
					String streamId = bindedRes.getStreamId();
					UserResource userResource = 
						smManager.createUserResource(userNode, resource, relatedC2s, streamId);
					
					userResource.setPresence(bindedRes.getPresence());
				}
			}
			

			RouteMessage searchCompleted = new RouteMessage(smManager.getName(),routeMessage.getStreamId());
			searchCompleted.setToUserNode(userNode);
			searchCompleted.addRouteExtension(new SearchCompletedExtension());
			smManager.sendToRouter(searchCompleted);
			
			return true;
		}
		// times <= total searching 
		else
		{
			CheckedNode node = new CheckedNode(smManager.getName());
			if (onlineUser != null)
			{
				UserResource[] resources = onlineUser.getAllUserResources();
				for (UserResource res : resources)
				{
					CheckedNode.BindedResouce bindedResource = 
						node.new BindedResouce(res.getResource(), 
									res.getRelatedC2s(), 
									res.getStreamId(),
									res.isSessionBinded());
					bindedResource.setPresence(res.getPresence());
					node.addBindedResouce(bindedResource);
				}
			}
			searchExtension.addCheckedNode(node);
			routeMessage.setFrom(smManager.getName());
			smManager.sendToRouter(routeMessage);
			
			smManager.removeOnlineUser(onlineUser);
			
			return true;
		}
	}

	@Override
	public boolean smMessageSent(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		return false;
	}

}
