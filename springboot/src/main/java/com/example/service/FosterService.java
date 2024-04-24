package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.FosterStatusEnum;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.RoomStatusEnum;
import com.example.entity.Account;
import com.example.entity.Foster;
import com.example.entity.Notice;
import com.example.entity.Room;
import com.example.mapper.FosterMapper;
import com.example.mapper.NoticeMapper;
import com.example.mapper.RoomMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 寄养信息表业务处理
 **/
@Service
public class FosterService {

    @Resource
    private FosterMapper fosterMapper;
    @Resource
    private RoomMapper roomMapper;

    /**
     * 新增
     */
    public void add(Foster foster) {
        foster.setStatus(FosterStatusEnum.ADOPT_NO.status);
        fosterMapper.insert(foster);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        fosterMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            fosterMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Foster foster) {
        fosterMapper.updateById(foster);

        // 对应的房间状态要修改成占用，房间对应的在养宠物也要更新
        Room room = roomMapper.selectById(foster.getRoomId());
        if (ObjectUtil.isNotEmpty(room)) {
            room.setStatus(RoomStatusEnum.ROOM_NO.status);
            room.setAnimal(foster.getName());
            roomMapper.updateById(room);
        }
    }

    /**
     * 根据ID查询
     */
    public Foster selectById(Integer id) {
        return fosterMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Foster> selectAll(Foster foster) {
        return fosterMapper.selectAll(foster);
    }

    /**
     * 分页查询
     */
    public PageInfo<Foster> selectPage(Foster foster, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            foster.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Foster> list = fosterMapper.selectAll(foster);
        return PageInfo.of(list);
    }

}