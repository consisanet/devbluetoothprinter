var exec = require('cordova/exec');

module.exports = {
   status: function (fnSuccess, fnError) {
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "status", []);
   },
   list: function(fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "list", []);
   },
   connect: function(name, fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "connect", [name]);
   },
   disconnect: function(fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "disconnect", []);
   },
   print: function(str, fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "print", [str]);
   },
   printText: function(str, fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "printText", [str]);
   },
   printImage: function(str, fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "printImage", [str]);
   },
   printPOSCommand: function(str, fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "printPOSCommand", [str]);
   },
   isConnected: function(fnSuccess, fnError){
      exec(fnSuccess, fnError, "DevBluetoothPrinter", "isConnected", []);
   },
};