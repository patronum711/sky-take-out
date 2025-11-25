package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据（需要回填）
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, String orderNumber);


    /**
     * 分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 根据状态和日期统计当日营业额
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select sum(amount) from orders " +
            "where status = #{status} " +
            "and order_time >= #{beginTime} " +
            "and order_time <= #{endTime}")
    Double sum(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据时间范围统计订单数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(*) from orders " +
            "where order_time >= #{beginTime} " +
            "and order_time <= #{endTime}")
    Integer countOrder(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据时间范围统计有效订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    @Select("select count(*) from orders " +
            "where order_time >= #{beginTime} " +
            "and order_time <= #{endTime} " +
            "and status = #{status}")
    Integer countValidOrder(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 查询商品销量排名
     * @param begin
     * @param end
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
