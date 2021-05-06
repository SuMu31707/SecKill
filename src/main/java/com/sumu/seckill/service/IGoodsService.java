package com.sumu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sumu.seckill.pojo.Goods;
import com.sumu.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> getGoodsVo();

    GoodsVo getGoodsVoByGoodsId(Long goodsId);
}
