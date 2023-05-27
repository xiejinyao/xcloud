package com.xjinyao.xcloud.admin.api.enums;


import lombok.Getter;
import java.util.Arrays;

/**
 * 业务日志类型枚举(字典分组Id:13,字典分组编码:business_log_type)
 * <p>
 * 所有枚举项的值应该与数据库数据字典对应，若数据字典更新，请更新此枚举类
 * </p>
 */
public enum BusinessLogTypeEnum {

    CHARGE_HOUSE_CHARGE_STANDARD_SETTING(Integer.parseInt(Codes.CHARGE_HOUSE_CHARGE_STANDARD_SETTING), Labels.CHARGE_HOUSE_CHARGE_STANDARD_SETTING, "计费-房产收费设置"),
    CHARGE_CHARGE_DETAIL(Integer.parseInt(Codes.CHARGE_CHARGE_DETAIL), Labels.CHARGE_CHARGE_DETAIL, "计费-应收款管理"),
    BILL_PAYMENT_DAYCLOSING_RECORD(Integer.parseInt(Codes.BILL_PAYMENT_DAYCLOSING_RECORD), Labels.BILL_PAYMENT_DAYCLOSING_RECORD, "收款-交账记录日志"),
    CHARGE_TYPE_CASH_PLEDGE(Integer.parseInt(Codes.CHARGE_TYPE_CASH_PLEDGE), Labels.CHARGE_TYPE_CASH_PLEDGE, "收费类型-押金"),
    CHARGE_TYPE_PROVISIONAL_COLLECTION(Integer.parseInt(Codes.CHARGE_TYPE_PROVISIONAL_COLLECTION), Labels.CHARGE_TYPE_PROVISIONAL_COLLECTION, "收费类型-临时收款"),
    CHARGE_TYPE_ADVANCES_RECEIVED(Integer.parseInt(Codes.CHARGE_TYPE_ADVANCES_RECEIVED), Labels.CHARGE_TYPE_ADVANCES_RECEIVED, "收费类型-预收款"),
    CHARGE_TYPE_PLEDGWE_REFUND(Integer.parseInt(Codes.CHARGE_TYPE_PLEDGWE_REFUND), Labels.CHARGE_TYPE_PLEDGWE_REFUND, "收费类型-押金退款"),
    CHARGE_TYPE_DEPOSIT_TRANSFER(Integer.parseInt(Codes.CHARGE_TYPE_DEPOSIT_TRANSFER), Labels.CHARGE_TYPE_DEPOSIT_TRANSFER, "收费类型-押金类转"),
    CHARGE_TYPE_DEPOSIT_ROLL_OUT(Integer.parseInt(Codes.CHARGE_TYPE_DEPOSIT_ROLL_OUT), Labels.CHARGE_TYPE_DEPOSIT_ROLL_OUT, "收费类型-押金转出"),
    CHARGE_TYPE_RECEIVED_REFUND(Integer.parseInt(Codes.CHARGE_TYPE_RECEIVED_REFUND), Labels.CHARGE_TYPE_RECEIVED_REFUND, "收费类型-预收款退款"),
    CHARGE_TYPE_REFUND(Integer.parseInt(Codes.CHARGE_TYPE_REFUND), Labels.CHARGE_TYPE_REFUND, "收费类型-退款"),
    CHARGE_TYPE_REFUND_RECEIVED(Integer.parseInt(Codes.CHARGE_TYPE_REFUND_RECEIVED), Labels.CHARGE_TYPE_REFUND_RECEIVED, "收费类型-退款转预收"),
    CHARGE_TYPE_CHARGE_AGAINST(Integer.parseInt(Codes.CHARGE_TYPE_CHARGE_AGAINST), Labels.CHARGE_TYPE_CHARGE_AGAINST, "收费类型-冲销"),
    CHARGE_TYPE_ADVANCE_ROLL_OUT(Integer.parseInt(Codes.CHARGE_TYPE_ADVANCE_ROLL_OUT), Labels.CHARGE_TYPE_ADVANCE_ROLL_OUT, "收费类型-预收款转出"),
    CHARGE_TYPE_ADVANCE_CARRY_DOWN(Integer.parseInt(Codes.CHARGE_TYPE_ADVANCE_CARRY_DOWN), Labels.CHARGE_TYPE_ADVANCE_CARRY_DOWN, "收费类型-预收款结转"),
    CHARGE_TYPE_PAID(Integer.parseInt(Codes.CHARGE_TYPE_PAID), Labels.CHARGE_TYPE_PAID, "收费类型-已缴款"),
    OWNER_CUSTOMER_INFO_CHANGE(Integer.parseInt(Codes.OWNER_CUSTOMER_INFO_CHANGE), Labels.OWNER_CUSTOMER_INFO_CHANGE, "客户管理-客户信息变更"),
    OWNER_HOUSE_INFO_CHANGE(Integer.parseInt(Codes.OWNER_HOUSE_INFO_CHANGE), Labels.OWNER_HOUSE_INFO_CHANGE, "房产管理-房产信息变更"),
    BILL_RECEIPT_INFO_OPERATE(Integer.parseInt(Codes.BILL_RECEIPT_INFO_OPERATE), Labels.BILL_RECEIPT_INFO_OPERATE, "票据管理-票据相关操作：开票、废弃、核销"),
    CHARGE_TYPE_DEDUCTION_RECEIVABLE(Integer.parseInt(Codes.CHARGE_TYPE_DEDUCTION_RECEIVABLE), Labels.CHARGE_TYPE_DEDUCTION_RECEIVABLE, "应收抵扣"),
    CHARGE_TYPE_ADVANCE_TRANSE_IN(Integer.parseInt(Codes.CHARGE_TYPE_ADVANCE_TRANSE_IN), Labels.CHARGE_TYPE_ADVANCE_TRANSE_IN, "预收款划入"),
    CHARGE_TYPE_ADVANCE_TRANSE_OUT(Integer.parseInt(Codes.CHARGE_TYPE_ADVANCE_TRANSE_OUT), Labels.CHARGE_TYPE_ADVANCE_TRANSE_OUT, "预收款划出"),
    CHARGE_TYPE_REFUND_MANAGEMENT(Integer.parseInt(Codes.CHARGE_TYPE_REFUND_MANAGEMENT), Labels.CHARGE_TYPE_REFUND_MANAGEMENT, "退款管理"),
    PROPERTY_STATUS_OPERATIONS(Integer.parseInt(Codes.PROPERTY_STATUS_OPERATIONS), Labels.PROPERTY_STATUS_OPERATIONS, "房产状态操作"),
    CHARGE_TYPE_DUTY_PARAGRAPH(Integer.parseInt(Codes.CHARGE_TYPE_DUTY_PARAGRAPH), Labels.CHARGE_TYPE_DUTY_PARAGRAPH, "税号设置");

