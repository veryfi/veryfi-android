package com.veryfi.android

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GetQuery(
    private val query: String? = null,
    private val page: Int? = null,
    private val page_size: Int? = null,
    private val return_audit_trail: Boolean? = null,
    private val order_by: String? = null,
    private val external_id: String? = null,
    private val status: String? = null,
    private val tag: String? = null,
    private val created__gt: LocalDate? = null,
    private val created__lt: LocalDate? = null,
    private val created__gte: LocalDate? = null,
    private val created__lte: LocalDate? = null,
    private val updated__gt: LocalDate? = null,
    private val updated__lt: LocalDate? = null,
    private val updated__gte: LocalDate? = null,
    private val updated__lte: LocalDate? = null,
    private val date__gt: LocalDate? = null,
    private val date__lt: LocalDate? = null,
    private val date__gte: LocalDate? = null,
    private val date__lte: LocalDate? = null,
) {

    private var bQuery = ""

    private fun appendQuery(name: String, value: Int?) {
        value?.let {
            bQuery = if (bQuery.isEmpty()) "${name}=${it}" else "${bQuery}&${name}=${it}"
        }
    }

    private fun appendQuery(name: String, value: String?) {
        value?.let {
            bQuery = if (bQuery.isEmpty()) "${name}=${it}" else "${bQuery}&${name}=${it}"
        }
    }

    private fun appendQuery(name: String, value: LocalDate?) {
        value?.let {
            bQuery = if (bQuery.isEmpty()) "${name}=${it}" else "${bQuery}&${name}=${it}"
        }
    }

    private fun appendQuery(name: String, value: Boolean?) {
        value?.let {
            bQuery = if (bQuery.isEmpty()) "${name}=${it}" else "${bQuery}&${name}=${it}"
        }
    }

    fun getQueryString(): String? {
        appendQuery(GetQuery.query, query)
        appendQuery(GetQuery.page, page)
        appendQuery(GetQuery.page_size, page_size)
        appendQuery(GetQuery.return_audit_trail, return_audit_trail)
        appendQuery(GetQuery.order_by, order_by)
        appendQuery(GetQuery.external_id, external_id)
        appendQuery(GetQuery.status, status)
        appendQuery(GetQuery.tag, tag)
        appendQuery(GetQuery.created__gt, created__gt)
        appendQuery(GetQuery.created__lt, created__lt)
        appendQuery(GetQuery.created__gte, created__gte)
        appendQuery(GetQuery.created__lte, created__lte)
        appendQuery(GetQuery.updated__gt, updated__gt)
        appendQuery(GetQuery.updated__lt, updated__lt)
        appendQuery(GetQuery.updated__gte, updated__gte)
        appendQuery(GetQuery.updated__lte, updated__lte)
        appendQuery(GetQuery.date__gt, date__gt)
        appendQuery(GetQuery.date__lt, date__lt)
        appendQuery(GetQuery.date__gte, date__gte)
        appendQuery(GetQuery.date__lte, date__lte)
        return if (bQuery.isEmpty()) null else bQuery
    }

    companion object {
        const val query = "q"
        const val page = "page"
        const val page_size = "page_size"
        const val return_audit_trail = "return_audit_trail"
        const val order_by = "order_by"
        const val external_id = "external_id"
        const val status = "status"
        const val tag = "tag"
        const val created__gt = "created__gt"
        const val created__lt = "created__lt"
        const val created__gte = "created__gte"
        const val created__lte = "created__lte"
        const val updated__gt = "updated__gt"
        const val updated__lt = "updated__lt"
        const val updated__gte = "updated__gte"
        const val updated__lte = "updated__lte"
        const val date__gt = "date__gt"
        const val date__lt = "date__lt"
        const val date__gte = "date__gte"
        const val date__lte = "date__lte"

        @RequiresApi(Build.VERSION_CODES.O)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}