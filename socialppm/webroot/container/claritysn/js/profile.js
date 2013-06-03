$(document).ready(function(){

  $(".editfield").click(function(){
    var field = $(this).parent().parent().find(".fieldvalue");
	field.attr("contentEditable", "true");
	var saveButton = $(this).parent().parent().find(".save_field");
	var cancelButton = $(this).parent().parent().find(".cancel_field");
	field.css("background-color","#FFFFCC");
    field.css("border", "1px solid rgb(188, 197, 213)");
	saveButton.css("display","inline-block");
	cancelButton.css("display","inline-block");
  });
  $(".save_field").click(function(){
	$(this).parent().attr("contentEditable", "false");
    $(this).parent().css("background-color","#FFFFFF");
    $(this).parent().css("border", "0px solid rgb(188, 197, 213)");
	$(this).css("display","none");
	$(this).parent().find(".cancel_field").css("display","none");
  });
  $(".cancel_field").click(function(){
	$(this).parent().attr("contentEditable", "false");
    $(this).parent().css("background-color","#FFFFFF");
    $(this).parent().css("border", "0px");

	$(this).css("display","none");
	$(this).parent().find(".save_field").css("display","none");
  });
  $("#save_profile").click(function(){
	alert("Saved");
  });
});
