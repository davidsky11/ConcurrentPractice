package com.wd.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class GetSMDCDataCallable implements Callable<List<Map<String, String>>> {


    private List<Map<String, String>> dataList;

    private CountDownLatch countDownLatch;

    public GetSMDCDataCallable(List<Map<String, String>> dataList, CountDownLatch countDownLatch) {
        this.dataList = dataList;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public List<Map<String, String>> call(){
        List<Map<String, String>> resListttt = new ArrayList<>();
        try{
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDateStr = "2020-06-01 00:00:00";
            String endDateStr = "2020-06-01 00:01:00";
            Date startTime = sd.parse(startDateStr);
            Date endTime = sd.parse(endDateStr);
            Long startTimeLong = getSecondTimestamp(startTime);
            Long endTimeLong = getSecondTimestamp(endTime);
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, String> map = dataList.get(i);
                String ip = map.get("ip");
                System.out.println("ip===="+ip);
                Map<String, String> dataMap = statisticalData(ip, startTimeLong, endTimeLong);
                if (dataMap != null && dataMap.size() > 0) {
                    String hostIp = dockerHostMappingMap.get(ip);
                    if (StringUtils.isEmpty(hostIp)) {
                        hostIp = "无数据";
                    }
                    dataMap.put("hostIp", hostIp);
                    dataMap.putAll(map);
                    resListttt.add(dataMap);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        this.countDownLatch.countDown();
        return resListttt;
    }

    // TODO: 2020/6/2 自己cookie
    private static String COOKIE = "BJ.7f404856866d4eeb99afdd06ba5c1413";

    private static String ALONEGRAPH_QUERY = "http://mdc.jd.com/alonegraph/query";//获取宿主机IP的URL

    private static String ZEBRA_CONTAINER_CHART4 = "http://mdc.jd.com/api/zebra/container/chart4";//获取秒级监控的URL

    private static String SMDC_MONITOR_QUERYMDCMONITORDATA = "http://smdc.jd.com/monitor/queryMdcMonitorData.do";//smdc 根据系统ID获取系统ip
    // TODO: 2020/6/2  THRESHOLD 代表docker或者宿主机cpu的阈值，大于这个值的才导出数据
    private static float THRESHOLD = 35;

    private static Map<String, String> dockerHostMappingMap = new HashMap<>();

//    private static List<Map<String, String>> getDockerIpBySystem(String systemId, int page) {
//        List<Map<String, String>> resList = new ArrayList<>();
//        for (int j = 1; j <= page; j++) {
//            Map<String, String> headerMap = new HashMap<>();
//            headerMap.put("Cookie", COOKIE);
//            Map<String, String> paramMap = new HashMap<>();
//            paramMap.put("ipPing", "1");
//            paramMap.put("unifySystemId", systemId);
//            paramMap.put("appName", "");
//            paramMap.put("groupName", "");
//            paramMap.put("cpuThredhold", "0");
//            paramMap.put("memoryThredhold", "0");
//            paramMap.put("diskThredhold", "0");
//            paramMap.put("tcpRetrans", "0");
//            paramMap.put("filterIps", "");
//            paramMap.put("umpIpPortDesc", "");
//            paramMap.put("tcpConns", "0");
//            paramMap.put("diskIoUtils", "0");
//            paramMap.put("portSurvival", "0");
//            paramMap.put("t", "0.5745251056205556");
//            paramMap.put("_search", "false");
//            paramMap.put("nd", System.currentTimeMillis() + "");
//            paramMap.put("pageSize", "100");
//            paramMap.put("currentPage", String.valueOf(j));
//            paramMap.put("sidx", "busGroupName asc,");
//            paramMap.put("sord", "asc");
//            paramMap.put("totalrows", "2000");
//            String result = httpPost(headerMap, paramMap, SMDC_MONITOR_QUERYMDCMONITORDATA);
//            if (StringUtils.isNotEmpty(result)) {
//                JSONObject resultJsonObject = JSON.parseObject(result);
//                JSONArray resultListJsonArray = resultJsonObject.getJSONArray("resultList");
//                if (resultListJsonArray != null && resultListJsonArray.size() > 0) {
//                    for (int i = 0; i < resultListJsonArray.size(); i++) {
//                        Map<String, String> map = new HashMap<>();
//                        JSONObject resultListJsonObject = resultListJsonArray.getJSONObject(i);
//                        String ip = resultListJsonObject.getString("ip");
//                        String groupName = resultListJsonObject.getString("groupName");
//                        String appName = resultListJsonObject.getString("appName");
//                        map.put("ip", ip);
//                        map.put("groupName", groupName);
//                        map.put("appName", appName);
//                        resList.add(map);
//                    }
//                }
//            }
//        }
//        return resList;
//    }


    private static Map<String, String> statisticalData(String ip, long startTime, long endTime) {
        Map<String, String> resMap = new HashMap<>();
        try{
            String hostIp = getHostInfo(ip);
            dockerHostMappingMap.put(ip, hostIp);
            Map<String, Float> miaojiDataMap = getZebraContainerChart4(ip, startTime, endTime);
            Map<String, Float> miaojiHostIpDataMap = null;
            if (StringUtils.isNotEmpty(hostIp)) {
                miaojiHostIpDataMap = getZebraContainerChart4(hostIp, startTime, endTime);
            }
            boolean dockerCpu = judgeThreshold(miaojiDataMap);//判断docker cpu使用率有没有大于35%
            boolean hostCpu = judgeThreshold(miaojiHostIpDataMap);//判断宿主机 cpu使用率有没有大于35%
            if (!dockerCpu && !hostCpu) {
                return resMap;
            }
            String dockerMaxTimePoint = "无数据";//docker cpu峰值时间点
            float dockerMaxValue = 0;//docker cpu峰值
            float hostIpValue = 0;//docker峰值时间点宿主机的cpu使用率
            String hostIpMaxTimePoint = "无数据";//宿主机 cpu峰值时间点
            float hostIpMaxValue = 0;//宿主机 cpu峰值
            //如果docker的map或者宿主机map里面有值，代表这两个有cpu使用率大于35%，需要继续出数据
            if (dockerCpu || hostCpu) {
                if (miaojiDataMap != null && !miaojiDataMap.isEmpty()) {
                    Map<String, Float> sortMiaojiDataMap = sortMapByValues(miaojiDataMap);
                    dockerMaxTimePoint = sortMiaojiDataMap.keySet().iterator().next();
                    dockerMaxValue = sortMiaojiDataMap.get(dockerMaxTimePoint);
                }
                if (miaojiHostIpDataMap != null && !miaojiHostIpDataMap.isEmpty()) {
                    Map<String, Float> sortMiaojiHostIpDataMap = sortMapByValues(miaojiHostIpDataMap);
                    if (!"无数据".equals(dockerMaxTimePoint)) {
                        hostIpValue = sortMiaojiHostIpDataMap.get(dockerMaxTimePoint);
                    }
                    hostIpMaxTimePoint = sortMiaojiHostIpDataMap.keySet().iterator().next();
                    hostIpMaxValue = sortMiaojiHostIpDataMap.get(hostIpMaxTimePoint);
                }
            }
            resMap.put("dockerMaxTimePoint", dockerMaxTimePoint);
            resMap.put("dockerMaxValue", String.valueOf(dockerMaxValue));
            resMap.put("hostIpValue", String.valueOf(hostIpValue));
            resMap.put("hostIpMaxTimePoint", String.valueOf(hostIpMaxTimePoint));
            resMap.put("hostIpMaxValue", String.valueOf(hostIpMaxValue));
        }catch (Exception e){
            System.out.println("statisticalData error,ip:"+ip);
            e.printStackTrace();
        }
        return resMap;
    }

    private static Map<String, Float> getZebraContainerChart4(String ip, long startTime, long endTime) {
        Map<String, Float> resMap = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Cookie", COOKIE);
        JSONObject paramJsonObject = new JSONObject();
        paramJsonObject.put("ip", ip);
        paramJsonObject.put("host", "false");
        paramJsonObject.put("monitor", "SYSTEM");
        paramJsonObject.put("startTime", startTime);
        paramJsonObject.put("endTime", endTime);
        String result = jsonHttpPost(headerMap, paramJsonObject, ZEBRA_CONTAINER_CHART4);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                if (dataJsonObject != null && !dataJsonObject.isEmpty()) {
                    JSONObject cpuPercentJSONObject = dataJsonObject.getJSONObject("CpuPercent");
                    if (cpuPercentJSONObject != null && !cpuPercentJSONObject.isEmpty()) {
                        JSONObject cpuPercentJSONObject2 = cpuPercentJSONObject.getJSONObject("CpuPercent");
                        if (cpuPercentJSONObject2 != null && !cpuPercentJSONObject2.isEmpty()) {
                            JSONArray dataJsonArray = cpuPercentJSONObject2.getJSONArray("data");
                            if (dataJsonArray != null && !dataJsonArray.isEmpty()) {
                                for (int i = 0; i < dataJsonArray.size(); i++) {
                                    JSONObject dataJsonObject2 = dataJsonArray.getJSONObject(i);
                                    String timePoint = dataJsonObject2.getString("x");
                                    float cpuPercent = dataJsonObject2.getFloatValue("y");
                                    resMap.put(dataJsonObject2.getString("x"), dataJsonObject2.getFloatValue("y"));
                                }
                            }
                        }
                    }
                }
            }
        }
        return resMap;
    }

    /**
     * 获取宿主机IP
     *
     * @param ip
     */
    private static String getHostInfo(String ip) {
        String hostIp = "";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", COOKIE);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ip", ip);
        String result = httpPost(headerMap, paramMap, ALONEGRAPH_QUERY);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject resultJsonObject = JSON.parseObject(result);
            JSONObject hostInfoJsonObject = resultJsonObject.getJSONObject("hostInfo");
            if (hostInfoJsonObject != null && !hostInfoJsonObject.isEmpty()) {
                hostIp = hostInfoJsonObject.getString("ip");
            }
        }
        return hostIp;
    }

    private static String httpPost(Map<String, String> headerMap, Map<String, String> paramMap, String url) {
        CloseableHttpClient httpClient = null;
        String result = "";
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null && !headerMap.isEmpty()) {
                Set<String> headerMapKeySet = headerMap.keySet();
                for (String headerMapKey : headerMapKeySet) {
                    httpPost.setHeader(headerMapKey, headerMap.get(headerMapKey));
                }
            }

            if (paramMap != null && !paramMap.isEmpty()) {
                Set<String> paramMapKeySet = paramMap.keySet();
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (String paramMapKey : paramMapKeySet) {
                    list.add(new BasicNameValuePair(paramMapKey, paramMap.get(paramMapKey)));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception e) {
            System.out.println("httpPost error,url:" + url);
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String jsonHttpPost(Map<String, String> headerMap, JSONObject jsonObject, String url) {
        CloseableHttpClient httpClient = null;
        String result = "";
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null && !headerMap.isEmpty()) {
                Set<String> headerMapKeySet = headerMap.keySet();
                for (String headerMapKey : headerMapKeySet) {
                    httpPost.setHeader(headerMapKey, headerMap.get(headerMapKey));
                }
            }
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception e) {
            System.out.println("jsonHttpPost error,url:" + url);
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static Long getSecondTimestamp(Date date) {
        if (null == date) {
            return 0L;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Long.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0L;
        }
    }

    private static LinkedHashMap<String, Float> sortMapByValues(Map<String, Float> aMap) {
        LinkedHashMap<String, Float> finalOut = new LinkedHashMap<>();
        aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
        return finalOut;
    }

    private static boolean judgeThreshold(Map<String, Float> map) {
        boolean res = false;
        if (map == null || map.isEmpty()) {
            return res;
        }
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            float f = map.get(key);
            if (f > THRESHOLD) {
                res = true;
            }
        }
        return res;
    }
}

