package br.com.pagseguro.rekognition.awsrekognition.controller

import br.com.pagseguro.rekognition.awsrekognition.service.ImageService
import com.amazonaws.services.rekognition.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/image")
class ImageController {

    @Autowired
    lateinit var imageService: ImageService

    @PostMapping
    fun upload(@RequestPart image: MultipartFile): IndexFacesResult {
        return this.imageService.upload(image)
    }

    @PostMapping("/find")
    fun find(@RequestPart image: MultipartFile): SearchFacesByImageResult {
        return this.imageService.find(image)
    }

    @PostMapping("/compare")
    fun compare(@RequestPart source: MultipartFile, @RequestPart target: MultipartFile): CompareFacesResult {
        return this.imageService.compare(source, target)
    }

    @PostMapping("/collection")
    fun createCollection(): CreateCollectionResult {
        return this.imageService.createCollection()
    }

    @GetMapping("/collection")
    fun findCollection(): DescribeCollectionResult {
        return this.imageService.validateCollection()
    }
}