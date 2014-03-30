var exec = require('child_process').exec, child;
var fs = require('fs');

// Call the tesseract method
var execute = function(imageDir, outputDir, callback) {
  child = exec('tesseract ' + imageDir + ' ' + outputDir + ' -psm 1 photonenglish', function (error, stdout, stderr) {
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
