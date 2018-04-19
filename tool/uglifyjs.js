var FS = require("fs");
var PATH = require("path");
var CHILD_PROC = require("child_process");
var UTIL = require("util");


function uglifyJS(sDir, fnCallback){
	CHILD_PROC.exec(
		UTIL.format('uglifyjs %s -mc -o %s', sDir, sDir), fnCallback);
}

function travelDir(sDir, fnCallback){
	FS.readdirSync(sDir).forEach(function(sFile){
		var sPath = PATH.join(sDir, sFile);
		if(FS.existsSync(sPath) && FS.statSync(sPath).isDirectory()){
/*			if(sFile === "builds"){
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
			}else{*/
				travelDir(sPath, fnCallback);
			// }
		}else if(FS.statSync(sPath).isFile() && sFile.match(/\.js$/)){
			console.log("Find js file: " + sPath);
			fnCallback(sPath);
		}
	});
}


travelDir("../../../ysccd", function(sPath){
	uglifyJS(sPath, function(){
		console.log("Uglify " + sPath);
	});
});
