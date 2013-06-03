$(document).ready(function(){
	$("#defaulttextarea").click(function(){	
		$("#buttons").show();
		$(this).css("min-height","60");
	});
	$("#buttons #attachments").click(function(){
		$("#statusbar").css("min-height","150");
		$("#statusbar").css("border-left", "1px solid rgb(154, 178, 228)");
		$("#statusbar").css("border-right", "1px solid rgb(154, 178, 228)");
		$("#attachment_field").css("height", "93px");
    $("#file_metadata").css("min-height","20");
		$("#attachment_field").show();
	});
	$("#attachment_field").hide();
	$(".progress").hide();
	
	$(".attachment_close").click( function() {
	  $("#defaulttextarea #statusbar").css("min-height","60");
	  $("#attachment_field").css("height", "0");
    $('#fileupload').val('');
    $("#file_metadata").css("min-height","0");
	  $("#attachment_field").hide();
	  $("#file_metadata #filename").hide();
	  $("#statusbar").css("height","auto");        
	});
  $("#buttons #post").hover(function(){
    if( $("#statusupdate_ta").val() && $("#statusupdate_ta").val().length > 0 )
    {
      $(this).css("cursor","pointer");
    }
    else
    {
      $(this).css("cursor","text");
    }
  });
  $("#buttons #post").click(function(){
    if( !$("#attachment_field").is(":hidden") && $("#statusupdate_ta").val() && $("#statusupdate_ta").val().length > 0 )
    {
      $("#fileuploadForm").submit();
    }
    else if( $("#statusupdate_ta").val() && $("#statusupdate_ta").val().length > 0 )
    {
      //post the activity feed.
      var bodyText = $("#statusupdate_ta").val();
      var viewerId = parent.document.getElementById("socialViewerId").value;
      var timestamp = new Date().getTime();	  
      var params = {userId: '@viewer', activity: {body: bodyText,title:'', userId:viewerId, postedTime: timestamp}};
      osapi.activities.create(params).execute(function(resp){
        loadActivities("sn_centerbar_activities");
      });
    }
    $("#statusupdate_ta").val("");
    $(".attachment_close").click();
  });

  activityUpdate = function(resp)
  {
    var result = "";
  }
	initStatusBar = function(){
		var bar=$('.bar');
		var percent=$('.percent');
		var status=$('#status');
		$('#fileuploadForm').ajaxForm(
          {
            beforeSend:function(){
			   $(".progress").show();
               status.empty();var percentVal='0%';bar.width(percentVal)			   
               percent.html(percentVal);
            },
            uploadProgress:function(event,position,total,percentComplete){
                var percentVal=percentComplete+'%';
                bar.width(percentVal)
                percent.html(percentVal);
            },
            success:function(){
                var percentVal='100%';
                bar.width(percentVal)
                percent.html(percentVal);
            },
            complete:function(xhr){
                status.html(xhr.responseText);
            }
          }
		);
	};
	initStatusBar();
	/*global document:false, $:false */
	var txt = $('#statusupdate_ta');
	var hiddenDiv = $(document.createElement('div'));
	var content = null;

	txt.addClass('txtstuff');
	hiddenDiv.addClass('hiddendiv common');

	$('#status_textfield').append(hiddenDiv);

	txt.on('keyup', function () {

		content = $(this).val();

		content = content.replace(/\n/g, '<br>');
		hiddenDiv.html(content + '<br class="lbr">');

		$(this).css('height', hiddenDiv.height());

	});

  $("#fileupload").on("change", function(e){
    var thefiles = e.target.files;
    $.each(thefiles, function(i, item){
      var thefile = item;
      $("#file_metadata #filename").html("<span>" + thefile.name + "</span>");
    });
  });

});
