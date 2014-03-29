var express = require('express');
var app = express();
var fs = require('fs');

var tesseract = require('./server_methods/tesseract_call.js').execute;
app.use(express.bodyParser({ keepExtensions: true, uploadDir: './images'}));
app.use(express.static(__dirname + '/public'));

app.post('/mobile', function(req, res) {
  console.log(req);
});

// Handle requests (assumes that the request comes with an image file)
app.post('/web', function(req, res) {
    console.log('req: ' + req.files);
  var time1 = new Date().getTime();
  var outputPath = './output/out' + time1;

  // Should we refine images before giving them to tesseract?
  // Could be a bonus project

  // Run tesseract, name output based on time started
//    child = exec('tesseract ' + req.files.image.path + ' ' + outputPath, 
//             function (error, stdout, stderr) {
//    console.log('stdout: ' + stdout);
//    console.log('stderr: ' + stderr);
//    if (error != null) {
//        console.log('exec error: ' + error);
//    }
//
    tesseract(req.files.image.path, outputPath, function(imagePath, outputPath) {
      // Used to measure the amount of time tesseract takes
      var time2 = new Date().getTime();
      console.log('Time taken (ms): ' + (time2 - time1));

      // Print the output to ensure it is correct
      // var output = fs.readFileSync(outputPath + '.txt');
      // console.log('tesseract output: ' + output);

      // Do some nifty error-checking and markov stuff

	fs.readFile(outputPath + '.txt', 'utf8', function(err, data) {
	    if (err) throw err;
	    res.setHeader('Content-Type', 'application/json');
	    res.end(JSON.stringify({
		"text": data}));
	});
	
        // Remove data
        fs.unlink(outputPath + '.txt', function(err) {
            if (err) throw err;
            console.log('Successfully deleted ' + outputPath + '.txt');
        });

        fs.unlink(imagePath, function(err) {
            if (err) throw err;
            console.log('Successfully deleted image ' + time1);
        });
    });

});

var server = app.listen(3000, function() {
    console.log("Listening to port %d", server.address().port);
});

app.get('/', function(req, res) {
  res.sendfile("index.html"); 
});
