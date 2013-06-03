// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import com.niku.union.persistence.PersistenceApplicationException;
import com.niku.union.persistence.PersistenceController;
import com.niku.union.persistence.PersistenceException;
import com.niku.union.persistence.PersistenceRequest;
import com.niku.union.persistence.PersistenceResponse;
import com.niku.union.persistence.results.DBResultSet;
import com.niku.union.utility.UtilityThreadLocal;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class PersonDBHandler implements PersonService
{

  @Override
  public Future<RestfulCollection<Person>> getPeople( Set<UserId> userIds, GroupId groupId,
                                                      CollectionOptions collectionOptions, Set<String> fields,
                                                      SecurityToken token ) throws ProtocolException
  {

    List<Person> personList = CouchDBUtils.getPeopleList( userIds, groupId, collectionOptions, fields, token );
    return Futures
      .immediateFuture( new RestfulCollection<Person>( personList, collectionOptions.getFirst(),
        personList != null ? personList.size() : 0,
        collectionOptions.getMax() ) );
  }

  @Override
  public Future<Person> getPerson( UserId id, Set<String> fields, SecurityToken token ) throws ProtocolException
  {
    Person person = CouchDBUtils.getDBInstance().getPerson( id.getUserId( token ) );
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

    CouchDBUtils.getDBInstance().updatePerson( person );
    return Futures.immediateFuture( person );
  }
}
