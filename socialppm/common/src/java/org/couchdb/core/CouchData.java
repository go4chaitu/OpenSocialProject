// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.core;


import java.util.List;

import org.apache.shindig.social.opensocial.model.*;

public interface CouchData
{
  void createPerson( Person person_ );

  Person getPerson( String userId );

  void updatePerson( Person userId );

  boolean deletePerson( String userId );

  void createActivity( Activity activity_ );

  List<Activity> getUserActivities( String userId_ );

  boolean updateActivity( Activity activity_ );

  boolean deleteActivity( String activityId, String userId );

  List<String> getFriendList( String userId_ );

  void createMessage( Message message_ );

  List<Message> getMessages( String userId_ );

  List<Message> getMessages( String userId_, String collectionId_ );

  List<Message> getMessages( String userId_, String collectionId_, String messageId_ );

  void deleteMessage();
}
