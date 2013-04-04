// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.core.model.PersonImpl;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.Person;
import org.couchdb.core.CouchData;
import org.couchdb.placeholders.ActivityHandler;
import org.couchdb.placeholders.FriendHandler;
import org.couchdb.placeholders.GroupHandler;
import org.couchdb.placeholders.MessageHolder;
import org.couchdb.placeholders.PersonHandler;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

public class CouchDataImpl implements CouchData
{
  private static final CouchDbClient dbClient = new CouchDbClient( "Couchdb/couchdb.properties" );

  public void createPerson( Person person_ )
  {
    PersonHandler peopleObject = new PersonHandler( (PersonImpl) person_, "person" );
    dbClient.save( peopleObject );
  }

  public Person getPerson( String userId_ )
  {
    PersonHandler personHandler = getPersonHandler( userId_ );
    PersonImpl person = personHandler != null ? personHandler.getPersonObject() : null;
    return person;
  }

  public PersonHandler getPersonHandler( String userId_ )
  {
    Person result = null;
    List params = new ArrayList();
    params.add( userId_ );
    params.add( "person" );
    List<PersonHandler> docObjectList =
      dbClient.view( "example/getPersonHandler" ).key( params ).includeDocs( true ).query( PersonHandler.class );
    System.out.println( docObjectList );
    return (docObjectList != null && docObjectList.get( 0 ) != null) ? docObjectList.get( 0 ) : null;
  }

  public void updatePerson( Person person_ )
  {

    PersonHandler personHandler = getPersonHandler( person_.getId() );
    if( personHandler != null )
    {
      personHandler.setPersonObject( (PersonImpl) person_ );
    }
    dbClient.update( personHandler );
  }

  public boolean deletePerson( String userId_ )
  {
    PersonHandler personHandler = getPersonHandler( userId_ );
    boolean result = false;
    if( personHandler != null )
    {
      dbClient.remove( personHandler.getId(), personHandler.getRevId() );
      result = true;
    }
    else
    {
      System.out.println( "This person is not found in the database!" );
    }
    return result;
  }

  @Override
  public void createActivity( Activity activity_ )
  {
    ActivityHandler activityObject = new ActivityHandler( (ActivityImpl) activity_, "activity" );
    dbClient.save( activityObject );
  }

  @Override
  public List<Activity> getUserActivities( String userId_ )
  {
    List<ActivityHandler> activityHandlers = getActivityHandlers( userId_ );
    List<Activity> activityList = new ArrayList<Activity>();
    if( activityHandlers != null )
    {
      for( ActivityHandler activityHandler : activityHandlers )
      {
        if( activityHandler != null )
        {
          activityList.add( activityHandler.getActivityAObject() );
        }
      }
    }
    return activityList;
  }

  public List<ActivityHandler> getActivityHandlers( String userId_ )
  {
    List params = new ArrayList();
    params.add( userId_ );
    params.add( "activity" );
    List<ActivityHandler> activityHandlerList =
      dbClient.view( "example/getActivityHandlers" ).key( params ).includeDocs( true ).query( ActivityHandler.class );
    System.out.println( activityHandlerList );
    return (activityHandlerList != null && activityHandlerList.size() > 0) ? activityHandlerList : null;
  }

  public ActivityHandler getActivityHandler( String activityId_, String userId_ )
  {
    List params = new ArrayList();
    params.add( activityId_ );
    params.add( userId_ );
    params.add( "activity" );
    List<ActivityHandler> activityHandlerList =
      dbClient.view( "example/getActivityHandler" ).key( params ).includeDocs( true ).query( ActivityHandler.class );
    System.out.println( activityHandlerList );
    return (activityHandlerList != null && activityHandlerList.get( 0 ) != null) ? activityHandlerList.get( 0 ) : null;
  }

  @Override
  public boolean updateActivity( Activity activity_ )
  {
    boolean status = false;
    ActivityHandler activityHandler = getActivityHandler( activity_.getId(), activity_.getUserId() );
    activityHandler.setActivityObject( (ActivityImpl) activity_ );
    Response response = dbClient.update( activityHandler );
    if( response.getError() != null && response.getError().length() > 0 )
    {
      System.out.println( "Activity updation failed! " );
    }
    else
    {
      status = true;
    }
    return status;
  }

  @Override
  public boolean deleteActivity( String activityId, String userId )
  {
    ActivityHandler activityHandler = getActivityHandler( activityId, userId );
    boolean status = false;
    if( activityHandler != null )
    {
      dbClient.remove( activityHandler );
      status = true;
    }
    else
    {
      System.out.println( "This activity doesn't exist in the database!" );
    }
    return status;
  }

  @Override
  public void createMessage( Message message_ )
  {
    MessageHolder messageHandler = new MessageHolder( (MessageImpl) message_, "message" );
    Response resp = dbClient.save( messageHandler );
  }

  @Override
  public List<Message> getMessages( String userId_ )
  {
    return getMessages( userId_, null, null );
  }

