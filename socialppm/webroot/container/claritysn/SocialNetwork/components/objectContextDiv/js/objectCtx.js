$(document).ready(function(){

  textAreaTemplate = function( textarea_li_id )
	{
		var txt = $('#'+textarea_li_id+"_ta");
		var hiddenDiv = $(document.createElement('div'));
		var content = null;

		txt.addClass('txtstuff');
		hiddenDiv.addClass('hiddendiv common');

		$('#'+textarea_li_id).append(hiddenDiv);

		txt.on('keyup', function () {

			content = $(this).val();

			content = content.replace(/\n/g, '<br>');
			hiddenDiv.html(content + '<br class="lbr">');

			$(this).css('height', hiddenDiv.height());

		});
	}
	textAreaTemplate("objectCtx_updatebar");

	$(function(){
		$('.text_value').each(function(){
			if($(this).val() == ''){
				$(this).css({"color":"#a9a9a9"});
				$(this).val($(this).data('placeholder'));
			}
		});
	});
	$('input').focusin(function(){
		if($(this).val() == $(this).data('placeholder')){
			$(this).css({"color":"black"});
			$(this).val('');
		}
	}).focusout(function(){
		if(!$(this).val() || $(this).val().length <= 0){
			$(this).css({"color":"#a9a9a9"});
			$(this).val($(this).data('placeholder'));
		}
	});

  var objectCode = $("#listViewObjectCode", parent.document).val();
  var instanceId = $("#listViewInstanceId", parent.document).val();
  var groupId = objectCode + '_' + instanceId;

  $("#objCtxstatusbar_btn_li #post").click(function(){
      var bodyText = $("#objectCtx_updatebar_ta").val();
      var viewerId = parent.document.getElementById("socialViewerId").value;
      var timestamp = new Date().getTime();
      var params = {userId: '@viewer', activity: {body: bodyText,title:'', appId: groupId, userId:viewerId, postedTime: timestamp}};
      osapi.activities.create(params).execute(function(resp){
        $("#objectCtx_updatebar_ta").val("");
        loadActivities("objCtxActivites_content",groupId);
      });
  });
  $("#objCtxstatusbar_btn_li #cancel").click(function(){
      $("#objectCtx_updatebar_ta").val("");
  });

  osapi.people.get({ userId : '@viewer', fields : ["id", "displayName"]}).execute( function(resp) {
    if( objectCode == 'task')
    {
      $(".objectCtx_title1").text('Task Wall');
    }
    else
    {
      $(".objectCtx_title1").text('Project Wall');;
    }
    osapi.claritydata.getInstanceData({ objectCode: objectCode, instanceId: instanceId }).execute( function(resp) {
      if( resp )
      {
        $(".objectCtx_title2").text(resp.instanceName);
      }
    });
    var params = {userId: '@viewer', groupId: groupId, count: 20};
    osapi.activities.get(params).execute(function(resp){
      var count = 0;
      if( resp && resp.list && resp.list.length > 0 )
      {
        count = resp.list.length;
      }
      $("#posts_count .count").text(count);
    });
    if( resp )
    {
      if( resp.photos && resp.photos[0] )
      {
        profileUrl = resp.photos[0].value;
      }
      if( resp.name && resp.name.formatted )
      {
        formatted = resp.name.formatted;
      }
      if( resp.jobInterests )
      {
        jobInterests = resp.jobInterests;
      }
      $("#objectCtx_inner_ul #objectCtx_profilepic img").attr("src",profileUrl);
      $("#objectCtx_inner_ul #objectCtx_name #displayname").text(formatted);
      $("#objectCtx_inner_ul #objectCtx_name #designation").text(jobInterests);

      osapi.groups.getGroup({ userId: '@viewer', groupId: groupId }).execute(function(resp){
          if( resp.id )
          {
            loadActivities("objCtxActivites_content", groupId);
          }
          else
          {
            osapi.groups.create({ userId: '@viewer', group: { id:groupId, title: objectCode + ' Wall', description: 'Project Wall'} }).execute(function(resp){
            });
          }
        });
    }
  });
});
