package br.com.pagseguro.rekognition.awsrekognition.service

import br.com.pagseguro.rekognition.awsrekognition.config.AmazonConfig
import com.amazonaws.services.rekognition.AmazonRekognition
import com.amazonaws.services.rekognition.model.*
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*


@Service
class ImageService {

    @Autowired
    lateinit var client: AmazonS3

    @Autowired
    lateinit var rekognitionClient: AmazonRekognition

    fun validateCollection(): DescribeCollectionResult {
        val describeCollectionRequest = DescribeCollectionRequest().withCollectionId(AmazonConfig.COLLECTION_ID)
        return this.rekognitionClient.describeCollection(describeCollectionRequest)
    }

    fun createCollection(): CreateCollectionResult {
        val createCollectionRequest = CreateCollectionRequest().withCollectionId(AmazonConfig.COLLECTION_ID)
        return this.rekognitionClient.createCollection(createCollectionRequest)
    }

    fun compare(multipartFileSource: MultipartFile, multipartFileTarget: MultipartFile): CompareFacesResult {
        val filenameSource = this.put(multipartFileSource)
        val filenameTarget = this.put(multipartFileTarget)
        val imageSource = this.image(filenameSource)
        val imageTarget = this.image(filenameTarget)
        val request = CompareFacesRequest()
                .withSourceImage(imageSource)
                .withTargetImage(imageTarget)
                .withSimilarityThreshold(70f)
        return this.rekognitionClient.compareFaces(request)
    }

    fun find(multipartFile: MultipartFile): SearchFacesByImageResult {
        val filename = this.put(multipartFile)
        val image = this.image(filename)
        val searchFacesByImageRequest = SearchFacesByImageRequest()
                .withCollectionId(AmazonConfig.COLLECTION_ID)
                .withImage(image)
                .withFaceMatchThreshold(70f)
                .withMaxFaces(1)
        return this.rekognitionClient.searchFacesByImage(searchFacesByImageRequest)
    }

    fun upload(multipartFile: MultipartFile): IndexFacesResult {
        val filename = this.put(multipartFile)
        val image = this.image(filename)
        val indexFacesRequest = IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO)
                .withMaxFaces(1)
                .withCollectionId(AmazonConfig.COLLECTION_ID)
                .withExternalImageId(filename)
                .withDetectionAttributes("DEFAULT")
        return this.rekognitionClient.indexFaces(indexFacesRequest)
    }

    private fun image(filename: String): Image {
        val s3Object = S3Object().withBucket(AmazonConfig.BUCKET_NAME).withName(filename)
        return Image().withS3Object(s3Object)
    }

    private fun put(multipartFile: MultipartFile): String {
        val filename = this.slugify(multipartFile)
        val file = this.convert(multipartFile)
        val putObjectRequest = PutObjectRequest(AmazonConfig.BUCKET_NAME, filename, file)
        this.client.putObject(putObjectRequest)
        return filename
    }

    private fun convert(multipartFile: MultipartFile): File {
        return File(multipartFile.originalFilename)
    }

    private fun slugify(multipartFile: MultipartFile): String {
        return Date().time.toString() + "-" + multipartFile.originalFilename?.replace(" ", "_")
    }

}