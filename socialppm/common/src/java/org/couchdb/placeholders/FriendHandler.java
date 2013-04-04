// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import java.util.List;

public class FriendHandler {
	private List<String> friendList;
  private String userId;
	private String type;
	private String _id;
	private String _rev;
	public FriendHandler()
	{
	}
	public FriendHandler(List<String> friendList_, String type_)
	{
		friendList = friendList_;
		type = type_;
	}
	
	public List<String> getFriendList(){
		return friendList;
	}
	public String getDocType(){
		return type;
	}
	public void setFriendList(List<String> friendList_){
		friendList = friendList_;
	}
	public void setDocType(String type_){
		type = type_;
	}
	public String getId(){
		return _id;
	}
	public void setId(String id_){
		_id = id_;
	}
	public String getRevId(){
		return _rev;
	}
	public void setRevId(String rev_){
		_rev = rev_;
  }
  public String getUserId()
  {
    return userId;
  }

  public void setUserId( String userId_ )
  {
    userId = userId_;
  }
}
