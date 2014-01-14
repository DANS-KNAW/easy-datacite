/**
* @Author lobo
* Javascript for the EasyUpload component. Uses an ajax polling mechanism for retreiving
* the status of an upload process. The upload process gets started from an iframe
*/


//-------------------------------------------
//------------- globals  --------------------
//-------------------------------------------

var uploads = new Uploads();
var pollInProgress = false;

//-------------------------------------------
//------------- user functions --------------
//-------------------------------------------


/**
 * Cancel an upload with the id uploadId.
 * Sends a signal to the back-end as well as it kills the iFrame doing the uploading
 * on the client-side.
 */
function cancelUpload(uploadId)
{
	return uploads.cancel(uploadId);
}

/**
 * Removes an upload tracking object. Does not cancel the upload, just removes the tracking object.
 */
function removeUpload(uploadId)
{
	return uploads.remove(uploadId);
}

/**
 * Removes all upload tracking objects that have finished tracking the upload (because the upload
 * either was canceled, received an error or was completed successful.
 */
function removeAllFinishedUploads()
{
	return uploads.removeAllFinished();
}

var UPLOAD_EVENT_ERROR     = 'uploadError';
var UPLOAD_EVENT_CANCELED  = 'canceled';
var UPLOAD_EVENT_COMPLETED = 'completed';
var UPLOAD_EVENT_FINISHED  = 'finished'; // finished means finished, not completed sucessfully!

/**
 * Registers an event handler that can be used to respond to events happening to the upload.
 *
 * @event the event to register to (see above constants for a list of the events)
 * @handler a function with one parameter: eventParams
 * @return the id of the event
 */
function registerUploadEventHandler(event, handler)
{
	return uploads.registerEventHandler(event, handler);
}

/**
 * Removes an event handler with the id eventHandlerId
 *
 * @eventHandlerId the id of the event you want to unregister
 * @return true if unregistered successfully
 */
function unregisterUploadEventHandler(eventHandlerId)
{
	return uploads.unregisterEventHandler(eventHandlerId);
}

/**
 * This may be used by any user of the upload to add parameters that will be send to the
 * server side for handling postprocessing.
 *
 * @name the name of the parameter. If a client parameter already exists with the same case-
 * sensitive name it will be overwritten with value.
 * @value the value of the parameter
 */
function addUploadClientParam(name, value)
{
	uploads.addClientParam(name, value);
}

/**
 * Removes a parameter with a certain name
 * @name case-sensitive name of a parameter
 */
function removeUploadClientParam(name)
{
	uploads.removeClientParam(name, value);
}

/**
 * Returns an assocative array with all client parameters.
 */
function getUploadClientParams()
{
	return uploads.getClientParams();
}

//-------------------------------------------
//------------- util ------------------------
//-------------------------------------------


//TODO: move this method along with handleException to another js file for reuse
//TODO: get standard error message from properties file
function handleError(msg)
{
        // write to user
        var stdErrorMsg = "A Javascript error occured.";
        var errWindow=window.open("", "errwindow", "width=400,height=200");
        if (errWindow)
        {
                errWindow.document.write(stdErrorMsg +'<br />');
                errWindow.document.write('<input type="button" value="Close" onclick="window.close()" />');
                errWindow.document.write('<input type="button" value="Details" onclick="document.getElementById(\'details\').style.display = \'block\';" />');
                errWindow.document.write('<div id="details" style="height: 100px; overflow: auto; display:none;">'+ msg +'</textarea></div>');
                errWindow.document.close();
                errWindow.focus();
        }
        else
        {
                // in case a popup window fails, we'll have to use a simple alert to notify the user
                alert(stdErrorMsg + "\nDetails: "+ msg);
        }

        // write to Wicket log
        if (typeof getWicketLog == "function")
        {
	    	var wicketLog = getWicketLog();
	      	if (typeof wicketLog == "object" && typeof wicketLog.error == "function")
	        	wicketLog.error(msg);
	    }

        return true;
}

function handleException(e)
{
        var msg = 'Caught exception';
        if (typeof e == 'object')
        {
                if ('name' in e) msg += ' '+ e.name;
                if ('fileName' in e) msg += ' in '+ e.fileName;
                if ('lineNumber' in e) msg += ' on line '+ e.lineNumber;
                if ('message' in e) msg += ': '+ e.message;
        }
        handleError(msg);
}


function getRandomNumber(length) {
	var chars = "0123456789";
	var randomString = '';
	for (var i = 0; i < length; i++) {
		randomString += chars.charAt( Math.floor(Math.random() * chars.length) );
	}
	return randomString;
}


//-------------------------------------------
//------------- ProgressBar object ----------
//-------------------------------------------


