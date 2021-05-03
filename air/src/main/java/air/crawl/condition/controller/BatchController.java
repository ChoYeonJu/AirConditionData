package air.crawl.condition.controller;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import air.crawl.condition.batch.UpdateDataJob;
import air.crawl.condition.service.BatchService;

@Controller
public class BatchController {

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	BatchService batchService;
	
	@PostConstruct
	public void start() {
		JobDetail jobDetail = batchService.buildJobDetail(UpdateDataJob.class, "testJob", "test", new HashMap());
		try {
			scheduler.scheduleJob(jobDetail, batchService.buildJobTrigger("10 00 * * * ?"));
		} catch (SchedulerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
