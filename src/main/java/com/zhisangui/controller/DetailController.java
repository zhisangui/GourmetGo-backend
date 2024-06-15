package com.zhisangui.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhisangui.common.BaseResponse;
import com.zhisangui.common.ResponseUtil;
import com.zhisangui.common.ResultCode;
import com.zhisangui.exception.BusinessException;
import com.zhisangui.model.domain.Detail;
import com.zhisangui.model.domain.Dish;
import com.zhisangui.service.DetailService;
import com.zhisangui.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/detail")
public class DetailController {
    @Resource
    private DetailService detailService;
    @Resource
    private DishService dishService;
    @GetMapping("/getDish")
    public BaseResponse<List<Dish>> getDishByOrderId(Integer orderId) {
        if (orderId == null)
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        if (orderId < 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        QueryWrapper<Detail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderID", orderId);
        List<Detail> detailList = detailService.list(queryWrapper);
        if (null == detailList) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        List<Dish> ans = new ArrayList<>();
        for (Detail detail : detailList) {
            Integer dishID = detail.getDishID();
            Dish dish = dishService.getById(dishID);
            if (null == dish) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            ans.add(dish);
        }
        if (ans.isEmpty()) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        return ResponseUtil.success(ans);
    }

}
