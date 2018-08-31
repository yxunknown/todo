package com.dev.hercat.todo.tools

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivitiesManager {
    private val liveActivity: MutableSet<Activity> = mutableSetOf()
    private lateinit var topActivity: Activity
    companion object {
        private lateinit var activitiesManager: ActivitiesManager

        fun getInstance(app: Application): ActivitiesManager {
            activitiesManager = ActivitiesManager()
            app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityPaused(activity: Activity?) {
                }

                override fun onActivityResumed(activity: Activity?) {
                    activitiesManager.liveActivity.add(activity as Activity)
                    activitiesManager.topActivity = activity
                }

                override fun onActivityStarted(activity: Activity?) {
                    activitiesManager.liveActivity.add(activity as Activity)
                    activitiesManager.topActivity = activity
                }

                override fun onActivityDestroyed(activity: Activity?) {
                    activitiesManager.liveActivity.remove(activity as Activity)
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                }
            })
            return activitiesManager
        }
    }
}