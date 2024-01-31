package com.ogzkesk.pdf

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MPdf(private val context: Context) {

    suspend fun create(models: List<TestModel1>, testModel2s: List<TestModel2>): ByteArray? {
        return withContext(Dispatchers.IO) {

            val byteArrayOutputStream = ByteArrayOutputStream()
            val pdfWriter = PdfWriter(byteArrayOutputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            val table = Table(UnitValue.createPercentArray(4)).useAllAvailableWidth()
            val table2 = Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()

            try {

                table.addHeaderCell("ID")
                table.addHeaderCell("Name")
                table.addHeaderCell("Surname")
                table.addHeaderCell("School")

                models.forEach { model ->
                    table.addCell(model.id.toString())
                    table.addCell(model.name)
                    table.addCell(model.surname)
                    table.addCell(model.test)
                }

                table2.addHeaderCell("ID")
                table2.addHeaderCell("Name")
                table2.addHeaderCell("Class")

                testModel2s.forEach { model ->
                    table2.addCell(model.id.toString())
                    table2.addCell(model.name)
                    table2.addCell(model.test)
                }


                document.add(table)
                document.add(AreaBreak()) // to send table2 to page2
                document.add(table2)
                document.close()

                byteArrayOutputStream.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }


    suspend fun exportPdf(pdf: ByteArray?): Boolean {
        return withContext(Dispatchers.IO) {

            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val date = Date()
            val name = formatter.format(date).run { "PDF-$this.pdf" }

            try {

                if (pdf == null || pdf.isEmpty()) {
                    throw NullPointerException("Pdf not available")
                }

                val fos: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver: ContentResolver = context.contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "Documents/HuntAssociation"
                    )

                    val uri = resolver.insert(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        contentValues
                    ) ?: throw NullPointerException("file uri not exists")

                    resolver.openOutputStream(uri)
                        ?: throw NullPointerException("openOutputStream not exists")
                } else {

                    // Permission Needed below api 28
                    if(context.checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        throw SecurityException("Permission Needed")
                    }

                    val root = Environment.getExternalStorageDirectory().absolutePath + File.separator + "HuntAssociation"
                    val dir = File(root)
                    if(!dir.exists()){
                        dir.mkdir()
                    }

                    val file = File(root, name)
                    FileOutputStream(file)
                }

                fos.write(pdf)
                fos.flush()
                fos.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

