// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import com.niku.union.persistence.PersistenceApplicationException;
import com.niku.union.persistence.PersistenceController;
import com.niku.union.persistence.PersistenceConversation;
import com.niku.union.persistence.PersistenceException;
import com.niku.union.persistence.PersistenceRequest;
import com.niku.union.persistence.PersistenceResponse;
import com.niku.union.persistence.results.DBResultSet;
import com.niku.union.utility.UtilityThreadLocal;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ClarityService;
import org.apache.shindig.social.opensocial.spi.RelationshipService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Singleton
public class ClarityDataDBHandler implements ClarityService
{

  @Override
  public Future<RestfulCollection<Person>> getAllUsers()
      throws ProtocolException
  {
    CouchDBUtils.getDBInstance().getAllUsers();
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Map<String,String>> getInstanceData( String objectCode, String instanceId )
      throws ProtocolException
  {
    Map<String,String> resultMap = new HashMap<String,String>();

    String queryName = "";

    List<DBResultSet> resultList = null;
    String instanceName = "";
    Map<String, String> inputMap = new HashMap<String, String>();

    if( objectCode.equals( "project" ) )
    {
      queryName = "odf.getProjectName";
    }
    else if( objectCode.equals( "task" ) )
    {
      queryName = "odf.getTaskName";
    }

    inputMap.put( "id", instanceId );

    try
    {
      PersistenceConversation persistenceMid =
        new PersistenceConversation( queryName );
      persistenceMid.setParameter( "id", instanceId );
      persistenceMid.process();
      if( persistenceMid.nextRow() )
      {
        instanceName = persistenceMid.getStringValue( "instancename" );
        resultMap.put("instanceName", instanceName);
      }
    }
    catch( PersistenceException pe )
    {
      pe.printStackTrace();
    }
    catch( PersistenceApplicationException pe )
    {
      pe.printStackTrace();
    }

    return Futures.immediateFuture(resultMap);
  }
}
