$(window).bind("load", function() {
				
	var $hands = $('#liveclock div.hand')

	window.requestAnimationFrame = window.requestAnimationFrame
								   || window.mozRequestAnimationFrame
								   || window.webkitRequestAnimationFrame
								   || window.msRequestAnimationFrame
								   || function(f){setTimeout(f, 60)}


	function updateclock(){
		var timeZone;
		// Getting the timezone from the server gathered data
		for(var i=0; i<jsonData.length; i++){
			var elmt = jsonData[i];
			if(elmt.name == 'LocalTime'){
				timeZone = elmt.data.timezone;
			}
		}
		
		if( timeZone != undefined && timeZone != null ) {
			
			var date = new Date();
			var time = moment(date.toISOString());
			
			// Converting the time using the timeZone
			var timeInfo = time.tz(timeZone);
			var localTime = timeInfo.format('h:mm:ss a');
			
			var curdate = new Date(timeInfo.format('YYYY-MM-DD[T]HH:mm:ss'));
			
			var hour_as_degree = ( parseInt(timeInfo.format('hh')) + parseInt(timeInfo.format('mm'))/60 ) / 12 * 360
			var minute_as_degree = parseInt(timeInfo.format('mm')) / 60 * 360
			var second_as_degree = ( parseInt(timeInfo.format('ss')) + parseInt(timeInfo.format('SSS'))/1000 ) /60 * 360
			$hands.filter('.hour').css({transform: 'rotate(' + hour_as_degree + 'deg)' })
			$hands.filter('.minute').css({transform: 'rotate(' + minute_as_degree + 'deg)' })
			$hands.filter('.second').css({transform: 'rotate(' + second_as_degree + 'deg)' })
			requestAnimationFrame(updateclock)
		}
	}

	requestAnimationFrame(updateclock)
});

function buildClockWidget(){
	var parent = $('#LocalTime');
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
