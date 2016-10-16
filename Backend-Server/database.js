var mysql = require('mysql');
var fs = require('fs');

function getDatabase() {

  var login = fs.readFileSync('config/login.json');
  var config = JSON.parse(login);
  
  var connection = mysql.createConnection(config['Database']);
  return connection;
}

function getIngredients(connection, callback) {
  connection.query('SELECT name FROM ingredients', function(err, rows, fields) {
    callback(rows.map(function(v) {return v.name}));
  });
}

function getRecipes(ingredients, connection, callback) {

  getIids(connection, ingredients, function(iids) {

    getRids(connection, function(rows) {
      var result = [];
      var kCount = 0;
      for (var k = 0; k < rows.length; k++) {
        getAssociationsIids(connection, rows[k], function(assocationsIids, currentRid) {
          kCount++;
          var count = 0;
          // what we need
          for (var i = 0; i < assocationsIids.length; i++) {
            var found = false;
            // what we have
            for (var j = 0; j < iids.length; j++) {
              if (assocationsIids[i] === iids[j]) {
                count++;
                found = true;
              } 
            }
            if (!found) {
              break;
            }
          }
          if (count !== 0 && count === assocationsIids.length) {
            // add rid to the thing we return
            result.push(currentRid.name);
          }

          if (kCount === rows.length) {
            callback(result);
          }
        });
      }
    });

  });
}

function getAssociationsIids(connection, rid, callback) {
  var query = 'SELECT * FROM association WHERE rid=' + rid.rid;
  connection.query(query, function(err, rows, fields) {
    if (err) {
      console.log("err: " + err);
      throw err;
    }
    var iids = rows.map(function(value) {
      return value.iid;
    });
    callback(iids, rid);
  });
}

function getRids(connection, callback) {
  var query = 'SELECT * FROM recipes';
  connection.query(query, function(err, rows, fields) {
    // var rids = rows.map(function(value) {
    //   return value.rid;
    // });
    // callback(rids);
    callback(rows);
  });
    
}

// returns a list of recipe names given a list of ingredients
function getIids(connection, ingredients, callback) {

  var iids = [];
  var count = 0;
  for (var i = 0; i < ingredients.length; i++) {
    var ingredient = ingredients[i];
    var query = 'SELECT * FROM ingredients WHERE name=\'' + ingredient + '\' LIMIT 1';
    connection.query(query, function(err, rows, fields) {
      if (err) {
        throw err;
      }
      if (rows.length != 0) {
        iids.push(rows[0].iid);
      }
      count++;

      if (count === ingredients.length) {
        callback(iids);
      }
    });
  }
    
}

module.exports.getRecipes = getRecipes;
module.exports.getIngredients = getIngredients;
module.exports.getDatabase = getDatabase;
