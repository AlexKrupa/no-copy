package dev.ahmedmourad.nocopy.compiler

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.JvmTarget
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class NoCopyPluginTests {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `non-data class with private constructor should compile just fine`() {
        val result = compile(kotlin("NonDataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          import dev.ahmedmourad.nocopy.annotations.NoCopy
          class NonDataClass private constructor(val a: Int)
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    @Test
    fun `data class with private constructor should compile just fine`() {
        val result = compile(kotlin("DataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          import dev.ahmedmourad.nocopy.annotations.NoCopy
          data class DataClass private constructor(val a: Int)
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    @Test
    fun `normal class with public constructor should compile just fine`() {
        val result = compile(kotlin("NormalClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          class NormalClass(val a: Int)
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    @Test
    fun `data class with public constructor should compile just fine`() {
        val result = compile(kotlin("DataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          data class DataClass(val a: Int)
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    @Test
    fun `using 'copy' of an data class with public constructor should compile just fine`() {
        val result = compile(kotlin("DataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          data class DataClass(val a: Int) {
            fun withFiveForNoReason() = this.copy(a = 4)
          }
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    @Test
    fun `using 'copy' of a data class with private constructor should fail compilation`() {
        val result = compile(kotlin("DataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          import dev.ahmedmourad.nocopy.annotations.NoCopy
          data class DataClass private constructor(val a: Int) {
            fun withFiveForNoReason() = this.copy(a = 3)
          }
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains("DataClass.kt: (4, 36): Unresolved reference: copy")
    }

    @Test
    fun `using 'copy' of a data class with private constructor which declares another 'copy' should compile just fine`() {
        val result = compile(kotlin("DataClass.kt",
                """
          package dev.ahmedmourad.nocopy.compiler
          import dev.ahmedmourad.nocopy.annotations.NoCopy
          data class DataClass private constructor(val a: Int) {
            fun withFiveForNoReason() = this.copy(a = 11)
            fun copy(a: Int) {
                println(a)
            }
          }
          """
        ))
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result.messages).isEmpty()
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation {
        return KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            compilerPlugins = listOf<ComponentRegistrar>(NoCopyPlugin())
            inheritClassPath = true
            sources = sourceFiles.asList()
            verbose = false
            jvmTarget = JvmTarget.JVM_1_8.description
        }
    }

    private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
        return prepareCompilation(*sourceFiles).compile()
    }
}
