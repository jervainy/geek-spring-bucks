package com.geektime.component

import com.alibaba.druid.filter.FilterEventAdapter
import com.alibaba.druid.proxy.jdbc.StatementProxy
import org.springframework.stereotype.Component

@Component
class SqlInjectFilter: FilterEventAdapter() {

    override fun statementExecuteQueryBefore(statement: StatementProxy?, sql: String?) {
        super.statementExecuteQueryBefore(statement, sql)
        checkSqlInject(sql)
    }

    override fun statementExecuteBefore(statement: StatementProxy?, sql: String?) {
        super.statementExecuteBefore(statement, sql)
        checkSqlInject(sql)
    }

    override fun statementExecuteBatchBefore(statement: StatementProxy?) {
        super.statementExecuteBatchBefore(statement)
        checkSqlInject(statement?.batchSql)
    }

    override fun statementExecuteUpdateBefore(statement: StatementProxy?, sql: String?) {
        super.statementExecuteUpdateBefore(statement, sql)
        checkSqlInject(sql)
    }

    private fun checkSqlInject(sql: String?) {
        if (sql == null) return
        var exp = sql.trim().toLowerCase()
        if (exp.endsWith(";")) {
            exp = exp.substring(0, exp.length - 1)
        }
        if (exp.contains(";")) throw RuntimeException("发现SQL注入：SQL拼接")
        while (exp.contains("in")) {
            exp = exp.substringAfter("in")
            val inExp = exp.substringAfter("(").substringBefore(")")
            if (inExp.split(",").size > 10) throw RuntimeException("发现SQL性能：in参数大于10个")
            exp = exp.substringAfter(")")
        }
    }

}