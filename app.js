var express = require('express');
var fs = require('fs');
var app = express();

var exec = require('child_process').exec, child;

var time = new Date().getTime();

child = exec('tesseract image.gif ./output/out', function (error, stdout, stderr) {
    console.log('stdout: ' + stdout);
    console.log('stderr: ' + stderr);
    if (error != null) {
	console.log('exec error: ' + error);
    }
    var time2 = new Date().getTime();
    console.log(time2 - time1);
    fs.readFile('./output/out.txt', function (err, data) {
	if (err) throw err;
	console.log('tesseract output: ' + data);
    }); 
});

app.get('/hello.txt', function(req, res) {

    res.send('Hello World');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});