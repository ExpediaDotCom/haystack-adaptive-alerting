/*
 * Copyright 2018 Expedia Group, Inc.
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
package com.expedia.adaptivealerting.anomdetect;

import com.expedia.adaptivealerting.core.anomaly.AnomalyResult;
import com.expedia.adaptivealerting.core.data.MappedMetricData;

import java.util.UUID;

import static com.expedia.adaptivealerting.core.util.AssertUtil.notNull;

/**
 * Abstract base class implementing {@link AnomalyDetector}.
 *
 * @author Willie Wheeler
 */
public abstract class AbstractAnomalyDetector implements AnomalyDetector {
    private UUID uuid;
    
    @Override
    public UUID getUuid() {
        return uuid;
    }
    
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    
    @Override
    public MappedMetricData classify(MappedMetricData mappedMetricData) {
        notNull(mappedMetricData, "mappedMetricData can't be null");
        final MappedMetricData result = new MappedMetricData(mappedMetricData, toAnomalyResult(mappedMetricData));
        result.setDetectorType(mappedMetricData.getDetectorType());
        result.setDetectorUuid(mappedMetricData.getDetectorUuid());
        result.setMetricData(mappedMetricData.getMetricData());
        return result;
    }
    
    protected abstract AnomalyResult toAnomalyResult(MappedMetricData mappedMetricData);
}