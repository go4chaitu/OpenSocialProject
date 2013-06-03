// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.utils;

import com.niku.union.security.SecurityIdentifier;
import com.niku.union.utility.UtilityThreadLocal;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.impl.CouchDataImpl;
import org.couchdb.placeholders.GroupHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: sarch04
 * Date: 4/24/13
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CouchDBUtils
{
  private static final CouchDataImpl dbImp = new CouchDataImpl();
  private static final Comparator<Person> NAME_COMPARATOR = new Comparator<Person>()
  {
    public int compare( Person person, Person person1 )
    {
      String name = person.getName().getFormatted();
      String name1 = person1.getName().getFormatted();
      return name.compareTo( name1 );
    }
  };

  public static final Comparator<Activity> ACTIVITY_COMPARATOR = new Comparator<Activity>()
  {
    public int compare( Activity activity1_, Activity activity2_ )
    {
      Long activity1_time = activity1_.getPostedTime();
      Long activity2_time = activity2_.getPostedTime();
      return activity2_time.compareTo(activity1_time);
    }
  };
  public static final Comparator<Activity> ACTIVITY_COMMENTS_COMPARATOR = new Comparator<Activity>()
  {
    public int compare( Activity activity1_, Activity activity2_ )
    {
      Long activity1_time = activity1_.getPostedTime();
      Long activity2_time = activity2_.getPostedTime();
      return activity1_time.compareTo(activity2_time);
    }
  };

  public static CouchDataImpl getDBInstance()
  {
      return dbImp;
  }

  public static Person getPerson( UserId userId, SecurityToken token ) throws ProtocolException
  {
    return dbImp.getPerson( userId.getUserId( token ) );
  }
  public static List<Person> getPeopleList( Set<UserId> userIds, GroupId groupId,
                                     CollectionOptions collectionOptions, Set<String> fields,
                                     SecurityToken token ) throws ProtocolException
  {

    List<Person> personList = new ArrayList<Person>();
//    if( userIds != null && userIds.size() > 0 )
//    {
//      for( UserId userId_ : userIds )
//      {
//        SecurityIdentifier secId = UtilityThreadLocal.getSecurityIdentifier();
//
//        Person person = dbImp.getPerson( userId_.getUserId( token ) );
//        if( person != null )
//        {
//          personList.add( person );
//        }
//      }
//    }

    switch( groupId.getType() )
    {
      case all:
      case friends:

        if( userIds != null && userIds.size() > 0 )
        {
          for( UserId userId_ : userIds )
          {
            List<String> friendList = dbImp.getFriendList( userId_.getUserId( token ) );
            if( friendList != null && friendList.size() > 0 )
            {
              for( String friend_ : friendList )
              {
                Person person = dbImp.getPerson( friend_ );
                personList.add( person );
              }
            }
          }
        }
        break;
      case objectId:
        GroupHandler groupHandler = dbImp.getGroupHandler( groupId.getObjectId().toString() );
        List<String> groupMembers = groupHandler.getGroupmembers();
        if( groupMembers != null && groupMembers.size() > 0 )
        {
          for( String userId_ : groupMembers )
          {
            Person person = dbImp.getPerson( userId_ );
            personList.add( person );
          }
        }
        break;
      case self:
        break;
    }

    if( collectionOptions.getSortBy().equals( Person.Field.NAME.toString() ) )
    {
      Collections.sort( personList, NAME_COMPARATOR );

      if( collectionOptions.getSortOrder() == SortOrder.descending )
      {
        Collections.reverse( personList );
      }
    }

    int totalSize = personList.size();
    int last = collectionOptions.getFirst() + collectionOptions.getMax();
    personList = personList.subList( collectionOptions.getFirst(), Math.min( last, totalSize ) );

    return personList;
  }
}
