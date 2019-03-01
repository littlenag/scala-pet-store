//window.i18next = require("i18next");
//window.i18nextXHRBackend = require("i18next-xhr-backend");
//window.i18nextBrowserLanguageDetector = require("i18next-browser-languagedetector");

//window.moment = require("moment");
//window.numeral = require("numeral");

require("bootstrap");
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
//import 'bootstrap/dist/js/bootstrap.js';

window['React'] = require('react');
window['PropTypes'] = require('prop-types');
window.React.PropTypes = window.PropTypes;
window['ReactDOM'] = require('react-dom');

/*
(function(global){
    global['mui'] = require("material-ui");
    global.mui.Styles = require("material-ui/styles");
    global.mui.SvgIcons = require('material-ui/SvgIcon');
    global.mui.Icons = require("material-ui-icons");
    global.mui.ChipInput = require("material-ui-chip-input").default;
    global.mui.createComponentWithStyle = function (styleCreator, options, componentClass) {
        var t = global.mui.Styles.withStyles(styleCreator, options)(componentClass);
        console.log(t);
        return t;
    };
})(window);
*/

window['logger'] = (function () {
    var log4javascript = require('log4javascript');
    var consoleAppender = new log4javascript.BrowserConsoleAppender();
    var logger = log4javascript.getDefaultLogger();
    logger.removeAllAppenders();
    logger.addAppender(consoleAppender);
    logger.setLevel(log4javascript.Level.ALL);
    return logger;
})();
