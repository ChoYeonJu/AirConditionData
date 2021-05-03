var AirMap = function(options){
  this.GEOSERVER_URL = options.GEOSERVER_URL;

  ol.Map.call(this, options);

  // 배경 레이어
  this.osm = new ol.layer.Tile({
    source: new ol.source.OSM(),
    className : 'osm'
  });

  /* vworld 타일 레이어 */
  this.vworld = new ol.layer.Tile({
      source: new ol.source.XYZ({
      url: 'http://xdworld.vworld.kr:8080/2d/Base/202002/{z}/{x}/{y}.png'
      }),
      className : 'vworld'
  })
  /* osm stamen 타일 레이어 */
  this.stamen1 = new ol.layer.Tile({
    source: new ol.source.Stamen({
      layer: 'watercolor',
    }),
    className : 'stamen'
  });
  this.stamen2 = new ol.layer.Tile({
    source: new ol.source.Stamen({
      layer: 'terrain-labels',
    }),
    className : 'stamen'
  });
  /* arc gis 타일 레이어 */
  this.arc = new ol.layer.Tile({
      source: new ol.source.XYZ({
        attributions:
          'Tiles © <a href="https://services.arcgisonline.com/ArcGIS/' +
          'rest/services/World_Topo_Map/MapServer">ArcGIS</a>',
        url:
          'https://server.arcgisonline.com/ArcGIS/rest/services/' +
          'World_Topo_Map/MapServer/tile/{z}/{y}/{x}',
      }),
      className : 'arc'
  });

  this.tiff = new ol.layer.Image({

    source : new ol.source.ImageWMS({
      url : 'http://localhost:8088/geoserver/air/wms?'
      +'service=WMS&version=1.1.0&request=GetMap&layers=air:itdata&'
      +'bbox=126.4345339,34.568164229,129.307704884,38.10922145&'
      +'width=623&height=768&srs=EPSG:4326&styles=&format=application/openlayers',

    })
  }) 
  
  this.addLayer(this.osm);

  this.bbox = [126.12589387679935, 33.22788686430661, 130.9054388316022, 38.380937003157285];

  this.bboxString = this.bbox.join(','); 

  // 벡터 소스 
  this.vectorSource = new ol.source.Vector({
    format: new ol.format.GeoJSON({
      extractStyles: false,
    }),
    url: this.GEOSERVER_URL+"/ows?"
        +"service=WFS&"
        +"version=1.1.1&"
        +"request=GetFeature&"
        +"typeNames=air:air_condition&"
        +"SRS=EPSG:4326&"
        +"outputFormat=application/json&"
        +"bbox="+this.bboxString+",EPSG:4326",
    strategy: ol.loadingstrategy.bbox,
  });

  // var fill = 

  var stroke = new ol.style.Stroke({
    color: '#fff',
    width: 1.25
  });
  this.goodIconStyle = new ol.style.Style({
    image : new ol.style.Circle({
      fill : new ol.style.Fill({
        color : 'rgba(0,0,255,0.6)'
      }),
      stroke : stroke,
      radius:6
    })
  });
  this.notBadIconStyle = new ol.style.Style({
    image : new ol.style.Circle({
      fill : new ol.style.Fill({
        color : 'rgba(11,156,49,0.6)'
      }),
      stroke : stroke,
      radius:6
    })
  });
  this.badIconStyle = new ol.style.Style({
    image : new ol.style.Circle({
      fill : new ol.style.Fill({
        color : 'rgba(255,255,0,0.6)'
      }),
      stroke : stroke,
      radius:6
    })
  });
  this.badlyIconStyle = new ol.style.Style({
    image : new ol.style.Circle({
      fill : new ol.style.Fill({
        color : 'rgba(255,0,0,0.8)'
      }),
      stroke : stroke,
      radius:6
    })
  });
  this.noDataIconStyle = new ol.style.Style({
    image : new ol.style.Circle({
      fill : new ol.style.Fill({
        color : '#fff'
      }),
      stroke : stroke,
      radius:6
    })
  });  
 
  this.vectorLayer = new ol.layer.Vector({
    source : this.vectorSource,
    zIndex : 100
  });

   // 히트맵 
   this.heatMapLayer = new ol.layer.Heatmap({
    source: this.vectorSource,
    blur: 30,
    radius: 10,
    opacity: 1,
    gradint : this.setGradient
  });

//  this.addLayer(this.vectorLayer);

// WFS 호출 파라미터 설정
  this.wfsParams = {
    service: 'WFS',
    version: '1.1.1',
    request: 'GetFeature',
    typeName: 'air:air_condition',
    srsName: 'EPSG:4326',
    outputFormat: 'application/json',
    CQL_FILTER : "located='서울'"
  };

  this.zoom = this.getZoom();
  
  this.values = this.vectorSource.uidIndex_;

  this.init();
}

