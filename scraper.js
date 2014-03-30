var jsdom = require('jsdom');

jsdom.env('http://www.reuters.com', function(err, window) {
    var post = window.document.getElementsByClassName('topStory')[0].textContent;
    console.log(post);
});