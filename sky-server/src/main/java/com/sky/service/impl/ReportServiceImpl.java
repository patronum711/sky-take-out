package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 获取每日营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnOver = orderMapper.sum(Orders.COMPLETED, beginTime, endTime);
            turnoverList.add(turnOver == null ? 0.0 : turnOver);
        }

        // 转换为字符串封装
        String dateListString = dateList.stream()
                .map(date -> date.toString())
                .collect(Collectors.joining( ","));
        String turnoverListString = turnoverList.stream()
                .map(turnover -> String.valueOf(turnover))
                .collect(Collectors.joining( ","));
        return new TurnoverReportVO(dateListString, turnoverListString);
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 新增用户列表
        List<Integer> newUserList = new ArrayList<>();
        // 总用户列表
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUsers = userMapper.countNewUser(beginTime, endTime);
            Integer totalUsers = userMapper.countTotalUser(endTime);
            newUserList.add(newUsers);
            totalUserList.add(totalUsers);
        }

        // 转换为字符串封装
        String dateListString = dateList.stream()
                .map(date -> date.toString())
                .collect(Collectors.joining(","));
        String newUserListString = newUserList.stream()
                .map(newUser -> newUser.toString())
                .collect(Collectors.joining(","));
        String totalUserListString = totalUserList.stream()
                .map(totalUser -> totalUser.toString())
                .collect(Collectors.joining(","));
        return new UserReportVO(dateListString, newUserListString, totalUserListString);
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 获取订单列表
        List<Integer> orderCountList = new ArrayList<>();
        // 获取有效订单列表
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = orderMapper.countOrder(beginTime, endTime);
            Integer validOrderCount = orderMapper.countValidOrder(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        // 总订单数
        Integer totalOrderCount = 0;
        for(Integer orders : orderCountList){
            totalOrderCount += orders;
        }
        // 有效订单数
        Integer validOrderCount = 0;
        for(Integer orders : validOrderCountList){
            validOrderCount += orders;
        }
        // 计算完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        // 封装返回对象VO
        String dateListString = dateList.stream()
                .map(date -> date.toString())
                .collect(Collectors.joining(","));
        String orderCountListString = orderCountList.stream()
                .map(order -> order.toString())
                .collect(Collectors.joining(","));
        String validOrderCountListString = validOrderCountList.stream()
                .map(validOrder -> validOrder.toString())
                .collect(Collectors.joining(","));
        return OrderReportVO.builder()
                .dateList(dateListString)
                .orderCountList(orderCountListString)
                .validOrderCountList(validOrderCountListString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询指定时间区间内的销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end){
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