function ProgressBar(container_el)
{
	this.container = container_el;
	this.percentage = 0;
	this.init();
}


ProgressBar.prototype.init = function()
{
	// create the barContainer
	this.barContainer = document.createElement('div');
	this.barContainer.className = 'progressbar-container'

	// create and initialize the bar element
	this.bar = document.createElement('div');
	this.bar.className = 'progressbar';
	this.bar.style.width = '0px';

	// add the bar and the barContainer to the document
	this.container.appendChild(this.barContainer);
	this.barContainer.appendChild(this.bar);

	// get maximum width for the bar
	this.barMaxWidth = this.barContainer.offsetWidth;
}

ProgressBar.prototype.setPercentage = function(percentage)
{
	// update the percentage
	if (typeof percentage != "number") percentage = 0;
	if (percentage > 100) percentage = 100;
	if (percentage < 0)   percentage = 0;
	this.percentage = percentage;

	// update the bar's width
	var newWidth = this.barMaxWidth * (percentage/100);
	this.bar.style.width = newWidth +'px';
}

ProgressBar.prototype.getPercentage = function()
{
	return this.percentage;
}


//-------------------------------------------
//------------- Uploads object --------------
//-------------------------------------------

function Uploads()
{
	this.uploads = new Array();
	this.length = 0;
	this.events = new Array();
	this.clientParams = new Object();
}

Uploads.prototype.add = function(upload)
{
	upload.parent = this;
	this.uploads.push(upload);
	this.length = this.uploads.length;
}

Uploads.prototype.registerEventHandler = function(event, handler)
{
	eventHandler = new Object;
	eventHandler.event = event;
	eventHandler.handler = handler;
	this.events.push(eventHandler);
	return this.events.length-1;
}

Uploads.prototype.fireEvent = function(event, eventParams)
{
	for (var i = 0; i < this.events.length; i++)
	{
		if (this.events[i].event == event)
			(this.events[i].handler)(eventParams);
	}
}

Uploads.prototype.unregisterEventHandler = function(eventHandlerId)
{
	if (eventHandlerId < this.events.length && eventHandlerId >= 0)
	{
		this.events.splice(eventHandlerId, 1);
	}
}


Uploads.prototype.getUploadIdxById = function(uploadId)
{
	// lame version of indexOf
	for (var idx = 0; idx < this.uploads.length; idx++)
	{
		if (this.uploads[idx].id == uploadId)
			return idx;
	}
	return -1;
}

Uploads.prototype.get = function(idx)
{
	return this.uploads[idx];
}

Uploads.prototype.remove = function(uploadId)
{
	var idx = this.getUploadIdxById(uploadId);
	if (idx >= 0)
	{
		this.uploads[idx].remove();

		this.uploads.splice(idx, 1);
		this.length = this.uploads.length;
	}
}

Uploads.prototype.removeAllFinished = function()
{
	// collect id's of all finished uploads
	var removeIds = new Array();
	for (var idx = 0; idx < this.uploads.length; idx++)
	{
		var upload = this.uploads[idx];
		if (typeof upload != 'object') continue;
		if (upload.finished)
			removeIds.push(upload.id);
	}

	// then remove by id
	for (var i = 0; i < removeIds.length; i++)
		this.remove(removeIds[i]);
}

Uploads.prototype.cancel = function(uploadId)
{
	var idx = this.getUploadIdxById(uploadId);
	if (idx >= 0)
		this.uploads[idx].cancel();
}

Uploads.prototype.getUploadById = function(uploadId)
{
	var idx = this.getUploadIdxById(uploadId);
	if (idx >= 0)
		return this.uploads[idx];
	else
		return null;
}

Uploads.prototype.serializePollIds = function()
{
	var result = "";
	for (var i = 0; i < this.uploads.length; i++)
	{
		if (i != 0)
			result += "&";
		if (this.uploads[i].polling)
			result += "uploadId="+ escape(this.uploads[i].id);
	}
	return result;
}

Uploads.prototype.addOneToPollCount = function()
{
	for (var i = 0; i < this.uploads.length; i++)
	{
		this.uploads[i].pollCount++;
	}
}

Uploads.prototype.checkTimeout = function()
{
	for (var i = 0; i < this.uploads.length; i++)
	{
		if (this.uploads[i].polling &&
			(this.uploads[i].pollCount - this.uploads[i].lastPollUpdate)  > POLL_TIMEOUT)
			this.uploads[i].setError("Timeout: no reply from server");
	}
}

Uploads.prototype.doPoll = function()
{
	for (var i = 0; i < this.uploads.length; i++)
	{
		if (this.uploads[i].polling) return true;
	}
	return false;
}

