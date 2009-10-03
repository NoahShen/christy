package net.sf.christy.router;

import net.sf.christy.router.impl.ResourceBinderServiceTracker;
import net.sf.christy.router.impl.RouterManagerImpl;
import net.sf.christy.router.impl.consistentHashingImpl.ConsistentHashingResourceBinder;
import net.sf.christy.router.impl.consistentHashingImpl.HashFunction;
import net.sf.christy.router.impl.consistentHashingImpl.HashFunctionServiceTracker;
import net.sf.christy.router.impl.consistentHashingImpl.Md5HashFunction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration md5HashFunctionRegistration;

	private HashFunctionServiceTracker hashFunctionServiceTracker;

	private ServiceRegistration consistentHashingResourceBinderRegistration;

	private ResourceBinderServiceTracker resourceBinderServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

		Md5HashFunction md5HashFunction = new Md5HashFunction();
		md5HashFunctionRegistration = context.registerService(HashFunction.class.getName(), md5HashFunction, null);

		hashFunctionServiceTracker = new HashFunctionServiceTracker(context);
		hashFunctionServiceTracker.open();

		ConsistentHashingResourceBinder consistentHashingResourceBinder = 
			new ConsistentHashingResourceBinder(hashFunctionServiceTracker, 50);// 50 replicas

		consistentHashingResourceBinderRegistration = 
			context.registerService(ResourceBinder.class.getName(), consistentHashingResourceBinder, null);

		resourceBinderServiceTracker = new ResourceBinderServiceTracker(context);
		resourceBinderServiceTracker.open();

		RouterManager rm = new RouterManagerImpl(resourceBinderServiceTracker);

		// test code
		rm.setDomain("example.com");
		rm.registerSmModule("sm_1", "md5password");
		rm.registerC2sModule("c2s_1", "md5password");
		rm.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (md5HashFunctionRegistration != null)
		{
			md5HashFunctionRegistration.unregister();
			md5HashFunctionRegistration = null;
		}

		if (hashFunctionServiceTracker != null)
		{
			hashFunctionServiceTracker.close();
			hashFunctionServiceTracker = null;
		}

		if (consistentHashingResourceBinderRegistration != null)
		{
			consistentHashingResourceBinderRegistration.unregister();
			consistentHashingResourceBinderRegistration = null;
		}

		if (resourceBinderServiceTracker != null)
		{
			resourceBinderServiceTracker.close();
			resourceBinderServiceTracker = null;
		}
	}

}
