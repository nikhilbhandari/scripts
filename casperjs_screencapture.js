phantom.casperTest = true;

var casper = require('casper').create();
var x = require('casper').selectXPath;
var firstUrl;
var xPath = "//*[text()=\""+casper.cli.get(1)+"\"]"
casper.options.viewportSize = {width: 1920, height: 1080};
casper.start(casper.cli.get(0));

casper.then(function() {
    this.test.assertExists({
        type: 'xpath',
        path: xPath
    }, "Found Element");
});

casper.then(function() {
    firstUrl = this.getCurrentUrl()
});

casper.thenClick(x(xPath), function() {
    console.log("Clicked!");
});

casper.waitFor(function check() {
	this.wait(5000);
    return true;
}, function then() {
    console.log("Captured Screenshot at "+casper.cli.get(2));
    this.capture(casper.cli.get(2));
});




casper.run();
