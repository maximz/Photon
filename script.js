// construct the array with all the words of the dictionary
var dictArray;
var fs=require('fs');
var dict = new Object();
	var data = fs.readFileSync('./dictionary.txt');
	dictArray = data.toString().split('\n');
	
	for(var i=0; i<dictArray.length; i++)
		dict[dictArray[i]]=true;
		
var sentenceArray;
var fs=require('fs');
var sentence = new Object();
	var data = fs.readFileSync('./wiki_100k.txt');
	sentenceArray = data.toString().split(' ');
	
	for(var i=0; i<sentenceArray.length; i++)
		sentence[sentenceArray[i]]=true;

// split the input file into words
var findWord = function(input){
	var substring="";
	
	for(var i = 0; i < input.length - 1; i++){		
		if (i==0){
			for(var k = 0; k < input.length; k++)
			{
				if(input[k] != " ")
					substring += input[k];
				else{
					i = k;
			//enter logic here
					break;
				}
			}
		}
		else
		{
			if(input[i+1]!=" "){
				for(var k = i + 1; k < input.length; k++)
				{
					if(input[k] != " ")
						substring += input[k];
					else{
						i = k;
			  //enter logic here
						break;
					}
				}
			}
			if(k == input.length)
				break;
		}
	}
    return substring
};

// remove noise
var filter = function(word){
	var re = new RegExp(/[A-Za-z0-9_.()?!]/); // contains any letters / numbers / spec. def. charac.
	
	if(dict[word])
		return;
	else{
		var copy = word;
		var j =0;
			while(j<word.length){
				//if there are no letters and no spec. defined charac.

				if(!re.test(copy.substring(0, 3))){
					copy = copy.substring(3, word.length);
					j+=3;
				}
				else{
					copy=copy.substring(1, copy.length)+copy[0];
					j++;
				}
			}
		console.log("final "+copy);

	}
};