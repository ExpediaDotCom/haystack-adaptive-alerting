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
package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.modelservice.entity.ElasticsearchDetector;
import com.expedia.adaptivealerting.modelservice.repo.EsDetectorRepository;
import com.expedia.adaptivealerting.modelservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service to fetch and modify detectors stored in elastic search
 */
@Slf4j
@Service
public class DetectorServiceImpl implements DetectorService {

    @Autowired
    private EsDetectorRepository detectorRepository;

    @Override
    public String createDetector(ElasticsearchDetector elasticsearchDetector) {
        return detectorRepository.createDetector(elasticsearchDetector);
    }

    @Override
    public void deleteDetector(String uuid) {
        detectorRepository.deleteDetector(uuid);
    }

    @Override
    public void updateDetector(String uuid, ElasticsearchDetector elasticsearchDetector) {
        detectorRepository.updateDetector(uuid, elasticsearchDetector);
    }

    @Override
    public List<ElasticsearchDetector> findByUuid(String uuid) {
        return detectorRepository.findByUuid(uuid);
    }

    @Override
    public List<ElasticsearchDetector> findByCreatedBy(String user) {
        return detectorRepository.findByCreatedBy(user);
    }

    @Override
    public void toggleDetector(String uuid, Boolean enabled) {
        detectorRepository.toggleDetector(uuid, enabled);
    }

    @Override
    public List<ElasticsearchDetector> getLastUpdatedDetectors(int interval) {
        val now = DateUtil.now().toInstant();
        val fromDate = DateUtil.toUtcDateString((now.minus(interval, ChronoUnit.MINUTES)));
        val toDate = DateUtil.toUtcDateString(now);
        return detectorRepository.getLastUpdatedDetectors(fromDate, toDate);
    }
}