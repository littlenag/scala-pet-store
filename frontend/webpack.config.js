'use strict';

var path = require("path");
var webpack = require('webpack');

//module.exports = require('./scalajs.webpack.config');

module.exports = {
  entry: {
    global: path.resolve(__dirname, './global.js')
  },
  output: {
    path: path.resolve(__dirname, "./build"),
    //filename: '[name].js'
    filename: 'bundle.min.js'
  },

  devtool: "source-map",

  plugins: [
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery'
    })
  ]
};