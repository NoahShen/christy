package net.sf.christy.router.consistenthashingdispatcher;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class HashFunctionServiceTracker extends ServiceTracker
{

	public HashFunctionServiceTracker(BundleContext context)
	{
		super(context, HashFunction.class.getName(), null);
	}

	public HashFunction getHashFunction()
	{
		return (HashFunction) getService();
	}
	
	public HashFunction getHashFunction(String name)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return null;
		}
		
		for (Object obj : services)
		{
			HashFunction function = (HashFunction) obj;
			if (name.equals(function.getName()))
			{
				return function;
			}
		}
		
		return null;
	}
	
	public HashFunction[] getAllHashFunctions()
	{
		Object[] services = getServices();
		return (HashFunction[]) services;
	}
}
