import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.javaparser.JavaParser
import com.opencsv.CSVWriter
import java.io.File


class App : CliktCommand() {

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true, showRequiredTag = true) }
    }

    private val outputFile: File by option("-o", "--output")
            .file(exists = false, writable = true)
            .default(File("output.csv"))

    private val sources: List<File> by argument()
            .file(exists = true, readable = true)
            .multiple()

    private val parser = JavaParser()

    override fun run() {

        CSVWriter(outputFile.bufferedWriter()).use { out ->

            out.writeNext(arrayOf("type", "path", "begin line", "comment"))

            val files = sources.asSequence()
                    .flatMap { it.walk() }
                    .filter { it.isFile }
                    .filter { "java" == it.extension }

            for (file in files) {

                val comments = parser.parse(file.readText()).commentsCollection.orElse(null) ?: continue

                for (comment in comments.blockComments) {
                    out.writeNext(arrayOf("Block", file.path, comment.beginLine.toString(), comment.toText()))
                }

                for (comment in comments.javadocComments) {
                    out.writeNext(arrayOf("Javadoc", file.path, comment.beginLine.toString(), comment.toText()))
                }

                for (comment in comments.contiguousLineComments) {
                    out.writeNext(arrayOf("Line", file.path, comment.beginLine.toString(), comment.toText()))
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    App().main(args)
}