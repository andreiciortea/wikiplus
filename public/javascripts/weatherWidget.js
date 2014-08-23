function buildWeatherWidget(){
	var outerDiv = $('#LocalWeather');
	var weatherJson;

	for (var i=0; i<jsonData.length;i++) {
		var elmt = jsonData[i];
		if(elmt.name == 'LocalWeather'){
			weatherJson = elmt.data;
		}
	}
	
	if(weatherJson!=undefined && weatherJson!=null){
		var container = $('<div>').addClass('weatherWidgetContainer').prop('id', 'weatherWidgetContainer').appendTo(outerDiv);
		var left = $('<div>').addClass('weatherLeft').appendTo(container);
		$('<div>').addClass('icon').append($('<img>',{id:'weatherIcon',src:weatherJson.icon})).appendTo(left);
		$('<div>').addClass('temp').text(Math.round(weatherJson.temp*100)/100 + '°C').appendTo(left);
		
		var right = $('<div>').addClass('weatherRight').appendTo(container);
		$('<div>').addClass('descriptionTxt').text('Current Weather in '+weatherJson.city).appendTo(right);
		$('<div>').addClass('weatherTxt').text(weatherJson.description).appendTo(right);
	}
}
