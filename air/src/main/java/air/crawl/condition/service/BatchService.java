package air.crawl.condition.service;

import static org.quartz.JobBuilder.newJob;

import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
	
    public Trigger buildJobTrigger(String scheduleExp) {
    	return TriggerBuilder.newTrigger()
    			.withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }
    
    public JobDetail buildJobDetail(Class job,String name, String group, Map params) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.putAll(params);
    	
    	return newJob(job).withIdentity(name, group)
    			.usingJobData(jobDataMap)
    			.build();
    }
}
