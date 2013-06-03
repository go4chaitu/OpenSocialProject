function showNotificationBar(message, duration, bgColor, txtColor, height) {

    /*set default values*/
    duration = typeof duration !== 'undefined' ? duration : 5000;
    bgColor = typeof bgColor !== 'undefined' ? bgColor : "#F4E0E1";
    txtColor = typeof txtColor !== 'undefined' ? txtColor : "black";
    height = typeof height !== 'undefined' ? height : 40;
    var HTMLmessage = "<div class='notification-message' style='text-align:center; line-height: " + height + "px;'> " + message + " </div>";
    var notify_bar = $('#notification-bar',parent.document);
    notify_bar.css({"position":"fixed", "bottom":"0px", "right":"0px", "display":"none", "padding":"5px","max-width": "300px", "height": height,  "background-color":bgColor, "font-size":"10px","position": "fixed","z-index": "100000","color":txtColor, "border": "1px solid #052955" })
    notify_bar.html(HTMLmessage);
    /*animate the bar*/
    notify_bar.slideDown(1000, function() {
        setTimeout(function() {
            $(this).slideUp(function() {});
        }, duration);
    });
}