AirMap.prototype = Object.create(ol.Map.prototype);

AirMap.prototype.constructor = AirMap;

AirMap.prototype.init = function(){

  var _this = this;
  // 줌인, 줌아웃 일경우 원 지름 크기 조율 
  _this.on('moveend', function () {
    var zoomLevel = _this.getView().getZoom().toFixed(2);
    if (zoomLevel > 10) {
      this.heatMapLayer.setRadius(100);
    } else if (zoomLevel > 13) {
      this.heatMapLayer.setRadius(20000);
    } else {
      this.heatMapLayer.setRadius(10);
    }
  });
}

// 줌 레벨 확인기능 
AirMap.prototype.getZoom = function(){
  return this.getView().getZoom();
}
// ajax 호출
AirMap.prototype.getData = function(){
  var _this = this;
  var param = $.extend({}, _this.wfsParams);

  $.get(_this.GEOSERVER_URL + '/wfs', $.param(param))
  .done(function(data){
    console.log(data);
  }).fail(function(xhr, status, thrown){
    console.log(status);
  });
}
/*
AirMap.prototype.setGradient = function(){
  var _this = this;
  var columnName = _this.KIND_OF_ELEMENT; 
  var sourceArr = _this.vectorSource;
  sourceArr.forEachFeature(function(element){
    var value = element.values_.columnName;

    var color;
    if(value >= 0  || value <= 15){
      
      // color = ["#14C9FF", "#41A6FF", "#2871FA", "#015EEA", "#00F"];
      // vector.setGradient(color);
    }else if(value > 15  || value <= 35){
      color = ["#95F985", "#4DED30", "#26D701", "#00C301", "#00AB08"];
      vector.setGradient(color);
    }else if(value > 35  || value <= 50){
      color = ["#FFFCC6", "#FFFAA8", "#FFF784", "#FFF568", "#FFEF43"];
      vector.setGradient(color);
    }else if(value < 50 || value > 75){
      color = ["#FEF001", "#FFCE03", "#FD9A01", "#FD6104", "#FF2C05"];
      vector.setGradient(color);
    }else if(value < 75){
      color = ["#AC131C", "#950706", "#750227", "#570044", "#3B005C"];
      vector.setGradient(color);
    }else if(value < 0){
      color = ["#F8F8FF"];
      vector.setGradient(color);
    }
  });
}
*/
AirMap.prototype.getTileLayer = function(layerName){

  var layer = "";
  var arrLayer = [];
  if(layerName == 'vworld'){
    /* vworld 타일 레이어 */
    layer = this.vworld;
  }else if(layerName == 'arc'){
    /* arc gis 타일 레이어 */
    layer = this.arc;
  }else if(layerName == 'stamen'){
    /* osm stamen 타일 레이어 */
    layer = this.stamen1;
  }else{
    layer = this.osm;
  }
  return layer;
};

AirMap.prototype.setFilteredData = function(element, state) {
  var _this = this;
  var range = _this.getRange(element, state);
  var query = '';
  if(range[0] == 'good' || range[0] == 'notbad' || range[0] == 'bad'){
    query = element + ' between ' + range[1] + ' AND ' + range[2];
  }else if(range[0] == 'badly'){
    query = element + ' >= ' + range[1];
  }else{
    query = element + ' = ' + range[1];
  }

  var vectorSource = new ol.source.Vector({
    format: new ol.format.GeoJSON({
      extractStyles: false,
    }),
    url: this.GEOSERVER_URL+"/ows?"
        +"service=WFS&"
        +"version=1.1.1&"
        +"request=GetFeature&"
        +"typeNames=air:air_condition&"
        +"SRS=EPSG:4326&"
        +"CQL_FILTER="+query+"&"
        +"outputFormat=application/json",
    strategy: ol.loadingstrategy.bbox,
  }); 

  return vectorSource;

};

