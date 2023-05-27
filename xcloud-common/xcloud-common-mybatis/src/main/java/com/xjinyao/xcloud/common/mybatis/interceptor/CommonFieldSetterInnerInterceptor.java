package com.xjinyao.xcloud.common.mybatis.interceptor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.xjinyao.xcloud.common.core.util.FieldUtil;
import com.xjinyao.xcloud.common.core.util.RequestHolder;
import com.xjinyao.xcloud.common.mybatis.annotations.AutoSetterValue;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.security.core.userdetails.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.constant.CommonConstants.PROJECT_ID;

/**
 * 公共字段初始化插件
 * <p>
 * 在对象新增和修改的时候对一些公共的字段进行自动初始化值，如创建人、创建时间、修改人、修改时间以及一些数据隔离的字段
 *
 * @author 谢进伟
 * @createDate 2022/11/29 15:14
 */
@Slf4j
public class CommonFieldSetterInnerInterceptor implements InnerInterceptor {

    private static final String SYS_TIME = "sysTime";
    private static final String UPDATE_USER_NAME = "updateUserName";
    private static final String CREATE_USER_NAME = "createUserName";
    private static final String UPDATE_USER_ID = "updateUserId";
    private static final String CREATE_USER_ID = "createUserId";
    private static final String UPDATE_USER = "updateUser";
    private static final String CREATE_USER = "createUser";
    private static final String DEL_FLAG = "delFlag";
    private static final String IS_DELETE = "isDelete";
    private static final String IS_DELETED = "isDeleted";
    private final List<CommonFieldInfo> commonFieldList = new ArrayList<>();
    private SqlCommandType sqlCommandType;

    public CommonFieldSetterInnerInterceptor() {
        //项目Id
        commonFieldList.add(CommonFieldInfo.builder()
                .insertBefore(true)
                .supplier(() -> RequestHolder.getHeaderValue(PROJECT_ID))
                .build()
                .addFieldName(PROJECT_ID));

        //逻辑删除字段
        commonFieldList.add(CommonFieldInfo.builder()
                .insertBefore(true)
                .supplier(() -> Boolean.FALSE)
                .build()
                .addFieldName(IS_DELETE)
                .addFieldName(IS_DELETED)
                .addFieldName(DEL_FLAG));

        //创建人、修改id
        commonFieldList.add(CommonFieldInfo.builder()
                .insertBefore(true)
                .supplier(SecurityUtils::getUserId)
                .build()
                .addFieldName(CREATE_USER_ID)
                .addFieldName(CREATE_USER));
        commonFieldList.add(CommonFieldInfo.builder()
                .updateBefore(true)
                .supplier(SecurityUtils::getUserId)
                .build()
                .addFieldName(UPDATE_USER_ID)
                .addFieldName(UPDATE_USER));

        //创建人、修改人登录名
        Supplier<Object> currentLoginUserName = () -> Optional.ofNullable(SecurityUtils.getUser())
                .stream()
                .filter(Objects::nonNull)
                .map(User::getUsername)
                .findFirst()
                .orElse(null);
        commonFieldList.add(CommonFieldInfo.builder()
                .insertBefore(true)
                .supplier(currentLoginUserName)
                .build()
                .addFieldName(CREATE_USER_NAME));
        commonFieldList.add(CommonFieldInfo.builder()
                .updateBefore(true)
                .supplier(currentLoginUserName)
                .build()
                .addFieldName(UPDATE_USER_NAME));

        //系统时间(微服务所在服务器的系统时间)
        commonFieldList.add(CommonFieldInfo.builder()
                .updateBefore(true)
                .insertBefore(true)
                .supplier(LocalDateTime::now)
                .build()
                .addFieldName(SYS_TIME));
    }

    private Object getFieldValue(Object et, TableFieldInfo f) {
        try {
            return f.getField().get(et);
        } catch (IllegalAccessException e) {
            //ignore
        }
        return null;
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        this.sqlCommandType = ms.getSqlCommandType();
        if (!support()) {
            return;
        }

        if (parameter instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) parameter;
            doSetDefaultValue(map, ms.getId());
        } else if (parameter.getClass().isAnnotationPresent(TableName.class)) {
            doSetEntityDefaultValue(parameter, parameter.getClass());
        }
    }

    private void doSetDefaultValue(Map<?, ?> map, String msId) {
        log.info("msId is {}", msId);
        Object et = map.getOrDefault(Constants.ENTITY, null);
        if (et != null) {
            Class<?> aClass = et.getClass();
            doSetEntityDefaultValue(et, aClass);
        }
    }

    private void doSetEntityDefaultValue(Object et, Class<?> aClass) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(aClass);
        if (tableInfo == null) {
            return;
        }
        List<String> commonFields = getCommonFields();
        boolean insertBefore = Objects.equals(this.sqlCommandType, SqlCommandType.INSERT);
        boolean updateBefore = Objects.equals(this.sqlCommandType, SqlCommandType.UPDATE);
        tableInfo.getFieldList().stream()
                .filter(f -> commonFields.contains(f.getProperty())
                        && Objects.isNull(getFieldValue(et, f))
                        && (!f.getField().isAnnotationPresent(AutoSetterValue.class)
                        || f.getField().getAnnotation(AutoSetterValue.class).value()))
                .forEach(f -> Optional.ofNullable(getDefaultValue(f.getProperty(), insertBefore, updateBefore))
                        .map(Object::toString)
                        .ifPresent(defaultValue -> FieldUtil.setFieldVal(et, f.getField(), defaultValue)));
    }

    private boolean support() {
        return Objects.equals(this.sqlCommandType, SqlCommandType.UPDATE)
                || Objects.equals(this.sqlCommandType, SqlCommandType.INSERT);
    }

    private List<String> getCommonFields() {
        return this.commonFieldList
                .stream()
                .map(CommonFieldInfo::getFieldNameList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Object getDefaultValue(String fieldName, boolean insertBefore, boolean updateBefore) {
        Supplier<Object> supplier = this.commonFieldList
                .stream()
                .filter(cf -> cf.getFieldNameList().contains(fieldName)
                        && ((cf.isInsertBefore() && insertBefore) || cf.isUpdateBefore() && updateBefore))
                .map(CommonFieldInfo::getSupplier)
                .findFirst()
                .orElse(null);
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }
}
