package com.megastudy.surlkdh.domain.statistics.service.port;

import com.megastudy.surlkdh.domain.statistics.entity.ShorturlClicksSec;

import java.util.List;

public interface ShorturlClicksSecRepository {

    void upsertAll(List<ShorturlClicksSec> statistics);
}
