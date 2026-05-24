package com.example.mc_server.service;

import com.example.mc_server.dto.CompanionList;
import com.example.mc_server.entity.CompanionAvatar;
import com.example.mc_server.entity.User;
import com.example.mc_server.mapper.CompanionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanionService {
    @Autowired
    private CompanionMapper companionMapper;

    //获取所有培护师头像
    public List<CompanionAvatar> selectPhotos(){
        return companionMapper.selectPhotos();
    }

    //添加培护师
    public void insertCompanion( String name, String mobile,Integer age, String sex,  String avatar, Integer active, Integer create_user_id){
        companionMapper.insertCompanion(name, mobile, age,sex, avatar, active,create_user_id);
    }

    public void updateCompanion(Long id, String name, String mobile, Integer age, String sex,  String avatar, Integer active){
        companionMapper.updateCompanion(id,name, mobile, age,sex, avatar, active);
    }

    //获取所有陪护师(分页)
    public PageInfo<CompanionList> getAllCompanionList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CompanionList> list = companionMapper.getAllCompanionList();
        PageInfo<CompanionList> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public void deleteCompanion(List<Long> ids){
        if(ids != null && !ids.isEmpty()){
            companionMapper.batchDeleteCompanions(ids);
        }
    }
}
