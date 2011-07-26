/**
* @Author lobo
* Javascript for the UploadIFrame component which is part of the EasyUpload component.<b>
* This creates an iFrame for every upload and then calls the parent frame for checking
* the status.
*/

var uploadFilename = "";


function handleError(msg)
{
	if (window.parent && typeof window.parent.handleError == "function")
	    window.parent.handleError(msg);
	setUploadFilename("");
}

function createIframe(iframeName)
{
	var hDiv = document.getElementById("hDiv");
	if (hDiv == null)
	{
		handleError("Could not find hDiv.");
		return null;
	}

	// create the iframe in a div inside hDiv
	var iframesElement = document.createElement('div');
	if (iframesElement == null)
	{
		handleError("Could not create iframes DIV element");
		return null;
	}
	hDiv.appendChild(iframesElement);

	// must use innerHTML here, for using the DOM one must use a timeout for some browsers
	// to add the iframe to the frames array and thus the "target" list
	iframesElement.innerHTML = '<iframe name="'+ iframeName +'" id="'+ iframeName +'"></iframe>';

	// return the iframe element
	return document.getElementById(iframeName);
}


function addReplaceGetParam(url, paramName, paramValue)
{
	regex = new RegExp( "([\\?&]"+ paramName +"=)[^&#]*" );
	var bIdx = url.search(regex);
	if (bIdx < 0)
	{
		url += ( url.indexOf("?") >= 0 ? "&" : "?" )+ paramName +"="+ escape(paramValue);
	}
	else
		url = url.replace(regex, "$1"+paramValue);
	return url;
}

function setUploadFilename(filename)
{
	// set global variable uploadFilename to filename without path
	var path = filename.lastIndexOf('/');
	if (path < 0) path = filename.lastIndexOf('\\');
	if (path >= 0)
		uploadFilename = filename.substring(path+1);
	else
		uploadFilename = filename;
}

function startUpload(form)
{
	try
	{
		// validate input (many browsers don't allow access to the value)
		if (typeof uploadFilename != "string" || uploadFilename == "")
		{
			alert('please select a file you wish to upload.');
			return false;
		}

		// generate an uploadId and send it via the GET method to the server alongside the upload
		var uploadId = -1;
		if (form.uploadId && form.uploadId.value)
			uploadId = form.uploadId.value;

		// if the uploadId is not present then start the upload process without progress tracking
		if (uploadId < 0)
		{
		    form.submit();
		    return true;
		}

	    // create iframe in hFrame
	    var iframe = createIframe("upload_"+ uploadId);
	    if (iframe == null)
	    {
	        handleError("Could not create iframe. Upload requires the dynamic creation of an iframe.'.");
	        return false;
	    }

		var wp = null;
		if (typeof window.parent != 'undefined' && typeof window.parent.showUploadProgress == "function")
			wp = window.parent;

	    // submit form to newly created iframe adding the uploadId to the get parameters
	    form.action = addReplaceGetParam(form.action, "uploadId", uploadId);
	    form.action = addReplaceGetParam(form.action, "filename", uploadFilename);
	    if (wp != null)
	    {
	    	var clientParams = wp.getUploadClientParams();
    		for (var name in clientParams)
    		{
	    		form.action = addReplaceGetParam(form.action, name, clientParams[name]);
    		}
	    }
	    form.target = iframe.name;
	    form.submit();

		if (wp != null) {
			// extract component id from the form id; get everything after the first '_'
			var componentId = form.id.substring(form.id.indexOf('_')+1);

			// start tracking upload progress
			wp.showUploadProgress(
				componentId, // support multiple upload components on a web page
				iframe,
				uploadId,
				uploadFilename
			);

			// This makes sure that after the upload is finished this iframe will
			// be reloaded. A new uploadId will thus be generated.
			var uploadHandlerId = wp.registerUploadEventHandler(
				'finished',
				function(eventParams) {
					wp.unregisterUploadEventHandler(uploadHandlerId);
					location.reload(true);
				}
			);
		}


		// reset needs to be done after a small timeout, because some browsers will cancel
		// the form submission if a reset comes in the same function as the submit has been
		// called. The form is disabled, because an uploadId is unique and can only be used
		// once.
		var submitButton = document.getElementById('submitButton');
		if (submitButton != null)
			submitButton.disabled = true;

		setTimeout('disableUploadForm()', 10);
		setTimeout('disableUploadForm()', 100);
	}
	catch(e)
	{
			if (window.parent && typeof window.parent.handleException == "function")
		        window.parent.handleException(e);
	        return false;
	}

	return true;
}

function getUploadForm()
{
	var forms = document.getElementsByTagName('form');
	if (forms.length > 0)
		return forms[0];
	return null;
}

function disableUploadForm()
{
	var form = getUploadForm();
	if (form != null)
	{
		form.file.disabled = true;
	}
}