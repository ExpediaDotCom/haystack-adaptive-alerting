package com.expedia.adaptivealerting.metrics.functions.source;

import lombok.val;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MetricFunctionsReaderTest {

    @Test
    public void testreadFromInputFile(){
        val functionInputFileName = "/config/functions-test.txt";
        List<MetricFunctionsSpec> metricFunctionsSpecList = MetricFunctionsReader.readFromInputFile(functionInputFileName);
        assertEquals(1, metricFunctionsSpecList.size());
        MetricFunctionsSpec metricFunctionsSpec = metricFunctionsSpecList.get(0);
        assertEquals("sumSeries(a.b.c)", metricFunctionsSpec.getFunction());
        assertEquals(30, metricFunctionsSpec.getIntervalInSecs());
        Iterator it = metricFunctionsSpec.getTags().entrySet().iterator();
        Map.Entry tag1 = (Map.Entry)it.next();
        assertEquals(tag1.getKey(), "app_name");
        assertEquals(tag1.getValue(), "sample_app1");
        Map.Entry tag2 = (Map.Entry)it.next();
        assertEquals(tag2.getKey(), "env");
        assertEquals(tag2.getValue(), "test");
    }

    @Test
    public void testreadFromInputFileException() throws Exception {
        val invalidFileName = "/config/no-such-file-test.txt";
        List<MetricFunctionsSpec> metricFunctionsSpecList = MetricFunctionsReader.readFromInputFile(invalidFileName);
    }
}
