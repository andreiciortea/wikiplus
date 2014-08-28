$(document).ready(function(){
	setInterval(function(){
		updateTime();
	},1000);
});

function updateTime(){
	// TODO: use the json response of the server
	var timeZone = "Europe/Moscow";
	
	var date = new Date();
	var time = moment(date.toISOString());
	
	// Converting the time using the timeZone
	var localTime = time.tz(timeZone).format('h:mm:ss a');
	
	$('#clockDiv').text(localTime);
	
};