/**
 * 
 */
package net.sf.christy.sm.disco.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.christy.sm.disco.DiscoItem;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 *
 */
public class DiscoItemServiceTracker extends ServiceTracker
{

	private DiscoManagerImpl discoManager;
	
	public DiscoItemServiceTracker(BundleContext context)
	{
		super(context, DiscoItem.class.getName(), null);
	}

	public void setDiscoManager(DiscoManagerImpl discoManager)
	{
		this.discoManager = discoManager;
	}


	public DiscoItem[] getDiscoItems(String node)
	{
		
		Object[] services = getServices();
		if (services == null)
		{
			return new DiscoItem[]{};
		}

		List<DiscoItem> discoItems = new ArrayList<DiscoItem>();
		for (Object obj : services)
		{
			DiscoItem discoItem = (DiscoItem) obj;
			if (node == null)
			{
				if (discoItem.getNode() == null)
				{
					discoItems.add(discoItem);
				}
			}
			else
			{
				if (node.equals(discoItem.getNode()))
				{
					discoItems.add(discoItem);
				}
			}
		}

		return discoItems.toArray(new DiscoItem[] {});

	}
	

	@Override
	public Object addingService(ServiceReference reference)
	{
		Object obj = super.addingService(reference);
		discoManager.discoItemChanged();
		return obj;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service)
	{
		super.modifiedService(reference, service);
		discoManager.discoItemChanged();
	}

	@Override
	public void removedService(ServiceReference reference, Object service)
	{
		super.removedService(reference, service);
		discoManager.discoItemChanged();
	}
}
