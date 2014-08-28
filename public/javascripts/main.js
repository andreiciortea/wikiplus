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

function buildClockWidget(){
	var parent = $('#PARENT_ELEMENT_ID');
	var container = $('<div>').addClass('outer_face').prop('id', 'liveclock').appendTo(parent);
	$('<div>').addClass('marker oneseven').appendTo(container);
	$('<div>').addClass('marker twoeight').appendTo(container);
	$('<div>').addClass('marker fourten').appendTo(container);
	$('<div>').addClass('marker fiveeleven').appendTo(container);
	
	var innerContainer = $('<div>').addClass('inner_face').appendTo(container);
	$('<div>').addClass('hand hour').appendTo(innerContainer);
	$('<div>').addClass('hand minute').appendTo(innerContainer);
	$('<div>').addClass('hand second').appendTo(innerContainer);
}
