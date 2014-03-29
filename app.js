var express = require('express');
var fs = require('fs');
var app = express();
var exec = require('child_process').exec, child;

var server = app.listen(3000, function() {
    console.log("Listening to port %d", server.address().port);
});

app.use(express.bodyParser({ keepExtensions: true, uploadDir: './images'}));

// I'm not entirely sure when this is called but it seems to specify the type of data to be submitted (does app.push override this?)
app.get('/', function(req, res){
    res.send('<form method="post" enctype="multipart/form-data">'
    + '<p>Image: <input type="file" name="image" /></p>'
    + '<p><input type="submit" value="Upload" /></p>'
	     + '</form>');
});

// Handle requests (assumes that the request comes with an image file)
app.get('/', function(req, res) {
    
    var time1 = new Date().getTime();
    var outputPath = './output/out' + time1;

    // Should we refine images before giving them to tesseract?

    // Run tesseract, name output based on time started
    child = exec('tesseract image.gif ' + outputPath,
//    child = exec('tesseract ' + req.files.image.path + ' ' + outputPath, 
		 function (error, stdout, stderr) {
	console.log('stdout: ' + stdout);
	console.log('stderr: ' + stderr);
	if (error != null) {
	    console.log('exec error: ' + error);
	}

	// Used to measure the amount of time tesseract takes
        var time2 = new Date().getTime();
        console.log('Time taken (ms): ' + (time2 - time1));

        // Print the output to ensure it is correct
        var output = fs.readFileSync(outputPath + '.txt');
	console.log('tesseract output: ' + output);

	// Do some nifty error-checking and markov stuff

	// Push the text back to device
	res.send(outputPath + '.txt');

	// Remove data
        fs.unlink(outputPath + '.txt', function(err) {
    	    if (err) throw err;
	    console.log('Successfully deleted ' + outputPath + '.txt');
        });
	fs.unlink(req.files.image.path, function(err) {
	    if (err) throw err;
	    console.log('Successfully deleted image ' + time1);
	});
    }); 
});

app.get('/', function(req, res) {
    res.send("Do stuff");
});