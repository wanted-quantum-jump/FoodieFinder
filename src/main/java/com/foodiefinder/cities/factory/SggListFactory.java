package com.foodiefinder.cities.factory;

import com.foodiefinder.cities.domain.SggList;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SggListFactory {
    public SggList createSggList(List<Sgg> sggs) {
        return new SggList(sggs);
    }
}
