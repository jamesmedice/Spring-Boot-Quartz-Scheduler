package com.medici.app.spring.controller;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.medici.app.spring.job.MessageBrokerJob;
import com.medici.app.spring.payload.SchedulerRequest;
import com.medici.app.spring.payload.SchedulerResponse;

/**
 * 
 * @author a73s
 *
 */
@RestController
public class MessageBrokerJobSchedulerController {
	private static final Logger logger = LoggerFactory.getLogger(MessageBrokerJobSchedulerController.class);

	@Autowired
	private Scheduler scheduler;

	@PostMapping("/scheduleMessage")
	public ResponseEntity<SchedulerResponse> schedulerMessage(@Valid @RequestBody SchedulerRequest scheduleEmailRequest) {
		try {
			ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(), scheduleEmailRequest.getTimeZone());

			if (dateTime.isBefore(ZonedDateTime.now())) {
				SchedulerResponse scheduleEmailResponse = new SchedulerResponse(false, "dateTime must be after current time");
				return ResponseEntity.badRequest().body(scheduleEmailResponse);
			}

			JobDetail jobDetail = buildJobDetail(scheduleEmailRequest);
			Trigger trigger = buildJobTrigger(jobDetail, dateTime);
			scheduler.scheduleJob(jobDetail, trigger);

			SchedulerResponse scheduleEmailResponse = new SchedulerResponse(true, jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
			return ResponseEntity.ok(scheduleEmailResponse);

		} catch (SchedulerException ex) {
			logger.error("Error scheduling message", ex);

			SchedulerResponse scheduleEmailResponse = new SchedulerResponse(false, "Error scheduling message. Please try later!");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleEmailResponse);
		}
	}

	private JobDetail buildJobDetail(SchedulerRequest schedulerRequest) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("title", schedulerRequest.getTitle());
		jobDataMap.put("subject", schedulerRequest.getSubject());
		jobDataMap.put("body", schedulerRequest.getBody());

		return JobBuilder.newJob(MessageBrokerJob.class).withIdentity(UUID.randomUUID().toString(), "message-jobs").withDescription("Send Message Job").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName(), "message-triggers").withDescription("Send Message Trigger").startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
	}
}
