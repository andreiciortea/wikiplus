$(document).ready(function(){
				
	var $hands = $('#liveclock div.hand')

	window.requestAnimationFrame = window.requestAnimationFrame
								   || window.mozRequestAnimationFrame
								   || window.webkitRequestAnimationFrame
								   || window.msRequestAnimationFrame
								   || function(f){setTimeout(f, 60)}


	function updateclock(){
		var timeZone = "Europe/Moscow";
		
		var date = new Date();
		var time = moment(date.toISOString());
		
		// Converting the time using the timeZone
		var timeInfo = time.tz(timeZone);
		var localTime = timeInfo.format('h:mm:ss a');
		
		var curdate = new Date(timeInfo.format('YYYY-MM-DD[T]HH:mm:ss'));
		var hour_as_degree = ( curdate.getHours() + curdate.getMinutes()/60 ) / 12 * 360
		var minute_as_degree = curdate.getMinutes() / 60 * 360
		var second_as_degree = ( curdate.getSeconds() + curdate.getMilliseconds()/1000 ) /60 * 360
		$hands.filter('.hour').css({transform: 'rotate(' + hour_as_degree + 'deg)' })
		$hands.filter('.minute').css({transform: 'rotate(' + minute_as_degree + 'deg)' })
		$hands.filter('.second').css({transform: 'rotate(' + second_as_degree + 'deg)' })
		requestAnimationFrame(updateclock)
	}

	requestAnimationFrame(updateclock)
});