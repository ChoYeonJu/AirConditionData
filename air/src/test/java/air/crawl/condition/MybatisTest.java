package air.crawl.condition;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import air.crawl.condition.mappers.AirMapper;
import air.crawl.condition.service.CrawlingDataService;

@SpringBootTest
class MybatisTest {

	
	@Autowired
	CrawlingDataService crawlingData;
	
	@Autowired
	AirMapper airMapper;
	
	@Test
	void airMapperTest() throws Exception {
//		AirDto airDto = new AirDto();
//		airDto.setNum(1);
//		airDto.setLocated("sdfsdf");
//		airDto.setLocated2("sdfsdf");
//		airDto.setMoni_address("sdfsdf");
//		airDto.setLon(2.015);
//		airDto.setLat(0);
//		airDto.setStat("sdfsdf");
//		airDto.setSt_code("sdfsdf");

		List<String> a = airMapper.getColumnList();
		System.out.println(a);
		
		crawlingData.process();
//		String a2 = airMapper.selectNow();
//		System.out.println(a2);
//		
		// Update Test
		
	}
	

}
