package com.michaelgidron.proxyserver

import android.content.Context
import android.content.Intent
import android.util.Log
import com.good.gd.GDStateListener
import com.michaelgidron.proxyserver.GDStateHandler.GDStatus



class GDStateHandler:GDStateListener {
    var isAuthorized = false
    var uiIntent:Intent? = null
    var context:Context? = null
    var appContext:Context? = null
    var uiLaunched = true
    var gdStatus = GDStatus.GD_STATUS_UNKNOWN

    val GD_AUTHORIZED = "gd_authorized"

    enum class GDStatus {
        GD_AUTHORIZED,
        GD_NOT_AUTHORIZED_TEMPORARY,
        GD_CONFIG_CHANGED,
        GD_POLICY_CHANGED,
        GD_SERVICES_CHANGED,
        GD_STATUS_UNKNOWN,
        GD_NOT_AUTHORIZED_PERMANENT
    }

    override fun onAuthorized() {
        Log.d("GDStateHandler", "onAuthorized")
        isAuthorized = true

        gdStatus = GDStatus.GD_AUTHORIZED
        appContext = GDStateHandler.sharedInstance().appContext

        if (!uiLaunched && uiIntent != null && context != null)
        {
            uiLaunched = true
        }
        broadcast(GD_AUTHORIZED)
    }

    override fun onLocked() {}
    override fun onWiped() {}
    override fun onUpdateConfig(map:Map<String, Any>) {}
    override fun onUpdatePolicy(map:Map<String, Any>) {}
    override fun onUpdateServices() {}
    override fun onUpdateEntitlements() {}
    private fun broadcast(notificationMsg:String) {}

    companion object {
        // notification constants
        val GD_AUTHORIZED = "gd_authorized"
        private var instance:GDStateHandler? = null
        @Synchronized fun sharedInstance():GDStateHandler {
            if (instance == null)
            {
                instance = GDStateHandler()
            }
            return instance!!
        }
    }
}