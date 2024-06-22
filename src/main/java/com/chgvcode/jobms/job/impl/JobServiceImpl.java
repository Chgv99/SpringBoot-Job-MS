package com.chgvcode.jobms.job.impl;

import com.chgvcode.jobms.job.Job;
import com.chgvcode.jobms.job.JobRepository;
import com.chgvcode.jobms.job.JobService;
import com.chgvcode.jobms.job.dto.JobDTO;
import com.chgvcode.jobms.job.external.Company;
import com.chgvcode.jobms.job.external.Review;
import com.chgvcode.jobms.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    //private List<Job> jobs = new ArrayList<Job>();
    JobRepository jobRepository;

    @Autowired
    RestTemplate restTemplate;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();

        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private JobDTO convertToDTO(Job job){

        // getForObject is useful when we know the response will be a single object
        Company company = restTemplate.getForObject(
                "http://companyms:8081/companies/" + job.getCompanyId(),
                Company.class
        );

        // exchange is more versatile than getForObject. I.E. it allows us to fetch a collection
        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange(
                "http://reviewms:8083/reviews?companyId=" + job.getCompanyId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
                }
        );
        List<Review> reviews = reviewResponse.getBody();

        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(
                job,
                company,
                reviews
        );
        //jobDTO.setCompany(company);

        return jobDTO;
    }

    public JobDTO getJobById(Long id){
        return convertToDTO(jobRepository.findById(id).orElse(null));
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {

        Optional<Job> jobOptional = jobRepository.findById(id);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job); //si existe, lo actualiza (upsert)
            return true;
        }
        return false;
    }
}
