package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.processor.SggProcessor;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.foodiefinder.datapipeline.writer.repository.SggRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class SggWriter implements ItemWriter<String> {

    @Autowired
    SggRepository sggRepository;

    @Autowired
    SggProcessor sggProcessor;


    @PostConstruct
    public void init() {
        // 애플리케이션 실행시 최초 1회 실행
        log.info("CSV 저장을 시작합니다.");
        write("sgg_lat_lon.csv");
        log.info("CSV 저장이 완료되었습니다.");
    }


    @Transactional
    @Override
    public void write(String csvPath) {
        List<Sgg> sggList = sggProcessor.process("sgg_lat_lon.csv");

        for (Sgg sgg : sggList) {
            Sgg exist = sggRepository.findByDosiAndSgg(sgg.getDosi(), sgg.getSgg());
            //1. 신규데이터
            if (exist == null) {
                sggRepository.save(sgg);
                continue;
            }

            //2. 데이터가 이미 존재
            if ((exist.getLat().equals(sgg.getLat()) && exist.getLon().equals(sgg.getLon()))) {
                //2.1 동일한 데이터
                continue;
            }
            // 2.2갱신이 필요한 데이터
            log.info("SGG : {}-{} 를 갱신합니다.", sgg.getDosi(), sgg.getSgg());
            exist.update(sgg.getLon(), sgg.getLat());
        }
    }
}
