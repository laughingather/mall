package com.flipped.mall.thirdparty.listener;

import com.aliyun.mns.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 消息接收监听类
 * <p>
 * 如果需要监听短信是否被对方成功接收，只需实现这个接口并初始化一个 Spring Bean 即可。
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Component
public class SmsReportMessageListener implements com.alibaba.cloud.spring.boot.sms.SmsReportMessageListener {

    private List<Message> smsReportMessageSet = new LinkedList<>();

    @Override
    public boolean dealMessage(Message message) {
        smsReportMessageSet.add(message);
        log.error(this.getClass().getName() + "; " + message.toString());
        return true;
    }

    public List<Message> getSmsReportMessageSet() {
        return smsReportMessageSet;
    }

}