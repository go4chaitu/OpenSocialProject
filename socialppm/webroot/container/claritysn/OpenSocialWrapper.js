// Copyright (c) 2013, CA Inc.  All rights reserved.

function OpenSocialWrapper() {

	// =============================== PEOPLE ===============================

	/*
	 * Loads the owner, the viewer, the owner's friends, and the viewer's
	 * friends.  Response data is put into the variables owner, viewer,
	 * ownerFriends, and viewerFriends, respectively.
	 *
	 * @param callback is the function to return the response to
	 */
	this.loadPeople = function(callback) {
		var batch = osapi.newBatch();
		batch.add('viewer', osapi.people.getViewer());
		batch.add('owner', osapi.people.getOwner());
		batch.add('viewerFriends', osapi.people.getViewerFriends());
		batch.add('ownerFriends', osapi.people.getOwnerFriends());
		batch.execute(callback);
	}

	this.loadViewerFriends = function(callback) {
		osapi.people.getViewerFriends().execute(callback);
	}

	this.loadOwnerFriends = function(callback) {
		osapi.people.getOwnerFriends().execute(callback);
	}

	// ========================= ACTIVITIES =============================
	this.loadActivities = function(callback) {
		var batch = osapi.newBatch();
		batch.add('viewerActivities', osapi.activities.get({userId: '@viewer', groupId: '@self'}));
		batch.add('ownerActivities', osapi.activities.get({userId: '@owner', groupId: '@self'}));
		batch.add('friendActivities', osapi.activities.get({userId: '@viewer', groupId: '@friends'}));
		batch.execute(callback);
	}

	this.loadViewerActivities = function(callback) {
		var req = osapi.activities.get({userId: '@viewer', groupId: '@self'});
		req.execute(callback);
	}

	this.loadViewerFriendsActivities = function(callback) {
		var req = osapi.activities.get({userId: '@viewer', groupId: '@friends'});
		req.execute(this.onLoadActivitiesFriends);
	}

	this.loadOwnerActivities = function(callback) {
		var req = osapi.activities.get({userId: '@owner', groupId: '@self'});
		req.execute(callback);
	}


	// ========================= ACTIVITY STREAMS =============================
	this.loadActivityEntries = function(callback) {
		var batch = osapi.newBatch();
		batch.add('viewerEntries', osapi.activitystreams.get({userId: '@viewer', groupId: '@self'}));
		//batch.add('ownerEntries', osapi.activitystreams.get({userId: '@owner', groupId: '@self'}));
		//batch.add('friendEntries', osapi.activitystreams.get({userId: '@viewer', groupId: '@friend'}));
		batch.execute(callback);
	}

	this.loadViewerActivityEntries = function(callback) {
		var params = {userId: '@viewer', groupId: '@self'};
		osapi.activitystreams.get(params).execute(callback);
	}

	this.loadOwnerActivityEntries = function(callback) {
		var params = {userId: '@owner', groupId: '@self'};
		osapi.activitystreams.get(params).execute(callback);
	}

	this.loadViewerFriendsActivityEntries = function(callback) {
		var params = {userId: '@viewer', groupId: '@friends'};
		osapi.activitystreams.get(params).execute(callback);
	}

	this.postActivityEntry = function(title, content, verb, actorId, actorName, objectName, objectSummary,
									  objectPermalink, objectType, callback) {
		var params = {
			userId: '@viewer',
			groupId: '@self',
			activity: {
				published: '2010-04-27T06:02:36+0000',
				title: title,
				content: content,
				actor: {
					id: actorId,
					displayName: actorName
				},
				verb: verb,
				object: {
					id: new	Date().getTime(),
					displayName: objectName,
					url: objectPermalink,
					objectType: objectType,
					summary: objectSummary
				}
			}
		};
		osapi.activitystreams.create(params).execute(callback);
	}

	this.deleteActivityEntryById = function(activityEntryId, callback) {
		var params = {
			userId: '@viewer',
			groupId: '@self',
			activityId: activityEntryId
		};
		osapi.activitystreams['delete'](params).execute(callback);
	}

	this.getActivityEntryById = function(activityEntryId, callback) {
		var params = {activityId: activityEntryId};
		osapi.activitystreams.get(params).execute(callback);
	}
}