    /**
     * 枚举值对应数据字典的值
     */
    @Getter
    private final Integer value;
    /**
     * 枚举值对应数据字典的标签
     */
    @Getter
    private final String label;
    /**
     * 枚举值对应数据字典的备注
     */
    @Getter
    private final String remark;

    /**
     * 枚举
     *
     * @param value  值
     * @param label  标签
     * @param remark 备注
     */
    BusinessLogTypeEnum(Integer value,String label, String remark) {
        this.value = value;
        this.label = label;
        this.remark = remark;
    }

    /**
     * 比较value是否相同
     *
     * @param value 需要比较的value值
     * @return Boolean
     */
    public boolean valueEquals(Integer value) {
        return this.value.equals(value);
    }

    /**
     * 通过值构建枚举
     * @param value 枚举值
     * @return BusinessLogTypeEnum
     */
    public static BusinessLogTypeEnum ofByValue(Integer value) {
        return Arrays.stream(BusinessLogTypeEnum.values())
                .filter(d -> d.valueEquals(value))
                .findFirst()
                .orElse(null);
    }

    public interface Codes {
        /**
         * 计费-房产收费设置
         */
        String CHARGE_HOUSE_CHARGE_STANDARD_SETTING = "1";
        /**
         * 计费-应收款管理
         */
        String CHARGE_CHARGE_DETAIL = "2";
        /**
         * 收款-交账记录日志
         */
        String BILL_PAYMENT_DAYCLOSING_RECORD = "3";
        /**
         * 收费类型-押金
         */
        String CHARGE_TYPE_CASH_PLEDGE = "4";
        /**
         * 收费类型-临时收款
         */
        String CHARGE_TYPE_PROVISIONAL_COLLECTION = "5";
        /**
         * 收费类型-预收款
         */
        String CHARGE_TYPE_ADVANCES_RECEIVED = "6";
        /**
         * 收费类型-押金退款
         */
        String CHARGE_TYPE_PLEDGWE_REFUND = "7";
        /**
         * 收费类型-押金类转
         */
        String CHARGE_TYPE_DEPOSIT_TRANSFER = "8";
        /**
         * 收费类型-押金转出
         */
        String CHARGE_TYPE_DEPOSIT_ROLL_OUT = "9";
        /**
         * 收费类型-预收款退款
         */
        String CHARGE_TYPE_RECEIVED_REFUND = "10";
        /**
         * 收费类型-退款
         */
        String CHARGE_TYPE_REFUND = "11";
        /**
         * 收费类型-退款转预收
         */
        String CHARGE_TYPE_REFUND_RECEIVED = "12";
        /**
         * 收费类型-冲销
         */
        String CHARGE_TYPE_CHARGE_AGAINST = "13";
        /**
         * 收费类型-预收款转出
         */
        String CHARGE_TYPE_ADVANCE_ROLL_OUT = "14";
        /**
         * 收费类型-预收款结转
         */
        String CHARGE_TYPE_ADVANCE_CARRY_DOWN = "15";
        /**
         * 收费类型-已缴款
         */
        String CHARGE_TYPE_PAID = "16";
        /**
         * 客户管理-客户信息变更
         */
        String OWNER_CUSTOMER_INFO_CHANGE = "17";
        /**
         * 房产管理-房产信息变更
         */
        String OWNER_HOUSE_INFO_CHANGE = "18";
        /**
         * 票据管理-票据相关操作：开票、废弃、核销
         */
        String BILL_RECEIPT_INFO_OPERATE = "19";
        /**
         * 应收抵扣
         */
        String CHARGE_TYPE_DEDUCTION_RECEIVABLE = "20";
        /**
         * 预收款划入
         */
        String CHARGE_TYPE_ADVANCE_TRANSE_IN = "21";
        /**
         * 预收款划出
         */
        String CHARGE_TYPE_ADVANCE_TRANSE_OUT = "22";
        /**
         * 退款管理
         */
        String CHARGE_TYPE_REFUND_MANAGEMENT = "23";
        /**
         * 房产状态操作
         */
        String PROPERTY_STATUS_OPERATIONS = "21";
        /**
         * 税号设置
         */
        String CHARGE_TYPE_DUTY_PARAGRAPH = "25";
    }

