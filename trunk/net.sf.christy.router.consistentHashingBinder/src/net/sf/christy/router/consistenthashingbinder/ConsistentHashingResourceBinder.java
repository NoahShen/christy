/**
 * 
 */
package net.sf.christy.router.consistenthashingbinder;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.christy.router.ResourceBinder;
import net.sf.christy.router.SmSession;

/**
 * @author noah
 *
 */
public class ConsistentHashingResourceBinder implements ResourceBinder
{
	private HashFunctionServiceTracker hashFunctionServiceTracker;

	private HashFunction hashFunction;
	
	private boolean isStartBinding = false;
	
	private int newAddedSmSessionCount;
	
	private final int numberOfReplicas;
	
	private int rehash;

	private final SortedMap<Integer, SmSession> circle = new TreeMap<Integer, SmSession>();

	/**
	 * @param hashFunctionServiceTracker
	 * @param numberOfReplicas
	 */
	public ConsistentHashingResourceBinder(HashFunctionServiceTracker hashFunctionServiceTracker, int numberOfReplicas)
	{
		this.hashFunctionServiceTracker = hashFunctionServiceTracker;
		this.hashFunction = this.hashFunctionServiceTracker.getHashFunction();
		this.numberOfReplicas = numberOfReplicas;
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
	public void handleRequest(String jidNode, String xml, Map<String, Object> properties)
	{
		
		String from = (String) properties.get("from");
		String streamid = (String) properties.get("streamid");
		
		StringBuilder sbuilder = 
			new StringBuilder("<route from=\"")
				.append(from)
				.append("\" streamid=\"").append(streamid)
				.append("\" type=\"set\">")
				.append(xml)
				.append("<bindResource jidNode=\"")
				.append(jidNode)
				.append("\" xmlns=\"christy:internal:bindResource\"/>");
		
		if (newAddedSmSessionCount > 0)
		{
			sbuilder.append("<redirect times=\"0\" total=\"")
					.append(newAddedSmSessionCount).append("\"/>");
		}
		sbuilder.append("</route>");
		
		SmSession smSession = get(jidNode);
		smSession.write(sbuilder.toString());
		
		if (!isStartBinding)
		{
			isStartBinding = true;
		}
	}

	@Override
	public String getName()
	{
		return "ConsistentHashingResourceBinder";
	}

	@Override
	public void smSessionAdded(SmSession smSession)
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
			success = true;
			break;
		}
		while (rehash2-- > 0);
		
		if (!success)
		{
			throw new RuntimeException("hash duplicate");
		}
		
		if (isStartBinding)
		{
			++newAddedSmSessionCount;
		}
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
	public void smSessionRemoved(SmSession smSession)
	{
		String key = smSession.getProperty("hashKey") != null ? 
				smSession.getProperty("hashKey").toString() : smSession.getSmName();
				
		for (int i = 0; i < numberOfReplicas; i++)
		{
			circle.remove(hashFunction.hash(key + i));
		}
		
		if (isStartBinding)
		{
			--newAddedSmSessionCount;
		}

	}
	
	public SmSession get(Object key)
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

}
