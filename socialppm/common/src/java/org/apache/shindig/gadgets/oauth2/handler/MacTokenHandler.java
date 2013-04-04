/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets.oauth2.handler;

import org.apache.shindig.common.crypto.Crypto;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.oauth2.OAuth2Accessor;
import org.apache.shindig.gadgets.oauth2.OAuth2Error;
import org.apache.shindig.gadgets.oauth2.OAuth2Message;
import org.apache.shindig.gadgets.oauth2.OAuth2Token;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 *
 * See {@link ResourceRequestHandler}
 *
 * Handles the mac token type
 */
public class MacTokenHandler implements ResourceRequestHandler {
  public static final String TOKEN_TYPE = OAuth2Message.MAC_TOKEN_TYPE;
  private static final OAuth2Error ERROR = OAuth2Error.MAC_TOKEN_PROBLEM;

  public OAuth2HandlerError addOAuth2Params(final OAuth2Accessor accessor, final HttpRequest request) {
    try {
      final OAuth2HandlerError handlerError = MacTokenHandler.validateOAuth2Params(accessor,
              request);
      if (handlerError != null) {
        return handlerError;
      }

      final OAuth2Token accessToken = accessor.getAccessToken();

      String ext = accessToken.getMacExt();
      if (ext == null || ext.length() == 0) {
        ext = "";
      }

      // REQUIRED. The MAC key identifier.
      final String id = new String(accessToken.getSecret(), "UTF-8");

      // REQUIRED. A unique string generated by the client to allow the
      // server to verify that a request has never been made before and
      // helps prevent replay attacks when requests are made over an
      // insecure channel. The nonce value MUST be unique across all
      // requests with the same MAC key identifier.
      // The nonce value MUST consist of the age of the MAC credentials
      // expressed as the number of seconds since the credentials were
      // issued to the client, a colon character (%x25), and a unique
      // string (typically random). The age value MUST be a positive
      // integer and MUST NOT include leading zeros (e.g.
      // "000137131200"). For example: "273156:di3hvdf8".
      // To avoid the need to retain an infinite number of nonce values
      // for future checks, the server MAY choose to restrict the time
      // period after which a request with an old age is rejected. If
      // such a restriction is enforced, the server SHOULD allow for a
      // sufficiently large window to accommodate network delays which
      // will affect the credentials issue time used by the client to
      // calculate the credentials' age.
      final long currentTime = System.currentTimeMillis() / 1000;
      final String nonce = Long.toString(currentTime - accessToken.getIssuedAt()) + ':'
              + String.valueOf(Math.abs(Crypto.RAND.nextLong()));

      // OPTIONAL. The HTTP request payload body hash as described in
      // Section 3.2.

      String bodyHash = MacTokenHandler.getBodyHash(request, accessToken.getMacSecret(),
              accessToken.getMacAlgorithm());
      if (bodyHash == null) {
        bodyHash = "";
      }

      // mac
      // REQUIRED. The HTTP request MAC as described in Section 3.3.
      final Uri uri = request.getUri();

      String uriString = uri.getPath();
      if (uri.getQuery() != null) {
        uriString = uriString + '?' + uri.getQuery();
      }

      String host = uri.getAuthority();
      String port = "80";
      final int index = host.indexOf(':');
      if (index > 0) {
        port = host.substring(index + 1);
        host = host.substring(0, index);
      } else {
        final String scheme = uri.getScheme();
        if ("https".equals(scheme)) {
          port = "443";
        }
      }

      final String mac = MacTokenHandler.getMac(nonce, request.getMethod(), uriString, host, port,
              bodyHash, ext, accessToken.getMacSecret(), accessToken.getMacAlgorithm());

      final String headerString = buildHeaderString(id, nonce, bodyHash, ext, mac);

      request.setHeader(OAuth2Message.AUTHORIZATION_HEADER, headerString);
      return null;
    } catch (final Exception e) {
      return MacTokenHandler.getError("Exception occurred " + e.getMessage(), e);
    }
  }

  private static String buildHeaderString(final String id, final String nonce,
          final String bodyHash, final String ext, final String mac) {
    final StringBuilder headerString = new StringBuilder();

    headerString.append(OAuth2Message.MAC_HEADER);
    headerString.append(" id = \"");
    headerString.append(id);
    headerString.append("\",");
    headerString.append(OAuth2Message.NONCE);
    headerString.append("=\"");
    headerString.append(nonce);
    if (bodyHash.length() > 0) {
      headerString.append("\",");
      headerString.append(OAuth2Message.BODYHASH);
      headerString.append("=\"");
      headerString.append(bodyHash);
    }
    if (ext.length() > 0) {
      headerString.append("\",");
      headerString.append(OAuth2Message.MAC_EXT);
      headerString.append("=\"");
      headerString.append(ext);
    }
    headerString.append("\",");
    headerString.append(OAuth2Message.MAC);
    headerString.append("=\"");
    headerString.append(mac);
    headerString.append('\"');
    return headerString.toString();
  }

