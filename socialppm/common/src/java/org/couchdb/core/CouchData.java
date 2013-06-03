// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.core;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.*;

public interface CouchData
{
  void createPerson( Person person_ );

  Person getPerson( String userId );

  void updatePerson( Person userId );

  boolean deletePerson( String userId );

  void createActivity( Activity activity_ );

  List<Activity> getActivityComments( String activityId_ );

  List<Activity> getNewActivites( long lastUpdatedTime );

  void addCommentToActivity( Activity comment_, String activityId_ );

  List<Person> getAllUsers();

  List<Activity> getUserActivities( String userId_ );

  List<Activity> getUserActivities( String userId_, String appId );

  List<Activity> getGroupActivities( String groupId );

  Activity getActivity( String activityId_, String userId_ );

  boolean updateActivity( Activity activity_ );

  boolean deleteActivity( String activityId, String userId );

  List<ActivityEntry> getUserActivityEntries( String userId_ );

  ActivityEntry getActivityEntry( String activityEntryId_, String userId_ );

  void createActivityEntry( ActivityEntry activityEntry_, String userId_ );

  boolean updateActivityEntry( ActivityEntry activity_, String userId_ );

  boolean deleteActivityEntry( String activityEntryId_, String userId_ );

  List<String> getFriendList( String userId_ );

  void createMessage( Message message_ );

  List<Message> getMessages( String userId_ );

  List<Message> getMessages( String userId_, String collectionId_ );

  List<Message> getMessages( String userId_, String collectionId_, String messageId_ );

  boolean deleteMessage( String userId_, String messageId_ );

  boolean updateMessage( String userId_, Message message_ );

  void createMessageCollection( MessageCollection message_, String userId_ );

  List<MessageCollection> getMessageCollections( String userId_ );

  List<MessageCollection> getMessageCollections( String userId_, String collectionId_ );

  boolean deleteMessageCollections( String userId_, List<String> collectionIds_ );

  boolean deleteMessageCollection( String userId_, String collectionId_ );

  void createGroup( Group group_, String userId_ );

  void deleteGroup( String userId_, String groupId_ );

  void updateGroup( Group group_ );

  void addUserToGroup( String userId_, String groupId_ );

  void removeGroupUser( String groupId_, String userId_ );

  Group getGroup( String groupId_ );

  List<Group> getUserGroups( String userId_ );

  List<Group> getUserFollowings( String userId_ );

  Map<String,String> uploadDocument( Document document_ );

  void deleteDocument( String docId, String revId );

  InputStream getDocument( String docId_, String docName_ );
}
