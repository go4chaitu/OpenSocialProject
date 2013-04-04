// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.BodyTypeImpl;
import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.*;
import org.couchdb.impl.CouchDataImpl;
import org.lightcouch.CouchDbClient;
import org.lightcouch.DesignDocument;
import org.lightcouch.Response;

public class CouchDataImplTest
{

  /**
   * @param args
   */
  public static void main( String[] args )
  {
    // TODO Auto-generated method stub
    CouchDbClient dbClient = new CouchDbClient( "Couchdb/couchdb.properties" );
    DesignDocument designDoc = dbClient.design().getFromDesk( "example" );
    Response response = dbClient.design().synchronizeWithDb( designDoc );
    CouchDataImplTest testInstance = new CouchDataImplTest();

    CouchDataImpl dbImp = new CouchDataImpl();
    Person person1 = testInstance.getTestPerson();
    Person person2 = testInstance.getTestPerson();
    Person person3 = testInstance.getTestPerson();
    Person person4 = testInstance.getTestPerson();
    Person person5 = testInstance.getTestPerson();
    dbImp.createPerson( person1 );
    dbImp.createPerson( person2 );
    dbImp.createPerson( person3 );
    dbImp.createPerson( person4 );
    dbImp.createPerson( person5 );
    Person p = dbImp.getPerson( person1.getId() );
    System.out.println( p.getAboutMe() );
    person2.setAboutMe( "It is changed by me!!!" );
    dbImp.updatePerson( person2 );
    dbImp.deletePerson( person1.getId() );
    Activity activity1 = testInstance.getTestActivity( person1 );
    Activity activity2 = testInstance.getTestActivity( person1 );
    Activity activity3 = testInstance.getTestActivity( person1 );
    Activity activity4 = testInstance.getTestActivity( person3 );
    Activity activity5 = testInstance.getTestActivity( person3 );

    dbImp.createActivity( activity1 );
    dbImp.createActivity( activity2 );
    dbImp.createActivity( activity3 );
    dbImp.createActivity( activity4 );
    dbImp.createActivity( activity5 );
    List<Activity> activityList = dbImp.getUserActivities( person1.getId() );
    testInstance.printActivities( activityList );
    if( activityList != null && activityList.size() > 1 )
    {
      Activity activityToBeUpdated = activityList.get( 0 );
      activityToBeUpdated.setBody( "This acitity is updated now!!" );
      boolean status = dbImp.updateActivity( activityToBeUpdated );
      if( status )
      {
        System.out.println( "Activity " + activityList.get( 0 ).getId() + " is updated!" );
      }

      status = dbImp.deleteActivity( activityList.get( 1 ).getId(), activityList.get( 1 ).getUserId() );
      if( status )
      {
        System.out.println( "Activity " + activityList.get( 1 ).getId() + " is deleted!" );
      }
    }
    Message message1 = testInstance.getTestMessage( person1, new ArrayList<Person>(Arrays.asList(person3,person4)) );
    Message message2 = testInstance.getTestMessage( person1, new ArrayList<Person>(Arrays.asList(person3,person5)) );
    Message message3 = testInstance.getTestMessage( person3, new ArrayList<Person>(Arrays.asList(person4,person1)) );
    Message message4 = testInstance.getTestMessage( person5, new ArrayList<Person>(Arrays.asList(person1,person3)) );
    Message message5 = testInstance.getTestMessage( person5, new ArrayList<Person>(Arrays.asList(person1,person4)) );

		dbImp.createMessage(message1);
    dbImp.createMessage(message2);
    dbImp.createMessage(message3);
    dbImp.createMessage(message4);
    dbImp.createMessage(message5);

		List<Message> messageList = dbImp.getMessages( person1.getId(), Message.Type.NOTIFICATION.name() ); // Receiver user-id
    System.out.println("******* MESSAGES WITH COLLECTION TYPE ********** " + person1.getId() + " @ NOTIFICATION" );
    if( messageList != null && messageList.size() > 0 )
    {
      testInstance.printMessages( messageList );
    }

    messageList = dbImp.getMessages( person1.getId(), message1.getType().name(), message1.getId() ); // Receiver user-id
    System.out.println("******* MESSAGES WITH COLLECTION TYPE AND MESSAGE ID ********** " + person1.getId() + " @ " + message1.getType().name() + " @ " + message1.getId() );
    if( messageList != null && messageList.size() > 0 )
    {
      testInstance.printMessages( messageList );
    }

    dbImp.addFriend( person1.getId(), person3.getId() );
    dbImp.addFriend( person1.getId(), person4.getId() );
    dbImp.addFriend( person1.getId(), person5.getId() );
    dbImp.addFriend( person3.getId(), person4.getId() );
    dbImp.addFriend( person3.getId(), person5.getId() );
    dbImp.addFriend( person4.getId(), person5.getId() );
    List<String> person1FrndList = dbImp.getFriendList( person1.getId() );
    List<String> person3FrndList = dbImp.getFriendList( person3.getId() );
    List<String> person4FrndList = dbImp.getFriendList( person4.getId() );
    testInstance.printFriendList( person1.getId(), person1FrndList );

    Group group1 = testInstance.getTestGroup();
    Group group2 = testInstance.getTestGroup();
    Group group3 = testInstance.getTestGroup();
    dbImp.createGroup( group1 );
    dbImp.createGroup( group2 );
    dbImp.createGroup( group3 );

    dbImp.addUserToGroup( person1.getId(), group1.getId() );
    dbImp.addUserToGroup( person3.getId(), group1.getId() );
    dbImp.addUserToGroup( person3.getId(), group2.getId() );
    dbImp.addUserToGroup( person3.getId(), group2.getId() );
    dbImp.addUserToGroup( person4.getId(), group3.getId() );

    List<Group> groups = dbImp.getUserGroups( person3.getId() );
    testInstance.printGroups( groups );
  }