Uploads.prototype.addClientParam = function(name, value)
{
	this.clientParams[name] = value;
}

Uploads.prototype.removeClientParam = function(name)
{
	if (typeof this.clientParams[name] != 'undefined')
		delete this.clientParams[name];
}

Uploads.prototype.getClientParams = function()
{
	return this.clientParams;
}

//-------------------------------------------
//------------- Upload object ---------------
//-------------------------------------------

// Note: the componentId is needed to
// support multiple upload components on a web page
function Upload(componentId, frame, id, filename)
{
	this.componentId = componentId;

	this.iframe = frame;
	this.id = id;
	this.filename = filename;
	this.progressContainer = null;
	this.pollCount = 0;
	this.lastPollUpdate = 0;
	this.polling = false;
	this.finished = false;
	this.parent = null;
}

Upload.prototype.init = function()
{
	// find the progress container div for this component
	var progressContainer = document.getElementById("uploadProgress_"+this.componentId);

	if (progressContainer == null)
	{
		handleError('could not find upload-progress div element');
		return false;
	}

	// create progress elements for this upload component
	this.progressContainer = document.createElement('div');
	this.progressContainer.className = "upload-container";
	this.progressContainer.style.display = "none";

	// create progress message
	this.progressMessage = document.createElement('SPAN');
	this.progressMessage.className = "upload-text-message";
	this.progressMessage.innerHTML = "Initializing upload for "+ decodeURIComponent(this.filename);

	// create the progress bar
	this.progressBarContainer = document.createElement('DIV');
	this.progressBarContainer.className = "upload-progressbar-container";
	this.progressBarContainer.id = "pb_"+ getRandomNumber(8);

	// create cancel button
	this.cancelButton = document.createElement('A');
	this.cancelButton.className = "button"; // "upload-cancel-link";
	this.cancelButton.href = 'javascript:cancelUpload('+ this.id +')';
	this.cancelButton.innerHTML = 'Cancel';

	// add to the progress container
	this.progressContainer.appendChild(this.progressBarContainer);
	this.progressContainer.appendChild(this.progressMessage);
	this.progressContainer.appendChild(this.cancelButton);

	// add the progress container of this upload to the global progress container
	this.progressContainer.style.display = "block";
	progressContainer.appendChild(this.progressContainer);

	progressContainer.style.display = "block";

	// create the progress bar after displaying, in some browser this causes
	// the progressbar to detect the wrong maximum width
	this.progressBar = new ProgressBar(this.progressBarContainer);

	// now the upload status object is ready for polling
	this.polling = true;

	return true;
}

Upload.prototype.fireEvent = function(event, eventParams)
{
	if (typeof eventParams != 'object') eventParams = new Object;
	eventParams.uploadId = this.id;
	if (typeof this.parent == 'object' && this.parent.fireEvent)
		this.parent.fireEvent(event, eventParams);
}

Upload.prototype.setError = function(message)
{
	this.progressMessage.className = "upload-error-message";
	this.progressMessage.innerHTML = message;

	this.fireEvent(UPLOAD_EVENT_ERROR);

	this.finish();
}

Upload.prototype.updateStatus = function(uploadStatus)
{
	this.lastPollUpdate = this.pollCount;

	// no updates needed after the upload has finished
	// doing this anyway is going to cause events to be
	// fired several times.
	if (this.finished)
		return;

	if (uploadStatus.error)
	{
		// got error from server
		if (uploadStatus.message)
			this.setError(uploadStatus.message);
		else
			this.setError('Got unknown error from server');

		return;
	}

	if (uploadStatus.finished)
	{
		// upload finished
		if (uploadStatus.message)
			this.progressMessage.innerHTML  = decodeURI(uploadStatus.message).replace(/[+]/g," ");
		else
			this.progressMessage.innerHTML = "Upload of '"+ decodeURI(this.filename).replace(/[+]/g," ") +"' complete.";

		this.fireEvent(UPLOAD_EVENT_COMPLETED);

		this.finish();

		return;
	}

	if (typeof uploadStatus.message == "string")
		this.progressMessage.innerHTML = decodeURI(uploadStatus.message).replace(/[+]/g," ");

	if (typeof uploadStatus.percentComplete == "number")
		this.progressBar.setPercentage(uploadStatus.percentComplete);
}

Upload.prototype.finish = function()
{
	if (this.finished)
		return;

	this.polling = false;
	this.finished = true;

	if (!AUTO_REMOVE_MESSAGES)
	{
		this.removeProgressBar();
		this.cancelToRemoveButton();
	}
	else
	{
		this.remove();
	}
	this.cleanupIFrame();

	// call javascript event handlers
	this.fireEvent(UPLOAD_EVENT_FINISHED);
}

