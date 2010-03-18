package com.google.code.christy.sm.consistenthashinginterceptor;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.routemessage.searchextension.CheckedNode;
import com.google.code.christy.routemessage.searchextension.SearchCompletedExtension;
import com.google.code.christy.routemessage.searchextension.SearchRouteExtension;
import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.SmToRouterInterceptor;
import com.google.code.christy.sm.UserResource;


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
						smManager.createUserResource(userNode, resource, relatedC2s, streamId, bindedRes.isSesseionBinded());
					
					userResource.setPresence(bindedRes.getPresence());
				}
			}
			

			RouteMessage searchCompleted = new RouteMessage(smManager.getName(),routeMessage.getStreamId());
			searchCompleted.setTo(routeMessage.getFrom());
			searchCompleted.setToUserNode(userNode);
			searchCompleted.addRouteExtension(new SearchCompletedExtension());
			smManager.sendToRouter(searchCompleted);
			
			return false;
			
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
				smManager.removeOnlineUser(onlineUser);
			}
			searchExtension.addCheckedNode(node);
			RouteMessage searchResponse = new RouteMessage(smManager.getName(),routeMessage.getStreamId());
			searchResponse.setToUserNode(routeMessage.getToUserNode());
			searchResponse.setFrom(smManager.getName());
			searchResponse.setTo(routeMessage.getFrom());
			searchResponse.addRouteExtension(searchExtension);
			searchResponse.setXmlStanza(routeMessage.getXmlStanza());
			smManager.sendToRouter(searchResponse);
			
			
		}
		return true;
	}

	@Override
	public boolean smMessageSent(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		return false;
	}

}
