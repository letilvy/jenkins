//DO run this command with super admin, or delete operation may fail

var FS = require("fs");
var PATH = require("path");
var CHILD_PROC = require("child_process");
var UTIL = require("util");

/**
 * Home directory of Jenkins here is "/var/lib/jenkins" 
*/
var HOME_JENKINS = "/var/lib/jenkins";


function deleteDir(sDir, fnCallback){
	CHILD_PROC.exec(
		UTIL.format('rm -rf %s', sDir), fnCallback);
}

function travelDir(sDir, fnCallback){
	FS.readdirSync(sDir).forEach(function(sFile){
		var sPath = PATH.join(sDir, sFile);
		if(FS.existsSync(sPath) && FS.statSync(sPath).isDirectory()){
			if(sFile === "builds"){
				//Only reserve the final build
				var aBuild = [], sBuildPath;
				FS.readdirSync(sPath).forEach(function(sBuild){
					sBuildPath = PATH.join(sPath, sBuild);
					if(sBuild.match(/^\d+$/)){
						aBuild.push(sBuildPath);
					}else{
						fnCallback(sBuildPath);
					}
				});

				aBuild.sort().forEach(function(sBuildPath, index, aSortedBuild){
					if(index < aSortedBuild.length - 1){
						fnCallback(sBuildPath);
					}
				});
			}else if(sFile === "modules" || sFile === "cobertura"){
				fnCallback(sPath);
			}else{
				travelDir(sPath, fnCallback);
			}
		}
	});
}


//Delete jobs
travelDir(HOME_JENKINS + "/jobs", function(sPath){
	deleteDir(sPath, function(){
		console.log("Delete " + sPath);
	});
});

//Delete workspace
deleteDir(HOME_JENKINS + "/workspace/*", function(){
	console.log("Delete " + HOME_JENKINS + "/workspace/*");
});

