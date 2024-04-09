package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Adopt;
import com.example.mapper.AdoptMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 领养信息表业务处理
 **/
@Service
public class AdoptService {

    @Resource
    private AdoptMapper adoptMapper;

    /**
     * 新增
     */
    public void add(Adopt adopt) {
        adoptMapper.insert(adopt);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        adoptMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adoptMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Adopt adopt) {
        adoptMapper.updateById(adopt);
    }

    /**
     * 根据ID查询
     */
    public Adopt selectById(Integer id) {
        return adoptMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Adopt> selectAll(Adopt adopt) {
        return adoptMapper.selectAll(adopt);
    }

    /**
     * 分页查询
     */
    public PageInfo<Adopt> selectPage(Adopt adopt, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Adopt> list = adoptMapper.selectAll(adopt);
        return PageInfo.of(list);
    }

}