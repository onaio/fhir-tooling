package org.smartregister.fct.device_database.data.transformation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.smartregister.fct.common.domain.transformation.BaseVisualTransformation
import org.smartregister.fct.device_database.data.persistence.DeviceDBConfigPersistence

class SQLQueryTransformation(
    isDarkTheme: Boolean,
    private val colorScheme: ColorScheme,
) : BaseVisualTransformation() {

    private var blueColor = Color(0xFF86B1FF)
    private var greenColor = Color(0xFF91BE61)
    private var yellowColor = Color(0xFFDEA834)
    private var grayColor = Color.Gray

    init {
        if (!isDarkTheme) {
            blueColor = Color(0xFF0050A5)
            greenColor = Color(0xFF457700)
            yellowColor = Color(0xFFBB8800)
            grayColor = Color.LightGray
        }
    }

    override fun AnnotatedString.Builder.transform(text: String) {

        val tablesAndColumnsName = DeviceDBConfigPersistence.tablesMap.map {
            DeviceDBConfigPersistence.tablesMap[it.key]?.map { tblInfo -> tblInfo.name } ?: listOf()
        }.flatten()

        // find table and columns name and sql tokens
        keywordRegex.findAll(text).forEach { matchResult ->
            val word = matchResult.value.uppercase()

            if (word in tokens) {
                addSpanStyle(blueColor, matchResult)
            } else if (tablesAndColumnsName.any { it.contentEquals(word, ignoreCase = true) }) {
                addSpanStyle(yellowColor, matchResult)
            }
        }

        // text regex
        textRegex.findAll(text).forEach { matchResult ->
            addSpanStyle(greenColor, matchResult)
        }

        // check comment regex
        commentRegex.findAll(text).forEach { matchResult ->
            var start = matchResult.range.first
            matchResult.value.trim().split(" ").forEach { token ->
                val end = start + token.length
                addStyle(
                    style = SpanStyle(grayColor),
                    start = start,
                    end = end
                )
                start = end + 1
            }
        }
    }

    private fun AnnotatedString.Builder.addSpanStyle(color: Color, matchResult: MatchResult) {
        addStyle(
            SpanStyle(color = color),
            start = matchResult.range.first,
            end = matchResult.range.last + 1
        )
    }

    private val keywordRegex = "\\S*".toRegex()
    private val textRegex = "((?<!\\\\)['\"])((?:.(?!(?<!\\\\)\\1))*.?)\\1(?!;)".toRegex()
    private val commentRegex = "--\\s*.*".toRegex()

    private val tokens = listOf(
        "ABORT",
        "ACTION",
        "ADD",
        "AFTER",
        "ALL",
        "ALTER",
        "ALWAYS",
        "ANALYZE",
        "AND",
        "AS",
        "ASC",
        "ATTACH",
        "AUTOINCREMENT",
        "BEFORE",
        "BEGIN",
        "BETWEEN",
        "BY",
        "CASCADE",
        "CASE",
        "CAST",
        "CHECK",
        "COLLATE",
        "COLUMN",
        "COMMIT",
        "CONFLICT",
        "CONSTRAINT",
        "CREATE",
        "CROSS",
        "CURRENT",
        "CURRENT_DATE",
        "CURRENT_TIME",
        "CURRENT_TIMESTAMP",
        "DATABASE",
        "DEFAULT",
        "DEFERRABLE",
        "DEFERRED",
        "DELETE",
        "DESC",
        "DETACH",
        "DISTINCT",
        "DO",
        "DROP",
        "EACH",
        "ELSE",
        "END",
        "ESCAPE",
        "EXCEPT",
        "EXCLUDE",
        "EXCLUSIVE",
        "EXISTS",
        "EXPLAIN",
        "FAIL",
        "FILTER",
        "FIRST",
        "FOLLOWING",
        "FOR",
        "FOREIGN",
        "FROM",
        "FULL",
        "GENERATED",
        "GLOB",
        "GROUP",
        "GROUPS",
        "HAVING",
        "IF",
        "IGNORE",
        "IMMEDIATE",
        "IN",
        "INDEX",
        "INDEXED",
        "INITIALLY",
        "INNER",
        "INSERT",
        "INSTEAD",
        "INTERSECT",
        "INTO",
        "IS",
        "ISNULL",
        "JOIN",
        "KEY",
        "LAST",
        "LEFT",
        "LIKE",
        "LIMIT",
        "MATCH",
        "MATERIALIZED",
        "NATURAL",
        "NO",
        "NOT",
        "NOTHING",
        "NOTNULL",
        "NULL",
        "NULLS",
        "OF",
        "OFFSET",
        "ON",
        "OR",
        "ORDER",
        "OTHERS",
        "OUTER",
        "OVER",
        "PARTITION",
        "PLAN",
        "PRAGMA",
        "PRECEDING",
        "PRIMARY",
        "QUERY",
        "RAISE",
        "RANGE",
        "RECURSIVE",
        "REFERENCES",
        "REGEXP",
        "REINDEX",
        "RELEASE",
        "RENAME",
        "REPLACE",
        "RESTRICT",
        "RETURNING",
        "RIGHT",
        "ROLLBACK",
        "ROW",
        "ROWS",
        "SAVEPOINT",
        "SELECT",
        "SET",
        "TABLE",
        "TEMP",
        "TEMPORARY",
        "THEN",
        "TIES",
        "TO",
        "TRANSACTION",
        "TRIGGER",
        "UNBOUNDED",
        "UNION",
        "UNIQUE",
        "UPDATE",
        "USING",
        "VACUUM",
        "VALUES",
        "VIEW",
        "VIRTUAL",
        "WHEN",
        "WHERE",
        "WINDOW",
        "WITH",
        "WITHOUT",
    )
}