package air.crawl.condition;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


class CrawlTest {

	@Test
	void test() {
		List<String> stCodeList = getSTcodeList();
		for(String stCode : stCodeList) {
			crawlingProcess(stCode);
		}
		
	}

	
   public static void crawlingProcess(String stCode) {

	  String url = getUrl(stCode);
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
      
      connectPostgresql(data);
   }
   
   /* DB 커넥션 부분 */
   public static void connectPostgresql(Map<String, String> data) {
	   
	   java.sql.Connection conn = null;
	   java.sql.Statement st = null;
	   ResultSet rs = null;
	   
	   String url = "jdbc:postgresql://localhost:5432/airquality";
	   String user = "yjjoh";
	   String password = "yjjoh";
	   
	   try {
		   conn = DriverManager.getConnection(url, user, password);
		   st = conn.createStatement();
		   
		   Set<Map.Entry<String, String>> entries = data.entrySet();
		   for(Map.Entry<String, String> entry : entries) {
			   String[] keyNameList = entry.getKey().split("]");
			   String guName = keyNameList[1];
			   String value = entry.getValue();
			   if(value.equals("-")) value= "-1";
			   st.executeUpdate("update air_condition set ps_data = " + value + " where located2 = '"+ guName + "'");
		   }
		   rs = st.executeQuery("select * from air_condition");
		   while(rs.next()) {
			   System.out.println(rs.getString(9));
		   }
		   
		  
	   }catch (SQLException sqlEx) {
		// TODO: handle exception
		   System.out.println(sqlEx);
	   }finally {
		   try {
			   //rs.close();
			   st.close();
			   conn.close();
		   }catch (SQLException sqlEx) {
			// TODO: handle exception
			   System.out.println(sqlEx);
		   }
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

   /* 해당 광역시,도 버튼 url 값 리턴 */
   public static String getSigunguUrl(String areaCode) {

      SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
      Calendar time = Calendar.getInstance();
      Calendar prevTime = Calendar.getInstance();
      prevTime.add(Calendar.DATE, -1);
      String ymd = format1.format(time.getTime());

      int lengthOfMon = getLengthOfMonth(ymd);
      String tDate = format1.format(prevTime.getTime());

      String url = "https://www.airkorea.or.kr/web/sidoAirInfo/sidoAirInfoDay01?" + "itemCode=10008&" + "ymd=" + ymd
            + "%2009&" + "areaCode=" + areaCode + "&" + "tDate=" + tDate + "&" + "monthDay=" + lengthOfMon;
      return url;
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
	public static List<String> crwawlingUrl() {

		String url = "https://www.airkorea.or.kr/web/sidoQualityCompare?itemCode=10008&pMENU_NO=102";

		WebClient webClient = new WebClient();
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
					String[] onClickParam = el3.attr("onclick").split(",");
					String menuCode = onClickParam[9].replace("'", "");
					String today = onClickParam[10].substring(0,11).replace("'", "");
					String _num = onClickParam[10].substring(12,14).replace("'", "");
					String st_code = onClickParam[11].replace("'", "").substring(0,4).trim();
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
	public static List<String> getSTcodeList() {

		String url = "https://www.airkorea.or.kr/web/sidoQualityCompare?itemCode=10008&pMENU_NO=102";

		WebClient webClient = new WebClient();
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
					String[] onClickParam = el3.attr("onclick").split(",");
					String st_code = onClickParam[11].replace("'", "").substring(0,4).trim();
					param.add(st_code);
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
	
	public static String getUrl(String areaCode) {
		List<String> param = crwawlingUrl();
		int monthDay = getLengthOfMonth(param.get(1));
		String strMonthDay = Integer.toString(monthDay);
		String url = "https://www.airkorea.or.kr/web/sidoAirInfo/sidoAirInfoDay01?"
				+ "itemCode="+param.get(0)+"&"
				+ "ymd="+param.get(1)+"%20"+param.get(2)+"&"
				+ "areaCode=" + areaCode + "&" + "tDate=2021-04-01&" + "monthDay=" + strMonthDay + "";
		
		return url;
	}
	
	

	   /* url 헤더 중 areaCode 구하는 메소드 */
	   public static String getST_code(String located) {
	      Map<String, String> stcode = new LinkedHashMap<String, String>();
	      stcode.put("서울", "02");
	      stcode.put("부산", "051");
	      stcode.put("대구", "053");
	      stcode.put("인천", "032");
	      stcode.put("광주", "062");
	      stcode.put("대전", "042");
	      stcode.put("울산", "052");
	      stcode.put("경기", "031");
	      stcode.put("강원", "033");
	      stcode.put("충북", "043");
	      stcode.put("충남", "041");
	      stcode.put("전북", "063");
	      stcode.put("전남", "061");
	      stcode.put("세종", "044");
	      stcode.put("경북", "054");
	      stcode.put("경남", "062");
	      stcode.put("제주", "064");

	      String code = "";
	      for (Entry<String, String> map : stcode.entrySet()) {
	         if (located.equals(map.getKey())) {
	            code = map.getValue();
	         }
	      }
	      return code;

	   }
	   
}
