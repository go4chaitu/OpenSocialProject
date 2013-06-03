// Copyright (c) 2013, CA Inc.  All rights reserved.
function shareData() {
	  var content = document.getElementById('content').value;
	  var req = osapi.appdata.update({userId: '@viewer', data: {content: content}});
	  req.execute(function(response) {
	    if (response.error) {
	      document.getElementById('result_appdata').innerHTML = response.error.message;
	    } else {
	      document.getElementById('result_appdata').innerHTML = 'Succeeded!';
	    }
	    gadgets.window.adjustHeight();
	  });
	}


	function fetchWallData() {
	  var req = osapi.appdata.get({userId: '@viewer', groupId: '@friends', keys: ['content']});
	  req.execute(function(response) {
	    for (var id in response) {
	      var obj = response[id];
	      document.getElementById('posts').innerHTML
	          += '<div id="contents"><li>' + id + ':<p>' + obj['content'] + '</p></li></div>';
	    }
	    
	  });
	}
	