// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.niku.union.security.SecurityIdentifier;
import com.niku.union.utility.UtilityThreadLocal;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.MessageCollectionImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Document;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.couchdb.adapters.ListFieldAdapter;
import org.couchdb.comparators.ActivityOrderByPostedTime;
import org.couchdb.core.CouchData;
import org.couchdb.placeholders.ActivityEntryHandler;
import org.couchdb.placeholders.ActivityHandler;
import org.couchdb.placeholders.FriendHandler;
import org.couchdb.placeholders.GroupHandler;
import org.couchdb.placeholders.MessageCollectionHolder;
import org.couchdb.placeholders.MessageHolder;
import org.couchdb.placeholders.PersonHandler;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CouchDataImpl implements CouchData
{
  private static final CouchDbClient dbClient = new CouchDbClient( "Couchdb/couchdb.properties" );

  static
  {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.registerTypeAdapter(ListField.class, new ListFieldAdapter()).create();
    dbClient.setGsonBuilder(gsonBuilder);
  }

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
    List<PersonHandler> docObjectList = null;
    params.add( userId_ );
    params.add( "person" );
    try
    {
        docObjectList = dbClient.view( "example/getPersonHandler" ).key( params ).includeDocs( true ).query( PersonHandler.class );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    // If user is not in the couch db but is an existing clarity user then automatically populate the user info into couch db.
    if( docObjectList == null || docObjectList.size() == 0 )
    {
      SecurityIdentifier secId = UtilityThreadLocal.getSecurityIdentifier();
      if( secId != null && userId_.equals( secId.getUserName() ) )
      {
        PersonHandler personHandler = new PersonHandler( (PersonImpl) getDefaultPerson( secId ), "person" );
        docObjectList = new ArrayList<PersonHandler>();
        docObjectList.add( personHandler );
        dbClient.save( personHandler );
      }
    }
    return (docObjectList != null && docObjectList.size() > 0 && docObjectList.get( 0 ) != null) ?
           docObjectList.get( 0 ) : null;
  }

  public List<PersonHandler> getPersonHandlers()
  {
    Person result = null;
    List params = new ArrayList();
    List<PersonHandler> docObjectList = null;
    params.add( "person" );
    try
    {
        docObjectList = dbClient.view( "example/getPersonHandler" ).key( params ).includeDocs( true ).query( PersonHandler.class );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    return docObjectList;
  }

  private Person getDefaultPerson( SecurityIdentifier secId_ )
  {
    Person newPerson = new PersonImpl();
    newPerson.setId( secId_.getUniqueName() );
    newPerson.setDisplayName( secId_.getUserName() );
    Name name = new NameImpl();
    name.setFormatted( secId_.getUserName() );
    name.setGivenName( secId_.getUniqueName() );
    newPerson.setName( name );
    newPerson.setAboutMe( "Hello!!! Nothing added about me..." );
    return newPerson;
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
  public void createActivityEntry( ActivityEntry activityEntry_, String userId_ )
  {
    ActivityEntryHandler activityEntryObject =
      new ActivityEntryHandler( (ActivityEntryImpl) activityEntry_, userId_, "activityEntry" );
    dbClient.save( activityEntryObject );
  }

  @Override
  public List<ActivityEntry> getUserActivityEntries( String userId_ )
  {
    List<ActivityEntryHandler> activityEntryHandlers = getActivityEntryHandlers( userId_ );
    List<ActivityEntry> activityEntryList = new ArrayList<ActivityEntry>();
    if( activityEntryHandlers != null )
    {
      for( ActivityEntryHandler activityEntryHandler : activityEntryHandlers )
      {
        if( activityEntryHandler != null )
        {
          activityEntryList.add( activityEntryHandler.getActivityEntryObject() );
        }
      }
    }
    return activityEntryList;
  }

  public Activity getActivity( String activityId_, String userId_ )
  {
    ActivityHandler activityHandler = getActivityHandler( activityId_, userId_ );
    Activity result = null;
    if( activityHandler != null )
    {
      result = activityHandler.getActivityAObject();
    }
    return result;
  }

  public List<ActivityHandler> getUserActivityHandlers( String userId_ )
  {
    return getUserActivityHandlers( userId_, "@friends" );
  }

  public List<ActivityHandler> getUserActivityHandlers( String userId_, String appId_ )
  {
    List params = new ArrayList();
    if( appId_ == null || appId_.length() == 0 || appId_.equals( "@friends" ) )
    {
      params.add( "@friends" );
    }
    else
    {
      params.add( appId_ );
    }
    params.add( userId_ );
    params.add( "activity" );
    return getActivityHandlers( params );
  }

  public List<ActivityHandler> getGroupActivityHandlers( String groupId_ )
  {
    List params = new ArrayList();
    if( groupId_ == null || groupId_.length() == 0 || groupId_.equals( "@friends" ) )
    {
      params.add( "@friends" );
    }
    else
    {
      params.add( groupId_ );
    }
    params.add( "activity" );
    return getActivityHandlers( params );
  }

  private List<ActivityHandler> getActivityHandlers( List parameters_ )
  {
    List<ActivityHandler> activityHandlerList =
      dbClient.view( "example/getGroupActivityHandlers" ).key( parameters_ ).includeDocs( true ).query( ActivityHandler.class );
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
  public void createActivity( Activity activity_ )
  {
    if( activity_.getId() == null || activity_.getId().length() == 0)
    {
      Random rand = new Random();
      int randInt = Math.abs( rand.nextInt() );
      activity_.setId( "Activity_" + randInt );
    }
    ActivityHandler activityObject = new ActivityHandler( (ActivityImpl) activity_, "activity" );
    dbClient.save( activityObject );
  }

  @Override
  public List<Activity> getActivityComments( String activityId_ )
  {
    List<Activity> result = new ArrayList<Activity>();
    List params = new ArrayList();
    params.add( activityId_ );
    params.add( "activityComment" );
    List<ActivityHandler> commentHandlers =
      dbClient.view( "example/getActivityCommentHandlers" ).key( params ).includeDocs( true )
        .query( ActivityHandler.class );
    for( ActivityHandler commentHandler: commentHandlers )
    {
      Activity activity = commentHandler.getActivityAObject();
      activity.setParentId( activityId_ );
      result.add(activity);
    }

    return result;
  }

  @Override
  public List<Activity> getNewActivites( long lastUpdatedTime )
  {
    List<Activity> result = new ArrayList<Activity>();
    List params = new ArrayList();
    params.add( lastUpdatedTime );
    params.add( "activity" );
    List<ActivityHandler> activityHandlers =
      dbClient.view( "example/getNewActivityHandlers" ).startKey( params ).includeDocs( true )
        .query( ActivityHandler.class );
    for( ActivityHandler activityHandler: activityHandlers )
    {
      Activity activity = activityHandler.getActivityAObject();
      result.add(activity);
    }

    return result;
  }

  @Override
  public void addCommentToActivity( Activity comment_, String activityId_ )
  {
    if( comment_.getId() == null || comment_.getId().length() == 0)
    {
      Random rand = new Random();
      int randInt = Math.abs( rand.nextInt() );
      comment_.setId( "ActivityComment_" + randInt );
    }
     ActivityHandler activityObject = new ActivityHandler( (ActivityImpl) comment_, activityId_, "activityComment" );
     dbClient.save( activityObject );
  }

  @Override
  public List<Person> getAllUsers()
  {
    List<Person> result = new ArrayList<Person>();
    List<PersonHandler> personHolders = getPersonHandlers();
    if( personHolders != null && personHolders.size() > 0 )
    {
      for( PersonHandler personHolder : personHolders )
      {
        result.add( personHolder.getPersonObject() );
      }
    }
    return result;
  }

  @Override
  public List<Activity> getUserActivities( String userId_ )
  {
    List<ActivityHandler> activityHandlers = getUserActivityHandlers( userId_ );
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
      Collections.sort( activityList, new ActivityOrderByPostedTime() );
    }
    return activityList;
  }

  @Override
  public List<Activity> getGroupActivities( String groupId )
  {
    List<ActivityHandler> activityHandlers = getGroupActivityHandlers( groupId );
    return getActivityListFromHandler( activityHandlers );
  }

  @Override
  public List<Activity> getUserActivities( String userId_, String appId )
  {
    List<ActivityHandler> activityHandlers = getUserActivityHandlers( userId_, appId );
    return getActivityListFromHandler( activityHandlers );
  }

  private List<Activity> getActivityListFromHandler( List<ActivityHandler> activityHandlers )
  {
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
      Collections.sort( activityList, new ActivityOrderByPostedTime() );
    }
    return activityList;
  }

  public ActivityEntry getActivityEntry( String activityId_, String userId_ )
  {
    ActivityEntryHandler activityEntryHandler = getActivityEntryHandler( activityId_, userId_ );
    ActivityEntry result = null;
    if( activityEntryHandler != null )
    {
      result = activityEntryHandler.getActivityEntryObject();
    }
    return result;
  }

  public List<ActivityEntryHandler> getActivityEntryHandlers( String userId_ )
  {
    List params = new ArrayList();
    params.add( userId_ );
    params.add( "activityEntry" );
    List<ActivityEntryHandler> activityHandlerList =
      dbClient.view( "example/getActivityEntryHandlers" ).key( params ).includeDocs( true )
        .query( ActivityEntryHandler.class );
    System.out.println( activityHandlerList );
    return (activityHandlerList != null && activityHandlerList.size() > 0) ? activityHandlerList : null;
  }

  public ActivityEntryHandler getActivityEntryHandler( String activityEntryId_, String userId_ )
  {
    List params = new ArrayList();
    params.add( activityEntryId_ );
    params.add( userId_ );
    params.add( "activityEntry" );
    List<ActivityEntryHandler> activityEntryHandlerList =
      dbClient.view( "example/getActivityEntryHandler" ).key( params ).includeDocs( true )
        .query( ActivityEntryHandler.class );
    System.out.println( activityEntryHandlerList );
    return (activityEntryHandlerList != null && activityEntryHandlerList.get( 0 ) != null) ?
           activityEntryHandlerList.get( 0 ) : null;
  }

  @Override
  public boolean updateActivityEntry( ActivityEntry activityEntry_, String userId )
  {
    boolean status = true;
    ActivityEntryHandler activityEntryHandler = getActivityEntryHandler( activityEntry_.getId(), null );
    if( activityEntryHandler != null )
    {
      activityEntryHandler.setActivityEntryObject( (ActivityEntryImpl) activityEntry_ );
      Response response = dbClient.update( activityEntryHandler );
      if( response.getError() != null && response.getError().length() > 0 )
      {
        System.out.println( "Activity updation failed! " );
        status = false;
      }
    }
    else
    {
      createActivityEntry( activityEntry_, userId );
    }
    return status;
  }

  @Override
  public boolean deleteActivityEntry( String activityEntryId, String userId )
  {
    ActivityEntryHandler activityEntryHandler = getActivityEntryHandler( activityEntryId, userId );
    boolean status = false;
    if( activityEntryHandler != null )
    {
      dbClient.remove( activityEntryHandler );
      status = true;
    }
    else
    {
      System.out.println( "This activity entry doesn't exist in the database!" );
    }
    return status;
  }

  @Override
  public void createMessageCollection( MessageCollection message_, String userId_ )
  {
    MessageCollectionHolder messageCollectionHandler =
      new MessageCollectionHolder( (MessageCollectionImpl) message_, userId_, "messageCollection" );
    Response resp = dbClient.save( messageCollectionHandler );
  }

  @Override
  public List<MessageCollection> getMessageCollections( String userId_ )
  {
    return getMessageCollections( userId_, null );
  }

  @Override
  public List<MessageCollection> getMessageCollections( String userId_, String collectionId_ )
  {
    List<MessageCollection> result = new ArrayList<MessageCollection>();
    List<MessageCollectionHolder> messageCollectionHolders = getMessageCollectionHolders( userId_ );
    if( messageCollectionHolders != null )
    {
      for( MessageCollectionHolder messageCollectionHolder : messageCollectionHolders )
      {
        result.add( messageCollectionHolder.getDocObject() );
      }
    }
    System.out.println( result );
    return result;
  }

  public List<MessageCollectionHolder> getMessageCollectionHolders( String userId_ )
  {
    return getMessageCollectionHolders( userId_, null );
  }

  public List<MessageCollectionHolder> getMessageCollectionHolders( String userId_, String collectionId_ )
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
    params.add( "messageCollection" );
    List<MessageCollectionHolder> messageCollectionHolders =
      dbClient.view( "example/getMessageCollectionHandlers" ).key( params ).includeDocs( true )
        .query( MessageCollectionHolder.class );
    System.out.println( messageCollectionHolders );
    return messageCollectionHolders;
  }

  @Override
  public boolean deleteMessageCollections( String userId_, List<String> collectionIds_ )
  {
    boolean status = true;
    if( collectionIds_ != null )
    {
      for( String collectionId : collectionIds_ )
      {
        status = status & deleteMessageCollection( userId_, collectionId );
      }
    }
    return status;
  }

  @Override
  public boolean deleteMessageCollection( String userId_, String collectionId_ )
  {
    List<MessageCollectionHolder> messageCollectionHolder = getMessageCollectionHolders( userId_, collectionId_ );
    boolean status = false;
    if( messageCollectionHolder != null && messageCollectionHolder.size() == 1 )
    {
      dbClient.remove( messageCollectionHolder.get( 0 ) );
      status = true;
    }
    else
    {
      System.out.println( "This message collection doesn't exist in the database!" );
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
    params.add( "messageCollection" );
    List<MessageHolder> messageHolders =
      dbClient.view( "example/getMessageHandlers" ).key( params ).includeDocs( true ).query( MessageHolder.class );
    System.out.println( messageHolders );
    return messageHolders;
  }

  @Override
  public boolean deleteMessage( String userId_, String messageId_ )
  {
    List<MessageHolder> messageHolder = getMessageHolder( userId_, null, messageId_ );
    boolean status = false;
    if( messageHolder != null && messageHolder.size() == 1 )
    {
      dbClient.remove( messageHolder.get( 0 ) );
      status = true;
    }
    else
    {
      System.out.println( "This message doesn't exist in the database!" );
    }
    return status;
  }

  @Override
  public boolean updateMessage( String userId_, Message message_ )
  {
    List<MessageHolder> messageHolder = getMessageHolder( userId_, null, message_.getId() );
    boolean status = true;
    if( messageHolder != null && messageHolder.size() == 1 )
    {
      MessageHolder msgHolder = messageHolder.get(0);
      if( msgHolder != null )
      {
        msgHolder.setDocObject( (MessageImpl)message_ );
        dbClient.update( msgHolder );
      }
      else
      {
        status = false;
      }
    }
    else
    {
      System.out.println( "This message doesn't exist in the database!" );
      status = false;
    }
    return status;
  }

  public void addFriend( String userId_, String friendId_ )
  {
    FriendHandler friendListHandler = getFriendListHandler( userId_ );
    List<String> friends = null;
    if( friendListHandler == null )
    {
      friendListHandler = new FriendHandler();
      friendListHandler.setDocType( "friend" );
      friendListHandler.setUserId( userId_ );
      friends = new ArrayList<String>();
      friends.add( friendId_ );
      friendListHandler.setFriendList( friends );
      dbClient.save( friendListHandler );
    }
    else
    {
      friends = friendListHandler.getFriendList();
      if( friends.contains( friendId_ ) )
      {
        System.out.println( friendId_ + " is already your friend!" );
      }
      else
      {
        friends.add( friendId_ );
        friendListHandler.setFriendList( friends );
        dbClient.update( friendListHandler );
      }
    }
  }

  public List<String> getFriendList( String userId_ )
  {
    FriendHandler friendHandler = getFriendListHandler( userId_ );
    return friendHandler != null ? friendHandler.getFriendList() : null;
  }

  public FriendHandler getFriendListHandler( String userId_ )
  {
    FriendHandler result = null;
    List params = new ArrayList();
    params.add( userId_ );
    params.add( "friend" );

    List<FriendHandler> friendHandler =
      dbClient.view( "example/getFriendHandler" ).key( params ).includeDocs( true ).query( FriendHandler.class );
    if( friendHandler != null && friendHandler.size() == 1 )
    {
      result = friendHandler.get( 0 );
    }
    return result;
  }

  @Override
  public void createGroup( Group group_, String userId_ )
  {
    GroupHandler groupHandler = new GroupHandler();
    groupHandler.setDocType( "group" );
    groupHandler.setGroupOwner( userId_ );
    groupHandler.setGroupObject( (GroupImpl) group_ );
    dbClient.save( groupHandler );
  }

  @Override
  public void deleteGroup( String userId_, String groupId_ )
  {
    GroupHandler groupHandler = getGroupHandler( groupId_ );
    if( groupHandler != null && groupHandler.getGroupOwner() != null && userId_.equals( groupHandler.getGroupOwner() ) )
    {
      dbClient.remove( groupHandler.getId(), groupHandler.getRevId() );
    }
  }

  @Override
  public void updateGroup( Group group_ )
  {
    GroupHandler groupHandler = getGroupHandler( group_.getId() );
    groupHandler.setGroupObject( (GroupImpl)group_ );
    dbClient.save( groupHandler );
  }

  @Override
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
      System.out.println( "Group doesn't exist in the db!" );
    }
  }

  @Override
  public void removeGroupUser( String groupId_, String userId_ )
  {
    GroupHandler groupHandler = getGroupHandler( groupId_ );
    if( groupHandler != null )
    {
      List<String> members = groupHandler.getGroupmembers();
      if( members != null && members.contains( userId_ ) )
      {
        members.remove( userId_ );
        groupHandler.setGroupmembers( members );
        dbClient.update( groupHandler );
      }
    }
    else
    {
      System.out.println( "Group doesn't exist in the db!" );
    }
  }

  @Override
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

  @Override
  public List<Group> getUserFollowings( String userId_ )
  {
    List<GroupHandler> userGroupHandlers = null;
    List<Group> userGroups = null;
    GroupHandler result = null;

    userGroupHandlers =
      dbClient.view( "example/getUserFollowings" ).key( userId_ ).includeDocs( true ).query( GroupHandler.class );
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

  @Override
  public List<Group> getUserGroups( String userId_ )
  {
    List<GroupHandler> userGroupHandlers = null;
    List<Group> userGroups = null;
    GroupHandler result = null;

    userGroupHandlers =
      dbClient.view( "example/getUserGroups" ).key( userId_ ).includeDocs( true ).query( GroupHandler.class );
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
      result = groupHandler.get( 0 );
    }
    return result;
  }

  public Map<String,String> uploadDocument( Document document_ )
  {
    Map<String,String> result = new HashMap<String,String>();
    InputStream inputStream = document_.getInputStream();
    String name = document_.getAttachmentName();
    String type = document_.getContentType();
    Response response = dbClient.saveAttachment( inputStream, name, type );
    System.out.println("**** Doc ID = " + response.getId() + "/" + name );
    String url = "http://127.0.0.1:5984/lightcouch-db-test/" + response.getId() + "/" + name;
    String attachmentId = response.getId();
    document_.setInputStream( null );
    document_.setDocType( "document" );
    document_.setAttachmentId( attachmentId );
    response = dbClient.save( document_ );
    result.put("docId", response.getId());
    result.put("url", url);
    return result;
  }

  public void deleteDocument( String docId, String revId )
  {
     dbClient.remove( docId, revId );
  }

  public InputStream getDocument( String docId_, String docName_ )
  {
     return dbClient.find(String.format("%s/%s", docId_, docName_));
  }
}
