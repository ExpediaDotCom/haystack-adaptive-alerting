/*
 * Copyright 2018-2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.modelservice.entity.Detector;
import com.expedia.adaptivealerting.modelservice.exception.RecordNotFoundException;
import com.expedia.adaptivealerting.modelservice.service.DetectorService;
import com.expedia.adaptivealerting.modelservice.tracing.Trace;
import com.expedia.www.haystack.client.Span;
import io.opentracing.SpanContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v3/detectors")
@Slf4j
public class DetectorController {

    @Autowired
    private DetectorService service;

    @Autowired
    private Trace trace;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createDetector(@Valid @RequestBody Detector detector, @RequestHeader HttpHeaders headers) {
        SpanContext parentSpanContext = trace.extractParentSpan(headers);
        Span span= trace.startSpan("create-detector", parentSpanContext);
        val uuid = service.createDetector(detector);
        span.finish();
        return uuid.toString();
    }

    @GetMapping(path = "/findByUuid", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Detector findByUuid(@RequestParam String uuid, @RequestHeader HttpHeaders headers) {
        SpanContext parentSpanContext = trace.extractParentSpan(headers);
        Span span = trace.startSpan("find-detector-by-uuid", parentSpanContext);
        Detector detector = service.findByUuid(uuid);
        if (detector == null) {
            span.setTag("Error", "Invalid UUID");
            span.finish();
            throw new RecordNotFoundException("Invalid UUID: " + uuid);
        }
        span.finish();
        return detector;
    }

    @GetMapping(path = "/findByCreatedBy", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Detector> findByCreatedBy(@RequestParam String user) {
        List<Detector> detectors = service.findByCreatedBy(user);
        if (detectors == null || detectors.isEmpty()) {
            throw new RecordNotFoundException("Invalid user: " + user);
        }

        return detectors;
    }

    @PostMapping(path = "/toggleDetector")
    @ResponseStatus(HttpStatus.OK)
    public void toggleDetector(@RequestParam String uuid, @RequestParam Boolean enabled) {
        Assert.notNull(uuid, "uuid can't be null");
        Assert.notNull(enabled, "enabled can't be null");
        service.toggleDetector(uuid, enabled);
    }

    @PostMapping(path = "/trustDetector")
    @ResponseStatus(HttpStatus.OK)
    public void trustDetector(@RequestParam String uuid, @RequestParam Boolean trusted) {
        Assert.notNull(uuid, "uuid can't be null");
        Assert.notNull(trusted, "trusted can't be null");
        service.trustDetector(uuid, trusted);
    }

    @GetMapping(path = "/getLastUpdatedDetectors", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Detector> getLastUpdatedDetectors(@RequestParam long interval) {
        return service.getLastUpdatedDetectors(interval);
    }

    @GetMapping(path = "/getLastUsedDetectors", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Detector> getLastUsedDetectors(@RequestParam int noOfDays) {
        return service.getLastUsedDetectors(noOfDays);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateDetector(@RequestParam String uuid, @RequestBody Detector detector) {
        service.updateDetector(uuid, detector);
    }

    @PostMapping(path = "/updateDetectorLastUsed", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updatedDetectorLastUsed(@RequestBody Map<String, String> params) {
        service.updateDetectorLastUsed(params.get("detectorUuid"));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteDetector(@RequestParam String uuid) {
        service.deleteDetector(uuid);
    }

    @GetMapping(path = "/getDetectorsToTrain", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Detector> getDetectorsToTrain(@RequestParam Long timestampMs, @RequestHeader HttpHeaders headers) {
        SpanContext parentSpanContext = trace.extractParentSpan(headers);
        Span span = trace.startSpan("find-detectors-to-train-next", parentSpanContext);
        val detectorList = service.getDetectorsToBeTrained(timestampMs);
        span.finish();

        return detectorList;
    }

    @PostMapping(path = "/updateDetectorTrainingTime")
    public void updateDetectorTrainingTime(@RequestParam String uuid,
                                           @RequestParam Long nextRun,
                                           @RequestHeader HttpHeaders headers) {
        Assert.notNull(uuid, "uuid can't be null");
        Assert.notNull(nextRun, "nextRun can't be null");
        SpanContext parentSpanContext = trace.extractParentSpan(headers);
        Span span = trace.startSpan("update-detector-training-time", parentSpanContext);
        span.setTag("detectorUuid", uuid);
        service.updateDetectorTrainingTime(uuid, nextRun);
        span.finish();
    }
}
