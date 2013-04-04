// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.Futures;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import com.google.inject.Singleton;
import org.couchdb.impl.CouchDataImpl;
import org.couchdb.placeholders.GroupHandler;

import javax.servlet.http.HttpServletResponse;

@Singleton
public class PersonDBHandler implements PersonService
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

  @Override
  public Future<RestfulCollection<Person>> getPeople( Set<UserId> userIds, GroupId groupId,
                                                      CollectionOptions collectionOptions, Set<String> fields,
                                                      SecurityToken token ) throws ProtocolException
  {

    List<Person> personList = new ArrayList<Person>();
    if( userIds != null && userIds.size() > 0 )
    {
      for( UserId userId_ : userIds )
      {
        Person person = dbImp.getPerson( userId_.getUserId() );
        personList.add( person );
      }
    }

    switch (groupId.getType()) {
    case all:
    case friends:

      if( userIds != null && userIds.size() > 0 )
      {
        for( UserId userId_ : userIds )
        {
          List<String> friendList = dbImp.getFriendList( userId_.getUserId() );
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

    return Futures
      .immediateFuture( new RestfulCollection<Person>( personList, collectionOptions.getFirst(), totalSize,
        collectionOptions.getMax() ) );
  }

  @Override
  public Future<Person> getPerson( UserId id, Set<String> fields, SecurityToken token ) throws ProtocolException
  {
    Person person = dbImp.getPerson( id.getUserId() );
    return Futures.immediateFuture( person );
  }

  @Override
  public Future<Person> updatePerson( UserId id, Person person, SecurityToken token ) throws ProtocolException
  {
    String viewer = token.getViewerId(); // viewer
    String user = id.getUserId( token ); // person to update

    if( !viewer.equals( user ) )
    {
      throw new ProtocolException( HttpServletResponse.SC_FORBIDDEN,
        "User '" + viewer + "' does not have enough privileges to update person '" + user + "'" );
    }

    dbImp.updatePerson( person );
    return Futures.immediateFuture( person );
  }
}
