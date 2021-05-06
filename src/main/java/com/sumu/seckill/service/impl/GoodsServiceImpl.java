package com.sumu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sumu.seckill.mapper.GoodsMapper;
import com.sumu.seckill.pojo.Goods;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取面纱商品列表
     * @return
     */
    @Override
    public List<GoodsVo> getGoodsVo() {

        return goodsMapper.getGoodsVo();
    }

    /**
     * 根据商品id查询商品详情
     * @return
     */
    @Override
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }
}
