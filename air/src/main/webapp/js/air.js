window.onload = function () {
  var map = new ol.Map({
    target: "map",
    layers: [
      new ol.layer.Tile({
        source: new ol.source.OSM(),
      }),
    ],
    view: new ol.View({
      center: ol.proj.fromLonLat([128.4, 37.2]),
      zoom: 7,
      extent: [
        12678546.51713,
        3734188.8033424,
        15854453.760059,
        5414661.8558898,
      ],
      minZoom: 1,
      maxZoom: 15,
    }),
  });
	var image = new ol.style.Circle({
	  radius: 5,
	  fill: null,
	  stroke: new ol.style.Stroke({color: 'red', width: 1}),
	});
	var styles = new ol.style.Style({
	      image: image
  });
  var vectorSource = new ol.layer.Vector({
    source: new ol.source.Vector({
      format: new ol.format.JSONFeature(),//new ol.format.GeoJSON(),
      url: "http://localhost:8080/geoserver/air/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=air:air_condition&maxFeatures=50&outputFormat=application/json",
      /* params: {
        'VERSION': "1.0.0",
        'TYPENAME': "air:air_condition",
        'BBOX': [
          126.12589387679935,
          33.22788686430661,
          130.9054388316022,
          38.380937003157285
        ],
        'SRS': "EPSG:4326",
        'maxFeatures':'50'
      },
      serverType: "geoserver" */
    }),
	style: styles
  });
  
  map.addLayer(vectorSource);
};