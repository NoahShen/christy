/**
 * 
 */
package com.google.code.christy.router.consistenthashingdispatcher;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.routemessage.searchextension.SearchCompletedExtension;
import com.google.code.christy.routemessage.searchextension.SearchRouteExtension;
import com.google.code.christy.router.RouterManager;
import com.google.code.christy.router.RouterToSmInterceptor;
import com.google.code.christy.router.RouterToSmMessageDispatcher;
import com.google.code.christy.router.SmSession;
import com.google.code.christy.util.StringUtils;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * @author noah
 *
 */
public class ConsistentHashingDispatcher implements RouterToSmMessageDispatcher, RouterToSmInterceptor
{
	private HashFunctionServiceTracker hashFunctionServiceTracker;

	private HashFunction hashFunction;
	
	private AtomicBoolean isStartWorking = new AtomicBoolean(false);
	
	private AtomicInteger newAddedSmSessionCount = new AtomicInteger(0);
	
	private final int numberOfReplicas;
	
	private int rehash;

	private final SortedMap<Integer, SmSession> circle = new TreeMap<Integer, SmSession>();

	private Map<String, SmSession> smSessions = new ConcurrentHashMap<String, SmSession>();
	
	private ListMultimap<String, Object> blockedMessages;
	
	private String hashCircleId;
	
	/**
	 * @param hashFunctionServiceTracker
	 * @param numberOfReplicas
	 */
	public ConsistentHashingDispatcher(HashFunctionServiceTracker hashFunctionServiceTracker, int numberOfReplicas)
	{
		this.hashFunctionServiceTracker = hashFunctionServiceTracker;
		this.hashFunction = this.hashFunctionServiceTracker.getHashFunction();
		this.numberOfReplicas = numberOfReplicas;
		
		blockedMessages = LinkedListMultimap.create();
		blockedMessages = Multimaps.synchronizedListMultimap(blockedMessages);
	}


	/**
	 * @return the rehash
	 */
	public int getRehash()
	{
		return rehash;
	}


	/**
	 * @param rehash the rehash to set
	 */
	public void setRehash(int rehash)
	{
		this.rehash = rehash;
	}

	@Override
	public void sendMessage(RouteMessage routeMessage)
	{
		
		String toUserNode = routeMessage.getToUserNode();
		SmSession selectedSmSession = get(toUserNode);
		
		selectedSmSession.write(routeMessage);
		
		
		if (!isStartWorking.get())
		{
			isStartWorking.set(true);
		}
	}

	@Override
	public String getName()
	{
		return "ConsistentHashingResourceBinder";
	}

	@Override
	public synchronized void smSessionAdded(SmSession smSession)
	{
		int rehash2 = rehash;
		boolean success = false;
		do
		{
			String key = smSession.getProperty("hashKey") != null ? 
						smSession.getProperty("hashKey").toString() : smSession.getSmName();
			if (hashDuplicate(key))
			{
				// duplicate
				smSession.setProperty("hashKey", smSession.getSmName() + rehash2);
				continue;
			}
			for (int i = 0; i < numberOfReplicas; i++)
			{
				circle.put(hashFunction.hash(key + i), smSession);
			}
			reGenerateHashCircleId();
			smSessions.put(smSession.getSmName(), smSession);
			success = true;
			break;
		}
		while (rehash2-- > 0);
		
		if (!success)
		{
			throw new RuntimeException("hash duplicate");
		}
		
		if (isStartWorking.get())
		{
			newAddedSmSessionCount.incrementAndGet();
		}

	}

	private void reGenerateHashCircleId()
	{
		StringBuilder builder = new StringBuilder();
		for (Integer i : circle.keySet())
		{
			builder.append(i);
		}
		hashCircleId = StringUtils.hash(builder.toString(), "MD5");
	}


	private boolean hashDuplicate(String smName)
	{
		for (int i = 0; i < numberOfReplicas; i++)
		{
			if (circle.containsKey(hashFunction.hash(smName + i)))
			{
				return true;
			}
		}
		return false;
	}


	@Override
	public synchronized void smSessionRemoved(SmSession smSession)
	{
		String key = smSession.getProperty("hashKey") != null ? 
				smSession.getProperty("hashKey").toString() : smSession.getSmName();
				
		for (int i = 0; i < numberOfReplicas; i++)
		{
			circle.remove(hashFunction.hash(key + i));
		}
		reGenerateHashCircleId();
		smSessions.remove(smSession.getSmName());
		if (isStartWorking.get())
		{
			newAddedSmSessionCount.decrementAndGet();
		}

	}
	
