package com.centit.scan.utils.passwordEncoder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 采用Spring 推荐的 BCryptPasswordEncoder 加密方式
 */
@Component
public class StandardPasswordEncoderImpl implements CentitPasswordEncoder, PasswordEncoder {

    private BCryptPasswordEncoder passwordEncoder ;

    public StandardPasswordEncoderImpl() {
        this(11);
    }

    public StandardPasswordEncoderImpl(int strength) {
        if(strength<5 ||strength >31){
            passwordEncoder = new BCryptPasswordEncoder(11);
        }else {
            passwordEncoder = new BCryptPasswordEncoder(strength);
        }
    }

    @Override
    public String createPassword(String rawPass, Object salt){
        return passwordEncoder.encode(rawPass);
    }

    @Override
    public boolean isPasswordValid(String encodedPassword, String rawPass, Object salt){
        return passwordEncoder.matches(rawPass, encodedPassword);
    }

    /**
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
     * greater hash combined with an 8-byte or greater randomly generated salt.
     *
     * @param rawPassword  rawPassword
     * @return encode
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verify the encoded password obtained from storage matches the submitted raw
     * password after it too is encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param rawPassword     the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from
     * storage
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean isCorrectPasswordFormat(String password){
        if(StringUtils.isBlank(password) || password.length()<48){
            return false;
        }
        return password.charAt(0) == '$'
                && password.charAt(3) == '$'
                && password.charAt(6) == '$';
    }
}
