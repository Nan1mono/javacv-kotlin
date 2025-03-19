package com.project.video.pdf

import cn.hutool.core.collection.CollectionUtil
import cn.hutool.core.img.ImgUtil
import cn.hutool.core.io.FileUtil
import com.spire.pdf.PdfDocument
import com.spire.pdf.graphics.PdfImageType
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max

class Pdf {

    companion object {

        private var num = 1

        @JvmStatic
        fun main(args: Array<String>) {
            val pdfList = readPdf()
            for ((i, pdf) in pdfList.withIndex()) {
                convertPdfToImg(pdf, i)
            }
        }


        private fun readPdf(): List<PDDocument> {
            FileUtil.del("D:\\Code\\KotlinProject\\javacv-kotlin\\result")
            FileUtil.mkdir("D:\\Code\\KotlinProject\\javacv-kotlin\\result")
            val docList = ArrayList<PDDocument>()
            for (i in 1..2) {
                val file = FileUtil.file("D:\\Code\\KotlinProject\\javacv-kotlin\\pdf\\$i.pdf")
                PDDocument.load(file).run {
                    docList.add(this)
                }
            }
            return docList
        }

        private fun convertPdfToImg(doc: PDDocument, count: Int) {
            val renderer = PDFRenderer(doc)
//            val imageList = ArrayList<BufferedImage>()
//            var maxWidth = 0
//            var height = 0
            for (i in 0..<doc.numberOfPages) {
                val bufferedImage = renderer.renderImageWithDPI(i, 160F, ImageType.RGB)
//                if (bufferedImage.width > maxWidth) {
//                    maxWidth = bufferedImage.width
//                }
//                height += bufferedImage.height
//                imageList.add(bufferedImage)
                ImageIO.write(
                    bufferedImage,
                    "jpg",
                    FileUtil.file("D:\\Code\\KotlinProject\\javacv-kotlin\\result\\${num++}.jpg")
                )
            }
            doc.close()
            // 合成图片
//            mergeImg(imageList, maxWidth, height, count)
        }

        private fun mergeImg(imgList: List<BufferedImage>, maxWidth: Int, height: Int, count: Int) {
            val filePath = "D:\\Code\\KotlinProject\\javacv-kotlin\\result\\ToPNG-$count.png"
            val exists = FileUtil.file(filePath).exists()
            if (exists) {
                FileUtil.del(filePath)
            }
            val file = FileUtil.touch(filePath)
            val finalImage = BufferedImage(maxWidth, height, BufferedImage.TYPE_INT_RGB)
            val graphics = finalImage.createGraphics()
            var wx = 0
            var hy = 0
            for (img in imgList) {
                graphics.drawImage(img, 0, hy, null)
                wx += img.width
                hy += img.height
            }
            FileUtil.file()
            ImageIO.write(finalImage, "png", file)
        }
    }

}