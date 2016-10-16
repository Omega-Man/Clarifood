
var mysql = require('mysql');

var input = process.argv;
var connection = mysql.createConnection({
    host: 'us-cdbr-azure-west-b.cleardb.com',
    user: 'b5cae1279ec02c',
    password: 'c0f7c884',
    database: 'acsm_d0d291057d61caa'
});

connection.connect();

var test = 'INSERT INTO ' + input[2] + ' (name) VALUES ("' + input[3] + '")'
console.log(test);
connection.query(test, function(err, row, fields){
    if(err){
	throw err;
    } else {
	console.log("added");
	connection.end();
    }
});


