var tesseract = require('../tesseract_call.js').execute;
var sanitize = require('../sanitize_picture.js').execute;

if (process.argv.length < 3) {
  throw "Invalid number of arguments";
}

var fileName = process.argv[2];
var middleFileName = "pre" + fileName;
var textOutput = middleFileName.split(".")[0] + "out";

sanitize(
