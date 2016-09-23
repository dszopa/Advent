var express = require('express');
var webpack = require('webpack');
var config = require('./webpack.config');

var app = express();

const port = 8080;

app.get('/*', function(req, res) {
    res.send("temp");
});

app.listen(port, 'localhost', (err) => {
    if (err) {
        console.log(err);
        return;
    }

    console.log('Listening at http://localhost:' + port);
});