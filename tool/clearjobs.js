//DO run this command with super admin, or delete operation may fail
/**
  To schedule this cleanup task run regularly on Linux, use command:
      sudo crontab -e
  Add such as following statements at the end of the opened file:
      # Schedule task for Jenkins server space clean-up
      #   Task run at UTC 22:00pm ( China time 06:00am and Germany time 00:00am) everyday
      #   Use "0 22 * * 0" to run at 22:00 every Sunday
      0 22 * * * sudo node /home/i306293/git/jenkins/tool/clearjobs.js
  Save and quit.  
*/

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
					if(index < aSortedBuild.length - 3){
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

//Other directories for delete
[
	(HOME_JENKINS + "/workspace/*"),
	(HOME_JENKINS + "/.m2/repository/sap/*"),
	(HOME_JENKINS + "/.m2/repository/com/sap/*")
].forEach(function(sDir){
	deleteDir(sDir, function(){
		console.log("Delete " + sDir);
	});
});