  private static OAuth2HandlerError validateOAuth2Params(final OAuth2Accessor accessor,
          final HttpRequest request) {
    if (accessor == null || !accessor.isValid() || accessor.isErrorResponse()) {
      return MacTokenHandler.getError("accessor is invalid " + accessor);
    }

    if (request == null) {
      return MacTokenHandler.getError("request is null");
    }

    final OAuth2Token accessToken = accessor.getAccessToken();

    if (accessToken == null || accessToken.getTokenType().length() == 0) {
      return MacTokenHandler.getError("accessToken is invalid " + accessToken);
    }

    if (!MacTokenHandler.TOKEN_TYPE.equalsIgnoreCase(accessToken.getTokenType())) {
      return MacTokenHandler.getError("token type mismatch expected " + MacTokenHandler.TOKEN_TYPE
              + " but got " + accessToken.getTokenType());
    }

    final String algorithm = accessToken.getMacAlgorithm();
    if (algorithm == null || algorithm.length() == 0) {
      return MacTokenHandler.getError("invalid mac algorithm " + algorithm);
    }

    if (!OAuth2Message.HMAC_SHA_1.equalsIgnoreCase(algorithm)) {
      return MacTokenHandler.getError("unsupported algorithm " + algorithm);
    }

    final byte[] macSecret = accessToken.getMacSecret();
    if (macSecret == null) {
      return MacTokenHandler.getError("mac secret is null");
    }

    if (macSecret.length == 0) {
      return MacTokenHandler.getError("invalid mac secret");
    }

    return null;
  }

  public String getTokenType() {
    return MacTokenHandler.TOKEN_TYPE;
  }

  private static String getBodyHash(final HttpRequest request, final byte[] key,
          final String algorithm) throws UnsupportedEncodingException, GeneralSecurityException {
    if (request.getPostBodyLength() > 0) {
      final byte[] text = MacTokenHandler.getBody(request);
      final byte[] hashed = MacTokenHandler.hash(text, key, algorithm);
      return new String(hashed, "UTF-8");
    }

    return "";
  }

  private static byte[] getBody(final HttpRequest request) throws UnsupportedEncodingException {
    return request.getPostBodyAsString().getBytes("UTF-8");
  }

  private static String getMac(final String nonce, final String method, final String uri,
          final String host, final String port, final String bodyHash, final String ext,
          final byte[] key, final String algorithm) throws UnsupportedEncodingException,
          GeneralSecurityException {
    final StringBuilder normalizedRequestString = MacTokenHandler.getNormalizedRequestString(nonce,
            method, uri, host, port, bodyHash, ext);
    final byte[] normalizedRequestBytes = normalizedRequestString.toString().getBytes("UTF-8");
    final byte[] mac = MacTokenHandler.hash(normalizedRequestBytes, key, algorithm);
    final byte[] encodedBytes = Base64.encodeBase64(mac);
    return new String(encodedBytes, "UTF-8");
  }

  private static byte[] hash(final byte[] text, final byte[] key, final String algorithm)
          throws GeneralSecurityException {
    if (OAuth2Message.HMAC_SHA_1.equalsIgnoreCase(algorithm)) {
      return Crypto.hmacSha1(key, text);
    }

    return new byte[] {};
  }

  private static StringBuilder getNormalizedRequestString(final String nonce, final String method,
          final String uri, final String host, final String port, final String bodyHash,
          final String ext) {
    final StringBuilder ret = new StringBuilder();
    ret.append(nonce);
    ret.append('\n');
    ret.append(method);
    ret.append('\n');
    ret.append(uri);
    ret.append('\n');
    ret.append(host);
    ret.append('\n');
    ret.append(port);
    ret.append('\n');
    ret.append(bodyHash);
    ret.append('\n');
    ret.append(ext);
    ret.append('\n');

    return ret;
  }

  private static OAuth2HandlerError getError(final String contextMessage) {
    return MacTokenHandler.getError(contextMessage, null);
  }

  private static OAuth2HandlerError getError(final String contextMessage, final Exception e) {
    return new OAuth2HandlerError(MacTokenHandler.ERROR, contextMessage, e);
  }
}