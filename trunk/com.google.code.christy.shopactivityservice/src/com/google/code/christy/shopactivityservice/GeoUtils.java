package com.google.code.christy.shopactivityservice;

/**
 *
 * CopyRight (C)    All rights reserved.<p>
 *
 * WuHan Inpoint Information Technology Development,Inc.<p>
 *
 * Author sinoly<p> Project Name: PostGeo
 *
 * @version 1.0    2006-11-13
 *
 * <p>Base on : JDK1.5<p>
 *
 */
public class GeoUtils
{
	public enum GaussSphere
	{
		Beijing54, Xian80, WGS84,
	}

	private static double rad(double d)
	{
		return d * Math.PI / 180.0;
	}

	public static double distanceOfTwoPoints(double lng1, double lat1, double lng2, double lat2, GaussSphere gs)
	{
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * (gs == GaussSphere.WGS84 ? 6378137.0 : (gs == GaussSphere.Xian80 ? 6378140.0 : 6378245.0));
		s = Math.round(s * 10000) / 10000;
		return s;
	}
}