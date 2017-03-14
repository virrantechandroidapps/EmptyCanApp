package com.meshyog.emptycan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.controller.BookCanFormActivity;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.database.CartInfo;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by varadhan on 11-12-2016.
 */
public class OrderInfoCardView extends CardWithList {
    protected AppConfig appConfig;
    ArrayList<CartInfo> cartInfoList=null;
    private Context cartContext;
    private Activity myActivity;
    public static float payableAmout;
    public OrderInfoCardView(Context context,ArrayList<CartInfo> cartInfoListParam,Activity activity) {
        super(context);
        payableAmout=0.0F;
        this.cartContext=context;
        this.myActivity=activity;
        //appConfig = (AppConfig)context;
        if(cartInfoListParam!=null)
            this.cartInfoList=cartInfoListParam;
        else
            this.cartInfoList=new ArrayList<>();

    }

    public void launcBookCanFormActivity(){
        Intent orderDetailActivity = new Intent(cartContext,BookCanFormActivity.class);
        orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoList);
        orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        myActivity.startActivity(orderDetailActivity);
       // this.myActivity.finish();
    }
    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        OrderDetailsCardHeader header = new OrderDetailsCardHeader(getContext(),R.layout.order_details_card_header);

        //Add a popup menu. This method set OverFlow button to visible
        header.setPopupMenu(R.menu.cartinfo_card_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                   String itemName= item.getTitle().toString();
                if(itemName.equals("Edit")){
                    launcBookCanFormActivity();
                }
            }
        });
        header.setTitle("Order Details"); //should use R.string.
        return header;
    }


    class OrderDetailsCardHeader extends CardHeader {
        OrderDetailsCardHeader(Context x0, int x1) {
            super(x0, x1);
        }

        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            TextView subTitle = (TextView) view.findViewById(R.id.carddemo_googlenow_main_inner_lastupdate);
            if (subTitle != null) {
               // subTitle.setText("ORDER ID: ORD201612200");
            }
        }
    }
    @Override
    protected void initCard() {
        setSwipeable(false);
        setSwipeable(false);
       /* setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }


        });*/
    }

    @Override
    protected List<ListObject> initChildren() {
         int size=cartInfoList.size();
        List<ListObject> mObjects = new ArrayList<ListObject>();
        for(int i=0;i<size;i++){
            mObjects.add(cartInfoList.get(i));
        }


       /* CartInfo cartInfo= new CartInfo(545193);
        cartInfo.setCartPrdctName("Product  1");
        cartInfo.setCartPrdctQty(2);
        cartInfo.setCartPrdctPrice(34.5f);

        CartInfo cartInfo2= new CartInfo(545194);
        cartInfo2.setCartPrdctName("Product  2");
        cartInfo2.setCartPrdctQty(5);
        cartInfo2.setCartPrdctPrice(70.2f);

        CartInfo cartInfo3= new CartInfo(545195);
        cartInfo3.setCartPrdctName("Product  3");
        cartInfo3.setCartPrdctQty(5);
        cartInfo3.setCartPrdctPrice(90.2f);

        mObjects.add(cartInfo);
        mObjects.add(cartInfo2);
        mObjects.add(cartInfo3);*/
        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        TextView productName = (TextView) convertView.findViewById(R.id.productName);

        TextView productQty = (TextView) convertView.findViewById(R.id.productQty);
        TextView productPrice = (TextView) convertView.findViewById(R.id.productPrice);
        TextView total = (TextView) convertView.findViewById(R.id.total);

        CartInfo cartInfo= (CartInfo)object;
        payableAmout+=cartInfo.getCartPrdctUnit()*cartInfo.getCartPrdctPrice();
       // icon.setImageResource(cartInfo.weatherIcon);
       float overAllValue= cartInfo.getCartPrdctUnit()* cartInfo.getCartPrdctPrice();

        productName.setText(cartInfo.getCartPrdctName());
        productQty.setText(String.valueOf(cartInfo.getCartPrdctUnit()));
        productPrice.setText(String.valueOf(cartInfo.getCartPrdctPrice()));
        total.setText(String.format("%.2f",overAllValue));
        productName. setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.cart_list_layout;
    }
}
