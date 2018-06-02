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
package com.expedia.adaptivealerting.samples;

import com.expedia.adaptivealerting.core.detector.EwmaOutlierDetector;
import com.expedia.adaptivealerting.core.detector.PewmaOutlierDetector;
import com.expedia.adaptivealerting.tools.pipeline.MetricFilter;
import com.expedia.adaptivealerting.tools.pipeline.MetricSource;
import com.expedia.adaptivealerting.tools.pipeline.filter.OutlierDetectorMetricFilter;
import com.expedia.adaptivealerting.tools.pipeline.sink.ChartSink;
import com.expedia.adaptivealerting.tools.pipeline.sink.ConsoleLogMetricSink;
import com.expedia.adaptivealerting.tools.pipeline.source.CsvMetricSource;
import com.expedia.adaptivealerting.tools.visualization.ChartSeries;

import java.io.InputStream;

import static com.expedia.adaptivealerting.tools.visualization.ChartUtil.*;

public class CsvPipeline {
    
    public static void main(String[] args) {
        final InputStream is = ClassLoader.getSystemResourceAsStream("samples/sample001.csv");
        final MetricSource source = new CsvMetricSource(is, "data", 200L);
        
        final MetricFilter ewmaFilter = new OutlierDetectorMetricFilter(new EwmaOutlierDetector());
        final MetricFilter pewmaFilter = new OutlierDetectorMetricFilter(new PewmaOutlierDetector());
        
        final ChartSeries ewmaSeries = new ChartSeries();
        final ChartSeries pewmaSeries = new ChartSeries();
    
        source.addSubscriber(ewmaFilter);
        source.addSubscriber(pewmaFilter);
        ewmaFilter.addSubscriber(new ConsoleLogMetricSink());
        ewmaFilter.addSubscriber(new ChartSink(ewmaSeries));
        pewmaFilter.addSubscriber(new ConsoleLogMetricSink());
        pewmaFilter.addSubscriber(new ChartSink(pewmaSeries));
        
        showChartFrame(createChartFrame(
                "Cal Inflow",
                createChart("EWMA", ewmaSeries),
                createChart("PEWMA", pewmaSeries)));
    
        source.start();
    }
}
