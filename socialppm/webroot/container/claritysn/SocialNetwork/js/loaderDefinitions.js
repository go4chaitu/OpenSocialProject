// Copyright (c) 2013, CA Inc.  All rights reserved.
    loadActivities = function(toId, groupId){
      if( !groupId )
      {
        groupId = '@friends';
      }
      $("#"+toId).load('/niku/socialppm/container/claritysn/SocialNetwork/components/activityDiv/activityDiv.html', function(){
        $("#divIdToLoad").val(toId);
        $("#activityGroupId").val(groupId);
        $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/components/activityDiv/js/activityDiv.js").done(
            function(script, textStatus) {
              var params = {userId: '@viewer', groupId: groupId, count: 20};
              osapi.activities.get(params).execute(activitycallback);
            });
      })};

    loadProfile = function( userId )
      {
        $( "#sn_main_content" )
          .load( '/niku/socialppm/container/claritysn/SocialNetwork/components/profileDiv/profileDiv.html', function()
        {
          $.getScript( "/niku/socialppm/container/claritysn/SocialNetwork/components/profileDiv/js/profileDiv.js" )
            .done(
                 function( script, textStatus )
                 {
                    osapi.people.get({ userId : userId, fields : ["id", "displayName"]}).execute(setProfileData);
                 }
            );
        } );
      };