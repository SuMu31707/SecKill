package com.sumu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sumu.seckill.pojo.Goods;
import com.sumu.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> getGoodsVo();

    GoodsVo getGoodsVoByGoodsId(Long goodsId);
}
