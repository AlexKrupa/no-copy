package com.ahmedmourad.nocopy.idea.extensions

import com.ahmedmourad.nocopy.compiler.NoCopySyntheticResolveExtension as NoCopyCompilerSyntheticResolveExtension

open class NoCopyIdeSyntheticResolveExtension : NoCopyCompilerSyntheticResolveExtension() {
    override fun onError(message: String) {
        // Don't throw an exception
    }
}
