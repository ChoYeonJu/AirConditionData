$(document).ready(function () {

	var elem= $('.menubar ul li').eq(1).children().attr('class');
	var status = '';
	var map = new AirMap({
		target: 'map',
		view: new ol.View({
			center: ol.proj.fromLonLat([127.86438, 36.009117]),
			zoom: 7,
			extent: [
				12678546.51713,
				3734188.8033424,
				15854453.760059,
				5414661.8558898,
			],
			minZoom: 1,
			maxZoom: 15
		}),
		GEOSERVER_URL: 'http://localhost:8088/geoserver/air',
	});
	map.addLayer(initVectorLayer());


	/* canvas 차트 부분 text */
	$('.chart_text .time').text(new Date().toLocaleString());
	$('.chart_text .airState').text('미세먼지 보통');

	$('.menuBtn').click(function () {
		if ($('.menubar').offset().left == -150) {
			$('.menubar').animate({ left: '0px' });
		} else if ($('.menubar').attr('left', '0')) {
			$('.menubar').animate({ left: '-150px' });
		}

		$('.menubar ul li').eq(0).unbind('hover');
	});

	$('.menubar input').click(function(){
		var elementName = $(this).attr('class');
		elem = elementName;
		map.getLayers().forEach(layer =>{
			if(layer == undefined) return true; 
			if(layer.getClassName() == 'ol-layer'){
				map.removeLayer(layer);
			}
		});
		if(isChecked()[0] == true){
			getFilteredLayer(status);
		}else{
			map.addLayer(vectorLayer(elementName));
		}
		changeTextLegend(elementName);
	});

	$('.map_selection input').click(function(){
		var id = $(this).attr('id');
		$('.map_selection input').each(function(index, item){
			if($(this).attr('id') != id && $(this).prop('checked') == true){
				$(this).prop('checked', false);
			}
		});
		var tileName = setTileLayerClassName();
		// map.removeLayer(map.getTileLayer(tileName));
		map.getLayers().forEach(layer => {
			if(layer == undefined) return true;
			var name = layer.getClassName();
			if(name == tileName){
				map.removeLayer(layer);
			}
		});
		if(tileName == 'stamen') {
			map.getLayers().forEach(layer => {
				if(layer == undefined) return true;
				var name = layer.getClassName();
				if(name == tileName){
					map.removeLayer(map.stamen1);
					map.removeLayer(map.stamen2);
				}
			});
		}
		map.addLayer(map.getTileLayer(id));
		if(id == 'stamen') map.addLayer(map.stamen2);


		if(isChecked()[0] == true){
			getFilteredLayer(status);
		}else{
			map.addLayer(vectorLayer(elem));
		}
	});

	$('.data_selection input').click(function(){
		console.log(elem);
		map.removeLayer(map.getLayers().forEach(layer => {
			if(layer == undefined) return true;
			if(layer.getClassName() == 'ol-layer'){
				map.removeLayer(layer);
			}
		}));
		var state = $(this).attr('class');
		status = state;
		$('.data_selection ul li').each(function(){
			if($(this).children('input').attr('class') != state && $(this).children('input').prop('checked') == true){
				$(this).children('input').prop('checked', false);
			}
		});

		getFilteredLayer(state);
	});


	function getFilteredLayer(state){
		map.vectorSource.clear();
		var source = map.setFilteredData(elem, state);
		map.addLayer(changedSourceVectorLayer(elem, source));
	}

	function isChecked(){
		var result = [];
		var isChecked = false;
		var className = '';
		$('.data_selection ul li').each(function(){
			if($(this).children('input').prop('checked') == true){
				isChecked = true;
				className = $(this).children('input').attr('class');
				result = [isChecked, className];
				return false;
			}else{
				result = [false, ''];
				return true;
			}
		});
		return result;
	}

	function getTileLayer() {
		var something;
		var tileLayer;
		tileLayer = map.getLayers().forEach(layer => {
			var name = layer.getClassName()
			if (name == 'osm' || name == 'arc' || name == 'vworld' || name == 'stamen') {
				something = layer;
				return something;
			} else {
				return something;
			}
		});
		return tileLayer;
	}
	function setTileLayerClassName() {
		var className = '';
		map.getLayers().forEach(layer => {
			var name = layer.getClassName();		//layer.className_ = "ol-layer"
			if (name == 'osm' || name == 'arc' || name == 'vworld' || name == 'stamen') {
				className = layer.getClassName();
				return true;
			} else {
				return false;
			}
		});
		return className;
	}

	/* 농도 확인 범례 텍스트 변경 */
	function changeTextLegend(elementName){
		var li = $('.data_selection ul li');
		if(elementName == 'pm2_5'){
			li.eq(0).children().text("좋음 ( 0 ~ 15)");
			li.eq(1).children().text("보통 (16 ~ 35)");
			li.eq(2).children().text("나쁨 (36 ~ 75)");
			li.eq(3).children().text("최악 (76 ~   )");
			li.eq(4).children().text("데이터 없음");
		}else if(elementName == 'pm10'){
			li.eq(0).children().text("좋음 ( 0 ~ 30 )");
			li.eq(1).children().text("보통 (31 ~ 80 )");
			li.eq(2).children().text("나쁨 (81 ~ 150)");
			li.eq(3).children().text("최악 (151 ~   )");
			li.eq(4).children().text("데이터 없음");
		}else if(elementName == 'o3'){
			li.eq(0).children().text("좋음 ( 0 ~ 0.03)");
			li.eq(1).children().text("보통 (0.031 ~ 0.09)");
			li.eq(2).children().text("나쁨 (0.091 ~ 0.15)");
			li.eq(3).children().text("최악 (0.151 ~     )");
			li.eq(4).children().text("데이터 없음");
		}else if(elementName == 'no2'){
			li.eq(0).children().text("좋음 ( 0 ~ 0.03)");
			li.eq(1).children().text("보통 (0.031 ~ 0.06)");
			li.eq(2).children().text("나쁨 (0.061 ~ 0.2)");
			li.eq(3).children().text("최악 (0.21 ~   )");
			li.eq(4).children().text("데이터 없음");
		}else if(elementName == 'co'){
			li.eq(0).children().text("좋음 ( 0 ~ 15)");
			li.eq(1).children().text("보통 (16 ~ 35)");
			li.eq(2).children().text("나쁨 (36 ~ 75)");
			li.eq(3).children().text("최악 (76 ~   )");
			li.eq(4).children().text("데이터 없음");
		}else if(elementName == 'so2'){
			li.eq(0).children().text("좋음 ( 0 ~ 2.0)");
			li.eq(1).children().text("보통 (2.01 ~ 9.0)");
			li.eq(2).children().text("나쁨 (9.01 ~ 15.0)");
			li.eq(3).children().text("최악 (15.01 ~   )");
			li.eq(4).children().text("데이터 없음");
		}
	}

	function vectorLayer(element){
		var layer = new ol.layer.Vector({
			source : map.vectorSource,
			style : function(feature){
				var value = feature.values_[element];
				var state = map.dataLegend(element, value)[0];
				if(state == 'good'){
					return map.goodIconStyle;
				}else if (state == 'notbad') {
					return map.notBadIconStyle;
				}else if (state == 'bad') {
					return map.badIconStyle;
				}else if (state == 'badly') {
					return map.badlyIconStyle;
				}else{
					return map.noDataIconStyle;
				}	
			},
		});
		return layer;
	}

		function changedSourceVectorLayer(element, source){
		var layer = new ol.layer.Vector({
			source : source,
			style : function(feature){
				var value = feature.values_[element];
				var state = map.dataLegend(element, value)[0];
				if(state == 'good'){
					return map.goodIconStyle;
				}else if (state == 'notbad') {
					return map.notBadIconStyle;
				}else if (state == 'bad') {
					return map.badIconStyle;
				}else if (state == 'badly') {
					return map.badlyIconStyle;
				}else{
					return map.noDataIconStyle;
				}	
			},
		});
		return layer;
	}

	function initVectorLayer(){
		var layer = new ol.layer.Vector({
			source : map.vectorSource,
			style : function(feature){
				
				var element = $('.menubar ul li').eq(1).children().attr('class');
				var value = feature.values_[element];
				var state = map.dataLegend(element, value)[0];
				if(state == 'good'){
					return map.goodIconStyle;
				}else if (state == 'notbad') {
					return map.notBadIconStyle;
				}else if (state == 'bad') {
					return map.badIconStyle;
				}else if (state == 'badly') {
					return map.badlyIconStyle;
				}else{
					return map.noDataIconStyle;
				}	
			},
		});
		return layer;
	}

	// 깜빡깜빡 하는 기능
	function intervalButton() {
		setInterval(function () {
			$(".menuBtn p").attr("color", "antiquewhite");
		}, 3000)
	}

	intervalButton();

	/* 차트 범례 그리기 */
	drawLengendChart();

	/* 막대 그래프 그리기 */
	drawAverageChart()

});
