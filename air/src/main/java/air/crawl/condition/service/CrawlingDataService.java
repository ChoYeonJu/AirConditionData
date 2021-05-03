package air.crawl.condition.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import air.crawl.condition.dto.AirDto;
import air.crawl.condition.dto.ParameterDto;
import air.crawl.condition.mappers.AirMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlingDataService {
	
	Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
	AirDto airDto;

	@Autowired
	ParameterDto parameterDto;
	
	@Autowired 
	AirMapper airService;
	
	@Autowired 
	AsyncService asyncService;
	
	public static String elementUrl = "https://www.airkorea.or.kr/web/sidoQualityCompare?";
	public static String hourUrl = "https://www.airkorea.or.kr/web/sidoAirInfo/sidoAirInfoDay01?";
	
	@PostConstruct
	public void init() {
		asyncService.setAsync(this);
	}
	
	public AsyncService setAsync() {
		return asyncService;
	}
	
	public void process() {
		List<String> columnList = airService.getColumnList();
		List<String> stCodeList = getSTcodeList();
		
		long beforeTime = System.currentTimeMillis();
		
		log.info("------- 데이타 크롤링 시작 -------");
		for(String columnName : columnList) {
			Map<String, String> data = new LinkedHashMap<String, String>();
			for(String stCode : stCodeList) {
				data = crwalingData(stCode, columnName);
				updateData(data, columnName, stCode);
				
//				asyncService.onAsync(columnName, stCode);
//				logger.info(columnName + " : " + stCode + "완료 !!!!!!!!!!!");
			}
		}
		long afterTime = System.currentTimeMillis();
		long diff = (afterTime - beforeTime)/(1000 * 60);
		log.info("시간 차이(분) : " + diff);
		log.info("------- 데이타 크롤링 마침 -------");
	}
	
	public List<String> getColumnList() { return airService.getColumnList();}
	public List<String> getStCodeList() { return getSTcodeList();}

	public Map<String, String> crwalingData(String stCode, String columnName) {

	  String url = getUrl(stCode, columnName);
      Document doc = null;
      String prevHour = getPrevHour();
      Map<String, String> data = new LinkedHashMap<String, String>();
      
      try {
         Connection connection = Jsoup.connect(url);
         connection.header("Accept-Language", "en");
         doc = Jsoup.connect(url).get();
         //Elements thead = doc.getElementsByTag("thead").select("tr");
         Elements tbody = doc.getElementsByTag("tbody").select("tr");
         for (Element el : tbody) {
            Elements th = el.getElementsByTag("th");
            Elements td = el.getElementsByTag("td");
            if (th.text().contains("당일평균") || th.text().contains("시간평균")) continue;
            String guName = th.text();
            String value = "";
            List<String> ab = td.eachText();
            for (int j = 3; j < td.eachText().size(); j++) {
               if (j == (Integer.parseInt(prevHour) + 2)) value = ab.get(j);
               else continue;
            }
            data.put(guName, value);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return data;
   }
   
   /* DB 데이타 수정 */
   public void updateData(Map<String, String> data, String columName, String stCode) {
	   
	   Set<Map.Entry<String, String>> entries = data.entrySet();
	   for(Map.Entry<String, String> entry : entries) {
		   String[] keyNameList = entry.getKey().split("]");
		   String guName = keyNameList[1];
		   String value = entry.getValue();
		   if(value.equals("-")) value= "-1";
		   parameterDto.setColumName(columName);
		   parameterDto.setValue(Double.parseDouble(value));
		   parameterDto.setGuName(guName);
		   airService.updateData(parameterDto);
	   }
   }

   /* 현재 시간의 전시간 가져오기 */
   public static String getPrevHour() {
      SimpleDateFormat sdf = new SimpleDateFormat("HH");
      Calendar prevHour = Calendar.getInstance();
      prevHour.add(Calendar.HOUR, -1);
      String hour = sdf.format(prevHour.getTime());
      return hour;
   }

   /* url 헤더 중 monthDay 일수 구하는 메소드 */
   public static int getLengthOfMonth(String ymd) {
      String[] intYmn = ymd.split("-");
      int yy = Integer.parseInt(intYmn[0].toString());
      int mm = Integer.parseInt(intYmn[1].toString());
      int dd = Integer.parseInt(intYmn[2].toString());
      LocalDate newDate = LocalDate.of(yy, mm, dd);
      int lengthOfMon = newDate.lengthOfMonth();

      return lengthOfMon;
   }

	/* 자바스크립트 코드에서 동적으로 받는 값 파싱하기 */
	public List<String> crwawlingUrl(String columnName) {
		WebClient webClient = new WebClient();
		List<String> pageParam = new ArrayList<String>(); 
		pageParam =	setItemCode(columnName);
		String url = elementUrl + "itemCode="+pageParam.get(0)+"&pMENU_NO=" + pageParam.get(1);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);

		List<String> param = new ArrayList<String>();
		HtmlPage htmlPage;
		try {
			htmlPage = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(3000);

			Document doc = Jsoup.parse(htmlPage.asXml());
			Elements el = doc.getElementsByClass("st_1 stoke").select("#sidoTable_thead");
			for(Element el2 : el) {
				Elements a = el2.getElementsByTag("a");
				for(Element el3 : a) {
					String[] onClickParam = new String[12]; 
							
					onClickParam = el3.attr("onclick").split(",");
					String menuCode = onClickParam[9].replace("'", "");
					String today = onClickParam[10].substring(0,11).replace("'", "");
					String _num = onClickParam[10].substring(12,14).replace("'", "");
					String st_code = onClickParam[11].replace("'", "").substring(0,4).trim();
					if(st_code.contains(")")) st_code.replace(")", "");
					param.add(0, menuCode);
					param.add(1, today);
					param.add(2, _num);
					param.add(3, st_code);
				}
			}
			
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
	   return param; 
   }
	
	/* 자바스크립트 코드에서 동적으로 지역 코드 값 받기 */
	public List<String> getSTcodeList() {
		WebClient webClient = new WebClient();
		String url = "https://www.airkorea.or.kr/web/sidoQualityCompare?itemCode=10008&pMENU_NO=102";

		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);

		List<String> param = new ArrayList<String>();
		HtmlPage htmlPage;
		try {
			htmlPage = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(3000);

			Document doc = Jsoup.parse(htmlPage.asXml());
			Elements el = doc.getElementsByClass("st_1 stoke").select("#sidoTable_thead");
			for(Element el2 : el) {
				Elements a = el2.getElementsByTag("a");
				for(Element el3 : a) {
					String[] onClickParam = new String[12]; 
					onClickParam = el3.attr("onclick").split(",");
					String st_code = onClickParam[11].replace("'", "").substring(0,4).trim();
					if(st_code.contains(")")) st_code = st_code.replace(")", "");
					param.add(st_code);
				}
			}
			
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return param; 
   }
	
	public String getUrl(String areaCode, String columnName) {
		
		List<String> param = new ArrayList<String>();
		param = crwawlingUrl(columnName);
		int monthDay = getLengthOfMonth(param.get(1));
		String strMonthDay = Integer.toString(monthDay);
		String url = hourUrl
				+ "itemCode="+param.get(0)+"&"
				+ "ymd="+param.get(1)+"%20"+param.get(2)+"&"
				+ "areaCode=" + areaCode + "&" + "tDate=2021-04-01&" + "monthDay=" + strMonthDay + "";
		
		return url;
	}
	
	public List<String> setItemCode(String columnName) {
		List<String> pageParam = new ArrayList<String>();

		if(columnName.equals("pm2_5")) {// 미세먼지
			pageParam.add(0, "10008");
			pageParam.add(1, "102");
		}else if(columnName.equals("pm10")) {// 초미세먼지
			pageParam.add(0, "10007");
			pageParam.add(1, "101");
		}else if(columnName.equals("o3")) {	// 오존
			pageParam.add(0, "10003");
			pageParam.add(1, "103");
		}else if(columnName.equals("no2")) {// 이산화질소
			pageParam.add(0, "10006");
			pageParam.add(1, "104");
		}else if(columnName.equals("co")) {	// 일산화탄소
			pageParam.add(0, "10002");
			pageParam.add(1, "105");
		}else /*if(columnName == "so2")*/ {	// 아황산가스
			pageParam.add(0, "10001");
			pageParam.add(1, "106");
		}		
		return pageParam;
	}
}