    public interface Labels {
        /**
         * 计费-房产收费设置
         */
        String CHARGE_HOUSE_CHARGE_STANDARD_SETTING = "计费-房产收费设置";
        /**
         * 计费-应收款管理
         */
        String CHARGE_CHARGE_DETAIL = "计费-应收款管理";
        /**
         * 收款-交账记录日志
         */
        String BILL_PAYMENT_DAYCLOSING_RECORD = "收款-交账记录";
        /**
         * 收费类型-押金
         */
        String CHARGE_TYPE_CASH_PLEDGE = "收费类型-押金";
        /**
         * 收费类型-临时收款
         */
        String CHARGE_TYPE_PROVISIONAL_COLLECTION = "收费类型-临时收款";
        /**
         * 收费类型-预收款
         */
        String CHARGE_TYPE_ADVANCES_RECEIVED = "收费类型-预收款";
        /**
         * 收费类型-押金退款
         */
        String CHARGE_TYPE_PLEDGWE_REFUND = "收费类型-押金退款";
        /**
         * 收费类型-押金类转
         */
        String CHARGE_TYPE_DEPOSIT_TRANSFER = "收费类型-押金类转";
        /**
         * 收费类型-押金转出
         */
        String CHARGE_TYPE_DEPOSIT_ROLL_OUT = "收费类型-押金转出";
        /**
         * 收费类型-预收款退款
         */
        String CHARGE_TYPE_RECEIVED_REFUND = "收费类型-预收款退款";
        /**
         * 收费类型-退款
         */
        String CHARGE_TYPE_REFUND = "收费类型-退款";
        /**
         * 收费类型-退款转预收
         */
        String CHARGE_TYPE_REFUND_RECEIVED = "收费类型-退款转预收";
        /**
         * 收费类型-冲销
         */
        String CHARGE_TYPE_CHARGE_AGAINST = "收费类型-冲销";
        /**
         * 收费类型-预收款转出
         */
        String CHARGE_TYPE_ADVANCE_ROLL_OUT = "收费类型-预收款转出";
        /**
         * 收费类型-预收款结转
         */
        String CHARGE_TYPE_ADVANCE_CARRY_DOWN = "收费类型-预收款结转";
        /**
         * 收费类型-已缴款
         */
        String CHARGE_TYPE_PAID = "收费类型-已缴款";
        /**
         * 客户管理-客户信息变更
         */
        String OWNER_CUSTOMER_INFO_CHANGE = "客户管理-客户信息变更";
        /**
         * 房产管理-房产信息变更
         */
        String OWNER_HOUSE_INFO_CHANGE = "房产管理-房产信息变更";
        /**
         * 票据管理-票据相关操作：开票、废弃、核销
         */
        String BILL_RECEIPT_INFO_OPERATE = "票据管理-票据相关操作";
        /**
         * 应收抵扣
         */
        String CHARGE_TYPE_DEDUCTION_RECEIVABLE = "应收抵扣";
        /**
         * 预收款划入
         */
        String CHARGE_TYPE_ADVANCE_TRANSE_IN = "预收款划入";
        /**
         * 预收款划出
         */
        String CHARGE_TYPE_ADVANCE_TRANSE_OUT = "预收款划出";
        /**
         * 退款管理
         */
        String CHARGE_TYPE_REFUND_MANAGEMENT = "退款管理";
        /**
         * 房产状态操作
         */
        String PROPERTY_STATUS_OPERATIONS = "房产状态操作";
        /**
         * 税号设置
         */
        String CHARGE_TYPE_DUTY_PARAGRAPH = "税号设置";
    }
}

