package com.example.guitarpro.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "guitar_tabs")
class GuitarTab(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var filename: String = "",

    @Lob // Указывает Hibernate, что это большой бинарный объект
    @Column(nullable = false)
    var data: ByteArray = byteArrayOf(),

    var title: String? = null,

    var artist: String? = null,

    var uploadedAt: LocalDateTime? = null
)
