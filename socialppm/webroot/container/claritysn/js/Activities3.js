// Copyright (c) 2013, CA Inc.  All rights reserved.

function ActivityStreamsRender() {

	// Private member that wraps the OpenSocial API
	var social = new OpenSocialWrapper();

	// =================== PUBLIC ====================


	// Get viewer and owner
	this.getViewerOwner = function(div, callback) {
		social.loadPeople(function(response) {
			viewer = response.viewer;
			owner = response.owner;
			var viewerFriends = response.viewerFriends;
			var ownerFriends = response.ownerFriends;
			var html = '<br>';
			callback();
		});
	}

	// Renders the activities
	this.renderActivities = function(div, callback) {
		social.loadActivities(function(response) {

			var viewerActivities = response.viewerActivities.list;
			var ownerActivities = response.ownerActivities.list;
			var friendActivities = response.friendActivities.list;


			var html = '<h1>Activities</h1>';
			html += 'Demonstrates use of the Activities service in Apache Shindig.  The ActivityStreams service does not interfere with this service.<br><br>';
			html += 'Activities for you and ' + owner.name.formatted + ':<br>';
			html += "<table border='1'>";
			html += '<tr>';
			html += '<td>Name</td>';
			html += '<td>Title</td>';
			html += '<td>Body</td>';
			html += '<td>Images</td>';
			html += '</tr>';
			html += processActivities(viewerActivities);
			html += processActivities(ownerActivities);
			html += processActivities(friendActivities);
			html += '</table>';
			document.getElementById(div).innerHTML = html;
			callback();
		});
	}

	// Renders activity entries
	this.renderActivityEntries = function(div, callback) {
		social.loadActivityEntries(function(response) {
			var html = '';
      html = '<h2>ActivityEntries</h2>';
      if( response.viewerEntries && response.viewerEntries.length > 0 )
      {
			  viewerEntries = response.viewerEntries.list;
        html += processActivityEntries(viewerEntries);
      }
			//ownerEntries = response.ownerEntries.list;
      if( response.friendEntries && response.friendEntries.length > 0 )
      {
			  friendEntries = response.friendEntries.list;
        html += processActivityEntries(friendEntries);
      }

			if ((!viewerEntries || viewerEntries.length == 0) && (!friendEntries || friendEntries.length == 0)) {
				html += '<tr><td>No entries to show!</td></tr>';
			}
			html += '</table><br><br>';
			document.getElementById(div).innerHTML = html;
			callback();
		});
	}


	// Renders Comments
	this.renderComments = function(div, callback) {
		social.loadActivityEntries(function(response) {
		var html = '';
		viewerEntries = response.viewerEntries.list;
		//ownerEntries = response.ownerEntries.list;
		friendEntries = response.friendEntries.list;
		html += processComments(viewerEntries,div);
		//html += processActivityEntries(ownerEntries);
		html += processComments(friendEntries,div);
		if ((viewerEntries.length == 0) && (friendEntries.length == 0)) {
			html += 'No comments to show!';
		}
		html += '';
		document.getElementById(div).innerHTML = html;
		callback();
	});
	}

	// ================== PRIVATE =====================



	// Processes activities and returns the rendered HTML
	function processActivities(activities) {
		var html = '';
		for (idx = 0; idx < activities.length; idx++) {
			html += '<tr>';
			html += '<td>' + activities[idx].userId + '</td>';
			html += '<td>' + activities[idx].title + '</td>';
			html += '<td>' + activities[idx].body + '</td>';
			var mediaItems = activities[idx].mediaItems;
			if (mediaItems != null) {
				for (itemIdx = 0; itemIdx < mediaItems.length; itemIdx++) {
					if (mediaItems[itemIdx].type == 'image') {
						html += "<td><img src='" + mediaItems[itemIdx].url + "' width=150 height=150/></td>";
					}
				}
			}
			html += '</tr>';
		}
		return html;
	}

	// Processes activity entries and returns the rendered HTML
	function processActivityEntries(entries) {
		var html = '';
		for (idx = 0; idx < entries.length; idx++) {
			if (entries[idx].verb != 'comment'){
				html += '<div id="contents" style="background-color: #d9e9fc;">';
				html += '<img src="/niku/socialppm/container/claritysn/imgs/img1.jpg" height="50" width="50" align="center">';
				html += entries[idx].actor.displayName + '<p style="margin-top:-40px;padding-left:30%;">';
				html += '<strong>' + entries[idx].title + '</strong><br>';
				if (entries[idx].content && entries[idx].content != 'null') {
					html += entries[idx].content + '<br>';
				}
				if (entries[idx].object.url && entries[idx].object.url != 'null') {
					html += "<a href='" + entries[idx].object.url + "'>" + entries[idx].title + '</a><br>';
				}
				//html += 'ID: ' + entries[idx].id + '<br>';
				var entryUniqueId = entries[idx].id;
				html += entryUniqueId + '<br>';
				html += entries[idx].published + '<br>';
				//html += 'Verb: ' + entries[idx].verb + '<br>';
				html += '</p>';
				html += '<textarea cols="95%" rows="1" maxlength="100" placeholder="Enter Comment" id="commentBody'+entryUniqueId+'" required></textarea>';
				html += '<input type="button" value="Comment" onclick="commentDiv(';
				html += "'"+entryUniqueId+"'";
				html += ')" style="border-radius: 3px;float:right;"/><br>';
				//html += '<div id="'+entryUniqueId+'" style="display:block;">';
				html += '<div id="'+entryUniqueId+'" style="display:block;">';
				//renderComments(entryUniqueId, refresh);
				var c = new loadComments(entryUniqueId);

				html += '</div>';
				html += '</div><br><br>';
			}
		}
		return html;
	}

	// Processes Comments
	function processComments(entries,div) {
		var html = '';
		for (idx = 0; idx < entries.length; idx++) {
			if ((entries[idx].verb == 'comment') && (entries[idx].title && entries[idx].title == div)) {
				html += '<div id="contents" style="font-size:1.0em;background-color: #fff;">';
				html += '<img src="http://localhost:8080/container/mock-screens/img1.jpg" height="30" width="30" align="center">';
				html += entries[idx].actor.displayName + '<p style="margin-top:-27px;padding-left:30%;">';
				html += entries[idx].content + '<br>';
				html += entries[idx].id + '<br>';
				html += entries[idx].title + '<br>';
				html += '</div>';

			}

		}
		return html;
	}
}
