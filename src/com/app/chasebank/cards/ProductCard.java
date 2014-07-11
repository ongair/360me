package com.app.chasebank.cards;

import com.app.chasebank.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import it.gmariotti.cardslib.library.internal.CardExpand;

public class ProductCard extends CardExpand {

    //Use your resource ID for your inner layout
    public ProductCard(Context context) {
        super(context, R.layout.list_product_holder);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View v) {

        if (v == null) return;
        //Retrieve TextView elements
        ImageView logo = (ImageView) v.findViewById(R.id.list_company_logo);
		TextView desc = (TextView) v.findViewById(R.id.list_company_description);
		TextView name = (TextView) v.findViewById(R.id.list_company_product_name);
    }
}