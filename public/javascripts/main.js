var buildContainer = function() {
	$('<div class="result-container">').appendTo($('body'));
	var container = $('.result-container');
	$('<div class="header">').appendTo(container);
	$('<h1>').text('WikiPlus').appendTo($('.header'));
	$('<div class="carousel-slide">').appendTo(container);
	$('<div class="carousel-inner">').appendTo($('.carousel-slide'));
};

var addMainWidget = function() {
	$('<div class="item active wikipedia">').appendTo($('.carousel-inner'));
	$('.wikipedia').append($('.mw-body'));
};

var addWidgets = function() {
	$('<div class="item test">').appendTo($('.carousel-inner'));
	
};

$(document).ready(function(){
	buildContainer();
	addMainWidget();
});
