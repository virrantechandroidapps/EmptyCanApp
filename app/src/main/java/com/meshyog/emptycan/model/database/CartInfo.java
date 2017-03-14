package com.meshyog.emptycan.model.database;

import com.google.gson.JsonObject;

import java.io.Serializable;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by varadhan on 10-12-2016.
 */
public class CartInfo  implements Serializable,CardWithList.ListObject {

    private int cartId;
    private String  cartPrdctName;
    private long  cartPrdctId  ;
    private int cartPrdctQty   ;
    private float cartPrdctPrice  ;
    private int  cartPrdctUnit ;
    private String  cartBookDate ;
    private long  cartBookby ;
    private String  cartPrdctOffer;

    public CartInfo(long productId) {
        cartPrdctId=productId;
        cartId=hashCode();
    }



    public long getCartBookby() {
        return cartBookby;
    }

    public void setCartBookby(long cartBookby) {
        this.cartBookby = cartBookby;
    }

    public String getCartBookDate() {
        return cartBookDate;
    }

    public void setCartBookDate(String cartBookDate) {
        this.cartBookDate = cartBookDate;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public long getCartPrdctId() {
        return cartPrdctId;
    }

    public void setCartPrdctId(long cartPrdctId) {
        this.cartPrdctId = cartPrdctId;
    }

    public String getCartPrdctName() {
        return cartPrdctName;
    }

    public void setCartPrdctName(String cartPrdctName) {
        this.cartPrdctName = cartPrdctName;
    }

    public String getCartPrdctOffer() {
        return cartPrdctOffer;
    }

    public void setCartPrdctOffer(String cartPrdctOffer) {
        this.cartPrdctOffer = cartPrdctOffer;
    }

    public float getCartPrdctPrice() {
        return cartPrdctPrice;
    }

    public void setCartPrdctPrice(float cartPrdctPrice) {
        this.cartPrdctPrice = cartPrdctPrice;
    }

    public int getCartPrdctQty() {
        return cartPrdctQty;
    }

    public void setCartPrdctQty(int cartPrdctQty) {
        this.cartPrdctQty = cartPrdctQty;
    }

    public int getCartPrdctUnit() {
        return cartPrdctUnit;
    }

    public void setCartPrdctUnit(int cartPrdctUnit) {
        this.cartPrdctUnit = cartPrdctUnit;
    }
    @Override
    public boolean equals(Object obj) {
        CartInfo cardInfo = (CartInfo) obj;
        return getCartPrdctId() == cardInfo.getCartPrdctId();
    }


    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public Card getParentCard() {
        return null;
    }

    @Override
    public void setOnItemClickListener(CardWithList.OnItemClickListener onItemClickListener) {

    }

    @Override
    public CardWithList.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public void setSwipeable(boolean isSwipeable) {

    }

    @Override
    public CardWithList.OnItemSwipeListener getOnItemSwipeListener() {
        return null;
    }

    @Override
    public void setOnItemSwipeListener(CardWithList.OnItemSwipeListener onSwipeListener) {

    }
}
