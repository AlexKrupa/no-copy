package dev.ahmedmourad.nocopy.compiler

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.hasPrimaryConstructor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyPrivateApi

internal fun ClassDescriptor.hasPrivatePrimaryConstructor(): Boolean {
    if (!hasPrimaryConstructor()) return false
    return constructors.first { it.isPrimary }.isEffectivelyPrivateApi
}

internal fun isGeneratedCopyMethod(
        name: Name
): Boolean {
    return name.asString() == "copy"
}

internal fun Collection<SimpleFunctionDescriptor>.findGeneratedCopyMethodIndex(
        classDescriptor: ClassDescriptor
): Int? {

    if (size == 1) {
        return 0
    }

    val primaryConstructor = classDescriptor.constructors.firstOrNull { it.isPrimary } ?: return null
    val primaryConstructorParameters = primaryConstructor.valueParameters

    val index = this.indexOfLast {
        it.name.asString() == "copy"
                && it.returnType == classDescriptor.defaultType
                && it.valueParameters.size == primaryConstructorParameters.size
                && it.valueParameters.filterIndexed { index, descriptor ->
            primaryConstructorParameters[index].type != descriptor.type &&
                    primaryConstructorParameters[index].name != descriptor.name
        }.isEmpty()
    }
    return if (index < 0) {
        null
    } else {
        index
    }
}
