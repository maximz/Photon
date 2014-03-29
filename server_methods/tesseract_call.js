var exec = require('child_process').exec, child;
var fs = require('fs');

// Call the tesseract method
var execute = function(imageDir, outputDir, callback) {
  child = exec('tesseract ' + imageDir + ' ' + outputDir, function (error, stdout, stderr) {
      console.log('stdout: ' + stdout);
      console.log('stderr: ' + stderr);
      if (error != null) {
          console.log('exec error: ' + error);
      }
      fs.readFile(outputDir + '.txt', function (err, data) {
          if (err) throw err;
          console.log('tesseract output: ' + data);
      }); 
      
    callback(imageDir, outputDir);
  });

};

exports.execute = execute;
