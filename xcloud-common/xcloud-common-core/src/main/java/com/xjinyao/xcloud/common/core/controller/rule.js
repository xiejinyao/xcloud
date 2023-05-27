let callback = (error) => {
    if (error) {
        console.error(error);
    } else {
        console.log("success");
    }
}

//DecimalMax validator
validator = (rule, value, callback) => {
    if (/\s*/.test(value) || value > 5) {
        callback(new Error('此字段值必须小于5'));
    } else {
        callback();
    }
}

/**
 * @NotEmpty validator
 * 不得为 null 空。
 * 支持的类型包括：
 * CharSequence （计算字符序列的长度）
 * Collection （评估集合大小）
 * Map （评估地图大小）
 * 数组（计算数组长度
 * @param rule
 * @param value
 * @param callback
 */
validator1 = (rule, value, callback) => {
    const BOOLEAN = 'boolean';
    const NUMBER = 'number';
    const STRING = 'string';
    const FUNCTION = 'function';
    const ARRAY = 'array';
    const DATE = 'date';
    const REGEXP = 'regExp';
    const UNDEFINED = 'undefined';
    const NULL = 'null';
    const OBJECT = 'object';
    let typeMap = {
        '[object Boolean]': BOOLEAN,
        '[object Number]': NUMBER,
        '[object String]': STRING,
        '[object Function]': FUNCTION,
        '[object Array]': ARRAY,
        '[object Date]': DATE,
        '[object RegExp]': REGEXP,
        '[object Undefined]': UNDEFINED,
        '[object Null]': NULL,
        '[object Object]': OBJECT
    }
    let toString = Object.prototype.toString
    let type = typeMap[toString.call(value)];
    let validated = false;
    if (type === ARRAY) {
        validated = value && value.length > 0;
    } else if (type === OBJECT) {
        validated = value && Object.keys(value).length > 0;
    } else {
        if (!/^\s*$/.test(value) && value != null && (value + '').length > 0) {
            validated = true;
        }
    }
    if (validated) {
        callback();
    } else {
        callback(new Error('此字段不能为空'));
    }
}
validator1(null, "323", callback)
