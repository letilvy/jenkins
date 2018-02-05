//DO run this command with super admin, or delete operation may fail

var FS = require("fs");
var PATH = require("path");
var CHILD_PROC = require("child_process");
var UTIL = require("util");

function deleteDir(sDir, fnCallback){
	CHILD_PROC.exec(
		UTIL.format('rm -rf %s', sDir), fnCallback);
}

function travelDir(sDir, fnCallback){
	FS.readdirSync(sDir).forEach(function(sFile){
		var sPath = PATH.join(sDir, sFile);
		if(FS.existsSync(sPath) && FS.statSync(sPath).isDirectory()){
			if(sFile === "builds" || sFile === "modules"){ //Only delete the "builds" and "modules" folders
				fnCallback(sPath);
			}else{
				travelDir(sPath, fnCallback);
			}
		}
	});
}

//Home directory of Jenkins here is "/var/lib/jenkins"
travelDir("/var/lib/jenkins/jobs", function(sPath){
	deleteDir(sPath, function(){
		console.log("Delete " + sPath);
	});
});

