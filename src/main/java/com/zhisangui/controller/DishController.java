package com.zhisangui.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhisangui.common.BaseResponse;
import com.zhisangui.common.ResponseUtil;
import com.zhisangui.common.ResultCode;
import com.zhisangui.constant.UserConstant;
import com.zhisangui.exception.BusinessException;
import com.zhisangui.model.domain.Detail;
import com.zhisangui.model.domain.Dish;
import com.zhisangui.model.domain.Orders;
import com.zhisangui.model.domain.User;
import com.zhisangui.service.DetailService;
import com.zhisangui.service.DishService;
import com.zhisangui.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private OrdersService ordersService;

    @Resource
    private DetailService detailService;
    /**
     * 增加菜品
     * @param dish 需要增加的菜品信息
     * @return     新增菜品的id
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addDish(@RequestBody Dish dish) {
        if (dish == null) {
            throw new BusinessException(ResultCode.NULL_ERROR);
        }
        String name = dish.getName();
        Integer price = dish.getPrice();
        String description = dish.getDescription();
        String dishUrl = dish.getDishUrl();

        if(StringUtils.isAnyBlank(name, description, dishUrl)) {
            throw new BusinessException(ResultCode.NULL_ERROR);
        }
        Dish newDish = new Dish();
        newDish.setName(name);
        newDish.setPrice(price);
        newDish.setDescription(description);
        newDish.setDishUrl(dishUrl);
        boolean res = dishService.save(newDish);
        if (!res) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        return ResponseUtil.success(newDish.getId());
    }

    /**
     *  对菜品进行修改
     * @param dish  原有菜品
     * @return      修改结果
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateDish(@RequestBody Dish dish) {
        if (dish == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        Integer id = dish.getId();
        String name = dish.getName();
        Integer price = dish.getPrice();
        String description = dish.getDescription();
        String dishUrl = dish.getDishUrl();

        Dish originDish = dishService.getById(id);

        // 没有查到当前菜品，则新增
        if (originDish == null)
            return addDish(dish);

        // 找到与当前菜品id相同的菜，但菜名不一致，则新增（todo:前端出现bug，返回的dish的id为当前菜品数目，不是严格意义的id）
        if (!originDish.getName().equals(dish.getName()))
            return addDish(dish);

        if (StringUtils.isNotBlank(name)) {
            originDish.setName(name);
        }
        if (price != null && price != 0) {
            originDish.setPrice(price);
        }
        if (StringUtils.isNotBlank(description)) {
            originDish.setDescription(description);
        }
        if (StringUtils.isNotBlank(dishUrl)) {
            originDish.setDishUrl(dishUrl);
        }
        originDish.setUpdateTime(new Date());

        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.eq("id", id);
        boolean res = dishService.update(originDish, dishUpdateWrapper);
        if (!res) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        return ResponseUtil.success(originDish.getId());
    }

    /**
     * 删除菜品
     * @param
     * @return       删除的结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDish(@RequestBody Dish dish) {
        if (dish == null)
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        Integer id = dish.getId();
        if (id < 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        boolean res = dishService.removeById(id);
        if (!res) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        return ResponseUtil.success(res);
    }

    /**
     * 搜索菜品
     * @param name
     * @param request
     * @return 搜索到的菜品列表
     */
    @GetMapping("/search")
    public BaseResponse<List<Dish>> searchDish(String name, HttpServletRequest request) { // todo: name的作用？
        log.info("searching dishes...");
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        List<Dish> dishList = dishService.list(queryWrapper);
        return ResponseUtil.success(dishList);
    }

    /**
     * 顾客下单
     * @param request 从请求头中拿 sessionid，得到下单的客户信息
     * @param dishes  客户下单的菜品信息
     * @return        下单的结果： 成功 or 失败
     */
    @PostMapping("/order")
    public BaseResponse<Boolean> orderDish(HttpServletRequest request, @RequestBody List<Dish> dishes) {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (null == user) {
            log.info("非法顾客");
            throw new BusinessException(ResultCode.NO_AUTH);
        }
        if (null == dishes) {
            log.info("非法订单");
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        Integer userId = user.getId();

        // 向 Order 插入数据
        long totalAmount = 0;
        for (Dish dish : dishes) {
            totalAmount += dish.getPrice();
        }
        Orders order = new Orders();
        order.setUserID(userId);
        order.setTotalAmount(totalAmount);
        order.setOrderTime(new Date());
        boolean res = ordersService.save(order);
        System.out.println(res);
        if (!res) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        // 向 Detail 插入数据
        int orderId = order.getId();
        for (Dish dish : dishes) {
            Detail detail = new Detail();
            detail.setOrderID(orderId);
            detail.setDishID(dish.getId());
            boolean result = detailService.save(detail);
            if (!result) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
        }

        System.out.println("====================================================");
        System.out.println("下单的顾客id： " + userId);
        System.out.println("下单的产品：" + dishes.toString());
        System.out.println("====================================================");
        return ResponseUtil.success(true);
     }

}
