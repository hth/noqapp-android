package com.noqapp.android.client.views.version_2.coroutines

import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.function.BiConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class Coroutines {

    @JvmOverloads
    fun <R> getContinuation(onFinished: BiConsumer<R?, Throwable?>, dispatcher: CoroutineDispatcher = Dispatchers.Default): Continuation<R> {
        return object : Continuation<R> {
            override val context: CoroutineContext
                get() = dispatcher

            override fun resumeWith(result: Result<R>) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onFinished.accept(result.getOrNull(), result.exceptionOrNull())
                }
            }
        }
    }

}