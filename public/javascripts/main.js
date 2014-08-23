var buildContainer = function() {
	$('<div class="result-container">').appendTo($('body'));
	var container = $('.result-container');
	$('<div class="header">').appendTo(container);
	$('<h1>').text('WikiPlus').appendTo($('.header'));
	$('<div class="slider">').appendTo(container);
	$('<div class="slide active-slide">').appendTo($('.slider'));
	$('<div>').addClass('slider-nav').appendTo(container);
	
};

var addMainWidget = function() {
	$('<div class="wikipedia">').appendTo($('.active-slide'));
	$('<div class="container">').appendTo($('.wikipedia'));
	$('<div class="row">').appendTo($('.container'));
	$('<div class="slide-copy col-xs-5">').appendTo($('.row'));
	$('.slide-copy').append($('.mw-body'));
};

var addWidgets = function() {
	$('<div class="item test">').appendTo($('.carousel-inner'));
	$('<div class="slide clock">').appendTo($('.slider'));
	$('<div class="container" id="clock">').appendTo($('.clock'));
	buildClockWidget();
	
};

function buildClockWidget(){
	var parent = $('#clock');
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

$(document).ready(function(){
	buildContainer();
	addMainWidget();
	addWidgets();
});


