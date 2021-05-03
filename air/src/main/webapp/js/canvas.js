function drawLengendChart(){
	
	var canvas = document.getElementById('canvas1');
	var context = canvas.getContext('2d');

	var x = 110;
	var y = 130;
	var r = 80;

	// 글자 채우기
	context.font = '2px';
	context.fillText('미세먼지 정보', 80,20);

	/* 미세먼지 좋음  */
	context.beginPath();    // 패스지정 초기화
	context.moveTo(x,y);    // 시작점 지정
	context.arc(x,y,r,1.0 * Math.PI,1.15*Math.PI,false);    // 원호 그리기
	context.closePath();    // 패스 닫음
	context.fillStyle = 'blue'; // 스타일
	context.fill();
	/* 미세먼지 보통  */
	context.beginPath();
	context.moveTo(x,y);
	context.arc(x,y,r,1.15 * Math.PI,1.35*Math.PI,false);
	context.closePath();
	context.fillStyle = 'green';
	context.fill();
	/* 미세먼지 나쁨 */
	context.beginPath();
	context.moveTo(x,y);
	context.arc(x,y,r, 1.35 * Math.PI,1.85*Math.PI,false);
	context.closePath();
	context.fillStyle = '#FFDC3C';
	context.fill();
	/* 미세먼지 매우 나쁨 */
	context.beginPath();
	context.moveTo(x,y);
	context.arc(x,y,r, 1.85 * Math.PI,2.0*Math.PI,false);
	context.closePath();
	context.fillStyle = 'red';
	context.fill();
	/* 중앙에 원 */
	context.beginPath();
	context.moveTo(x,y);
	context.arc(x,y,30, 0, Math.PI,true);
	context.closePath();
	context.fillStyle = 'white';
	context.fill();
	/* 중앙에 원 */
	context.beginPath();
	context.moveTo(x, y);
	context.lineTo(x - 39, y - 39);
	context.stroke();
}

function drawAverageChart(){
	drawAverageGraph();
	drawAverageBar();
}
function drawAverageGraph() {
	var ctx = document.getElementById('canvas2').getContext('2d');

	ctx.beginPath();
	ctx.moveTo(30, 20);
	ctx.lineTo(30, 130);

	ctx.moveTo(30, 130);
	ctx.lineTo(280, 130);

	ctx.strokeStyle ='#000';
	ctx.stroke();

	// 글자 채우기
	ctx.font = '2px';
	ctx.fillText('최저', 80,145);
	ctx.fillText('평균', 160, 145);
	ctx.fillText('최고', 240, 145);


}
function drawAverageBar(){
  var bar = document.getElementById('canvas2').getContext('2d');

  // bar.beginPath();
  bar.fillStyle = "#74c2f5";
  bar.fillRect(80, 130, 15, -80);
  bar.fillRect(160, 130, 15, -50);
  bar.fillRect(240, 130, 15, -30);
}
