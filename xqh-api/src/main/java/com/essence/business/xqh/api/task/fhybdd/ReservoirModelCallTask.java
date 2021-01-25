package com.essence.business.xqh.api.task.fhybdd;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReservoirModelCallTask {


    /**
     * 水库模型调用
     * @param
     * @return
     * @throws InterruptedException
     */
    CompletableFuture<Integer> reservoirModelCall(String skdd_run, String skdd_model_template,String skdd_model_template_input, String skdd_model_template_output,Long step, List<String> rkll,String SWYB_MODEL_TEMPLATE);

    CompletableFuture<String> text(String name,int i);

    void haha();

    //CompletableFuture<String> reservoirModelCall(String name) throws Exception ;
}
