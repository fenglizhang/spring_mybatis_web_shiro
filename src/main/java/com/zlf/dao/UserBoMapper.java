package com.zlf.dao;

import com.zlf.bo.UserBo;

public interface UserBoMapper {
    int deleteByPrimaryKey(String userid);

    int insert(UserBo record);

    int insertSelective(UserBo record);

    UserBo selectByPrimaryKey(String userid);

    int updateByPrimaryKeySelective(UserBo record);

    int updateByPrimaryKey(UserBo record);
}