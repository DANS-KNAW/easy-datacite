/*
 * Create a DANS-namespace
 */
var DANS = function () {};

DANS.createHelpPopup = function(anchor) {
	var popupButton = new YAHOO.widget.Button('popupButton'+anchor);
	var popupDialog = new YAHOO.widget.Dialog('popupDialog'+anchor, { 
		xy:[500,10], 
		visible:false,
		close: true,
		constraintoviewport: true,
		modal: true });
	popupDialog.render();
	
	YAHOO.util.Event.addListener('popupButton'+anchor, 'click', showPopup, popupButton, true);
	
	function showPopup () {
		document.getElementById('popupDialog'+anchor).style.display = 'block';
		popupDialog.show();
	}
}

