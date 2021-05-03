package air.crawl.condition.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	
	private CrawlingDataService crawlingData;
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	public void setAsync(CrawlingDataService crawlingData) {
		// TODO Auto-generated method stub
		this.crawlingData = crawlingData;
	}
	@Async("fooExecutor")
	public void onAsync(String columnName, String stCode) {
		Map<String, String> data = crawlingData.crwalingData(stCode, columnName);
		crawlingData.updateData(data, columnName, stCode);
		
	}
}
