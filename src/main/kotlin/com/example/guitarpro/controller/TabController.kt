package com.example.guitarpro.controller

import com.example.guitarpro.dto.TabSummary
import com.example.guitarpro.entity.GuitarTab
import com.example.guitarpro.repository.GuitarTabRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/tabs")
class TabController(private val repository: GuitarTabRepository) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadTab(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("title") title: String,
        @RequestParam("artist") artist: String
    ): ResponseEntity<Map<String, Long>> {
        val songTitle = title.trim()
        val songArtist = artist.trim()

        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File must not be empty")
        }
        if (songTitle.isEmpty() || songTitle.length > 255 || songArtist.isEmpty() || songArtist.length > 255) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and artist must each contain 1 to 255 characters")
        }

        val tab = repository.save(
            GuitarTab(
                filename = file.originalFilename?.takeIf { it.isNotBlank() } ?: "tab.gp",
                data = file.bytes,
                title = songTitle,
                artist = songArtist,
                uploadedAt = LocalDateTime.now()
            )
        )

        return ResponseEntity.ok(mapOf("id" to checkNotNull(tab.id)))
    }

    @GetMapping
    fun getAllTabs(
        @RequestParam("query", required = false) query: String?,
        @PageableDefault(size = 10) pageable: Pageable
    ): PagedModel<TabSummary> {
        val page = PageRequest.of(pageable.pageNumber, pageable.pageSize)
        return PagedModel(repository.findCatalog(query.orEmpty().trim(), page))
    }

    @GetMapping("/{id}/download")
    @Transactional(readOnly = true)
    fun downloadTab(@PathVariable("id") id: Long): ResponseEntity<ByteArray> {
        val tab = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Tab not found")
        }
        val disposition = ContentDisposition.attachment()
            .filename(tab.filename, StandardCharsets.UTF_8)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(tab.data)
    }
}
