package com.medici.app.spring.config;

import java.io.IOException;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.medici.app.spring.job.StandAloneTrigger;

@Configuration
@PropertySource(value = "classpath:application.yaml")
public class appConfig {

	@Bean
	public JobDetail jobDetail() {
		return JobBuilder.newJob(StandAloneTrigger.class).withIdentity("Job", "group").storeDurably(true).build();
	}

	@Bean
	public Trigger jobTrigger() {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever();
		return TriggerBuilder.newTrigger().forJob(jobDetail()).withIdentity("jobTrigger").withSchedule(scheduleBuilder).build();
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(jobTrigger());
		scheduler.setJobDetails(jobDetail());
		return scheduler;
	}

}
