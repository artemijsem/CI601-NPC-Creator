package com.almasb.grammy.editor

import com.almasb.grammy.Grammy
import com.fasterxml.jackson.core.io.JsonEOFException
import com.github.almasb.OpenAI
import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.FileChooser
import java.io.File
import java.io.PrintWriter
import javax.json.*;

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GrammyApp : Application(){

//    companion object {
//        private val VERBS = Files.readAllLines(Path.of(RandomStoryCreator::class.java.getResource("verbs.txt").toURI()))
//        private val NOUNS = Files.readAllLines(Path.of(RandomStoryCreator::class.java.getResource("nouns.txt").toURI()))
//        private val ADJECTIVES = Files.readAllLines(Path.of(RandomStoryCreator::class.java.getResource("adj.txt").toURI()))
//    }

    private fun createContent(): Parent {
        val root = VBox(10.0)
        root.setPrefSize(800.0, 600.0)
        /*OpenAI.run();*/
        val area1 = TextArea()
        area1.font = Font.font(38.0)
        area1.isWrapText = true
        area1.text = "World:\n" +
                "Characters:\n" +
                "Location:\n" +
                "Context:"

        val area2 = TextArea()
        area2.font = Font.font(38.0)
        area2.isWrapText = true



        val btnRun = Button("Run")
        btnRun.setOnAction {


            area2.text += generateDialogue(area1.text)




//            grammar.addSymbol("noun", NOUNS)
//            grammar.addSymbol("verb", VERBS)
//            grammar.addSymbol("adj", ADJECTIVES)


        }

        val btnCreate = Button("Random Text")
        btnCreate.setOnAction {
            val creator = RandomStoryCreator()

            area1.text = creator.createStoryAsJSON()
        }


        val box = HBox(area1, area2)
        box.prefHeight = 550.0

        root.children.addAll(box, HBox(btnRun, btnCreate))

        return root
    }

    override fun start(stage: Stage) {
        stage.scene = Scene(createContent())
        stage.show()


    }
}

fun generateDialogue(inputText : String): String {
    var outputText = "";
    try {
        val openAItext = OpenAI.run(inputText);

        val openAItexts = openAItext.split("@").toTypedArray()
        for (a in openAItexts)
            {
                println(a)
                val grammar = Grammy.createGrammar(a);
                try {
                    outputText += grammar.flatten() + "\n\n"
                } catch (e: Exception) {
                    println( "Syntax error: $e" )
                    outputText = generateDialogue(inputText)
                }

            }

        }

        catch (e: JsonEOFException)
        {
            println("\n\n\n=======================!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!=====================\n\n\n ERROR: $e\n\n\n==================!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!==========================\n\n\n")
            outputText + "Error"
            outputText = generateDialogue(inputText)
        }


    val out = PrintWriter("Output/Text_Output.txt")

    out.println(outputText)

    out.close()


    return outputText
}

fun main() {
    Application.launch(GrammyApp::class.java)
}