function buildWeatherWidget(){
	var outerDiv = $('#weather');
	var weatherJson;

	var jsonData = [{"name":"LocalWeather","data":{"city":"Lyon","icon":"http://upload.wikimedia.org/wikipedia/commons/thumb/d/df/Lion_place_Sathonay_Lyon.JPG/220px-Lion_place_Sathonay_Lyon.JPG","description":"Sky is clear","temp":"298.40"}}];

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
		$('<div>').addClass('temp').text(weatherJson.temp).appendTo(left);
		
		var right = $('<div>').addClass('weatherRight').appendTo(container);
		$('<div>').addClass('descriptionTxt').text('Current Weather in '+weatherJson.city).appendTo(right);
		$('<div>').addClass('weatherTxt').text(weatherJson.description).appendTo(right);
	}
}