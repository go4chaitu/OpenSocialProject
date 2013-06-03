// Copyright (c) 2013, CA Inc.  All rights reserved.

var objectCode = $("#listViewObjectCode", parent.document).val();
var instanceId = $("#listViewInstanceId", parent.document).val();
var groupId = objectCode + '_' + instanceId;
var profileId = parent.document.getElementById("socialViewerId").value;

$('button.followButton').click( function(){
    $button = $(this);
    if($button.hasClass('following')){
      osapi.groups.getGroup({ userId: '@viewer', groupId: groupId }).execute(function(resp)
      {
        if( resp && resp.id)
        {
          osapi.people.delete({ userId: '@viewer', groupId: groupId,  userIds: [profileId]}).execute(function(){});
        }
      });

      $button.removeClass('following');
      $button.removeClass('unfollow');
      $button.text('Follow');
    } else {

        // $.ajax(); Do Follow
        osapi.groups.getGroup({ userId: '@viewer', groupId: groupId }).execute(function(resp)
        {
          var xyz = resp.id.abc;
          if( resp && resp.id)
          {
            osapi.people.create({ userId: '@viewer', groupId: groupId,  person: { id: profileId }}).execute(function(){});
          }
        });
        $button.addClass('following');
        $button.text('Following');
    }
});

$('button.followButton').hover(function(){
     $button = $(this);
    if($button.hasClass('following')){
        $button.addClass('unfollow');
        $button.text('Unfollow');
    }
}, function(){
    if($button.hasClass('following')){
        $button.removeClass('unfollow');
        $button.text('Following');
    }
});

osapi.people.get({ userId: '@viewer', groupId: groupId }).execute(function(resp){
  if( resp && resp.list && resp.list.length > 0 )
  {
    $('button.followButton').addClass('following');
    $('button.followButton').text('Following');
  }
});


