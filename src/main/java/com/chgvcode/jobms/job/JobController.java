package com.chgvcode.jobms.job;

import com.chgvcode.jobms.job.dto.JobWithCompanyDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs") // base url for all requests
public class JobController {

    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobWithCompanyDTO>> findAll(){
        //new ResponseEntity<>(jobService.findAll(), HttpStatus.OK);
        return ResponseEntity.ok(jobService.findAll()); // another way of doing the same
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobWithCompanyDTO> getJobById(@PathVariable Long id){
        JobWithCompanyDTO jobWithCompanyDTO = jobService.getJobById(id);
        if (jobWithCompanyDTO != null){
            return new ResponseEntity<>(jobWithCompanyDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job){
        jobService.createJob(job);
        // we could check if a job's company exists here
        return new ResponseEntity<>("Job added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        if (jobService.deleteJobById(id)) return new ResponseEntity<>("Job removed successfully", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    //@RequestMapping(value = "/jobs/{id}", method = RequestMethod.PUT); // Another way
    public ResponseEntity<String> updateJob(@PathVariable Long id, @RequestBody Job updatedJob){
        if (jobService.updateJob(id, updatedJob)) return new ResponseEntity<>("Job updated successfully", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