AirMap.prototype.dataLegend = function(element, value){

  if(element == 'pm2_5'){
    // good
    if(value >= 0 && value < 16) return ['good', 0, 15];
    //not bad
    else if(value >= 16 && value < 36) return ['notbad', 16, 35]; 
    // bad
    else if(value >= 36 && value < 76) return ['bad', 36, 75]; 
    // badly
    else if(value >= 76) return ['badly', 76]; 
    // no data
    else return ['noData', -1];
  }else if(element == 'pm10'){
    // good
    if(value >= 0 && value < 31) return ['good', 0, 30];
    //not bad
    else if(value >= 31 && value < 81) return ['notbad', 31, 80]; 
    // bad
    else if(value >= 81 && value < 151) return ['bad', 81, 150]; 
    // badly
    else if(value >= 151) return ['badly', 151]; 
    // no data
    else return ['noData', -1];
  }else if(element == 'o3'){
    // good
    if(value >= 0 && value < 0.031) return ['good', 0, 0.03];
    //not bad
    else if(value >= 0.031 && value < 0.091) return ['notbad', 0.031, 0.09]; 
    // bad
    else if(value >= 0.091 && value < 0.151) return ['bad', 0.091, 0.150]; 
    // badly
    else if(value >= 0.151) return ['badly', 0.151]; 
    // no data
    else return ['noData', -1];
  }else if(element == 'no2'){
    // good
    if(value >= 0 && value < 0.031) return ['good', 0, 0.030];
    //not bad
    else if(value >= 0.031 && value < 0.061) return ['notbad', 0.031, 0.060]; 
    // bad
    else if(value >= 0.061 && value < 0.21) return ['bad', 0.061, 0.20]; 
    // badly
    else if(value >= 0.21) return ['badly', 0.21]; 
    // no data
    else return ['noData', -1];
  }else if(element == 'co'){
    // good
    if(value >= 0 && value < 2.01) return ['good', 0, 2.00];
    //not bad
    else if(value >= 2.01 && value < 9.01) return ['notbad', 2.01, 9.00]; 
    // bad
    else if(value >= 9.01 && value < 15.01) return ['bad', 9.01, 15.00]; 
    // badly
    else if(value >= 15.01) return ['badly', 15.01]; 
    // no data
    else return ['noData', -1];
  }else if(element == 'so2'){
    // good
    if(value >= 0 && value < 0.021) return ['good', 0, 0.020];
    //not bad
    else if(value >= 0.021 && value < 0.051) return ['notbad', 0.021, 0.050]; 
    // bad
    else if(value >= 0.051 && value < 0.151) return ['bad', 0.051, 0.150]; 
    // badly
    else if(value >= 0.151) return ['badly', 0.151]; 
    // no data
    else return ['noData', -1]; 
  }
};
AirMap.prototype.getRange = function(element, state){

  if(element == 'pm2_5'){
    if(state == 'good') return ['good', 0, 15];
    else if(state == 'notbad') return ['notbad', 16, 35]; 
    else if(state == 'bad') return ['bad', 36, 75]; 
    else if(state == 'badly') return ['badly', 76]; 
    else return ['noData', -1];
  }else if(element == 'pm10'){
    if(state == 'good') return ['good', 0, 30];
    else if(state == 'notbad') return ['notbad', 31, 80]; 
    else if(state == 'bad') return ['bad', 81, 150]; 
    else if(state == 'badly') return ['badly', 151]; 
    else return ['noData', -1];
  }else if(element == 'o3'){
    if(state == 'good') return ['good', 0, 0.03];
    else if(state == 'notbad') return ['notbad', 0.031, 0.09]; 
    else if(state == 'bad') return ['bad', 0.091, 0.150]; 
    else if(state == 'badly') return ['badly', 0.151]; 
    else return ['noData', -1];
  }else if(element == 'no2'){
    if(state == 'good') return ['good', 0, 0.030];
    else if(state == 'notbad') return ['notbad', 0.031, 0.060]; 
    else if(state == 'bad') return ['bad', 0.061, 0.20]; 
    else if(state == 'badly') return ['badly', 0.21]; 
    else return [state == 'noData', -1];
  }else if(element == 'co'){
    if(state == 'good') return ['good', 0, 2.00];
    else if(state == 'notbad') return ['notbad', 2.01, 9.00]; 
    else if(state == 'bad') return ['bad', 9.01, 15.00]; 
    else if(state == 'badly') return ['badly', 15.01]; 
    else return ['noData', -1];
  }else if(element == 'so2'){
    if(state == 'good') return ['good', 0, 0.020];
    else if(state == 'notbad') return ['notbad', 0.021, 0.050]; 
    else if(state == 'bad') return ['bad', 0.051, 0.150]; 
    else if(state == 'badly') return ['badly', 0.151]; 
    else return ['noData', -1]; 
  }
};
