package org.dromara.dynamictp.admin.infrastructure.typehandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.dromara.dynamictp.admin.common.domain.KVPairs;
import org.dromara.dynamictp.admin.infrastructure.util.GsonUtil;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * JobData List 类型处理器
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.typehandler.JobDataTypeHandler
 * @CreateTime 2024/5/28 - 22:12
 */

@Slf4j
public class JobDataTypeHandler extends BaseTypeHandler<List<KVPairs>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<KVPairs> kvPairs, JdbcType jdbcType) throws SQLException {
        // 将JobDataMap对象转换为JSON字符串，存储在数据库中
        ps.setString(i, GsonUtil.toJson(kvPairs));
    }

    @SneakyThrows
    @Override
    public List<KVPairs> getNullableResult(ResultSet rs, String columnName) {
        return GsonUtil.fromJsonList(rs.getString(columnName), KVPairs.class);
    }

    @Override
    public List<KVPairs> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return GsonUtil.fromJsonList(rs.getString(columnIndex), KVPairs.class);
    }

    @Override
    public List<KVPairs> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return GsonUtil.fromJsonList(cs.getString(columnIndex), KVPairs.class);
    }
}
