// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.sample.spi;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.AnonymousSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.model.FilterOperation;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.SocialApiTestsGuiceModule;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test the JSONOpensocialService
 */
public class MyJsonDbOpensocialServiceTest{
  private JsonDbOpensocialService db;

  private static final UserId CANON_USER = new UserId(UserId.Type.userId, "canonical");
  private static final UserId JOHN_DOE = new UserId(UserId.Type.userId, "john.doe");
  private static final UserId JANE_DOE = new UserId(UserId.Type.userId, "jane.doe");
  private static final UserId ANONYMOUS = new UserId(UserId.Type.userId, AnonymousSecurityToken.ANONYMOUS_ID);

  private static final GroupId SELF_GROUP = new GroupId(GroupId.Type.self, null);
  private static final String APP_ID = "1";
  private static final String CANONICAL_USER_ID = "canonical";

  private SecurityToken token = new FakeGadgetToken();
  
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(new SocialApiTestsGuiceModule());
    db = injector.getInstance(JsonDbOpensocialService.class);
  }   
  
  public static void main(String[] args) throws Exception {	  
	  MyJsonDbOpensocialServiceTest mytest = new MyJsonDbOpensocialServiceTest();
	  mytest.setUp();
	  mytest.testGetPersonDefaultFields();
	  mytest.testViewerCanUpdatePerson();
	  
  }
  public void testGetPersonDefaultFields() throws Exception {
    Person person = db
        .getPerson(CANON_USER, Person.Field.DEFAULT_FIELDS, token).get();

    System.out.println("Canonical user not found"+ person);
    System.out.println("Canonical user has no id"+ person.getId());
    System.out.println("Canonical user has no name"+ person.getName());
    System.out.println("Canonical user has no thumbnail"+
        person.getThumbnailUrl());
  }

  
  public void testGetAnonymousUser() throws Exception {
    Person person = db.getPerson(ANONYMOUS, Person.Field.DEFAULT_FIELDS, token).get();
    System.out.println( person.getId());
    System.out.println( person.getName().getFormatted());
    System.out.println( person.getNickname());
  }

  
  public void testGetPersonAllFields() throws Exception {
    Person person = db
        .getPerson(CANON_USER, Person.Field.ALL_FIELDS, token).get();
    System.out.println("Canonical user not found"+ person);
  }

  
  public void testGetPersonAllAppData() throws Exception {
    Person person = db
        .getPerson(CANON_USER, ImmutableSet.of("id", "appData"), token).get();

    System.out.println("Canonical user not found"+ person);
    System.out.println( person.getId());
  }

  
  public void testGetPersonOneAppDataField() throws Exception {
    Person person = db
        .getPerson(CANON_USER, ImmutableSet.of("id", "appData.size"), token).get();

    System.out.println("Canonical user not found"+ person);
    System.out.println( person.getId());
  }

  
  public void testGetPersonMultipleAppDataFields() throws Exception {
    Person person = db
        .getPerson(CANON_USER,
            ImmutableSet.of("id", "appData.size", "appData.count", "appData.bogus"),
            token).get();

    System.out.println("Canonical user not found"+ person);
    System.out.println( person.getId());
  }

  
  public void testGetExpectedFriends() throws Exception {
    CollectionOptions options = new CollectionOptions();
    options.setSortBy(PersonService.TOP_FRIENDS_SORT);
    options.setSortOrder(SortOrder.ascending);
    options.setFilter(null);
    options.setFilterOperation(FilterOperation.contains);
    options.setFilterValue("");
    options.setFirst(0);
    options.setMax(20);

    RestfulCollection<Person> responseItem = db.getPeople(
        ImmutableSet.of(CANON_USER), new GroupId(GroupId.Type.friends, null),
        options, Collections.<String>emptySet(), token).get();
    System.out.println(responseItem);
    System.out.println( responseItem.getTotalResults());
    // Test a couple of users
    System.out.println( responseItem.getList().get(0).getId());
    System.out.println( responseItem.getList().get(1).getId());
  }

  
  public void testGetExpectedUsersForPlural() throws Exception {
    CollectionOptions options = new CollectionOptions();
    options.setSortBy(PersonService.TOP_FRIENDS_SORT);
    options.setSortOrder(SortOrder.ascending);
    options.setFilter(null);
    options.setFilterOperation(FilterOperation.contains);
    options.setFilterValue("");
    options.setFirst(0);
    options.setMax(20);

    RestfulCollection<Person> responseItem = db.getPeople(
        ImmutableSet.of(JOHN_DOE, JANE_DOE), new GroupId(GroupId.Type.friends, null),
       options, Collections.<String>emptySet(), token).get();
    System.out.println(responseItem);
    System.out.println( responseItem.getTotalResults());
    // Test a couple of users
    System.out.println( responseItem.getList().get(0).getId());
    System.out.println( responseItem.getList().get(1).getId());
  }

  
  public void testGetExpectedActivities() throws Exception {
    RestfulCollection<Activity> responseItem = db.getActivities(
        ImmutableSet.of(CANON_USER), SELF_GROUP, APP_ID, Collections.<String>emptySet(), null,
        new FakeGadgetToken()).get();
  }

  
  public void testGetExpectedActivitiesForPlural() throws Exception {
    RestfulCollection<Activity> responseItem = db.getActivities(
        ImmutableSet.of(CANON_USER, JOHN_DOE), SELF_GROUP, APP_ID, Collections.<String>emptySet(), null,
        new FakeGadgetToken()).get();
  }

  
  public void testGetExpectedActivity() throws Exception {
    Activity activity = db.getActivity(
        CANON_USER, SELF_GROUP, APP_ID,
        ImmutableSet.of("appId", "body", "mediaItems"), APP_ID, new FakeGadgetToken()).get();
    System.out.println(activity);
    // Check that some fields are fetched and others are not
    System.out.println(activity.getBody());
  }

  
  public void testDeleteExpectedActivity() throws Exception {
    db.deleteActivities(CANON_USER, SELF_GROUP, APP_ID, ImmutableSet.of(APP_ID),
        new FakeGadgetToken());

    // Try to fetch the activity
    try {
      db.getActivity(
          CANON_USER, SELF_GROUP, APP_ID,
          ImmutableSet.of("appId", "body", "mediaItems"), APP_ID, new FakeGadgetToken()).get();
    } catch (ProtocolException sse) {
      System.out.println( sse.getCode());
    }
  }

  
  public void testGetExpectedAppData() throws Exception {
    DataCollection responseItem = db.getPersonData(
        ImmutableSet.of(CANON_USER), SELF_GROUP, APP_ID, Collections.<String>emptySet(),
        new FakeGadgetToken()).get();
    System.out.println(responseItem.getEntry().isEmpty());
    System.out.println(responseItem.getEntry().get(CANONICAL_USER_ID).isEmpty());
  }

  
  public void testGetExpectedAppDataForPlural() throws Exception {
    DataCollection responseItem = db.getPersonData(
        ImmutableSet.of(CANON_USER, JOHN_DOE), SELF_GROUP, APP_ID, Collections.<String>emptySet(),
        new FakeGadgetToken()).get();
    System.out.println(responseItem.getEntry().isEmpty());
    System.out.println(responseItem.getEntry().get(CANONICAL_USER_ID).isEmpty());

    System.out.println(responseItem.getEntry().get(JOHN_DOE.getUserId()).isEmpty());
  }

  
  public void testDeleteExpectedAppData() throws Exception {
    // Delete the data
    db.deletePersonData(CANON_USER, SELF_GROUP, APP_ID,
        ImmutableSet.of("count"), new FakeGadgetToken());

    // Fetch the remaining and test
    DataCollection responseItem = db.getPersonData(
        ImmutableSet.of(CANON_USER), SELF_GROUP, APP_ID, Collections.<String>emptySet(),
        new FakeGadgetToken()).get();
    System.out.println(responseItem.getEntry().isEmpty());
    System.out.println(responseItem.getEntry().get(CANONICAL_USER_ID).isEmpty());

    System.out.println(responseItem.getEntry().get(CANONICAL_USER_ID).containsKey("count"));
  }

  
  public void testUpdateExpectedAppData() throws Exception {
    // Delete the data
    db.updatePersonData(CANON_USER, SELF_GROUP, APP_ID,
        null, ImmutableMap.of("count", "10", "newvalue", "20"), new FakeGadgetToken());

    // Fetch the remaining and test
    DataCollection responseItem = db.getPersonData(
        ImmutableSet.of(CANON_USER), SELF_GROUP, APP_ID, Collections.<String>emptySet(),
        new FakeGadgetToken()).get();

    System.out.println(responseItem.getEntry().isEmpty());
    System.out.println(responseItem.getEntry().get(CANONICAL_USER_ID).isEmpty());

    System.out.println( responseItem.getEntry().get(CANONICAL_USER_ID).get("count"));
    System.out.println( responseItem.getEntry().get(CANONICAL_USER_ID).get("newvalue"));
  }

  
  public void testGetExpectedActivityEntries() throws Exception {
    RestfulCollection<ActivityEntry> responseItem = db.getActivityEntries(
        ImmutableSet.of(JOHN_DOE), SELF_GROUP, APP_ID, Collections.<String>emptySet(), null,
        new FakeGadgetToken()).get();
  }

  
  public void testGetExpectedActivityEntriesForPlural() throws Exception {
    RestfulCollection<ActivityEntry> responseItem = db.getActivityEntries(
        ImmutableSet.of(CANON_USER, JOHN_DOE), SELF_GROUP, APP_ID, Collections.<String>emptySet(), null,
        new FakeGadgetToken()).get();
  }

  
  public void testGetExpectedActivityEntry() throws Exception {
    ActivityEntry entry = db.getActivityEntry(JOHN_DOE, SELF_GROUP, APP_ID,
        ImmutableSet.of("title"), "activity2", new FakeGadgetToken()).get();
    System.out.println(entry);
    // Check that some fields are fetched and others are not
    System.out.println(entry.getTitle());
  }

  
  public void testDeleteExpectedActivityEntry() throws Exception {
    db.deleteActivityEntries(JOHN_DOE, SELF_GROUP, APP_ID, ImmutableSet.of(APP_ID),
        new FakeGadgetToken());

    // Try to fetch the activity
    try {
      db.getActivityEntry(
          JOHN_DOE, SELF_GROUP, APP_ID,
          ImmutableSet.of("body"), APP_ID, new FakeGadgetToken()).get();
    } catch (ProtocolException sse) {
      System.out.println( sse.getCode());
    }
  }

  
  public void testViewerCanUpdatePerson() throws Exception {
    // Create new user
    JSONArray people = db.getDb().getJSONArray("people");
    JSONObject jsonPerson = new JSONObject();
    jsonPerson.put("id", "updatePerson");
    people.put(people.length(),jsonPerson);

    SecurityToken updateToken = new FakeGadgetToken("appId", "appUrl", "domain", "updatePerson", "trustedJson", "updatePerson", "20");

    // Get user
    UserId userId = new UserId(UserId.Type.userId, "updatePerson");
    Person person = db
        .getPerson(userId, Person.Field.ALL_FIELDS, token).get();
    System.out.println("User 'updatePerson' not found"+ person);

    // update a field in user object
    person.setThumbnailUrl("http://newthumbnail.url");
    // Save user to db
    db.updatePerson(userId, person, updateToken);
    // Get user again from db and check if the fields were properly updated
    person = db.getPerson(userId, Person.Field.ALL_FIELDS, token).get();
    System.out.println("User 'updatePerson' not found"+ person);

    System.out.println( person.getThumbnailUrl());
  }

  
  public void testViewerNotAllowedUpdatePerson() throws Exception {
    // Create new user
    JSONArray people = db.getDb().getJSONArray("people");
    JSONObject jsonPerson = new JSONObject();
    jsonPerson.put("id", "updatePerson");
    people.put(people.length(),jsonPerson);

    SecurityToken updateToken = new FakeGadgetToken("appId", "appUrl", "domain", "viewer", "trustedJson", "viewer", "20");

    // Get user
    UserId userId = new UserId(UserId.Type.userId, "updatePerson");
    Person person = db
        .getPerson(userId, Person.Field.ALL_FIELDS, token).get();

    // update a field in user object
    person.setThumbnailUrl("http://newthumbnail.url");
    // Save user to db, should throw an exception
    try {
      db.updatePerson(userId, person, updateToken);
    } catch (ProtocolException sse) {
      System.out.println( sse.getCode());
    }
  }

}
