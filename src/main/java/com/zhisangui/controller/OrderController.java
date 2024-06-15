package com.zhisangui.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhisangui.common.BaseResponse;
import com.zhisangui.common.ResponseUtil;
import com.zhisangui.common.ResultCode;
import com.zhisangui.exception.BusinessException;
import com.zhisangui.model.domain.Detail;
import com.zhisangui.model.domain.Dish;
import com.zhisangui.model.domain.Orders;
import com.zhisangui.model.domain.User;
import com.zhisangui.model.domain.request.UpdateOrderRequest;
import com.zhisangui.model.domain.response.OrdersDescription;
import com.zhisangui.service.DetailService;
import com.zhisangui.service.DishService;
import com.zhisangui.service.OrdersService;
import com.zhisangui.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/order")
@Slf4j
@RestController
public class OrderController {
    @Resource
    private UserService userService;
    @Resource
    private OrdersService ordersService;
    @Resource
    private DishService dishService;
    @Resource
    private DetailService detailService;
    @GetMapping("/search")
    public BaseResponse<List<OrdersDescription>> searchOrders() {
        log.info("searching ordersDescription...");
        List<Orders> ordersList = ordersService.list();
        if (null == ordersList) {
            throw new BusinessException(ResultCode.NULL_ERROR);
        }
        List<OrdersDescription> ordersDescriptionList = new ArrayList<>();
        for (Orders orders : ordersList) {
            Integer orderId = orders.getId();
            if (orderId == null) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            QueryWrapper<Detail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("orderID", orderId);
            List<Detail> detailList = detailService.list(queryWrapper);
            if (null == detailList) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            List<Dish> dishList = new ArrayList<>();
            for (Detail detail : detailList) {
                Integer dishID = detail.getDishID();
                Dish dish = dishService.getById(dishID);
                if (null == dish) {
                    throw new BusinessException(ResultCode.SYSTEM_ERROR);
                }
                dishList.add(dish);
            }
            User user = userService.getById(orders.getUserID());
            ordersDescriptionList.add(new OrdersDescription(orders.getId(), user.getUsername(), orders.getTotalAmount(), orders.getOrderTime(), orders.getStatus(),orders.getCreateTime(), dishList));
        }
        if (ordersDescriptionList.isEmpty())
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        return ResponseUtil.success(ordersDescriptionList);
    }
    @GetMapping("/update")
    public BaseResponse<OrdersDescription> updateOrders(UpdateOrderRequest updateOrderRequest) {
        // 获取 id 对应的 Orders
        if (updateOrderRequest == null)
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        Integer id = updateOrderRequest.getId();

        Orders orders = ordersService.getById(id);
        if (orders == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        // 更新 Orders
        orders.setStatus(updateOrderRequest.getStatus());
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        ordersService.update(orders, updateWrapper);

        // 根据 id 获取返回的所有数据，并封装成 OrdersDescription
        Integer orderId = orders.getId();
        if (orderId == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        QueryWrapper<Detail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderID", orderId);
        List<Detail> detailList = detailService.list(queryWrapper);
        if (null == detailList) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        List<Dish> dishList = new ArrayList<>();
        for (Detail detail : detailList) {
            Integer dishID = detail.getDishID();
            Dish dish = dishService.getById(dishID);
            if (null == dish) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            dishList.add(dish);
        }
        User user = userService.getById(orders.getUserID());
        OrdersDescription ordersDescription = new OrdersDescription(orders.getId(), user.getUsername(), orders.getTotalAmount(), orders.getOrderTime(), orders.getStatus(), orders.getCreateTime(), dishList);
        return ResponseUtil.success(ordersDescription);
    }
}
