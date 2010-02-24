package com.google.code.christy.router.consistenthashingdispatcher;

import com.google.code.christy.util.StringUtils;

public class Md5HashFunction implements HashFunction
{

	@Override
	public String getName()
	{
		return "Md5HashFunction";
	}

	@Override
	public int hash(Object obj)
	{
		byte[] b = StringUtils.hashBytes(obj.toString(), "MD5");
		int hashCode = Math.abs(bytesToInt(b));
		return hashCode;
	}
	

	private static int bytesToInt(byte[] bytes)
	{
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

}
