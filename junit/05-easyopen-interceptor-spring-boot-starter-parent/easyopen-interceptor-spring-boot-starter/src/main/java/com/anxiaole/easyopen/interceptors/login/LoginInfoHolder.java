package com.anxiaole.easyopen.interceptors.login;

import com.anxiaole.passport.enums.SystemUserTypeEnum;
import com.anxiaole.passport.util.LoginInfoUtils;
import com.anxiaole.passport.vo.AdvisorInfo;
import com.anxiaole.passport.vo.BaseLoginInfo;
import com.anxiaole.passport.vo.EmployeeUserInfo;
import com.anxiaole.passport.vo.InvestorInfo;

import com.gitee.easyopen.ApiContext;

/**
 * @author LiuXianfa
 * 
 * @date 12/1 18:47
 */
public class LoginInfoHolder {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<SystemUserTypeEnum> userType = new ThreadLocal<>();
    private static final ThreadLocal<BaseLoginInfo> loginInfo = new ThreadLocal<>();


    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void setUserType(String token) {
        try {
            int type = token.charAt(0) - '0';
            for (SystemUserTypeEnum userTypeEnum : SystemUserTypeEnum.values()) {
                if (type == userTypeEnum.key) {
                    userType.set(userTypeEnum);
                    tokenHolder.set(token);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static SystemUserTypeEnum getUserType() {
        return userType.get();
    }


    public static void setLoginInfo(String token) {
        setUserType(token);
        SystemUserTypeEnum userType = getUserType();

        if (userType == SystemUserTypeEnum.INVESTOR) {
            loginInfo.set(LoginInfoUtils.getInvestorInfo(token));
        } else if (userType == SystemUserTypeEnum.ADVISOR || userType == SystemUserTypeEnum.ADVISOR_H5) {
            loginInfo.set(LoginInfoUtils.getAdvisorInfo(token));
        } else if (userType == SystemUserTypeEnum.SAAS) {
            loginInfo.set(LoginInfoUtils.getEmployeeUserInfo(token));
        }
    }

    public static BaseLoginInfo getLoginInfo() {
        return loginInfo.get();
    }

    /**
     * easyopen接口调用时,如果传的access_token为mock,则返回模拟的用户信息
     *
     * @return
     */
    public static boolean isMock() {
        String accessToken = ApiContext.getApiParam().fatchAccessToken();
        return "mock".equals(accessToken);
    }

    public static InvestorInfo getInvestorInfo() {
        if (isMock()) {
            InvestorInfo investor = new InvestorInfo();
            investor.setId(1);
            investor.setCustomerId(1);
            investor.setEntId(1);
            investor.setName("mockInvestorName");
            investor.setPictureUrl("/test_default.png");
            return investor;
        }
        InvestorInfo investorInfo = null;
        try {
            investorInfo = (InvestorInfo) loginInfo.get();
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("登录用户为[%s]类型.不是[%s]类型.", getUserType().value, SystemUserTypeEnum.INVESTOR.value), e);
        }
        return investorInfo;
    }

    public static AdvisorInfo getAdvisorInfo() {
        if (isMock()) {
            AdvisorInfo advisor = new AdvisorInfo();
            advisor.setId(1);
            advisor.setIndependent(0);
            advisor.setEntId(1);
            advisor.setName("mockAdvisorName");
            advisor.setPictureUrl("/test_default.png");
            return advisor;
        }
        AdvisorInfo advisorInfo = null;
        try {
            advisorInfo = (AdvisorInfo) loginInfo.get();
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("登录用户为[%s]类型.不是[%s]类型.", getUserType().value, SystemUserTypeEnum.ADVISOR.value), e);
        }
        return advisorInfo;
    }

    public static AdvisorInfo getAdvisorInfoH5() {
        if (isMock()) {
            AdvisorInfo advisor = new AdvisorInfo();
            advisor.setId(1);
            advisor.setIndependent(0);
            advisor.setEntId(1);
            advisor.setName("mockAdvisorName");
            advisor.setPictureUrl("/test_default.png");
            return advisor;
        }
        AdvisorInfo advisorInfo = null;
        try {
            advisorInfo = (AdvisorInfo) loginInfo.get();
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("登录用户为[%s]类型.不是[%s]类型.", getUserType().value, SystemUserTypeEnum.ADVISOR.value), e);
        }
        return advisorInfo;
    }

    public static EmployeeUserInfo getEmployeeUserInfo() {
        if (isMock()) {
            EmployeeUserInfo employee = new EmployeeUserInfo();
            employee.setId(1);
            employee.setIndependent(0);
            employee.setEntId(1);
            employee.setName("mockEmployeeName");
            employee.setPictureUrl("/test_default.png");
            return employee;
        }
        EmployeeUserInfo baseLoginInfo = null;
        try {
            baseLoginInfo = (EmployeeUserInfo) loginInfo.get();
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("登录用户为[%s]类型.不是[%s]类型.", getUserType().value, SystemUserTypeEnum.SAAS.value), e);
        }
        return baseLoginInfo;
    }

    public static void clearAll() {
        loginInfo.remove();
        tokenHolder.remove();
        userType.remove();
    }
}
