var exec = require('child_process').exec;
var child;
var fs = require('fs');

var execute = function(imagePath, outputPath, callback) {
  child = exec('convert ' + imagePath + ' -contrast -contrast -contrast ' +
      '-level 5%,10% -contrast -contrast -contrast' + outputPath, function(error, stdout, stderr) {

    if (error != null) {
      console.log('sanitize exec error: ' + error);
    }

    callback(imagePath, outputPath);
  });
}

exports.execute = execute;
