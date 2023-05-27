package template

{domain.packageName};

import lombok.Getter;
import java.util.Arrays;

/**
 * ${domain.dict.description}枚举(字典分组Id:${domain.dict.id},字典分组编码:${domain.dict.type})
 * <p>
 * 所有枚举项的值应该与数据库数据字典对应，若数据字典更新，请更新此枚举类
 * </p>
 */
public enum ${domain.enumFileName} {

<#list domain.dictItems as item>
    <#if domain.itemDataType = "Integer">
    ${item.enumCode}(Integer.parseInt(Codes.${item.enumCode}), Labels.${item.enumCode}, "${item.remark}")<#if item_has_next>,<#else>;</#if>
    <#else>
    ${item.enumCode}(Codes.${item.enumCode}, Labels.${item.enumCode}, "${item.remark}")<#if item_has_next>,<#else>;</#if>
    </#if>
</#list>

    /**
     * 枚举值对应数据字典的值
     */
    @Getter
    private final ${domain.itemDataType} value;
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
    ${domain.enumFileName}(${domain.itemDataType} value,String label, String remark) {
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
    public boolean valueEquals(${domain.itemDataType} value) {
        return this.value.equals(value);
    }

    /**
     * 通过值构建枚举
     * @param value 枚举值
     * @return ${domain.enumFileName}
     */
    public static ${domain.enumFileName} ofByValue(${domain.itemDataType} value) {
        return Arrays.stream(${domain.enumFileName}.values())
                .filter(d -> d.valueEquals(value))
                .findFirst()
                .orElse(null);
    }

    public interface Codes {
    <#list domain.dictItems as item>
        /**
         * ${item.remark}
         */
        String ${item.enumCode} = "${item.value}";
    </#list>
    }

    public interface Labels {
        <#list domain.dictItems as item>
        /**
         * ${item.remark}
         */
        String ${item.enumCode} = "${item.label}";
        </#list>
    }
}
