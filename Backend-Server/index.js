var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var database = require('./database');

// body parsing
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

var connection = database.getDatabase();
connection.connect();

app.post('/recipes', function (req, res) {
  var ingredients = req.body.ingredients;
  console.log("POST /recipes: " + ingredients);
  database.getRecipes(ingredients, connection, function(recipes) {
    console.log("sending to client: " + recipes);
    res.json(recipes);
  });
});

app.get('/ingredients', function (req, res) {
  console.log("GET /ingredients");
  database.getIngredients(connection, function(ingredients) {
    console.log("sending to client: " + ingredients);
    res.json(ingredients);
  });
});

// input command line to end server
process.stdin.on('data', function(text) {
  console.log("ending connection");
  connection.end();
});

app.listen(80, function () {
  console.log('Backend app listening on port 80!');
});
