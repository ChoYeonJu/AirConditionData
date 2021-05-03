package air.crawl.condition.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InitController {

//	@Autowired AirService airService;
	
	/* static 폴더와 template 폴더 차이 
	 * static : 정적인 자료를 다룰때 html, css, js 
	 * template : 동적인 자료를 다룰때 thymeleaf
	 * */
//	@GetMapping("/map")
//	public ResponseEntity<String> main() {
//		return new ResponseEntity<String>(airService.selectNow());
//	}
	
	@RequestMapping(value = "/aa", method = RequestMethod.GET)
	public String crawl() {
//		CrawlingDataService crawlingData = new CrawlingDataService();
//		crawlingData.process();
		return "index";
	}
	
//	@RequestMapping(value = "/api", method = RequestMethod.POST)
//	public String select() throws Exception{
//		List<AirDto>  aa = airService.select();
//		String toSTRING = aa.toString();
//		return "toSTRING";
//	}

	
}
