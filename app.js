var express = require('express');
var app = express();

// Takes one parameter: the word
var tesseract = require('./server_methods/tesseract_call.js').execute;

app.use(express.static(__dirname + '/public'));

app.get('/', function(req, res) {
  res.sendfile("index.html"); 
});

app.post('/web', function(req, res) {
  res.send(format('\nuploaded %s (%d Kb) to %s as %s'
      , req.files.image.name
      , req.files.image.size / 1024 | 0
      , req.files.image.path
      , req.body.title));
}

var server = app.listen(3000, function() {
  console.log('Listening on port %d', server.address().port);
});