  @Override
  public List<Message> getMessages( String userId_, String collectionId_ )
  {
    return getMessages( userId_, collectionId_, null );
  }

  @Override
  public List<Message> getMessages( String userId_, String collectionId_, String messageId_ )
  {
    List<MessageHolder> messageHolders = getMessageHolder( userId_, collectionId_, messageId_ );
    List<Message> messageList = new ArrayList<Message>();
    if( messageHolders != null && messageHolders.size() > 0 )
    {
      for( MessageHolder messageHolder : messageHolders )
      {
        if( messageHolder.getDocObject() != null )
        {
          messageList.add( messageHolder.getDocObject() );
        }
      }
    }
    return messageList;
  }

  public List<MessageHolder> getMessageHolder( String userId_, String collectionId_, String messageId_ )
  {
    List params = new ArrayList();
    if( userId_ != null )
    {
      params.add( userId_ );
    }
    if( collectionId_ != null )
    {
      params.add( collectionId_ );
    }
    if( messageId_ != null )
    {
      params.add( messageId_ );
    }
    List<MessageHolder> messageHolders =
      dbClient.view( "example/getMessageHandlers" ).key( params ).includeDocs( true ).query( MessageHolder.class );
    System.out.println( messageHolders );
    return messageHolders;
  }

  @Override
  public void deleteMessage()
  {
    // TODO Auto-generated method stub

  }

  public void addFriend( String userId_, String friendId_ )
  {
    FriendHandler friendListHandler =  getFriendListHandler( userId_ );
    List<String> friends = null;
    if( friendListHandler == null )
    {
      friendListHandler = new FriendHandler();
      friendListHandler.setDocType( "friend" );
      friendListHandler.setUserId( userId_ );
      friends = new ArrayList<String>();
      friends.add(friendId_);
      friendListHandler.setFriendList( friends );
      dbClient.save( friendListHandler );
    }
    else
    {
      friends = friendListHandler.getFriendList();
      if( friends.contains( friendId_ ))
      {
        System.out.println( friendId_ + " is already your friend!");
      }
      else
      {
        friends.add(friendId_);
        friendListHandler.setFriendList( friends );
        dbClient.update( friendListHandler );
      }
    }
  }

  public List<String> getFriendList( String userId_ )
  {
    FriendHandler friendHandler = getFriendListHandler( userId_ );
    return friendHandler != null?friendHandler.getFriendList():null;
  }
  public FriendHandler getFriendListHandler( String userId_ )
  {
    FriendHandler result = null;
    List params = new ArrayList();
    params.add( userId_ );
    params.add( "friend" );

    List<FriendHandler> friendHandler =
      dbClient.view( "example/getFriendHandler" ).key( params ).includeDocs( true ).query( FriendHandler.class );
    if( friendHandler != null && friendHandler.size() == 1)
    {
      result = friendHandler.get(0);
    }
    return result;
  }
  public void createGroup( Group group_ )
  {
    GroupHandler groupHandler = new GroupHandler();
    groupHandler.setDocType( "group" );
    groupHandler.setGroupObject( (GroupImpl)group_ );
    dbClient.save( groupHandler );
  }

  public void addUserToGroup( String userId_, String groupId_ )
  {
    GroupHandler groupHandler = getGroupHandler( groupId_ );
    if( groupHandler != null )
    {
      List<String> members = groupHandler.getGroupmembers();
      if( members == null )
      {
        members = new ArrayList<String>();
      }
      if( !members.contains( userId_ ) )
      {
        members.add( userId_ );
        groupHandler.setGroupmembers( members );
        dbClient.update( groupHandler );
      }
    }
    else
    {
      System.out.println("Group doesn't exist in the db!");
    }
  }
  public Group getGroup( String groupId_ )
  {
    GroupHandler groupHandler = getGroupHandler( groupId_ );
    Group result = null;
    if( groupHandler != null )
    {
      result = groupHandler.getGroupObject();
    }
    return result;
  }
  public List<Group> getUserGroups( String userId_ )
  {
    List<GroupHandler> userGroupHandlers = null;
    List<Group> userGroups = null;
    GroupHandler result = null;

    userGroupHandlers = dbClient.view( "example/getUserGroups" ).key( userId_ ).includeDocs( true ).query( GroupHandler.class );
    if( userGroupHandlers != null )
    {
      userGroups = new ArrayList<Group>();
      for( GroupHandler groupHandler : userGroupHandlers )
      {
        userGroups.add( groupHandler.getGroupObject() );
      }
    }
    return userGroups;
  }

  public GroupHandler getGroupHandler( String groupId_ )
  {
    GroupHandler result = null;
    List params = new ArrayList();
    params.add( groupId_ );
    params.add( "group" );

    List<GroupHandler> groupHandler =
      dbClient.view( "example/getGroupHandler" ).key( params ).includeDocs( true ).query( GroupHandler.class );
    if( groupHandler != null && groupHandler.size() == 1 )
    {
      result = groupHandler.get(0);
    }
    return result;
  }
}
