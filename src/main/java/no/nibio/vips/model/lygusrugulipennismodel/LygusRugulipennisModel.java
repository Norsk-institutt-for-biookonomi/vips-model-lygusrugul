/*
 * Copyright (c) 2016 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of LygusRugulipennisModel.
 * LygusRugulipennisModel is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * LygusRugulipennisModel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with LygusRugulipennisModel.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */

package no.nibio.vips.model.lygusrugulipennismodel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
import no.nibio.vips.entity.ResultImpl;
import no.nibio.vips.entity.WeatherObservation;
import no.nibio.vips.i18n.I18nImpl;
import no.nibio.vips.model.ConfigValidationException;
import no.nibio.vips.model.Model;
import no.nibio.vips.model.ModelExcecutionException;
import no.nibio.vips.model.ModelId;
import no.nibio.vips.util.CommonNamespaces;
import no.nibio.vips.util.InvalidAggregationTypeException;
import no.nibio.vips.util.ModelUtil;
import no.nibio.vips.util.WeatherElements;
import no.nibio.vips.util.WeatherObservationListException;
import no.nibio.vips.util.WeatherUtil;

/**
 * Temperature based model for infection risk of Lygus rugulipennis (NO: Håra engtege)
 * Counting number of days from start of warning period up until and including today 
 * where the TX > treshold value (pt. 15 degrees Celcius). Uses historical 
 * measurements as far as possible/available
 * 
 * @copyright 2016 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor J. Johansen <tor.johansen@nibio.no> (Model research)
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no> (Programming)
 * 
 */
public class LygusRugulipennisModel extends I18nImpl implements Model{
    public final static ModelId MODEL_ID = new ModelId("LYGUSRUGUL");
    
    private TimeZone timeZone;
    private DataMatrix dataMatrix;
    
    public final static Double LOWER_TEMPERATURE_THRESHOLD = 10.0; // Degrees Celcius
    public final static Double UPPER_TEMPERATURE_THRESHOLD = 15.0; // Degrees Celcius
    public final static Integer MINIMUM_DAYS_ABOVE_UPPER_THRESHOLD = 3;
    
    private final ModelUtil modelUtil;

    public LygusRugulipennisModel()
    {
        super("no.nibio.vips.model.lygusrugulipennismodel.texts");
        this.modelUtil = new ModelUtil();
    }
    @Override
    public List<Result> getResult() throws ModelExcecutionException {
        
       this.aggregateDaysAboveThreshold();
       //System.out.println(this.dataMatrix.toString());
       List<Result> retVal = new ArrayList<>();
       Date currentDate = this.dataMatrix.getFirstDateWithParameterValue(DataMatrix.DAYS_ABOVE_THRESHOLD);
       Calendar cal = Calendar.getInstance(this.timeZone);
       DecimalFormat dFormat = new DecimalFormat("###.##");
       while(this.dataMatrix.getParamIntValueForDate(currentDate, DataMatrix.DAYS_ABOVE_THRESHOLD) != null)
       {
           Result result = new ResultImpl();
           result.setValidTimeStart(currentDate);
           
           // Adding model parameters
           Integer daysAboveUpperThreshold = this.dataMatrix.getParamIntValueForDate(currentDate, DataMatrix.DAYS_ABOVE_THRESHOLD);
           Double TXDCurrentDate = ((WeatherObservation)this.dataMatrix.getParamValueForDate(currentDate, DataMatrix.TXD)).getValue();
           
           result.setValue(CommonNamespaces.NS_WEATHER, DataMatrix.TXD, dFormat.format(TXDCurrentDate));
           result.setValue(this.getModelId().toString(), DataMatrix.DAYS_ABOVE_THRESHOLD, String.valueOf(daysAboveUpperThreshold));
           result.setValue(this.getModelId().toString(), "LOWER_TEMPERATURE_THRESHOLD", String.valueOf(LygusRugulipennisModel.LOWER_TEMPERATURE_THRESHOLD));
           result.setValue(this.getModelId().toString(), "UPPER_TEMPERATURE_THRESHOLD", String.valueOf(LygusRugulipennisModel.UPPER_TEMPERATURE_THRESHOLD));
           
           // Deciding warning status
           Integer warningStatus = Result.WARNING_STATUS_NO_RISK;
           // When number of days with max temp >= upper threshold reaches 3
           // The warning status is at least yellow as long as max temp > 10
           if(daysAboveUpperThreshold >= LygusRugulipennisModel.MINIMUM_DAYS_ABOVE_UPPER_THRESHOLD && TXDCurrentDate >= LygusRugulipennisModel.LOWER_TEMPERATURE_THRESHOLD)
           {
               warningStatus = Result.WARNING_STATUS_MINOR_RISK;
               // When 4 days or more with max temp >= upper threshold,
               // all days with max temp >= upper threshold causes high risk of infection
               if(daysAboveUpperThreshold > LygusRugulipennisModel.MINIMUM_DAYS_ABOVE_UPPER_THRESHOLD && TXDCurrentDate >= LygusRugulipennisModel.UPPER_TEMPERATURE_THRESHOLD)
               {
                   warningStatus = Result.WARNING_STATUS_HIGH_RISK;
               }
           }
           result.setWarningStatus(warningStatus);
           retVal.add(result);
           // Moving on...
           cal.setTime(currentDate);
           cal.add(Calendar.DATE, 1);
           currentDate = cal.getTime();
       }
       return retVal;
    }

