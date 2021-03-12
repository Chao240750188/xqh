package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ModelCallHandleDataService {


    CompletableFuture<Integer> saveRainToDb(List<YwkPlaninRainfall> result);
}
