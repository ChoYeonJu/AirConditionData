package air.crawl.condition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@EnableScheduling		// 스케줄러 기능 활성화
//@EnableBatchProcessing	// 배치 기능 활성화 
@SpringBootApplication
@ComponentScan
public class AirApplication {

	private static Logger logger = LogManager.getLogger(AirApplication.class);
	 
	public static void main(String[] args) {
		logger.info("yjjoh__Starting Air Quality Index Project.......");
		SpringApplication.run(AirApplication.class, args);
	}

}
