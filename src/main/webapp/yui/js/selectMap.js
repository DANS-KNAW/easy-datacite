NOT_CHECKED  = 0;
SOME_CHECKED = 1;
CHECKED  = 2;

ITEM_DIRECTORY = 1;
ITEM_FILE = 0;

CHILD_COUNT_UNKNOWN = -1;

/* 
 Example DirSelect object
selectMap = {
	status: NOT_CHECKED,
	item_type : ITEM_DIRECTORY,
	item : {
		sid : 0,
		select_children : Array(),
		child_count : CHILD_COUNT_UNKNOWN
	} 	
};
alert(selectMap.item.child_count);
*/ 


/*function createFileSelectObject(status, sid)
{
	return {
		status: status,
		item_type : ITEM_FILE,
		item : {
			sid : sid
		} 	
	};
}*/

function Item(sid, children, child_count)
{
	this.sid = sid;
	if (typeof children    != 'undefined') this.children = children;
	if (typeof child_count != 'undefined') this.child_count = child_count;
}

function FileSelect(sid, status)
{
	this.status = status;
	this.item_type = ITEM_FILE;
	this.item = new Item(sid); 	
}

/*
function createDirSelectObject(status, sid,  child_count, children)
{
	// default params
	if (typeof children == 'undefined') children = array();
	else {
		if (!(children instanceof array))
			alert('createDirSelectObject(): error: children is not of the type array');
	}
	if (typeof child_count == 'undefined') 
		child_count = CHILD_COUNT_UNKNOWN;
	
	// create object
	return {
		status: status,
		item_type : ITEM_DIRECTORY,
		item : {
			sid : sid,
			select_children : children,
			child_count : child_count
		} 	
	};
}
*/

function DirSelect(sid, status, child_count, children)
{
	// default params
	if (typeof children == 'undefined') children = array();
	else {
		if (!(children instanceof array))
			alert('createDirSelectObject(): error: children is not of the type array');
	}
	if (typeof child_count == 'undefined') 
		child_count = CHILD_COUNT_UNKNOWN;
	
	this.status = status;
	this.item_type = ITEM_DIRECTORY;
	this.item = new Item(sid, children, child_count);
}


/* Example
var d = new DirSelect(CHECKED, 299);
alert(d.item.sid);
*/
 
function SelectMap() {
	this.selectmap = array();
}

/**
 * Searches through the selectmap for a FileSelect object or DirSelect object
 * with the sid sid. If the parentSelectMap paramter is not used this search
 * starts at the root level (this.selectmap)
 * 
 * Returns null when the object could not be found. Otherwise returns
 * the FileSelect object or DirSelectObject. 
 */
SelectMap.prototype.getSelectObject = function(sid, parentSelectMap) {
	if (typeof parentSelectMap == 'undefined') parentSelectMap = this.selectMap;
	
	for (var i = 0; i < parentSelectMap.length; i++) {
		if (parentSelectMap[i].item.sid == sid)
			return parentSelectMap[i];
		if (parentSelectMap[i].item_type == ITEM_DIRECTORY){
			var result = this.getSelectObject(sid, parentSelectMap[i].item.children);
			if (result) return result;
		}	
	}
	
	return null;
}


SelectMap.prototype.setStatusFile = function(sid, parentSid, status) {
	parentSelect = this.getSelectObject(parentSid);
	if (parentSelect == null) 
		alert('PRobleem!');
	fileSelect = this.getSelectObject(sid, parentSelect);
	if (fileSelect) 
		fileSelect.status = status;
	else
		parentSelect.push( new FileSelect(sid, status) );
} 

SelectMap.prototype.setStatusDir = function(sid, parentSid, status) {
	//todo.
} 