Upload.prototype.removeProgressBar = function()
{
	if (this.progressBarContainer != null)
		this.progressBarContainer.parentNode.removeChild(this.progressBarContainer);
	this.progressBarContainer = null;
	this.progressBar = null;
}

Upload.prototype.remove = function()
{
	if (this.progressContainer != null)
		this.progressContainer.parentNode.removeChild(this.progressContainer);
	this.progressContainer = null;
}

Upload.prototype.cancelToRemoveButton = function()
{
	this.cancelButton.innerHTML = "Remove";
	this.cancelButton.className = "upload-remove-link";
	this.cancelButton.href = 'javascript:removeUpload('+ this.id +')';
}

Upload.prototype.cleanupIFrame = function()
{
	if (this.iframe)
	{
		this.iframe.parentNode.removeChild(this.iframe);
		this.iframe = null;
	}
}

Upload.prototype.cancel = function()
{
	this.polling = false;

	// simply by removing the iframe the upload is canceled on the client-side
	this.cleanupIFrame();

	// send cancel signal to server
	var requestUrl  = UPLOAD_CANCEL_REQUEST_URL;
	requestUrl += '?uploadId='+ escape(this.id);
	requestUrl += '&anticache='+ escape(Math.random());

	jQuery.ajax({
		type: "GET",
		url : requestUrl
	});

	// reomve the upload from the screen
	this.remove();

	// call javascript event handlers
	this.fireEvent(UPLOAD_EVENT_CANCELED);
	this.fireEvent(UPLOAD_EVENT_FINISHED);
}

//-------------------------------------------
//------------- polling functions -----------
//-------------------------------------------

function startPoll()
{
	if (pollInProgress)
		return true;

	return poll();
}

function poll()
{
	if (!uploads.doPoll())
		return false;


	// build request
	var requestUrl  = UPLOAD_STATUS_REQUEST_URL;
	requestUrl += '?'+ uploads.serializePollIds();
	requestUrl += '&anticache='+ escape(Math.random());

	// start request
	jQuery.ajax({
		type    : "GET",
		url     : requestUrl,
		cache   : false,
		success : function(response) { pollUpdate(response); }
	});

	// add one to all poll counts
	uploads.addOneToPollCount();

	pollInProgress = true;
	return true;
}

function parsePollupdateResponseText(responseText)
{
	// get JSON response
	if (typeof reponseText != "string" && responseText.length == 0)
		return null;

	// get object from JSON response
	try
	{
		var jobj = JSON.parse(responseText);
		if (typeof jobj != "object" || (typeof jobj == "object" && typeof jobj.length == "undefined"))
			return null;
		return jobj;
	}
	catch(e)
	{
		return null;
	}
}

function pollUpdate(response)
{
	jobj = parsePollupdateResponseText(response);
	if (jobj == null)
	{
		pollInProgress = false;
		return false;
	}

	// update progressbars
	for (var i = 0; i < jobj.length; i++)
	{
		// get uploadStatus object and check for validity
		var uploadStatus = jobj[i];
		if (typeof uploadStatus != "object")
		{
			handleError('uploadStatus is not an object');
			continue;
		}
		if (!uploadStatus.uploadId)
		{
			handleError('uploadStatus object does not contain an id');
			continue;
		}
		if (uploadStatus.uploadId < 0)
		{
			handleError('Got error from server: '+ uploadStatus.message);
			pollInProgress = false;
			return false;
		}

		// update the progress status locally
		var upload = uploads.getUploadById(uploadStatus.uploadId);
		if (!upload)
		{
			handleError('got a status of an upload object that does not exists locally');
			continue;
		}
		upload.updateStatus(uploadStatus);
	}

	// repeat polling process
	pollRetry();
}

var pollTimeCode = -1;
function pollRetry()
{
	// check for upload polling processes that timed out
	uploads.checkTimeout();

	// if no uploads timed out, retry
	if (uploads.doPoll())
	{
		if (pollTimeCode > 0) clearTimeout(pollTimeCode); // <- this is important!
		pollTimeCode = setTimeout('poll()', POLL_UPDATE_MS);
	}
	else
		pollInProgress = false;
}

/**
 * Creates a new upload progress tracking object. This object
 * will receive information from the back-end on what is happening
 * to the upload.
 *
 * Note: the componentId is needed to
 * support multiple upload components on a web page
 */
function showUploadProgress(componentId, uploadFrame, uploadId, filename)
{
	// create new upload object and add to uploads
	var upload = new Upload(componentId, uploadFrame, uploadId, filename);

	if (!upload.init())
	{
		handleError('unknown error initializing the upload');
		return false;
	}

	uploads.add(upload);

	// poll!
	poll();

	return upload;
}
