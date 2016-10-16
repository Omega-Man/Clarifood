var http = require('http');

var options = {
  host: 'localhost',
  path: '/ingredients'
};

callback = function(response) {
  var str = '';

  //another chunk of data has been recieved, so append it to `str`
  response.on('data', function (chunk) {
    str += chunk;
  });

  //the whole response has been recieved, so we just print it out here
  response.on('end', function () {
    console.log(str);
  });
}

function ping() {
  http.request(options, callback).end();
  console.log("heartbeat");
}

// do this every 10 seconds
setInterval(ping, 15000);