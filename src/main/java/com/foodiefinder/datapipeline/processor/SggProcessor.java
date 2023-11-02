package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Component
public class SggProcessor implements ItemProcessor<String, List<Sgg>> {

    @Override
    public List<Sgg> process(String csvPath) {
        try {
            Resource resource = new ClassPathResource(csvPath);
            InputStream inputStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build(); // 첫 번째 줄은 헤더이므로 스킵
            List<Sgg> sggList = get(csvReader);
            return sggList;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.NOT_VALID_FILEPATH);
        }
    }

    private static List<Sgg> get(CSVReader csvReader) {
        List<Sgg> sggList = new ArrayList<>();
        String[] line;
        try {
            while ((line = csvReader.readNext()) != null) {
                String dosi = line[0];
                String sgg = line[1];
                Double lon = Double.parseDouble(line[2]);
                Double lat = Double.parseDouble(line[3]);
                Sgg sggObject = Sgg.builder()
                        .dosi(dosi)
                        .sgg(sgg)
                        .lat(lat)
                        .lon(lon)
                        .build();
                sggList.add(sggObject);
            }
        } catch (CsvValidationException e) {
            throw new CustomException(ErrorCode.NOT_VALID_CSV);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CSV_FILE_EXCEPTION);
        }
        return sggList;
    }
}
