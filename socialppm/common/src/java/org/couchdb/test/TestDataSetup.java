// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.BodyTypeImpl;
import org.apache.shindig.social.core.model.DocumentImpl;
import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.MediaLinkImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Document;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.MediaLink;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.couchdb.adapters.ListFieldAdapter;
import org.couchdb.impl.CouchDataImpl;
import org.lightcouch.CouchDbClient;
import org.lightcouch.DesignDocument;
import org.lightcouch.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestDataSetup
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
    TestDataSetup testInstance = new TestDataSetup();
    //System.exit(1);

    CouchDataImpl dbImp = new CouchDataImpl();
    Person p1 = testInstance.getPersonObject( "namita.json" );
    Person p2 = testInstance.getPersonObject( "manjula.json" );
    Person p3 = testInstance.getPersonObject( "soni.json" );
    Person p4 = testInstance.getPersonObject( "asif.json" );
    Person p5 = testInstance.getPersonObject( "ravi.json" );
    Person p6 = testInstance.getPersonObject( "anil.json" );

    dbImp.createPerson( p1 );
    dbImp.createPerson( p2 );
    dbImp.createPerson( p3 );
    dbImp.createPerson( p4 );
    dbImp.createPerson( p5 );
    dbImp.createPerson( p6 );

    dbImp.addFriend( p1.getId(), p2.getId() );
    dbImp.addFriend( p1.getId(), p3.getId() );
    dbImp.addFriend( p1.getId(), p4.getId() );
    dbImp.addFriend( p1.getId(), p5.getId() );
    dbImp.addFriend( p1.getId(), p6.getId() );

    dbImp.addFriend( p2.getId(), p1.getId() );
    dbImp.addFriend( p2.getId(), p3.getId() );
    dbImp.addFriend( p2.getId(), p4.getId() );
    dbImp.addFriend( p2.getId(), p5.getId() );
    dbImp.addFriend( p2.getId(), p6.getId() );

    dbImp.addFriend( p3.getId(), p1.getId() );
    dbImp.addFriend( p3.getId(), p2.getId() );
    dbImp.addFriend( p3.getId(), p4.getId() );
    dbImp.addFriend( p3.getId(), p5.getId() );
    dbImp.addFriend( p3.getId(), p6.getId() );

    dbImp.addFriend( p4.getId(), p1.getId() );
    dbImp.addFriend( p4.getId(), p2.getId() );
    dbImp.addFriend( p4.getId(), p3.getId() );
    dbImp.addFriend( p4.getId(), p5.getId() );
    dbImp.addFriend( p4.getId(), p6.getId() );

    dbImp.addFriend( p5.getId(), p1.getId() );
    dbImp.addFriend( p5.getId(), p2.getId() );
    dbImp.addFriend( p5.getId(), p3.getId() );
    dbImp.addFriend( p5.getId(), p4.getId() );
    dbImp.addFriend( p5.getId(), p6.getId() );

    dbImp.addFriend( p6.getId(), p1.getId() );
    dbImp.addFriend( p6.getId(), p2.getId() );
    dbImp.addFriend( p6.getId(), p3.getId() );
    dbImp.addFriend( p6.getId(), p4.getId() );
    dbImp.addFriend( p6.getId(), p5.getId() );

    /*Activity activity1 = testInstance.getActivityObject( "emma_activity_1.json" );
    dbImp.createActivity( activity1 );
    Activity comment1 = testInstance.getActivityObject( "emma_activity_1_comment_1.json" );
    dbImp.addCommentToActivity( comment1, activity1.getId() );*/
  }


  public Person getPersonObject( String jsonFileName )
  {
    Gson gson = null;
    String baseDir = "..\\src\\META-INF\\Couchdb\\TestDataSetup\\";
    PersonImpl obj = null;
    try
    {
      BufferedReader br = new BufferedReader(
        new FileReader( baseDir + jsonFileName ) );

      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter( ListField.class, new ListFieldAdapter() );
      gson = gsonBuilder.create();
      //convert the json string back to object
      obj = gson.fromJson( br, PersonImpl.class );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }
    return obj;
  }

  public Activity getActivityObject( String jsonFileName )
  {
    Gson gson = null;
    String baseDir = "..\\src\\META-INF\\Couchdb\\TestDataSetup\\";
    ActivityImpl obj = null;
    try
    {
      BufferedReader br = new BufferedReader(
        new FileReader( baseDir + jsonFileName ) );

      GsonBuilder gsonBuilder = new GsonBuilder();
      gson = gsonBuilder.create();
      //convert the json string back to object
      obj = gson.fromJson( br, ActivityImpl.class );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }
    return obj;
  }

}
