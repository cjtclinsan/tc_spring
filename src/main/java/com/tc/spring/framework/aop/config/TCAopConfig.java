package com.tc.spring.framework.aop.config;

import lombok.Data;

/**
 * @author taosh
 * @create 2019-09-14 14:32
 */
@Data
public class TCAopConfig {
    private String pointCut;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectClass;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;
}
