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
            for (i in 0..<doc.numberOfPages) {
                val bufferedImage = renderer.renderImageWithDPI(i, 160F, ImageType.RGB)
                ImageIO.write(
                    bufferedImage,
                    "jpg",
                    FileUtil.file("D:\\Code\\KotlinProject\\javacv-kotlin\\result\\${num++}.jpg")
                )
            }
            doc.close()
        }
    }

}