var request = require('request');
var cheerio = require('cheerio');

var word = new Array();
for (var i = 0; i < 100; i++) {
    word[i] = 'horses';
}

var url = 'http://www.google.com/search?q=';

var time1 = new Date().getTime();

for (var i = 0; i < word.length; i++) {

    request(url + word[i], function(err, resp, body) {
	if (err) throw err;
	var $ = cheerio.load(body);
	console.log($('#resultStats').text());
	var time2 = new Date().getTime();
	console.log('Time taken: ' + (time2 - time1));
    });

}