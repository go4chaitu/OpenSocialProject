// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Album;
import org.apache.shindig.social.opensocial.spi.AlbumService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


@Singleton
public class AlbumDBHandler implements AlbumService {
  @Override
  public Future<Album> getAlbum( UserId userId, String appId, Set<String> fields, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums( UserId userId, String appId, Set<String> fields,
                                                     CollectionOptions options, Set<String> albumIds,
                                                     SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums( Set<UserId> userIds, GroupId groupId, String appId,
                                                     Set<String> fields, CollectionOptions options,
                                                     SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> deleteAlbum( UserId userId, String appId, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> createAlbum( UserId userId, String appId, Album album, SecurityToken token )
    throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> updateAlbum( UserId userId, String appId, Album album, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
