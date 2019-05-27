package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.modelservice.entity.ElasticsearchDetector;
import com.expedia.adaptivealerting.modelservice.repo.EsDetectorRepository;
import com.expedia.adaptivealerting.modelservice.test.ObjectMother;
import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class DetectorServiceTest {

    @InjectMocks
    private DetectorService detectorService = new DetectorServiceImpl();

    @Mock
    private EsDetectorRepository detectorRepository;

    private ElasticsearchDetector elasticsearchDetector;

    private List<ElasticsearchDetector> elasticsearchDetectors = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initTestObjects();
        initDependencies();
    }

    @Test
    public void testCreateDetector() {
        String acutalCreateId = detectorService.createDetector(elasticsearchDetector);
        assertNotNull(acutalCreateId);
        assertEquals("1", acutalCreateId);
    }

    @Test
    public void testDeleteDetector() {
        DetectorService detectorService = mock(DetectorService.class);
        doNothing().when(detectorService).deleteDetector(anyString());
        detectorService.deleteDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639");
        verify(detectorService, times(1)).deleteDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639");
    }

    @Test
    public void testUpdateDetector() {
        DetectorService detectorService = mock(DetectorService.class);
        doNothing().when(detectorService).updateDetector(anyString(), any(ElasticsearchDetector.class));
        detectorService.updateDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639", elasticsearchDetector);
        verify(detectorService, times(1)).updateDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639", elasticsearchDetector);
    }

    @Test
    public void testFindByUuid() {
        List<ElasticsearchDetector> actualDetectors = detectorService.findByUuid("uuid");
        assertNotNull(actualDetectors);
        assertCheck(actualDetectors);
    }

    @Test
    public void testFindByCreatedBy() {
        List<ElasticsearchDetector> actualDetectors = detectorService.findByCreatedBy("test-user");
        assertNotNull(actualDetectors);
        assertCheck(actualDetectors);
    }

    @Test
    public void testGetLastUpdatedDetectors() {
        List<ElasticsearchDetector> actualDetectors = detectorService.getLastUpdatedDetectors(10);
        assertNotNull(actualDetectors);
        assertCheck(actualDetectors);
    }

    @Test
    public void testToggleDetector() {
        DetectorService detectorService = mock(DetectorService.class);
        doNothing().when(detectorService).toggleDetector(anyString(), anyBoolean());
        detectorService.toggleDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639", true);
        verify(detectorService, times(1)).toggleDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639", true);
        detectorService.toggleDetector("aeb4d849-847a-45c0-8312-dc0fcf22b639", true);
    }


    private void initTestObjects() {
        ObjectMother mom = ObjectMother.instance();
        elasticsearchDetector = mom.getElasticsearchDetector();
        elasticsearchDetectors.add(elasticsearchDetector);
    }

    private void initDependencies() {
        Mockito.when(detectorRepository.createDetector(any())).thenReturn("1");
        Mockito.when(detectorRepository.findByUuid(Mockito.anyString())).thenReturn(elasticsearchDetectors);
        Mockito.when(detectorRepository.findByCreatedBy(Mockito.anyString())).thenReturn(elasticsearchDetectors);
        Mockito.when(detectorRepository.getLastUpdatedDetectors(Mockito.anyString(), Mockito.anyString())).thenReturn(elasticsearchDetectors);
    }

    private void assertCheck(List<ElasticsearchDetector> actualDetectors) {
        Assert.assertEquals(1, actualDetectors.size());
        Assert.assertEquals("aeb4d849-847a-45c0-8312-dc0fcf22b639", actualDetectors.get(0).getUuid());
        Assert.assertEquals("test-user", actualDetectors.get(0).getCreatedBy());
        Assert.assertEquals(true, actualDetectors.get(0).getEnabled());
    }
}