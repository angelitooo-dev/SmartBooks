package co.edu.cecar.smartbooks.core.security

import android.content.Context
import co.edu.cecar.smartbooks.core.datastore.UserPreferences
import kotlinx.coroutines.flow.first

object TokenManager {

    private var cachedToken: String? = null
    private var userPreferences: UserPreferences? = null

    private fun getPreferences(context: Context): UserPreferences {
        return userPreferences ?: synchronized(this) {
            val instance = UserPreferences(context.applicationContext)
            userPreferences = instance
            instance
        }
    }
    suspend fun saveToken(context: Context, jwt: String) {
        cachedToken = jwt
        getPreferences(context).saveToken(jwt)
    }

    suspend fun getToken(context: Context): String? {
        if (cachedToken == null) {
            try {
                cachedToken = getPreferences(context).tokenFlow.first()
            } catch (e: Exception) {
                android.util.Log.e("TOKEN_MANAGER", "Error al leer token", e)
                cachedToken = null
            }
        }
        return cachedToken
    }

    suspend fun clearToken(context: Context) {
        cachedToken = null
        getPreferences(context).clearToken()
    }
}