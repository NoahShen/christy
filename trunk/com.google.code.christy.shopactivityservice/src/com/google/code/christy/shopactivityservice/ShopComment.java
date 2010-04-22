package com.google.code.christy.shopactivityservice;

public class ShopComment
{
	private long commentId;
	
	private long shopId;
	
	private String username;
	
	private int score;
	
	private String content;
	
	private long lasModitDate;

	public ShopComment()
	{
		super();
	}

	public long getCommentId()
	{
		return commentId;
	}

	public void setCommentId(long commentId)
	{
		this.commentId = commentId;
	}

	public long getShopId()
	{
		return shopId;
	}

	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public long getLasModitDate()
	{
		return lasModitDate;
	}

	public void setLasModitDate(long lasModitDate)
	{
		this.lasModitDate = lasModitDate;
	}
	
	
}
