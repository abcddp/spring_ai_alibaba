package com.abc.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

//public class TimeTools {
//
//    @Tool(description = "通过时区id获取当前时间")
//    public String getTimeByZoneId(@ToolParam(description = "时区id, 比如Asia/Shanghai")
//                                  String zoneId) {
//        ZoneId zid = ZoneId.of(zoneId);
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
//        return zonedDateTime.format(formatter);
//    }
//
//}