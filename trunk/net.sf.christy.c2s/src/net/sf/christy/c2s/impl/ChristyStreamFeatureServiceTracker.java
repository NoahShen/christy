/**
 * 
 */
package net.sf.christy.c2s.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.christy.c2s.ChristyStreamFeature;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class ChristyStreamFeatureServiceTracker extends ServiceTracker
{

	public ChristyStreamFeatureServiceTracker(BundleContext context)
	{
		super(context, ChristyStreamFeature.class.getName(), null);
	}

	public ChristyStreamFeature[] getStreamFeatures(ChristyStreamFeature.SupportedType type)
	{
		Object[] services = getServices();
		List<ChristyStreamFeature> features = new ArrayList<ChristyStreamFeature>();
		if (services != null)
		{
			for (Object obj : services)
			{
				ChristyStreamFeature feature = (ChristyStreamFeature) obj;
				if (feature.getType() == type)
				{
					features.add(feature);
				}
			}
		}
		return features.toArray(new ChristyStreamFeature[]{});
	}
	
	public ChristyStreamFeature[] getAllStreamFeatures()
	{
		
		Object[] services = getServices();
		if (services == null)
		{
			return new ChristyStreamFeature[]{};
		}
		return (ChristyStreamFeature[]) services;
	}
	
}
