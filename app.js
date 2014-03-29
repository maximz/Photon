var express = require('express');
var app = express();

var tesseract = require('./server_methods/tesseract_call.js').execute('image.png');

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});
