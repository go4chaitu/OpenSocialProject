// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Album;
import org.apache.shindig.social.opensocial.spi.AlbumService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

import java.util.Set;
import java.util.concurrent.Future;


@Singleton
public class AlbumDBHandler implements AlbumService {
  @Override
  public Future<Album> getAlbum( UserId userId, String appId, Set<String> fields, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums( UserId userId, String appId, Set<String> fields,
                                                     CollectionOptions options, Set<String> albumIds,
                                                     SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums( Set<UserId> userIds, GroupId groupId, String appId,
                                                     Set<String> fields, CollectionOptions options,
                                                     SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> deleteAlbum( UserId userId, String appId, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> createAlbum( UserId userId, String appId, Album album, SecurityToken token )
    throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> updateAlbum( UserId userId, String appId, Album album, String albumId, SecurityToken token )
    throws ProtocolException
  {
    return null;  
  }
}
