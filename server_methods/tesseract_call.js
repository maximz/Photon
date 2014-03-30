var exec = require('child_process').exec, child;
var fs = require('fs');

// Call the tesseract method
var execute = function(imagePath, outputPath, callback) {
  child = exec('tesseract ' + imagePath + ' ' + outputPath + ' photonenglish', function (error, stdout, stderr) {
      console.log('stdout: ' + stdout);
      console.log('stderr: ' + stderr);
      if (error != null) {
          console.log('tesseract exec error: ' + error);
      }
      fs.readFile(outputPath + '.txt', function (err, data) {
          if (err) throw err;
          console.log('tesseract output: ' + data);
      }); 
      
    callback(imagePath, outputPath);
  });

};

exports.execute = execute;
