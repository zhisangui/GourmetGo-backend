package com.zhisangui.model.domain.response;

import com.zhisangui.model.domain.Dish;
import com.zhisangui.model.domain.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDescription {
    private Integer id;
    private String username;
    private Long totalAmount;
    private Date orderTime;
    private Integer status;
    private Date createTime;
    private List<Dish> dishList;
}
