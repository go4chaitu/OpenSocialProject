// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.comparators;

import org.apache.shindig.social.opensocial.model.Activity;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: sarch04
 * Date: 5/14/13
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityOrderByPostedTime  implements Comparator<Activity>
{
  @Override
  public int compare( Activity o1, Activity o2 )
  {
    return o1.getPostedTime() > o2.getPostedTime()? -1 : (o1.getPostedTime() < o2.getPostedTime()? 1 : 0);
  }
}
