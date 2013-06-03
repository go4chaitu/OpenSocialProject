// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.MediaItemService;
import org.apache.shindig.social.opensocial.spi.UserId;

import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class MediaItemDBHandler implements MediaItemService {
  @Override
  public Future<MediaItem> getMediaItem( UserId userId, String appId, String albumId, String mediaItemId,
                                         Set<String> fields, SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems( UserId userId, String appId, String albumId,
                                                             Set<String> mediaItemIds, Set<String> fields,
                                                             CollectionOptions options, SecurityToken token )
    throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems( UserId userId, String appId, String albumId,
                                                             Set<String> fields, CollectionOptions options,
                                                             SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems( Set<UserId> userIds, GroupId groupId, String appId,
                                                             Set<String> fields, CollectionOptions options,
                                                             SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> deleteMediaItem( UserId userId, String appId, String albumId, String mediaItemId,
                                       SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> createMediaItem( UserId userId, String appId, String albumId, MediaItem mediaItem,
                                       SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> updateMediaItem( UserId userId, String appId, String albumId, String mediaItemId,
                                       MediaItem mediaItem, SecurityToken token ) throws ProtocolException
  {
    return null;  
  }
}
