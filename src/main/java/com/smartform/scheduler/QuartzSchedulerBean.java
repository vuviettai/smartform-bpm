package com.smartform.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

//@ApplicationScoped
public class QuartzSchedulerBean {
	//@Inject
    Scheduler quartz;
	
	void onStart(@Observes StartupEvent event) throws SchedulerException {
//	       JobDetail job = JobBuilder.newJob(QuartzJob.class)
//	                         .withIdentity("myJob", "myGroup")
//	                         .build();
//	       Trigger trigger = TriggerBuilder.newTrigger()
//	                            .withIdentity("myTrigger", "myGroup")
//	                            .startNow()
//	                            .withSchedule(
//	                               SimpleScheduleBuilder.simpleSchedule()
//	                                  .withIntervalInSeconds(10)
//	                                  .repeatForever())
//	                            .build();
//	       quartz.scheduleJob(job, trigger); 
	}

	public void scheduleJob(JobDetail job, Trigger trigger) throws SchedulerException{
		 //quartz.scheduleJob(job, trigger); 
	}
}
