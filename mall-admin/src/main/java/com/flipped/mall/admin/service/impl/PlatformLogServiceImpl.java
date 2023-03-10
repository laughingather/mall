package com.flipped.mall.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flipped.mall.admin.entity.query.PlatformLogQuery;
import com.flipped.mall.admin.entity.vo.PlatformLogExcelVO;
import com.flipped.mall.admin.mapper.PlatformLogMapper;
import com.flipped.mall.admin.service.PlatformLogService;
import com.flipped.mall.common.entity.PlatformLog;
import com.flipped.mall.common.entity.api.MyPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 平台日志逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service
public class PlatformLogServiceImpl implements PlatformLogService {

    @Resource
    private PlatformLogMapper platformLogMapper;

    @Override
    public MyPage<PlatformLog> listPlatformLogsWithPage(PlatformLogQuery platformLogQuery) {
        // 分页参数
        IPage<PlatformLog> page = new Page<>(platformLogQuery.getPn(), platformLogQuery.getPs());

        // 拼接查询条件
        QueryWrapper<PlatformLog> queryWrapper = new QueryWrapper<>();
        if (Objects.nonNull(platformLogQuery.getLogin())) {
            queryWrapper.lambda().eq(PlatformLog::getLogin, platformLogQuery.getLogin());
        }
        if (Objects.nonNull(platformLogQuery.getSuccess())) {
            queryWrapper.lambda().eq(PlatformLog::getSuccess, platformLogQuery.getSuccess());
        }
        if (StringUtils.isNotBlank(platformLogQuery.getOperation())) {
            queryWrapper.lambda().eq(PlatformLog::getOperationUserid, platformLogQuery.getOperation()).or().eq(PlatformLog::getOperationUsername, platformLogQuery.getOperation());
        }

        IPage<PlatformLog> platformLogPage = platformLogMapper.selectPage(page, queryWrapper);
        return MyPage.restPage(platformLogPage);
    }

    @Override
    public List<PlatformLogExcelVO> listPlatformLogsWithExport(PlatformLogQuery platformLogQuery) {
        return platformLogMapper.listPlatformLogsWithExport(platformLogQuery);
    }
}

