var request = require('request');
var cheerio = require('cheerio');

var url = 'http://www.google.com/search?q=horses';

request(url, function(err, resp, body) {
    if (err) throw err;
    var page = cheerio.load(body);
    console.log(page);
});