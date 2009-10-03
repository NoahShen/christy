/**
 * 
 */
package net.sf.christy.router.impl.consistentHashingImpl;

import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.christy.router.BindTask;
import net.sf.christy.router.BindTaskListener;
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


	@Override
	public BindTask bindResouce(String jidNode, String resouce)
	{
		return bindResouce(jidNode, resouce, null);
	}


	@Override
	public BindTask bindResouce(String jidNode, String resouce, BindTaskListener listener)
	{
		// TODO Auto-generated method stub
		
		
		
		if (!isStartBinding)
		{
			isStartBinding = true;
		}
		return null;
	}

	@Override
	public String getName()
	{
		return "ConsistentHashingResourceBinder";
	}

	@Override
	public void smSessionAdded(SmSession smSession)
	{

		
		if (isStartBinding)
		{
			++newAddedSmSessionCount;
		}

		for (int i = 0; i < numberOfReplicas; i++)
		{
			circle.put(hashFunction.hash(smSession.getSmName() + i), smSession);
		}
	}

	@Override
	public void smSessionRemoved(SmSession smSession)
	{
		for (int i = 0; i < numberOfReplicas; i++)
		{
			circle.remove(hashFunction.hash(smSession.getSmName() + i));
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
