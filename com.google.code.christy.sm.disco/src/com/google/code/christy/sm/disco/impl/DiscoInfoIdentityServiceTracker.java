package com.google.code.christy.sm.disco.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.xmpp.disco.DiscoInfoIdentity;

/**
 * @author noah
 * 
 */
public class DiscoInfoIdentityServiceTracker extends ServiceTracker
{

	private DiscoManagerImpl discoManager;
	
	public DiscoInfoIdentityServiceTracker(BundleContext context)
	{
		super(context, DiscoInfoIdentity.class.getName(), null);
	}
	
	public void setDiscoManager(DiscoManagerImpl discoManager)
	{
		this.discoManager = discoManager;
	}

	public DiscoInfoIdentity[] getDiscoInfoIdentities(String node)
	{

		List<DiscoInfoIdentity> identities = new ArrayList<DiscoInfoIdentity>();
		Object[] services = getServices();
		if (services == null)
		{
			return identities.toArray(new DiscoInfoIdentity[] {});
		}

		for (Object obj : services)
		{
			DiscoInfoIdentity identity = (DiscoInfoIdentity) obj;
			if (node == null)
			{
				if (identity.getNode() == null)
				{
					identities.add(identity);
				}
			}
			else
			{
				if (node.equals(identity.getNode()))
				{
					identities.add(identity);
				}
			}
		}

		return identities.toArray(new DiscoInfoIdentity[] {});
	}
	

	@Override
	public Object addingService(ServiceReference reference)
	{
		Object obj = super.addingService(reference);
		discoManager.discoInfoChanged();
		return obj;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service)
	{
		super.modifiedService(reference, service);
		discoManager.discoInfoChanged();
	}

	@Override
	public void removedService(ServiceReference reference, Object service)
	{
		super.removedService(reference, service);
		discoManager.discoInfoChanged();
	}
}