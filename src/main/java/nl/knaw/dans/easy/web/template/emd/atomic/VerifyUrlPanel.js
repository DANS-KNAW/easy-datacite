function checkUrl(relationUrl) {
	var RegExp = /^(https?:\/\/(\w+:{0,1}\w*@)?(\S+)|)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
	var inputElements = document.getElementsByTagName("input");
	var len = inputElements.length;
	var relationUrlValue = "";
	for ( var i = 0; i < len; i++) {
		if (inputElements[i].name == relationUrl) {
			relationUrlValue = inputElements[i].value;
			break;
		}
	}
	if ("" == relationUrlValue) {
		alert("No url specified.");
	} else if (!RegExp.test(relationUrlValue)) {
		alert("Specified url not valid.");
	} else {
		var options = "height=400,width=650,left=0,top=0,location=yes,scrollbars=yes,resizable=yes,status=yes";
		var hasProtocol = relationUrlValue.substring(0, 7);
		var url = (hasProtocol ? "" : "http://") + relationUrlValue;
		window.open(url, "Verify", options).focus();
	}
	return false;
}
