/**
 * 使用hutool实现的字段脱敏序列号器,
 * <p>
 * 需要使用 {@link com.fasterxml.jackson.databind.ObjectMapper} 进行序列化
 *
 * <pre>
 * {@code
 * @Data
 *     public class People {
 *
 *       @PrivacyEncrypt(type = cn.hutool.core.util.DesensitizedUtil.DesensitizedType.USER_ID) // 隐藏用户ID
 *       private Integer id;
 *
 *       private String name;
 *
 *       private Integer sex;
 *
 *       private Integer age;
 *
 *       @PrivacyEncrypt(type = cn.hutool.core.util.DesensitizedUtil.DesensitizedType.MOBILE_PHONE) // 隐藏手机号
 *       private String phone;
 *
 *       @PrivacyEncrypt(type = cn.hutool.core.util.DesensitizedUtil.DesensitizedType.EMAIL) // 隐藏邮箱
 *       private String email;
 *     }
 * }
 * </pre>
 * <p>
 * https://mp.weixin.qq.com/s/Bj3juN4ifHKbipjtHr9Tdw
 */
package com.liuxianfa.junit.springboot.jsonserializer.desensitization;