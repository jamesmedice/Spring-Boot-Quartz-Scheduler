package com.medici.app.spring.job;

import java.time.LocalDateTime;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class StandAloneTrigger implements Job {

	private static final Logger logger = LoggerFactory.getLogger(StandAloneTrigger.class);
 

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Running StandAloneTrigger | Time {}", LocalDateTime.now().getSecond());
	}

}
