function FriendsRender() {

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

	// Renders the welcome text (viewer, owner, and friends)
	this.renderWelcome = function(div, callback) {
		social.loadPeople(function(response) {
			viewer = response.viewer;
			owner = response.owner;
			var viewerFriends = response.viewerFriends;
			var ownerFriends = response.ownerFriends;


			var html = '<font style="font-size:1.2em;line-height: 1.4em;font-family: Arial, sans-serif;">Welcome ' + viewer.name.formatted + '!'+viewer.gender;
			html += '<br>You are viewing ' + owner.name.formatted + "'s data. <br>"+owner.gender+"<br>";
			html += 'List of friends: </font><br>';
			html += '<div id="team_header"><div id="team_score_label"><input placeholder="Type Name" id="autosuggest_col" class="ui-autocomplete-input" autocomplete="off" style="margin-top:-26px;margin-right:-230px;"><input style="border-radius:3px;margin-left:670px;margin-top:-22px;" type="submit" value="Send request" name="submit1" class="update_button1"></div></div>';
			html += '<div id="choosen_players_1">';
			for (i = 0; i < viewerFriends.list.length; i++) {
				html += '<ul class="player_box" player_id="1"><li style="text-align:center;"><img class="player_img_content" id="player1" src="/niku/socialppm/container/claritysn/imgs/dummy.jpg" align="bottom"><br>' + viewerFriends.list[i].name.formatted + '</li></ul>';
			}
			html += '</div>';
			document.getElementById(div).innerHTML = html;
			callback();
		});
	}
}
