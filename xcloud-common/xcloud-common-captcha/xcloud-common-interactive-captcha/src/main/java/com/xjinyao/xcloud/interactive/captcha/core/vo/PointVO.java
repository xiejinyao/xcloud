package com.xjinyao.xcloud.interactive.captcha.core.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class PointVO implements Serializable {

    private String secretKey;

    @Getter
    @Setter
    public int x;

    @Getter
    @Setter
    public int y;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public PointVO(int x, int y, String secretKey) {
        this.secretKey = secretKey;
        this.x = x;
        this.y = y;
    }

    public PointVO() {
    }

    public PointVO(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
