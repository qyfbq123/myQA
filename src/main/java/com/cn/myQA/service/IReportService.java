package com.cn.myQA.service;

public interface IReportService {
    /**
     * 周报
     * @throws Exception
     */
    public void weekReport() throws Exception;
    
    /**
     * 日报
     * @throws Exception
     */
    public void dailyReport() throws Exception;
}
