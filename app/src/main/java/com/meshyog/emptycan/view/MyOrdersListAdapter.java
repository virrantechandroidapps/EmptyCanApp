package com.meshyog.emptycan.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.controller.BookCanFormActivity;
import com.meshyog.emptycan.controller.MyOrderDetailsActivity;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class MyOrdersListAdapter extends ArrayAdapter {

    Context context;
    JsonArray orderListJsonArray;
    public SweetAlertDialog progressDialog;
    private int layoutId;
    JSONObject orderDetails;
    public MyOrdersListAdapter(Context context, JsonArray list, int layoutId) {
        super(context, layoutId);
        this.context = context;
        this.orderListJsonArray = list;
        this.layoutId=layoutId;
    }

    @Override
    public int getCount() {
        return orderListJsonArray.size();
    }

    @Override
    public JsonArray getItem(int i) {
        return orderListJsonArray.get(i).getAsJsonArray();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, viewGroup, false);
            //view= LayoutInflater.from(context).inflate(R.layout.settingslistview_item,viewGroup,false);
            viewHolder=new ViewHolder();
            viewHolder.date=(TextView)view.findViewById(R.id.listview_item_date);
            viewHolder.bookingId =(TextView)view.findViewById(R.id.bookingId);
            viewHolder.payableAmount =(TextView)view.findViewById(R.id.payableAmnt);
              viewHolder.orderStatus=    (TextView)view.findViewById(R.id.orderStatus);


            //viewHolder.image=(ImageView)view.findViewById(R.id.circle_imageview);


           // System.out.println(orderInfoJson);
            view.setTag(viewHolder);


        }else{
            viewHolder=(ViewHolder)view.getTag();
        }

        JsonObject orderInfoJson= orderListJsonArray.get(i).getAsJsonObject();
        long orderKey= orderInfoJson.get("id").getAsJsonObject().get("id").getAsLong();
        String status= orderInfoJson.get("orderStatus").getAsString();
        if(EmptycanDataBase.ORDER_CANCELLED_BY_CUS.equals(status)){
            viewHolder.orderStatus.setText("ORDER CANCELLED BY YOU");
        }else if(EmptycanDataBase.ORDER_INITIATED.equals(status)){
            viewHolder.orderStatus.setText("ORDER INITIATED");
        }else if(EmptycanDataBase.ORDER_SCHEDULED.equals(status)){
            viewHolder.orderStatus.setText("ORDER SCHEDULED");
        }else if(EmptycanDataBase.ORDER_CANCELLED_BY_DELVIRYBOY.equals(status)){
            viewHolder.orderStatus.setText("ORDER CANCELLED BY TEAM");
        }else if(EmptycanDataBase.ORDER_DELIVERED.equals(status)){
            viewHolder.orderStatus.setText("DELIVERED");
        }

        String formattedDate=orderInfoJson.get("orderReceivedDateTime")!=null?orderInfoJson.get("orderReceivedDateTime").getAsString():"";
        String bookingId=orderInfoJson.get("orderId")!=null?orderInfoJson.get("orderId").getAsString():"";
        float totalPayment=orderInfoJson.get("totalPayment")!=null?orderInfoJson.get("totalPayment").getAsFloat():0.0f;
        viewHolder.orderId=bookingId;
        viewHolder.orderKey=orderKey;
        viewHolder.totalAmount=totalPayment;
        //viewHolder.cartInfoList=orderInfoJson.get("cartInfoList").getAsJsonArray();
        viewHolder.bookingId.setText("Order Id : "+bookingId);
        viewHolder.date.setText(formattedDate);
        viewHolder.payableAmount.setText("\u20B9 " +totalPayment);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    ViewHolder viewHolder= (ViewHolder)v.getTag();
                     orderDetails=new JSONObject();
                    orderDetails.put("booking_date",viewHolder.date.getText());
                    orderDetails.put("order_key",viewHolder.orderKey);
                    orderDetails.put("total_amount",viewHolder.totalAmount);
                    orderDetails.put("order_id",viewHolder.orderId);
                    orderDetails.put("cart_list",viewHolder.cartInfoList);
                    getCartListOfOrder(viewHolder.orderKey);

                }catch(Exception e){
                    e.printStackTrace();
                }

                // Toast.makeText(context,""+viewHolder.bookingId,Toast.LENGTH_SHORT).show();
            }
        });
       // DateUtils.
        /*Resources res = context.getResources();
        Bitmap src = BitmapFactory.decodeResource(res, R.drawable.download);
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(res, src);
        dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);*/
//        viewHolder.image.setImageDrawable(dr);
        return view;
    }

    private class ViewHolder{
        TextView date;
        TextView bookingId;
        TextView  payableAmount;
        ImageView image;
        JsonArray cartInfoList;
        String orderId;
        String orderDate;
        float totalAmount;
        long orderKey;
        TextView orderStatus;
    }

    public void launchBookingDetailsActivity(JSONObject orderMetaData){
            Intent orderDetailActivity = new Intent(context,MyOrderDetailsActivity.class);
            orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            orderDetailActivity.putExtra(AppConstants.ORDERMETADATA, orderMetaData.toString());
            this.context.startActivity(orderDetailActivity);
}

    public void getCartListOfOrder(long orderId){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonArray> call=null;
        progressDialog = new SweetAlertDialog(this.context,SweetAlertDialog.PROGRESS_TYPE);

        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try{
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call = webServiceInterface.getCartList(orderId);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Response<JsonArray> response, Retrofit retrofit)    {
                    try{
                        if (response.message().equals("OK")) {
                            orderDetails.put("cart_list",response.body().getAsJsonArray());
                            launchBookingDetailsActivity(orderDetails);
                        } else {

                        }
                    }catch (Exception e){

                    }finally {
                        progressDialog.dismiss();
                    }


                }
                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
        }


    }
}
