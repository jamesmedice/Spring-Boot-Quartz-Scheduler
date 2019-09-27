package com.medici.app.spring.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class MessageBrokerJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(MessageBrokerJob.class);

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("subject");
		String body = jobDataMap.getString("body");
		String title = jobDataMap.getString("title");

		sendMessage(title, subject, body);
	}

	private void sendMessage(String title, String subject, String body) {
		logger.info("Sending Message   {} : {} : {}  ", title, subject, body);

	}
}