    @Override
    public ModelId getModelId() {
        return LygusRugulipennisModel.MODEL_ID;
    }

    @Override
    public String getModelName() {
        return this.getModelName(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelName(String language) {
        return this.getText("name", language);
    }

    @Override
    public String getLicense() {
        return "Copyright (c) 2016 NIBIO <http://www.nibio.no/>. \n" +
            "\n" +
            "This file is part of LygusRugulipennisModel. \n" + 
            "LygusRugulipennisModel is free software: you can redistribute it and/or modify \n" + 
            "it under the terms of the NIBIO Open Source License as published by \n" + 
            "NIBIO, either version 1 of the License, or (at your option) any \n" +
            "later version. \n" +
            "\n" +
            "LygusRugulipennisModel is distributed in the hope that it will be useful, \n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of \n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the \n" +
            "NIBIO Open Source License for more details. \n" +
            "" +
            "You should have received a copy of the NIBIO Open Source License \n" +
            "along with LygusRugulipennisModel.  If not, see <http://www.nibio.no/licenses/>. \n";
    }

    @Override
    public String getCopyright() {
        return "(c) 2016 NIBIO (http://www.nibio.no/). Contact: post@nibio.no";
    }

    @Override
    public String getModelDescription() {
        return this.getModelDescription(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelDescription(String language) {
        try
        {
            return this.modelUtil.getTextWithBase64EncodedImages(this.getText("description", language), this.getClass());
        }
        catch(IOException ex)
        {
            return this.getText("description", language);
        }
    }

    @Override
    public String getWarningStatusInterpretation() {
        return this.getWarningStatusInterpretation(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getWarningStatusInterpretation(String language) {
        return this.getText("statusInterpretation", language);
    }

    @Override
    public String getModelUsage() {
        return this.getModelUsage(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelUsage(String language) {
        return this.getText("usage", language);
    }

    @Override
    public String getSampleConfig() {
        return  "{\n" +
                "\t\"loginInfo\":{\n" +
                "\t\t\"username\":\"example\",\n" +
                "\t\t\"password\":\"example\"\n" +
                "\t},\n" +
                "\t\"modelId\":\"" + MODEL_ID.toString() + "\",\n" +
                "\t\"configParameters\":{\n" +
                "\t\t\"observations\":[\n" +
                "\t\t{\n" +
                "\t\t\t\t\"timeMeasured\": \"2015-01-01T00:00:00+02:00\",\n" +
                "\t\t\t\t\"elementMeasurementTypeId\":\"TX\",\n" +
                "\t\t\t\t\"logIntervalId\":2,\n" +
                "\t\t\t\t\"value\":1.1\n" +
                "\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}\n";
    }

    @Override
    public void setConfiguration(ModelConfiguration config) throws ConfigValidationException {
        
        this.dataMatrix = new DataMatrix();
        // Setting timezone
        this.timeZone = TimeZone.getTimeZone((String) config.getConfigParameter("timeZone"));
        // Importing weather data, creating collections
        // Either daily values of TX
        // OR hourly values of TX
        List<WeatherObservation> TX = new ArrayList<>();
        // OR lastly hourly values of TM (not optimal, but better than nothing)
        List<WeatherObservation> TM = new ArrayList<>();
        WeatherUtil wUtil = new WeatherUtil();
        List<WeatherObservation> observations = this.modelUtil.extractWeatherObservationList(config.getConfigParameter("observations"));
        for(WeatherObservation o:observations)
        {
            switch(o.getElementMeasurementTypeId())
            {
                case WeatherElements.TEMPERATURE_MAXIMUM:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1D))
                    {
                        o.setTimeMeasured(wUtil.pragmaticAdjustmentToMidnight(o.getTimeMeasured(), timeZone));
                        this.dataMatrix.setParamValueForDate(o.getTimeMeasured(), DataMatrix.TXD, o);
                    }else{
                        TX.add(o);
                    }break;
                case WeatherElements.TEMPERATURE_MEAN:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        TM.add(o);
                    }
                default:
                    // Keep calm and continue importing data
                    break;
            }
        }
        
        // So, what have we got here?
        if(dataMatrix.getFirstDateWithParameterValue(DataMatrix.TXD) == null)
        {
            List<WeatherObservation> TXDCalculated = null;
            // No daily TX values, check for hourly TX values
            if(!TX.isEmpty())
            {
                try
                {
                    TXDCalculated = new WeatherUtil().getAggregatedDailyValues(TX, timeZone, 15, 15, WeatherUtil.AGGREGATION_TYPE_MAXIMUM);
                }catch(WeatherObservationListException | InvalidAggregationTypeException ex)
                {
                    throw new ConfigValidationException(ex.getMessage());
                }
            }
            else if(!TM.isEmpty())
            {
                try
                {
                    TXDCalculated = new WeatherUtil().getAggregatedDailyValues(TM, timeZone, 15, 15, WeatherUtil.AGGREGATION_TYPE_MAXIMUM);
                }catch(WeatherObservationListException | InvalidAggregationTypeException ex)
                {
                    throw new ConfigValidationException(ex.getMessage());
                }
            }
            
            if(TXDCalculated != null && !TXDCalculated.isEmpty())
            {
                for(WeatherObservation o:TXDCalculated)
                {
                    this.dataMatrix.setParamValueForDate(o.getTimeMeasured(), DataMatrix.TXD, o);
                }
            }
        }
        
        if(dataMatrix.getFirstDateWithParameterValue(DataMatrix.TXD) == null)
        {
            throw new ConfigValidationException("Could not find any valid weather data.");
        }
    }

    private void aggregateDaysAboveThreshold() {
        Date currentDate = dataMatrix.getFirstDateWithParameterValue(DataMatrix.TXD);
        //System.out.println("currentDate=" + currentDate +", value=" + ((WeatherObservation) dataMatrix.getParamValueForDate(currentDate, DataMatrix.TXD)).getValue());
        Calendar cal = Calendar.getInstance(this.timeZone);
        Integer daysAboveThreshold = 0;
        while(dataMatrix.getParamValueForDate(currentDate, DataMatrix.TXD) != null)
        {
            Double TXD = ((WeatherObservation) dataMatrix.getParamValueForDate(currentDate, DataMatrix.TXD)).getValue();
            if(TXD >= LygusRugulipennisModel.UPPER_TEMPERATURE_THRESHOLD)
            {
                daysAboveThreshold++;
            }
            dataMatrix.setParamIntValueForDate(currentDate, DataMatrix.DAYS_ABOVE_THRESHOLD, daysAboveThreshold);
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, 1);
            currentDate = cal.getTime();
        }
    }
}
