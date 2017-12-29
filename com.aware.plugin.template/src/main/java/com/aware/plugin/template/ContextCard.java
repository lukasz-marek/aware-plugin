package com.aware.plugin.template;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.aware.utils.IContextCard;

public class ContextCard implements IContextCard {

    public ContextCard() {
    }

    @Override
    public View getContextCard(Context context) {
        final View card = LayoutInflater.from(context).inflate(R.layout.card, null);

        return card;
    }
}
