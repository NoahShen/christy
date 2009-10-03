package net.sf.christy.router.impl.consistentHashingImpl;

import net.sf.christy.util.StringUtils;

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
		String str = obj.toString();
		String hash = StringUtils.hash(str, "MD5");
		int hashCode = Integer.valueOf(hash.substring(0, 7), 16);
		return hashCode;
	}

}
