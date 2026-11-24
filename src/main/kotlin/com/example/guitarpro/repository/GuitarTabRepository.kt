package com.example.guitarpro.repository

import com.example.guitarpro.dto.TabSummary
import com.example.guitarpro.entity.GuitarTab
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GuitarTabRepository : JpaRepository<GuitarTab, Long> {

    @Query(
        value = """
            select new com.example.guitarpro.dto.TabSummary(
                tab.id, tab.filename, coalesce(tab.title, tab.filename), tab.artist, tab.uploadedAt
            )
            from GuitarTab tab
            where locate(lower(:query), lower(coalesce(tab.title, tab.filename))) > 0
               or locate(lower(:query), lower(tab.artist)) > 0
            order by tab.uploadedAt desc nulls last, tab.id desc
        """,
        countQuery = """
            select count(tab)
            from GuitarTab tab
            where locate(lower(:query), lower(coalesce(tab.title, tab.filename))) > 0
               or locate(lower(:query), lower(tab.artist)) > 0
        """
    )
    fun findCatalog(@Param("query") query: String, pageable: Pageable): Page<TabSummary>
}
