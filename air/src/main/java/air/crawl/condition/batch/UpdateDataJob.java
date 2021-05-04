package air.crawl.condition.batch;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import air.crawl.condition.service.CrawlingDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UpdateDataJob extends QuartzJobBean implements InterruptableJob{

	@Autowired
	CrawlingDataService crawl;
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
//		crawl.process();
		log.info("안녕~~~~~~~~~~~~~~");
	}	
}