  public void printGroups( List<Group> groups_ )
  {
    if( groups_ != null )
    {
      for( Group group : groups_ )
      {
        System.out.println(group.getId() + " : " + group.getTitle() + " : " + group.getDescription());
      }
    }
  }

  public void printFriendList( String userId_, List<String> frndList )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( userId_ );
    if( frndList != null )
    {
      for( String userId : frndList )
      {
        sb.append( userId ).append( " : " );
      }
    }
    System.out.println(sb.toString());
  }

  public Person getTestPerson()
  {
    PersonImpl people = new PersonImpl();
    Random rand = new Random();
    int randInt = Math.abs(rand.nextInt());
    people.setId( "User_" + randInt );
    people.setAboutMe( "About Me" + randInt );
    people.setActivities( Arrays.asList( "swimming", "running" ) );
    people.setAge( randInt );
    BodyTypeImpl bodyType = new BodyTypeImpl();
    bodyType.setBuild( "bodyType_build_" + randInt );
    bodyType.setEyeColor( "bodyType_eyeColor_" + randInt );
    bodyType.setHairColor( "bodyType_hairColor_" + randInt );
    bodyType.setHeight( Float.parseFloat( "1.841" ) );
    bodyType.setWeight( Float.parseFloat( "79" ) );
    people.setBodyType( bodyType );
    people.setBooks( Arrays.asList( "The Monk who sold his ferrari", "The Alchemist" ) );
    return people;
  }

  public Activity getTestActivity( Person person_ )
  {
    ActivityImpl activity = new ActivityImpl();
    Random rand = new Random();
    int randInt = Math.abs(rand.nextInt());
    activity.setId( "Activity_" + randInt );
    activity.setTitle( "ActivityTitle_" );
    activity.setBody( "ActivityBody_" );
    activity.setUserId( person_.getId() );
    return activity;
  }

  public Message getTestMessage( Person sender_, List<Person> reciepients_ )
  {
    Message message = new MessageImpl();
    Random rand = new Random();
    int randInt = Math.abs(rand.nextInt());
    message.setId( "Message_" + randInt );
    message.setSenderId( sender_.getId() );
    if( reciepients_ != null && reciepients_.size() > 0 )
    {
      List<String> userIds = new ArrayList<String>();
      for( Person receiver : reciepients_ )
      {
        userIds.add( receiver.getId() );
      }
      message.setRecipients(userIds);
    }
    int rand2 = Math.abs(randInt%3);
    List<String> collectionIds = new ArrayList<String>();
    Message.Type type = null;
    switch( rand2 )
    {
      case 0: message.setType( Message.Type.NOTIFICATION ); type = Message.Type.NOTIFICATION; break;
      case 1: message.setType( Message.Type.PRIVATE_MESSAGE ); type = Message.Type.PRIVATE_MESSAGE; break;
      case 2: message.setType( Message.Type.PUBLIC_MESSAGE ); type = Message.Type.PUBLIC_MESSAGE; break;
      default: message.setType( Message.Type.NOTIFICATION ); type = Message.Type.NOTIFICATION; break;
    }
    collectionIds.add( type.toString() );
    message.setCollectionIds( collectionIds );
    message.setTitle( "Title_" + randInt );
    return message;
  }

  public Group getTestGroup()
  {
    Group group = new GroupImpl();
    Random rand = new Random();
    int randInt = Math.abs(rand.nextInt());

    group.setId( "group_" + randInt );
    group.setDescription( "groupdesc_" + randInt );
    group.setTitle( "grouptitle_" + randInt );

    return group;
  }

  public void printActivities( List<Activity> activityList_ )
  {
    if( activityList_ != null )
    {
      for( Activity activity_ : activityList_ )
      {
        System.out.println( activity_.getUserId() + " :: " + activity_.getId() + " :: " + activity_.getTitle() );
      }
    }
  }
  public void printMessages( List<Message> messageList_ )
  {
    if( messageList_ != null )
    {
      for( Message message_ : messageList_ )
      {
        System.out.println( message_.getId() + " :: " + message_.getSenderId() + " :: " + message_.getTitle() );
      }
    }
  }
}
