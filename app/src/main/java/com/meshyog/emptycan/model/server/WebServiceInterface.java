package com.meshyog.emptycan.model.server;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Aniruthan on 3/13/2016.
 */
public interface WebServiceInterface {
    @POST("virrantech/distributorservice/adddistributor")
    Call<JsonObject> addNewDistributor(@Body String data);

    @GET("virrantech/distributorservice/authenticate-distributor")
    Call<JsonObject> distributorAuthenticateService(@Query("key1") String username, @Query("key2") String passwrd);

    @POST("virrantech/distributorservice/addnewshop")
    Call<JsonObject> addNewShop(@Body String data);

    @POST("virrantech/distributorservice/addnewitem")
    Call<JsonObject> addNewItem(@Body String data);

    @GET("virrantech/distributorservice/getallwatercan")
    Call<JsonArray> getAllShopWaterCans(@Query("distributorid") String distributorId);

    @POST("virrantech/distributorservice/update-watercaninfo")
    Call<JsonObject> updateWaterCans(@Body String updateData);

    @POST("virrantech/distributorservice/addnewstock")
    Call<JsonObject> newStockCan(@Body String newStockCan);

    @GET("virrantech/distributorservice/consumeraddresses")
    Call<JsonArray> consumerAddresses(@Query("consumerid") String consumerId);

    @POST("virrantech/distributorservice/approvenewconsumer")
    Call<String> approveNewConsumer(@Body String approveUserInfo);

    @POST("virrantech/distributorservice/denynewconsumer")
    Call<String> denyNewConsumer(@Body String denyUserInfo);

    @GET("virrantech/distributorservice/get-consumers-by-ditributors")
    Call<JsonArray> getAllCustomerByDistributor(@Query("distributorid") String distributorId);

    @POST("virrantech/distributorservice/add-consumer")
    Call<JsonObject> addNewConsumer(@Body String consumerSignUpPOJO);

    @GET("virrantech/distributorservice/find-distributor-by-location")
    Call<JsonObject> searchDistributorByLocation(@Query("city") String cityName, @Query("area") String areaName);

    @GET("virrantech/distributorservice/newcustomerrequest")
    Call<JsonObject> addNewDistrbutorRequest(@Query("consumerid") String consumerid, @Query("distributorid") String distributorid);

    @POST("virrantech/distributorservice/save-delivery-address")
    Call<JsonObject> saveDeliveryAddress(@Body String consumerDeliveryAddress);

    @GET("virrantech/distributorservice/get-ditributors-by-consumers")
    Call<JsonArray> getAllDistibutorsByConsumer(@Query("consumerid") String consumerid);

    @POST("virrantech/distributorservice/update-consumer-profile-data")
    Call<JsonObject> updateProfileData(@Body String consumerProfileData);

    @POST("virrantech/distributorservice/update-consumer-password")
    Call<JsonObject> updatePassword(@Body String consumerPasswrd);

    @POST("virrantech/distributorservice/update-delivery-address")
    Call<JsonObject> updateAddressData(@Body String consumerDeliveryAddress);

    @POST("virrantech/distributorservice/new-water-can-request")
    Call<JsonObject> sendWaterCanRequestToDistirbutor(@Body String consumerDeliveryAddress);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("save-new-consumer")
    Call<JsonObject> saveNewConsumer(@Body JsonObject consumerProifle);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-new-servicerequest")
    Call<JsonObject> bookNewWaterCan(@Body JsonObject bookCanObject);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-new-address")
    Call<JsonObject> addConsumerAddress(@Body JsonObject consumerAddress);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-fcm-token-registraion")
    Call<JsonObject> sendFCMToken(@Body JsonObject consumerAddress);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-login-authenticate")
    Call<JsonObject> consumerLoginAuthentication(@Body JsonObject consumerLogin);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-new-order")
    Call<JsonObject> consumerNewOrder(@Body JsonObject orderInfoJsonObj);


    @POST("consumer-address/{consumerId}")
    Call<JsonArray> getConsumerAddress(@Path(value = "consumerId", encoded = false) long consumerId);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-add-basic-profile")
    Call<JsonObject> consumerAddBasicProfile(@Body JsonObject orderInfoJsonObj);

    @POST("consumer-phone-no-exists/{phoneNo}")
    Call<JsonObject> isPhoneNoExists(@Path(value = "phoneNo", encoded = false) String phoneNo);


    @POST("get-distributor-products/{distributorId}")
    Call<JsonArray> getProducts(@Path(value = "distributorId", encoded = false) long distributorId);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("update-notification-status")
    Call<JsonObject> updateNotificationStatus(@Body JsonObject notificationBundle);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("get-consumer-orders")
    Call<JsonArray> getMyOrders(@Body JsonObject orderQueryDetails);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-refferal-details")
    Call<JsonObject> addReferalDetails(@Body JsonObject referralDetails);

    /*@GET("consumer-image-upload")
    Call<JsonObject> uploadImage(@Query("userprofilepic") InputStream consumerid);*/

    /*@Multipart
    @POST("consumer-image-upload")
    Call<JsonObject> uploadImage (@Header("Authorization") String authorization,
                               @Part("file\"; filename=\"pp.png\" ") RequestBody file ,
                               @Part("FirstName") RequestBody fname,
                               @Part("Id") RequestBody id);*/
    /*@Multipart
    @POST("consumer-image-upload")
    Call<JsonObject> uploadImage (  @Part("profile_pic\"; filename=\"pp.png\" ") RequestBody fileName);*/
    @Multipart
    @POST("consumer-image-upload/{consumerKey}")
    Call<JsonObject> uploadImage (   @Part("file\"; filename=\"pp.png\" ") MultipartBody.Part file, @Path(value = "consumerKey", encoded = false) long consumerKey );
   /* @Multipart
    @POST("consumer-image-upload/{consumerKey}")
    Call<JsonObject> uploadImage (  @Path(value = "consumerKey", encoded = false) long consumerKey );*/
   @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
   @POST("update-user-location")
   Call<JsonObject> saveUserLocation(@Body JsonObject saveUserLocation);
    @POST("find-order-cartlist/{orderId}")
    Call<JsonArray> getCartList(@Path(value = "orderId", encoded = false) long orderId);
    @POST("cancel-booking-order/{orderId}")
    Call<JsonObject> cancelOrder(@Path(value = "orderId", encoded = false) long orderId,@Query("canceledBy") String canceledBy);

    @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("delete-consumer-address")
    Call<JsonObject> deleteAddress(@Body JsonObject orderQueryDetails);

   /* @Headers({"Content-Type: application/json; charset=utf-8", "User-Agent: application/json", "dataType:json", "accept:application/json"})
    @POST("consumer-login-authenticate")
    Call<JsonObject> editAddress(@Body JsonObject consumerLogin);*/
}


