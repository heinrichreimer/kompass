package io.sellmair.kompass.compiler.destination.visitor

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.sellmair.kompass.compiler.ClassNames
import io.sellmair.kompass.compiler.destination.tree.DestinationsRenderTree

class KompassBuilderAutoCraneVisitor : DestinationVisitor {
    override fun visit(target: DestinationsRenderTree) {
        target.kompassBuilderExtensions.autoCrane.visit(target)
    }

    private fun FunSpec.Builder.visit(tree: DestinationsRenderTree) {
        buildHeader()
        buildImplementation(tree)
    }

    private fun FunSpec.Builder.buildHeader() {
        addTypeVariable(TypeVariableName("T", ClassNames.any))
        receiver(ClassNames.kompassBuilder("T"))
    }

    private fun FunSpec.Builder.buildImplementation(tree: DestinationsRenderTree) {
        addStatement("""
        this.addCrane(AutoCrane<T>())
        """.trimIndent())
    }

}