package com.chgvcode.jobms.job;

import com.chgvcode.jobms.job.dto.JobWithCompanyDTO;

import java.util.List;

public interface JobService {
    List<JobWithCompanyDTO> findAll();
    JobWithCompanyDTO getJobById(Long id);
    void createJob(Job job);
    boolean deleteJobById(Long id);
    boolean updateJob(Long id, Job updatedJob);
}
