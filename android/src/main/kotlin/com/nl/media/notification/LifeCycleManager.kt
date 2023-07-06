package com.nl.media.notification

import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import com.nl.media.notification.notification.NotificationUIManager

class LifeCycleManager: DefaultLifecycleObserver {
    var isForeground = true;
    companion object {
        val get by lazy(LazyThreadSafetyMode.NONE) {
            LifeCycleManager()
        }
    }

    fun register(){
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun unRegister(){
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        isForeground = true
        super.onResume(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        isForeground = false
        super.onPause(owner)
    }
}