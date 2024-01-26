package com.xeridia;

import com.xeridia.config.JobConfiguration;
import com.xeridia.listener.JobCompletionNotificationListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBatchTest
@DirtiesContext
@SpringBootTest
@EnableAutoConfiguration
@Slf4j
public class SpringBootBatchIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JobCompletionNotificationListener listener;

    @Autowired
    private Step step;

    @Autowired
    private JobRepository jobRepository;

    @MockBean
    private JobConfiguration jobConfiguration; // Just to avoid the use of real JobConfiguration

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void givenCommentList_whenJobExecuted_thenSuccess() throws Exception {
        jobLauncherTestUtils.setJob(new JobBuilder("importCommentJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();
        ExitStatus jobExitStatus = jobExecution.getExitStatus();

        Assertions.assertEquals("importCommentJob", jobInstance.getJobName());
        Assertions.assertEquals("COMPLETED", jobExitStatus.getExitCode());
    }

}