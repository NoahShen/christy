package com.google.code.christy.sm.disco.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.xmpp.disco.DiscoInfoFeature;

/**
 * @author noah
 * 
 */
public class DiscoInfoFeatureServiceTracker extends ServiceTracker
{
	private DiscoManagerImpl discoManager;
	
	public DiscoInfoFeatureServiceTracker(BundleContext context)
	{
		super(context, DiscoInfoFeature.class.getName(), null);
	}

	public void setDiscoManager(DiscoManagerImpl discoManager)
	{
		this.discoManager = discoManager;
	}

	public DiscoInfoFeature[] getDiscoInfoFeatures(String node)
	{
		
		Object[] services = getServices();
		if (services == null)
		{
			return new DiscoInfoFeature[]{};
		}

		List<DiscoInfoFeature> features = new ArrayList<DiscoInfoFeature>();
		for (Object obj : services)
		{
			DiscoInfoFeature feature = (DiscoInfoFeature) obj;
			if (node == null)
			{
				if (feature.getNode() == null)
				{
					features.add(feature);
				}
			}
			else
			{
				if (node.equals(feature.getNode()))
				{
					features.add(feature);
				}
			}
		}

		return features.toArray(new DiscoInfoFeature[] {});

	}

	@Override
	public Object addingService(ServiceReference reference)
	{
		Object obj = super.addingService(reference);
		if (discoManager != null)
		{
			discoManager.discoInfoChanged();
		}
		return obj;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service)
	{
		super.modifiedService(reference, service);
		if (discoManager != null)
		{
			discoManager.discoInfoChanged();
		}
		
	}

	@Override
	public void removedService(ServiceReference reference, Object service)
	{
		super.removedService(reference, service);
		if (discoManager != null)
		{
			discoManager.discoInfoChanged();
		}
		
	}
	
	
}