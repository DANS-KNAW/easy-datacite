//FormModificationDetector.js

var formOnRender;
var detectFormModification = true;
var prevOnLoad;
var form_id = "${form_id}";
var last_form_id = null; // hack to prevent recusrion

if (window.onload)
	prevOnLoad = window.onload;

function setDetectFormModification(flag) {
	detectFormModification = flag;
}

function getFormWatched() {
	return document.getElementById(form_id);
}

function setFormOnRender() {
	var formWatched = getFormWatched();
	if (formWatched == null) {
		return;
	}
	formOnRender = Wicket.Form.doSerialize(formWatched);
}

window.onload = function() {
	var formWatched = getFormWatched();
    
    // **start** prevent recursion for Administration tab of file expolorer
    //alert('new: '+form_id+ ", old: "+last_form_id);
    // TODO rather prevent the recursion cause (active tab not clickable would be more efficient anyhow) 
	if (form_id != last_form_id){
		last_form_id = form_id;
	} else {
		return;
	}
	// **end** prevent recursion for Administration tab of file expolorer
	
	if (formWatched == null) {
		return;
	}
	if (prevOnLoad)
		prevOnLoad();
	var prevOnSubmit;
	if (formWatched.onsubmit)
		prevOnSubmit = formWatched.onsubmit;

	formWatched.onsubmit = function() {
		if (prevOnSubmit)
			prevOnSubmit();
		setDetectFormModification(false);
		return true;
	};

	// retrieve the form values. 'Wicket.Form.doSerialize'
	// function is defined in wicket-ajax.js.
	formOnRender = Wicket.Form.doSerialize(formWatched);
	last_form_id = null;
}

var prevOnBeforeUnload;
if (window.onbeforeunload)
	prevOnBeforeUnload = window.onbeforeunload;

// Before the windown unloads, check for any modifications
window.onbeforeunload = function(event) {
	var formWatched = getFormWatched();
	if (formWatched == null) {
		return;
	}
	if (prevOnBeforeUnload)
		prevOnBeforeUnload();

	if (detectFormModification) {
		formBeforeSubmit = Wicket.Form.doSerialize(formWatched);
		if (formOnRender != formBeforeSubmit) {
			return "${message}";
		}
	}
};