package com.laughingather.gulimall.auth.feign.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-12 11:33:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 会员等级id
	 */
	private Long levelId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 头像
	 */
	private String header;

	/**
	 * 性别
	 */
	private Integer gender;

	/**
	 * 生日
	 */
	private LocalDate birth;

	/**
	 * 所在城市
	 */
	private String city;

	/**
	 * 职业
	 */
	private String job;

	/**
	 * 个性签名
	 */
	private String sign;

	/**
	 * 用户来源
	 */
	private Integer sourceType;

	/**
	 * 积分
	 */
	private Integer integration;

	/**
	 * 成长值
	 */
	private Integer growth;

	/**
	 * 启用状态
	 */
	private Integer status;

	/**
	 * @JsonDeserialize(using = LocalDateTimeDeserializer.class)
	 * 用于解决feign远程调用序列化时间失败
	 * <p>
	 * 注册时间
	 */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 社交帐号唯一id
	 */
	private Long socialUid;

	/**
	 * 社交账号令牌
	 */
	private String accessToken;

	/**
	 * 社交令牌有效时间
	 */
	private Long expiresIn;

}