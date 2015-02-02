/**
 * Implements resizing of the file-explorer panels (like a Windows or Mac
 * file-explorer).
 * It uses a cookie to remember the state of the file-explorer. This is dataset
 * specific. Cookie is reset upon browser close (session based).
 *
 * This script depends on JQuery (1.11+) and JQuery-UI (1.11+).
 *
 * Source is based on example code in a StackOverflow answer
 * (http://stackoverflow.com/a/12403136) and the W3Schools explanation of
 * Javascript cookies (http://www.w3schools.com/JS/js_cookies.asp).
 */

function getCookie(cname) {
    // Source: http://www.w3schools.com/JS/js_cookies.asp
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}

$(document).ready(function() {
    // Adapted from: http://stackoverflow.com/a/12403136

    // init
    var container = $("#fileexplorer-resize-container");
    var leftPanel = container.children().first();
    var rightPanel = container.children().last();
    var resize= leftPanel;
    var containerWidth = container.width();
    var initialResizePanelWidth = resize.width();
    // this accounts for padding in the panels +
    // borders, you could calculate this using jQuery
    var padding = (resize.outerWidth()-resize.width()) + (rightPanel.outerWidth() - rightPanel.width());

    // Check if the panels have been resized before and resize the panels to that size
    var leftPanelWidth = getCookie("leftPanelWidth") || false;
    if (leftPanelWidth) {
        leftPanel.width(leftPanelWidth);
        rightPanel.width(containerWidth - leftPanelWidth - padding);
    }

    $(resize).resizable({
        handles: 'e',  // Only resize the panel on the right (eastern) side
        maxWidth: containerWidth * 0.67,
        minWidth: initialResizePanelWidth,

        resize: function(event, ui){
            var currentWidth = ui.size.width;

            // this accounts for some lag in the ui.size value, if you take this away
            // you'll get some instable behaviour
            $(this).width(currentWidth);

            // set the content panel width
            rightPanel.width(containerWidth - currentWidth - padding);

            // set a cookie to remember the panel width
            document.cookie="leftPanelWidth=" + currentWidth;
        }
    });
});