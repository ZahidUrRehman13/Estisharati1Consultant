package digital.upbeat.estisharati_consultant.CommonApiHelper

import android.content.Context
import android.util.Log
import android.widget.Toast
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.SplashScreen
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SendDeviceTokenHelper(val context: Context, val splashScreen: SplashScreen?, val IfNotService: Boolean) {
    var helperMethods: HelperMethods
    var retrofitInterface: RetrofitInterface
    var preferencesHelper: SharedPreferencesHelper

    init {
        helperMethods = HelperMethods(this.context)
        preferencesHelper = SharedPreferencesHelper(this.context)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun SendDeviceTokenFirebase() {
        if (!preferencesHelper.isConsultantLogIn) {
            return
        }
        if (IfNotService) {
            helperMethods.showProgressDialog(context.getString(R.string.please_wait_while_loading))
        }
        if (GlobalData.FcmToken.equals("")) {
            helperMethods.dismissProgressDialog()
            Toast.makeText(context, context.getString(R.string.could_not_get_your_device_token_please_try_again), Toast.LENGTH_LONG).show();
            if (splashScreen != null) {
                splashScreen.checkSelfPermission()
            }

            return
        }
        val responseBodyCall = retrofitInterface.UPDATE_FCM_API_CALL("Bearer ${preferencesHelper.logInConsultant.access_token}", GlobalData.FcmToken)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val user = jsonObject.getString("user")
                            } else {
                                val message = jsonObject.optString("message")
                                helperMethods.showToastMessage(message)
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    return
                                }
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(context.getString(R.string.something_went_wrong_on_backend_server))

                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(context.getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
                if (splashScreen != null) {
                    splashScreen.checkSelfPermission()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                //                Toast.makeText(context, context.getString(R.string.your_network_connection_is_slow_please_try_again), Toast.LENGTH_LONG).show()
                if (splashScreen != null) {
                    splashScreen.checkSelfPermission()
                }
            }
        })
    }
}
