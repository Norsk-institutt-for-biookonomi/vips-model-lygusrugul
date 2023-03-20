/*
 * Copyright (c) 2016 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of LygusRugulipennisModel.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 */

package no.nibio.vips.model.lygusrugulipennismodel;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static junit.framework.Assert.fail;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
import no.nibio.vips.entity.WeatherObservation;
import no.nibio.vips.model.ModelId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class LygusRugulipennisModelTest {
    
    public LygusRugulipennisModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getResult method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetResult() throws Exception {
        System.out.println("getResult");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        ModelConfiguration config = this.getConfiguration("/aas_2015_txd.json");
        instance.setConfiguration(config);
        List<Result> result = instance.getResult();
        assertNotNull(result);
        config = this.getConfiguration("/AgDir_2023.json");
        instance.setConfiguration(config);
        result = instance.getResult();
        assertNotNull(result);
        /*
        Collections.sort(result);
        for(Result oneResult:result)
        {
            System.out.println(oneResult);
        }*/
        
        
    }

    /**
     * Test of getModelId method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetModelId() {
        System.out.println("getModelId");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        ModelId expResult = LygusRugulipennisModel.MODEL_ID;
        ModelId result = instance.getModelId();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getModelName method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetModelName_0args() {
        System.out.println("getModelName");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getModelName();
        assertNotNull(result);

    }

    
    /**
     * Test of getLicense method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetLicense() {
        System.out.println("getLicense");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getLicense();
        assertNotNull(result);
    }

    /**
     * Test of getCopyright method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetCopyright() {
        System.out.println("getCopyright");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getCopyright();
        assertNotNull(result);
    }

    /**
     * Test of getModelDescription method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetModelDescription_0args() {
        System.out.println("getModelDescription");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getModelDescription();
        assertNotNull(result);
    }

    
    /**
     * Test of getWarningStatusInterpretation method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetWarningStatusInterpretation_0args() {
        System.out.println("getWarningStatusInterpretation");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getWarningStatusInterpretation();
        assertNotNull(result);
    }

    
    /**
     * Test of getModelUsage method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetModelUsage_0args() {
        System.out.println("getModelUsage");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getModelUsage();
        assertNotNull(result);
    }

    

    /**
     * Test of getSampleConfig method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testGetSampleConfig() {
        System.out.println("getSampleConfig");
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        String result = instance.getSampleConfig();
        assertNotNull(result);
    }

    /**
     * Test of setConfiguration method, of class LygusRugulipennisModel.
     */
    @org.junit.Test
    public void testSetConfiguration() throws Exception {
        System.out.println("setConfiguration");
        ModelConfiguration config = this.getConfiguration("/aas_2015_txd.json");
        assertNotNull(config);
        LygusRugulipennisModel instance = new LygusRugulipennisModel();
        instance.setConfiguration(config);

    }
    
    private ModelConfiguration getConfiguration(String fileName)
    {
        try {
            ModelConfiguration config = new ModelConfiguration();
            config.setModelId(LygusRugulipennisModel.MODEL_ID.toString());
            
            
            config.setConfigParameter("timeZone", "Europe/Oslo");
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream(fileName));
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createParser(inputStream);
            JsonNode all = jp.readValueAsTree();
            List<WeatherObservation> observations = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            Date firstDate = null;
            Date lastDate = null;
            if(all.isArray())
            {
                for(JsonNode node : all){
                    Date timeMeasured = (Date)mapper.convertValue(node.get("timeMeasured").asText(), new TypeReference<Date>(){});
                    if(firstDate == null || firstDate.compareTo(timeMeasured) > 0)
                    {
                        firstDate = timeMeasured;
                    }
                    if(lastDate == null || lastDate.compareTo(timeMeasured) < 0)
                    {
                        lastDate = timeMeasured;
                    }
                    //System.out.println(node.toString());
                    WeatherObservation observation = new WeatherObservation();
                    observation.setTimeMeasured(timeMeasured);
                    observation.setLogIntervalId(node.get("logIntervalId").asInt());
                    observation.setElementMeasurementTypeId(node.get("elementMeasurementTypeId").asText());
                    observation.setValue(node.get("value").asDouble());
                    observations.add(observation);
                }

            }
            else
            {
                fail("Data input from file is not a JSON array");
            }
            config.setConfigParameter("observations", observations);

            
            return config;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } 
    }
}