	public synchronized SmSession get(Object key)
	{
		if (circle.isEmpty())
		{
			return null;
		}
		int hash = hashFunction.hash(key);
		if (!circle.containsKey(hash))
		{
			SortedMap<Integer, SmSession> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return circle.get(hash);
	}


	@Override
	public boolean routeMessageReceived(RouterManager routerManager, RouteMessage routeMessage, SmSession smSession)
	{
		if (routeMessage.containExtension(SearchCompletedExtension.ELEMENTNAME, 
									SearchCompletedExtension.NAMESPACE))
		{
			String node = routeMessage.getToUserNode();
			List<Object> messages = blockedMessages.removeAll(node);
			for (Object obj : messages)
			{
				if (obj instanceof RouteMessage)
				{
					smSession.write((RouteMessage) obj);
				}
				
			}
			return true;
		}
		
		SearchRouteExtension searchExtension = 
			(SearchRouteExtension) routeMessage.getRouteExtension(SearchRouteExtension.ELEMENTNAME, 
										SearchRouteExtension.NAMESPACE);
		if (searchExtension != null)
		{
			// put message to blockMessages
			if (!blockedMessages.containsKey(routeMessage.getToUserNode()))
			{
				blockedMessages.put(routeMessage.getToUserNode(), System.currentTimeMillis());
			}
			
			int total = searchExtension.getTotal();
			// hash ring changed
			String circleId = searchExtension.getHashCircleId();
			if (total != newAddedSmSessionCount.get() || !hashCircleId.equals(circleId))
			{
				routeMessage.removeRouteExtension(searchExtension);
				sendMessage(routeMessage);
			}
			else
			{
				RouteMessage searchMessage = new RouteMessage(routeMessage.getTo(), routeMessage.getStreamId());
				searchExtension.incrementTimes();
				searchMessage.setToUserNode(routeMessage.getToUserNode());
				searchMessage.setXmlStanza(routeMessage.getXmlStanza());
				searchMessage.addRouteExtension(searchExtension);
				
				SmSession smSession2 = null;
				if (searchExtension.getTimes() > searchExtension.getTotal())
				{
					String startNode = searchExtension.getStartNode();
					smSession2 = smSessions.get(startNode);
				}
				else
				{
					smSession2 = getNextSmSession(routeMessage.getToUserNode(), searchExtension);
				}
				
				
				if (smSession2 == null || !smSession2.isConnected())
				{
					// SM Module may be breakdown, research 
					routeMessage.removeRouteExtension(searchExtension);
					sendMessage(routeMessage);
					
				}
				else
				{
					smSession2.write(searchMessage);
				}
			}
			return true;
		}

		
		return false;
	}


	private synchronized SmSession getNextSmSession(String prepedUserNode, SearchRouteExtension searchExtension)
	{
		if (circle.isEmpty())
		{
			return null;
		}
		int hash = hashFunction.hash(prepedUserNode);
		SortedMap<Integer, SmSession> tailMap = circle.tailMap(hash);
		for (SmSession session :  tailMap.values())
		{
			if (!searchExtension.containCheckedNode(session.getSmName()))
			{
				return session;
			}
		}
		
		//reach tail, not find smsession
		return circle.get(circle.firstKey());
	}


	@Override
	public boolean routeMessageSent(RouterManager routerManager, RouteMessage routeMessage, SmSession smSession)
	{
		if (newAddedSmSessionCount.get() > 0)
		{
			//should not intercept search messsage	
			if (!routeMessage.containExtension(SearchRouteExtension.ELEMENTNAME, 
							SearchRouteExtension.NAMESPACE))
			{
				String node = routeMessage.getToUserNode();
				if (blockedMessages.containsKey(node))
				{
					// MayBe ,sm crash , never remove the user's blockedMessages
					List<Object> values = blockedMessages.get(node);
					Long time = (Long) values.get(0);
					if (time == null || System.currentTimeMillis() - time > 20 * 1000)
					{
						blockedMessages.removeAll(node);
						return true;
					}
					
					blockedMessages.put(node, routeMessage);
					return true;
				}
				
				String c2sName = routeMessage.getFrom();
				SearchRouteExtension searchExtension = 
					new SearchRouteExtension(0, 
										newAddedSmSessionCount.get(),
										hashCircleId,
										smSession.getSmName(), 
										c2sName);
				routeMessage.addRouteExtension(searchExtension);
			}

			
		}
		return false;
	}

}
