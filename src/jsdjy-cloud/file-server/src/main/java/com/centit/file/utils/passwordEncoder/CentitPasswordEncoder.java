package com.centit.file.utils.passwordEncoder;

/**
 * @author ：cui_jian
 * @date ：Created in 2020/7/1 17:07
 */
public abstract interface CentitPasswordEncoder {
    String createPassword(String rawPass, Object salt);
    /**
     * 等价于 createPassword
     * @param rawPass 明文原始密码
     * @param salt 盐
     * @return 密文
     */
    @Deprecated
    default String encodePassword(String rawPass, Object salt){
        return createPassword( rawPass, salt);
    }

    boolean isPasswordValid(String encodedPassword, String rawPass, Object salt);

    default boolean isCorrectPasswordFormat(String password){
        return true;
    }
}
