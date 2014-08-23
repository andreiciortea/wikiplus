var buildContainer = function() {
	$('<div class="result-container">').appendTo($('body'));
	var container = $('.result-container');
	$('<div>').addClass('header').appendTo(container);
	$('<h1>').text('WikiPlus').appendTo($('.header'));
	$('<div>').addClass('wiki-container').addClass('col-md-9').appendTo(container);
	$('<div>').addClass('widget-container').addClass('col-md-3').appendTo(container);
	$('<div>').addClass('slider-nav').appendTo($('.widget-container'));
	$('<a href="#">').addClass("arrow-prev").appendTo($('.slider-nav'));
	$('<img src="http://s3.amazonaws.com/codecademy-content/courses/ltp2/img/flipboard/arrow-prev.png">').appendTo($('.arrow-prev'));
	$('<ul>').addClass('slider-dots').appendTo($('.slider-nav'));
	$('<a>').addClass("arrow-next").appendTo($('.slider-nav'));
	$('<img src="http://s3.amazonaws.com/codecademy-content/courses/ltp2/img/flipboard/arrow-next.png">').appendTo($('.arrow-next'));
	$('<div>').addClass('slider').appendTo($('.widget-container'));

	$('.arrow-next').click(function(){
        var currentSlide, nextSlide;
        currentSlide = $('.active-slide');
        nextSlide = currentSlide.next();
        if(nextSlide.length == 0) {
            nextSlide = $('.slide').first();
        }
        currentSlide.fadeOut(600);
        currentSlide.removeClass('active-slide');
        nextSlide.fadeIn(600);
        nextSlide.addClass('active-slide');
        
        var currentDot = $('.active-dot');
        var nextDot = currentDot.next();
        if(nextDot.length == 0) {
            nextDot = $('.dot').first();
        }
        currentDot.removeClass('active-dot');
        nextDot.addClass('active-dot');
    });

    $('.arrow-prev').click(function(){
        var currentSlide, prevSlide;
        currentSlide = $('.active-slide');
        prevSlide = currentSlide.prev();
        if(prevSlide.length == 0) {
            prevSlide = $('.slide').last();
        }
        currentSlide.fadeOut(600);
        currentSlide.removeClass('active-slide');
        prevSlide.fadeIn(600);
        prevSlide.addClass('active-slide');
        
        var currentDot = $('.active-dot');
        var prevDot = currentDot.prev();
        if(prevDot.length == 0) {
            prevDot = $('.dot').last();
        }
        currentDot.removeClass('active-dot');
        prevDot.addClass('active-dot');
    });
	
};


var addMainWidget = function() {
	$('<div class="wikipedia">').appendTo($('.wiki-container'));
	$('.wikipedia').append($('.mw-body'));
	
};

var addWidgets = function() {
	var bulletNumber = jsonData.length;
	if (bulletNumber) {
		$('<li>').addClass('dot active-dot').html('&bull;').appendTo($('.slider-dots'));
		var sliderDots = $('.slider-dots');
		for (var i = 1; i < bulletNumber; i++) {
			$('<li>').addClass('dot').html('&bull;').appendTo(sliderDots);
		}

		for (var i = 0; i < bulletNumber; i++) {
			var currentValue = jsonData[i].name;
			$('<div class="slide">').addClass(currentValue).appendTo($('.slider'));
			$('<div>').addClass('container').prop('id', currentValue).appendTo('.'+currentValue);
		}

		$('.' + jsonData[0].name).addClass('active-slide');
	}

	
	$('<div class="item test">').appendTo($('.carousel-inner'));
	
	
	buildClockWidget();
	buildWeatherWidget();
	
};

$(document).ready(function(){
	buildContainer();
	addMainWidget();
	addWidgets();

	
    
    
});


