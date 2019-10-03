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

        CSVWriter(outputFile.writer()).use { out ->

            out.writeNext(arrayOf("type", "path", "comment range", "sentence"))

            val javaFiles = sources.asSequence()
                    .flatMap { it.walk() }
                    .filter { it.isFile }
                    .filter { "java" == it.extension }

            for (file in javaFiles) {

                val comments = parser.parse(file.readText()).commentsCollection.orElse(null) ?: continue

                for (comment in comments.blockComments) {
                    comment.toText().sentences().forEach { sentence ->
                        out.writeNext(arrayOf("BlockComment", file.path, comment.startLine().toString(), sentence))
                    }
                }

                for (comment in comments.javadocComments) {
                    comment.toText().sentences().forEach { sentence ->
                        out.writeNext(arrayOf("JavadocComment", file.path, comment.startLine().toString(), sentence))
                    }
                }

                for (comment in comments.contiguousLineComments) {
                    comment.toText().sentences().forEach { sentence ->
                        out.writeNext(arrayOf("LineComment", file.path, comment.startLine().toString(), sentence))
                    }
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    App().main(args)
}